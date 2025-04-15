package kz.muhammadzahid.eem.service.impl;

import kz.muhammadzahid.eem.dto.UserRequest;
import kz.muhammadzahid.eem.dto.UserResponse;
import kz.muhammadzahid.eem.exception.EmailAlreadyExistsException;
import kz.muhammadzahid.eem.exception.UserNotFoundException;
import kz.muhammadzahid.eem.exception.UsernameAlreadyExistsException;
import kz.muhammadzahid.eem.repo.UserRepository;
import kz.muhammadzahid.eem.service.UserService;
import kz.muhammadzahid.eem.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(Mapper::mapToUserResponse)
                .toList();
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        var user = Mapper.mapToUser(userRequest);
        var savedUser = userRepository.save(user);
        return Mapper.mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        return Mapper.mapToUserResponse(userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        var user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getEmail().equals(userRequest.getEmail())) {
            boolean emailExists = userRepository.existsByEmailAndIdNot(userRequest.getEmail(), id);
            if (emailExists) {
                throw new EmailAlreadyExistsException("Email " + userRequest.getEmail() + " is already in use");
            }
        }

        if (!user.getUsername().equals(userRequest.getUsername())) {
            boolean usernameExists = userRepository.existsByUsernameAndIdNot(userRequest.getUsername(), id);
            if (usernameExists) {
                throw new UsernameAlreadyExistsException("Username " + userRequest.getUsername() + " is already taken");
            }
        }

        var updatedUser = Mapper.mapToUser(userRequest, user);
        userRepository.save(updatedUser);
        return getUserById(id);
    }

    @Override
    public void deleteUser(Long id) {
        var user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }
}

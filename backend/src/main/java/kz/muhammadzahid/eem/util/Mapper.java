package kz.muhammadzahid.eem.util;

import kz.muhammadzahid.eem.dto.UserRequest;
import kz.muhammadzahid.eem.dto.UserResponse;
import kz.muhammadzahid.eem.entity.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class Mapper {

    public UserResponse mapToUserResponse(User user) {
        if (user.getRoles() == null) {
            throw new IllegalArgumentException("User roles must not be null");
        }
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roles(roles)
                .build();
    }

    public User mapToUser(UserRequest userRequest) {
        return User.builder()
                .username(userRequest.getUsername())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .password(userRequest.getPassword())
                .roles(userRequest.getRoles())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public User mapToUser(UserRequest userRequest, User user) {
        user.setUsername(userRequest.getUsername());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setActive(true);
        user.setRoles(userRequest.getRoles());
        return user;
    }
}

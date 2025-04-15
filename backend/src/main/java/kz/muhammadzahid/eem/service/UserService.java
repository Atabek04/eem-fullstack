package kz.muhammadzahid.eem.service;

import kz.muhammadzahid.eem.dto.UserRequest;
import kz.muhammadzahid.eem.dto.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);
}

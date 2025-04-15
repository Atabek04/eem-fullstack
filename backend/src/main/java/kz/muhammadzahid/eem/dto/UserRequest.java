package kz.muhammadzahid.eem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kz.muhammadzahid.eem.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class UserRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password length must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "Password must contain at least one digit, one lowercase letter, and one uppercase letter"
    )
    private String password;

    @NotNull(message = "Roles are required. ROLE_USER is default")
    @Size(min = 1, message = "At least one role is required")
    private Set<Role> roles;
}

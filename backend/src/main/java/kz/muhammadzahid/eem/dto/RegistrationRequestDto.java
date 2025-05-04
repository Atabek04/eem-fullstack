package kz.muhammadzahid.eem.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for registration requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {
    
    /**
     * Optional user comments or notes for the registration
     */
    @Size(max = 500, message = "Comments cannot exceed 500 characters")
    private String comments;
}
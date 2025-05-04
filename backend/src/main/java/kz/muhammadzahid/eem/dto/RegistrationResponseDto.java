package kz.muhammadzahid.eem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for registration responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponseDto {
    
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String userFullName;
    private String username;
    private String registrationCode;
    private LocalDateTime registrationTime;
    private String status;
    private String comments;
    private String cancelReason;
    private LocalDateTime eventStartDateTime;
    private LocalDateTime eventEndDateTime;
    private String eventLocation;
}
package kz.muhammadzahid.eem.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kz.muhammadzahid.eem.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Start date and time is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    @FutureOrPresent(message = "End date must be in the present or future")
    private LocalDateTime endDateTime;

    private Long cityId;

    private String address;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull
    private Boolean online;

    private String onlineLink;

    private Set<Long> tagIds = new HashSet<>();
    
    @Builder.Default
    private List<EventImageDto> images = new ArrayList<>();
    
    @Builder.Default
    private boolean coverImageExists = false;
    
    private Long coverImageId;
    
    private String organizerNotes;
    
    @Size(max = 255, message = "External registration link cannot exceed 255 characters")
    private String externalRegistrationLink;
    
    @Builder.Default
    private boolean publiclyVisible = true;
    
    @Builder.Default
    private boolean registrationRequired = true;
}

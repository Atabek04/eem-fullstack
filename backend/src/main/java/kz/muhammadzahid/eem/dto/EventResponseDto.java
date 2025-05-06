package kz.muhammadzahid.eem.dto;

import kz.muhammadzahid.eem.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long cityId;
    private String address;
    private EventType eventType;
    private Integer capacity;
    private boolean online;
    private String onlineLink;
    private Long createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Long> tagIds;
    
    // New fields for availability tracking
    private Integer registeredAttendeesCount;
    private boolean hasAvailablePlaces;
    
    // New fields for event images
    @Builder.Default
    private List<EventImageDto> images = new ArrayList<>();
    private Long coverImageId;
}
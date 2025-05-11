package kz.muhammadzahid.eem.util;

import kz.muhammadzahid.eem.dto.CityDto;
import kz.muhammadzahid.eem.dto.EventImageDto;
import kz.muhammadzahid.eem.dto.EventResponseDto;
import kz.muhammadzahid.eem.dto.RegistrationRequestDto;
import kz.muhammadzahid.eem.dto.RegistrationResponseDto;
import kz.muhammadzahid.eem.dto.TagDto;
import kz.muhammadzahid.eem.dto.TagRequest;
import kz.muhammadzahid.eem.dto.UserRequest;
import kz.muhammadzahid.eem.dto.UserResponse;
import kz.muhammadzahid.eem.entity.City;
import kz.muhammadzahid.eem.entity.Event;
import kz.muhammadzahid.eem.entity.EventImage;
import kz.muhammadzahid.eem.entity.Registration;
import kz.muhammadzahid.eem.entity.Tag;
import kz.muhammadzahid.eem.entity.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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

    public CityDto mapToCityDto(City city) {
        return CityDto.builder()
                .id(city.getId())
                .name(city.getName())
                .region(city.getRegion())
                .build();
    }

    public TagDto mapToTagDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .colorCode(tag.getColorCode())
                .build();
    }

    public EventResponseDto mapToEventResponseDto(Event event) {
        User creator = event.getCreatedBy();
        var eventResponse = EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .eventType(event.getEventType())
                .capacity(event.getCapacity())
                .online(event.isOnlineEvent())
                .createdById(creator.getId())
                // Add creator information
                .creatorUsername(creator.getUsername())
                .creatorFirstName(creator.getFirstName())
                .creatorLastName(creator.getLastName())
                .creatorEmail(creator.getEmail())
                .creatorPhoneNumber(creator.getPhoneNumber())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .tagIds(event.getTags().stream()
                        .map(Tag::getId)
                        .collect(Collectors.toSet()))
                .registeredAttendeesCount(event.getRegisteredAttendeesCount())
                .hasAvailablePlaces(event.isHasAvailablePlaces())
                .images(mapEventImagesToDtos(event.getImages()))
                .build();

        if (event.isOnlineEvent() && event.getOnlineLink() != null) {
            eventResponse.setOnlineLink(event.getOnlineLink());
        }
        if (event.getCity() != null && event.getAddress() != null) {
            eventResponse.setCityId(event.getCity().getId());
            eventResponse.setAddress(event.getAddress());
        }

        event.getImages().stream()
                .filter(EventImage::isCoverImage)
                .findFirst()
                .ifPresent(coverImage -> eventResponse.setCoverImageId(coverImage.getId()));

        return eventResponse;
    }

    public Tag mapToTag(TagRequest tag) {
        return Tag.builder()
                .name(tag.getName())
                .description(tag.getDescription())
                .colorCode(tag.getColorCode())
                .build();
    }
    
    public EventImageDto mapToEventImageDto(EventImage image) {
        return EventImageDto.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .description(image.getDescription())
                .isCoverImage(image.isCoverImage())
                .build();
    }
    
    public List<EventImageDto> mapEventImagesToDtos(List<EventImage> images) {
        return images.stream()
                .map(Mapper::mapToEventImageDto)
                .collect(Collectors.toList());
    }
    
    public EventImage mapToEventImage(EventImageDto imageDto, Event event) {
        return EventImage.builder()
                .id(imageDto.getId())
                .imageUrl(imageDto.getImageUrl())
                .description(imageDto.getDescription())
                .isCoverImage(imageDto.isCoverImage())
                .event(event)
                .build();
    }
    
    /**
     * Map a Registration entity to a RegistrationResponseDto
     */
    public RegistrationResponseDto mapToRegistrationResponseDto(Registration registration) {
        if (registration == null) {
            return null;
        }
        
        Event event = registration.getEvent();
        User user = registration.getUser();
        
        String eventLocation = "";
        if (event.isOnlineEvent() && event.getOnlineLink() != null) {
            eventLocation = "Online: " + event.getOnlineLink();
        } else if (event.getCity() != null && event.getAddress() != null) {
            eventLocation = event.getCity().getName() + ", " + event.getAddress();
        }
        
        String userFullName = "";
        if (user != null) {
            userFullName = (user.getFirstName() != null ? user.getFirstName() : "") + " " +
                          (user.getLastName() != null ? user.getLastName() : "");
            userFullName = userFullName.trim();
        }
        
        return RegistrationResponseDto.builder()
                .id(registration.getId())
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .userId(user.getId())
                .username(user.getUsername())
                .userFullName(userFullName)
                .registrationCode(registration.getRegistrationCode())
                .registrationTime(registration.getRegistrationTime())
                .comments(registration.getComments())
                .eventStartDateTime(event.getStartDateTime())
                .eventEndDateTime(event.getEndDateTime())
                .eventLocation(eventLocation)
                .build();
    }
    
    /**
     * Map a list of Registration entities to RegistrationResponseDtos
     */
    public List<RegistrationResponseDto> mapToRegistrationResponseDtos(List<Registration> registrations) {
        if (registrations == null) {
            return List.of();
        }
        return registrations.stream()
                .map(Mapper::mapToRegistrationResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Create a new Registration entity from a request, event and user
     */
    public Registration createRegistration(RegistrationRequestDto requestDto, Event event, User user) {
        Registration registration = Registration.builder()
                .event(event)
                .user(user)
                .registrationTime(LocalDateTime.now())
                .comments(requestDto != null ? requestDto.getComments() : null)
                .build();
        
        return registration;
    }
}

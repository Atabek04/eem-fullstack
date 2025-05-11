package kz.muhammadzahid.eem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kz.muhammadzahid.eem.dto.EventImageDto;
import kz.muhammadzahid.eem.dto.EventRequestDto;
import kz.muhammadzahid.eem.dto.EventResponseDto;
import kz.muhammadzahid.eem.entity.City;
import kz.muhammadzahid.eem.entity.Event;
import kz.muhammadzahid.eem.entity.EventImage;
import kz.muhammadzahid.eem.entity.Tag;
import kz.muhammadzahid.eem.entity.User;
import kz.muhammadzahid.eem.repo.CityRepository;
import kz.muhammadzahid.eem.repo.EventRepository;
import kz.muhammadzahid.eem.repo.TagRepository;
import kz.muhammadzahid.eem.repo.UserRepository;
import kz.muhammadzahid.eem.security.SecurityUserContext;
import kz.muhammadzahid.eem.service.EventService;
import kz.muhammadzahid.eem.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CityRepository cityRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final SecurityUserContext securityUserContext;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        if (eventRequestDto.getEndDateTime().isBefore(eventRequestDto.getStartDateTime())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        String loggedUserUserName = securityUserContext.getCurrentUserUserName();
        User loggedUser = userRepository.findByUsername(loggedUserUserName)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + loggedUserUserName));

        Set<Tag> tags = new HashSet<>();
        if (eventRequestDto.getTagIds() != null && !eventRequestDto.getTagIds().isEmpty()) {
            tags = new HashSet<>(tagRepository.findAllById(eventRequestDto.getTagIds()));

            if (tags.size() != eventRequestDto.getTagIds().size()) {
                throw new EntityNotFoundException("One or more tags not found");
            }
        }

        Event event = Event.builder()
                .title(eventRequestDto.getTitle())
                .description(eventRequestDto.getDescription())
                .startDateTime(eventRequestDto.getStartDateTime())
                .endDateTime(eventRequestDto.getEndDateTime())
                .address(eventRequestDto.getAddress())
                .eventType(eventRequestDto.getEventType())
                .capacity(eventRequestDto.getCapacity())
                .onlineEvent(eventRequestDto.getOnline())
                .tags(tags)
                .createdBy(loggedUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .registeredAttendeesCount(0)
                .hasAvailablePlaces(true)
                .build();

        if (event.isOnlineEvent() && eventRequestDto.getOnlineLink() != null) {
            event.setOnlineLink(eventRequestDto.getOnlineLink());
        } else if (eventRequestDto.getCityId() != null && eventRequestDto.getAddress() != null) {
            City city = cityRepository.findById(eventRequestDto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + eventRequestDto.getCityId()));

            event.setCity(city);
            event.setAddress(eventRequestDto.getAddress());
        }
        
        processEventImages(event, eventRequestDto);
        
        event.updateAvailabilityStatus();

        Event savedEvent = eventRepository.save(event);
        return Mapper.mapToEventResponseDto(savedEvent);
    }

    @Override
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(Mapper::mapToEventResponseDto)
                .toList();
    }

    @Override
    public EventResponseDto getEventById(Long id) {
        return Mapper.mapToEventResponseDto(eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id)));
    }
    
    @Override
    @Transactional
    public EventResponseDto updateEvent(Long id, EventRequestDto eventRequestDto) {
        if (eventRequestDto.getEndDateTime().isBefore(eventRequestDto.getStartDateTime())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        
        updateEventFields(event, eventRequestDto);
        event.setUpdatedAt(LocalDateTime.now());
        
        processTagsForEvent(event, eventRequestDto);
        processLocationInfo(event, eventRequestDto);
        processEventImages(event, eventRequestDto);
        
        event.updateAvailabilityStatus();
        
        Event updatedEvent = eventRepository.save(event);
        return Mapper.mapToEventResponseDto(updatedEvent);
    }
    
    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        
        eventRepository.delete(event);
    }
    
    private void updateEventFields(Event event, EventRequestDto dto) {
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartDateTime(dto.getStartDateTime());
        event.setEndDateTime(dto.getEndDateTime());
        event.setEventType(dto.getEventType());
        event.setCapacity(dto.getCapacity());
        event.setOnlineEvent(dto.getOnline());
        
        if (dto.getOnline()) {
            event.setOnlineLink(dto.getOnlineLink());
        }
    }
    
    private void processTagsForEvent(Event event, EventRequestDto dto) {
        if (dto.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(dto.getTagIds()));
            
            if (!dto.getTagIds().isEmpty() && tags.size() != dto.getTagIds().size()) {
                throw new EntityNotFoundException("One or more tags not found");
            }
            
            event.getTags().clear();
            event.getTags().addAll(tags);
        }
    }
    
    private void processLocationInfo(Event event, EventRequestDto dto) {
        if (event.isOnlineEvent()) {
            event.setCity(null);
            event.setAddress(null);
            event.setOnlineLink(dto.getOnlineLink());
        } else if (dto.getCityId() != null && dto.getAddress() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + dto.getCityId()));
            
            event.setCity(city);
            event.setAddress(dto.getAddress());
            event.setOnlineLink(null);
        }
    }
    
    private void processEventImages(Event event, EventRequestDto dto) {
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<EventImage> imagesToKeep = new ArrayList<>();
            
            for (int i = 0; i < dto.getImages().size(); i++) {
                EventImageDto imageDto = dto.getImages().get(i);
                EventImage image;
                
                if (imageDto.getId() != null) {
                    image = event.getImages().stream()
                            .filter(img -> img.getId().equals(imageDto.getId()))
                            .findFirst()
                            .orElseGet(() -> Mapper.mapToEventImage(imageDto, event));
                    
                    image.setImageUrl(imageDto.getImageUrl());
                    image.setDescription(imageDto.getDescription());
                } else {
                    image = Mapper.mapToEventImage(imageDto, event);
                }
                
                boolean isCoverByFlag = imageDto.isCoverImage();
                boolean isCoverById = dto.getCoverImageId() != null && 
                                    imageDto.getId() != null && 
                                    imageDto.getId().equals(dto.getCoverImageId());
                boolean isCoverByIndex = dto.getCoverImageIndex() != null &&
                                      dto.getCoverImageIndex() == i;
                
                if (isCoverByFlag || isCoverById || isCoverByIndex) {
                    resetCoverImageFlags(event);
                    image.setCoverImage(true);
                }
                
                imagesToKeep.add(image);
            }
            
            event.getImages().clear();
            event.getImages().addAll(imagesToKeep);
        }
    }
    
    private void resetCoverImageFlags(Event event) {
        event.getImages().forEach(image -> image.setCoverImage(false));
    }
}

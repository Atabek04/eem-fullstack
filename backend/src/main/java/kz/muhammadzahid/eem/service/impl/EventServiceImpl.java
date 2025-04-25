package kz.muhammadzahid.eem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kz.muhammadzahid.eem.dto.EventRequestDto;
import kz.muhammadzahid.eem.dto.EventResponseDto;
import kz.muhammadzahid.eem.entity.City;
import kz.muhammadzahid.eem.entity.Event;
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
                .build();

        if (event.isOnlineEvent() && eventRequestDto.getOnlineLink() != null) {
            event.setOnlineLink(eventRequestDto.getOnlineLink());
        } else if (eventRequestDto.getCityId() != null && eventRequestDto.getAddress() != null) {
            City city = cityRepository.findById(eventRequestDto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + eventRequestDto.getCityId()));

            event.setCity(city);
            event.setAddress(eventRequestDto.getAddress());
        }

        return Mapper.mapToEventResponseDto(eventRepository.save(event));
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

}

package kz.muhammadzahid.eem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kz.muhammadzahid.eem.dto.EventResponseDto;
import kz.muhammadzahid.eem.entity.Event;
import kz.muhammadzahid.eem.entity.FavoriteEvent;
import kz.muhammadzahid.eem.entity.User;
import kz.muhammadzahid.eem.exception.BadRequestException;
import kz.muhammadzahid.eem.exception.UserNotFoundException;
import kz.muhammadzahid.eem.repo.EventRepository;
import kz.muhammadzahid.eem.repo.FavoriteEventRepository;
import kz.muhammadzahid.eem.repo.UserRepository;
import kz.muhammadzahid.eem.security.SecurityUserContext;
import kz.muhammadzahid.eem.service.FavoriteEventService;
import kz.muhammadzahid.eem.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteEventServiceImpl implements FavoriteEventService {

    private final FavoriteEventRepository favoriteEventRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SecurityUserContext securityUserContext;

    @Override
    @Transactional
    public EventResponseDto addFavorite(Long eventId) {
        // Get current authenticated user
        User currentUser = getCurrentUser();

        // Get event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        // Check if already favorited
        if (favoriteEventRepository.existsByUserAndEvent(currentUser, event)) {
            // Already favorited, just return the event
            EventResponseDto responseDto = Mapper.mapToEventResponseDto(event);
            responseDto.setFavorited(true);
            return responseDto;
        }

        // Create and save favorite entity
        FavoriteEvent favoriteEvent = FavoriteEvent.builder()
                .user(currentUser)
                .event(event)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteEventRepository.save(favoriteEvent);
        log.info("User {} favorited event {}", currentUser.getUsername(), event.getTitle());

        EventResponseDto responseDto = Mapper.mapToEventResponseDto(event);
        responseDto.setFavorited(true);
        return responseDto;
    }

    @Override
    @Transactional
    public void removeFavorite(Long eventId) {
        User currentUser = getCurrentUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        FavoriteEvent favoriteEvent = favoriteEventRepository.findByUserAndEvent(currentUser, event)
                .orElseThrow(() -> new BadRequestException("Event is not in user's favorites"));

        favoriteEventRepository.delete(favoriteEvent);
        log.info("User {} removed event {} from favorites", currentUser.getUsername(), event.getTitle());
    }

    @Override
    public List<EventResponseDto> getCurrentUserFavorites() {
        User currentUser = getCurrentUser();

        List<FavoriteEvent> favorites = favoriteEventRepository.findByUser(currentUser);

        return favorites.stream()
                .map(favorite -> {
                    EventResponseDto dto = Mapper.mapToEventResponseDto(favorite.getEvent());
                    dto.setFavorited(true);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEventFavoritedByCurrentUser(Long eventId) {
        User currentUser = getCurrentUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        return favoriteEventRepository.existsByUserAndEvent(currentUser, event);
    }

    @Override
    public Set<Long> getCurrentUserFavoriteIds() {
        Long userId = getCurrentUser().getId();

        return favoriteEventRepository.findEventIdsByUserId(userId);
    }

    /**
     * Helper method to get current authenticated user
     */
    private User getCurrentUser() {
        String loggedUserUsername = securityUserContext.getCurrentUserUserName();
        return userRepository.findByUsername(loggedUserUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loggedUserUsername));
    }
}

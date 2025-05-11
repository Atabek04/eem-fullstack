package kz.muhammadzahid.eem.controller;

import kz.muhammadzahid.eem.dto.EventResponseDto;
import kz.muhammadzahid.eem.service.FavoriteEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteEventController {

    private final FavoriteEventService favoriteEventService;

    /**
     * Add an event to the current user's favorites
     * 
     * @param eventId ID of the event to favorite
     * @return EventResponseDto for the favorited event
     */
    @PostMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> addFavorite(@PathVariable Long eventId) {
        log.info("Adding event {} to current user's favorites", eventId);
        EventResponseDto eventResponseDto = favoriteEventService.addFavorite(eventId);
        return ResponseEntity.ok(eventResponseDto);
    }

    /**
     * Remove an event from the current user's favorites
     * 
     * @param eventId ID of the event to unfavorite
     * @return Empty response with 204 status
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long eventId) {
        log.info("Removing event {} from current user's favorites", eventId);
        favoriteEventService.removeFavorite(eventId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all events favorited by the current user
     * 
     * @return List of EventResponseDto for favorited events
     */
    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getCurrentUserFavorites() {
        log.info("Getting all favorites for current user");
        List<EventResponseDto> favorites = favoriteEventService.getCurrentUserFavorites();
        return ResponseEntity.ok(favorites);
    }
}

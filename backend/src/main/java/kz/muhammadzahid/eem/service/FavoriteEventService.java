package kz.muhammadzahid.eem.service;

import kz.muhammadzahid.eem.dto.EventResponseDto;

import java.util.List;
import java.util.Set;

public interface FavoriteEventService {
    
    /**
     * Add an event to the current user's favorites
     * 
     * @param eventId ID of the event to favorite
     * @return EventResponseDto for the favorited event
     */
    EventResponseDto addFavorite(Long eventId);
    
    /**
     * Remove an event from the current user's favorites
     * 
     * @param eventId ID of the event to unfavorite
     */
    void removeFavorite(Long eventId);
    
    /**
     * Get all events favorited by the current user
     * 
     * @return List of EventResponseDto for favorited events
     */
    List<EventResponseDto> getCurrentUserFavorites();
    
    /**
     * Check if an event is favorited by the current user
     * 
     * @param eventId ID of the event to check
     * @return true if favorited, false otherwise
     */
    boolean isEventFavoritedByCurrentUser(Long eventId);
    
    /**
     * Get all event IDs favorited by the current user
     * 
     * @return Set of event IDs
     */
    Set<Long> getCurrentUserFavoriteIds();
}

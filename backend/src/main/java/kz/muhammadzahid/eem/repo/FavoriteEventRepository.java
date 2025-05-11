package kz.muhammadzahid.eem.repo;

import kz.muhammadzahid.eem.entity.Event;
import kz.muhammadzahid.eem.entity.FavoriteEvent;
import kz.muhammadzahid.eem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FavoriteEventRepository extends JpaRepository<FavoriteEvent, FavoriteEvent.FavoriteEventId> {

    /**
     * Find all favorite events for a specific user
     */
    List<FavoriteEvent> findByUser(User user);
    
    /**
     * Find all favorite events ids for a specific user
     */
    @Query("SELECT fe.event.id FROM FavoriteEvent fe WHERE fe.user.id = :userId")
    Set<Long> findEventIdsByUserId(Long userId);
    
    /**
     * Find by user and event
     */
    Optional<FavoriteEvent> findByUserAndEvent(User user, Event event);
    
    /**
     * Check if an event is favorited by a specific user
     */
    boolean existsByUserAndEvent(User user, Event event);
    
    /**
     * Delete a favorite event by user and event
     */
    void deleteByUserAndEvent(User user, Event event);
}

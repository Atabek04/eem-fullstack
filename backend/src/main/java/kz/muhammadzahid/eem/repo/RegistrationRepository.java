package kz.muhammadzahid.eem.repo;

import kz.muhammadzahid.eem.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    /**
     * Find all registrations for a specific user
     */
    List<Registration> findByUserId(Long userId);
    
    /**
     * Find all registrations for a specific event with pagination
     */
    Page<Registration> findByEventId(Long eventId, Pageable pageable);
    
    /**
     * Find registration for a specific user and event
     */
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);
    
    /**
     * Check if a user has a registration for an event
     */
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    
    /**
     * Count registrations for an event
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event.id = :eventId")
    int countRegistrationsByEventId(Long eventId);
    
    /**
     * Find by registration code
     */
    Optional<Registration> findByRegistrationCode(String registrationCode);
}
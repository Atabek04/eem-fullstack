package kz.muhammadzahid.eem.repo;

import kz.muhammadzahid.eem.entity.Registration;
import kz.muhammadzahid.eem.entity.Registration.RegistrationStatus;
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
     * Find active (confirmed) registration for a specific user and event
     */
    Optional<Registration> findByUserIdAndEventIdAndStatus(Long userId, Long eventId, RegistrationStatus status);
    
    /**
     * Check if a user has an active registration for an event
     */
    boolean existsByUserIdAndEventIdAndStatus(Long userId, Long eventId, RegistrationStatus status);
    
    /**
     * Count active registrations for an event
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    int countActiveRegistrationsByEventId(Long eventId);
    
    /**
     * Find by registration code
     */
    Optional<Registration> findByRegistrationCode(String registrationCode);
}
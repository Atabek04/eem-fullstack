package kz.muhammadzahid.eem.service;

import kz.muhammadzahid.eem.dto.RegistrationRequestDto;
import kz.muhammadzahid.eem.dto.RegistrationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service for managing event registrations
 */
public interface RegistrationService {

    /**
     * Register current authenticated user for an event
     * 
     * @param eventId The ID of the event to register for
     * @return Registration response with details
     */
    RegistrationResponseDto registerForEvent(Long eventId);
    
    /**
     * Register a user for an event with additional details
     * 
     * @param eventId The ID of the event to register for
     * @param registrationRequestDto Registration details
     * @return Registration response with details
     */
    RegistrationResponseDto registerForEvent(Long eventId, RegistrationRequestDto registrationRequestDto);
    
    /**
     * Cancel a registration
     * 
     * @param registrationId The ID of the registration to cancel
     * @param reason Optional reason for cancellation
     */
    void cancelRegistration(Long registrationId, String reason);
    
    /**
     * Get all registrations for the currently authenticated user
     * 
     * @return List of registrations
     */
    List<RegistrationResponseDto> getCurrentUserRegistrations();
    
    /**
     * Get all registrations for an event with pagination
     * 
     * @param eventId The ID of the event
     * @param pageable Pagination information
     * @return Page of registrations
     */
    Page<RegistrationResponseDto> getEventRegistrations(Long eventId, Pageable pageable);
    
    /**
     * Get a specific registration by ID
     * 
     * @param registrationId The ID of the registration
     * @return Registration details
     */
    RegistrationResponseDto getRegistrationById(Long registrationId);
    
    /**
     * Check if the current user is registered for an event
     * 
     * @param eventId The ID of the event
     * @return true if registered, false otherwise
     */
    boolean isCurrentUserRegisteredForEvent(Long eventId);
}
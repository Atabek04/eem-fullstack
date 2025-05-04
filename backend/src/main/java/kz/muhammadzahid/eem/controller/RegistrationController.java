package kz.muhammadzahid.eem.controller;

import jakarta.validation.Valid;
import kz.muhammadzahid.eem.dto.RegistrationRequestDto;
import kz.muhammadzahid.eem.dto.RegistrationResponseDto;
import kz.muhammadzahid.eem.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for handling event registrations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    
    /**
     * Register the current user for an event
     *
     * @param eventId The ID of the event to register for
     * @param requestDto Registration details (optional)
     * @return Registration details
     */
    @PostMapping("/{eventId}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponseDto registerForEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody(required = false) RegistrationRequestDto requestDto) {
        
        log.info("Registering user for event with ID: {}", eventId);
        if (requestDto != null) {
            return registrationService.registerForEvent(eventId, requestDto);
        } else {
            return registrationService.registerForEvent(eventId);
        }
    }

    /**
     * Cancel a registration
     *
     * @param eventId The ID of the event
     * @param registrationId The ID of the registration to cancel
     * @param requestBody Optional cancellation reason
     */
    @DeleteMapping("/{eventId}/registrations/{registrationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRegistration(
            @PathVariable Long eventId,
            @PathVariable Long registrationId,
            @RequestBody(required = false) Map<String, String> requestBody) {
        
        String reason = requestBody != null ? requestBody.get("reason") : null;
        log.info("Cancelling registration ID: {} for event ID: {}", registrationId, eventId);
        registrationService.cancelRegistration(registrationId, reason);
    }

    /**
     * Get all registrations for an event (organizers and admins only)
     *
     * @param eventId The ID of the event
     * @param pageable Pagination info
     * @return List of registrations
     */
    @GetMapping("/{eventId}/registrations")
    public Page<RegistrationResponseDto> getEventRegistrations(
            @PathVariable Long eventId,
            Pageable pageable) {
        
        log.info("Fetching registrations for event ID: {}", eventId);
        return registrationService.getEventRegistrations(eventId, pageable);
    }

    /**
     * Get a specific registration
     *
     * @param eventId The ID of the event
     * @param registrationId The ID of the registration
     * @return Registration details
     */
    @GetMapping("/{eventId}/registrations/{registrationId}")
    public RegistrationResponseDto getRegistrationById(
            @PathVariable Long eventId,
            @PathVariable Long registrationId) {
        
        log.info("Fetching registration ID: {} for event ID: {}", registrationId, eventId);
        return registrationService.getRegistrationById(registrationId);
    }

    /**
     * Check if current user is registered for an event
     *
     * @param eventId The ID of the event
     * @return Registration status
     */
    @GetMapping("/{eventId}/registrations/status")
    public Map<String, Boolean> getRegistrationStatus(@PathVariable Long eventId) {
        boolean isRegistered = registrationService.isCurrentUserRegisteredForEvent(eventId);
        return Map.of("registered", isRegistered);
    }

    /**
     * Get all registrations for the current authenticated user
     *
     * @return List of registrations
     */
    @GetMapping("/user/registrations")
    public List<RegistrationResponseDto> getCurrentUserRegistrations() {
        log.info("Fetching registrations for current user");
        return registrationService.getCurrentUserRegistrations();
    }
}
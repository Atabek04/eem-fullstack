package kz.muhammadzahid.eem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kz.muhammadzahid.eem.dto.RegistrationRequestDto;
import kz.muhammadzahid.eem.dto.RegistrationResponseDto;
import kz.muhammadzahid.eem.entity.Event;
import kz.muhammadzahid.eem.entity.Registration;
import kz.muhammadzahid.eem.entity.User;
import kz.muhammadzahid.eem.exception.AlreadyRegisteredException;
import kz.muhammadzahid.eem.exception.BadRequestException;
import kz.muhammadzahid.eem.exception.EventFullException;
import kz.muhammadzahid.eem.exception.RegistrationNotFoundException;
import kz.muhammadzahid.eem.exception.UserNotFoundException;
import kz.muhammadzahid.eem.repo.EventRepository;
import kz.muhammadzahid.eem.repo.RegistrationRepository;
import kz.muhammadzahid.eem.repo.UserRepository;
import kz.muhammadzahid.eem.security.SecurityUserContext;
import kz.muhammadzahid.eem.service.RegistrationService;
import kz.muhammadzahid.eem.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final SecurityUserContext securityUserContext;

    @Override
    @Transactional
    public RegistrationResponseDto registerForEvent(Long eventId) {
        return registerForEvent(eventId, new RegistrationRequestDto());
    }

    @Override
    @Transactional
    public RegistrationResponseDto registerForEvent(Long eventId, RegistrationRequestDto registrationRequestDto) {
        // Get current authenticated user
        String loggedUserUsername = securityUserContext.getCurrentUserUserName();
        User currentUser = userRepository.findByUsername(loggedUserUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loggedUserUsername));

        // Get event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        // Validate event is active and registration is required
        validateEventForRegistration(event);

        // Check if user is already registered
        if (registrationRepository.existsByUserIdAndEventId(currentUser.getId(), eventId)) {
            throw new AlreadyRegisteredException("User is already registered for this event");
        }

        // Check capacity and register
        if (!event.isHasAvailablePlaces()) {
            throw new EventFullException("Event is already at full capacity");
        }

        // Create registration entity
        Registration registration = Mapper.createRegistration(registrationRequestDto, event, currentUser);
        
        // Update event capacity
        boolean registrationSuccess = event.registerAttendee();
        if (!registrationSuccess) {
            throw new EventFullException("Failed to register: Event is at full capacity");
        }

        // Save both registration and updated event
        Registration savedRegistration = registrationRepository.save(registration);
        eventRepository.save(event);

        log.info("User {} registered for event {}", currentUser.getUsername(), event.getTitle());
        
        return Mapper.mapToRegistrationResponseDto(savedRegistration);
    }

    @Override
    @Transactional
    public void deleteRegistration(Long registrationId) {
        // Get current authenticated user
        String loggedUserUsername = securityUserContext.getCurrentUserUserName();
        User currentUser = userRepository.findByUsername(loggedUserUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loggedUserUsername));

        // Find registration
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException("Registration not found with id: " + registrationId));

        // Validate user owns this registration or is an admin
        if (!registration.getUser().getId().equals(currentUser.getId()) && 
                !currentUser.getRoles().stream().anyMatch(role -> 
                        role.getName().name().equals("ROLE_ADMIN"))) {
            throw new BadRequestException("You do not have permission to delete this registration");
        }

        // Get the event before deleting the registration
        Event event = registration.getEvent();
        
        // Delete the registration
        registrationRepository.delete(registration);
        
        // Update event capacity counter
        event.unregisterAttendee();
        eventRepository.save(event);

        log.info("Registration {} for event {} deleted", registrationId, event.getTitle());
    }

    @Override
    public List<RegistrationResponseDto> getCurrentUserRegistrations() {
        // Get current authenticated user
        String loggedUserUsername = securityUserContext.getCurrentUserUserName();
        User currentUser = userRepository.findByUsername(loggedUserUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loggedUserUsername));

        // Get all registrations for this user
        List<Registration> registrations = registrationRepository.findByUserId(currentUser.getId());

        return Mapper.mapToRegistrationResponseDtos(registrations);
    }

    @Override
    public Page<RegistrationResponseDto> getEventRegistrations(Long eventId, Pageable pageable) {
        // Get event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        // Get current authenticated user
        String loggedUserUsername = securityUserContext.getCurrentUserUserName();
        User currentUser = userRepository.findByUsername(loggedUserUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loggedUserUsername));

        // Validate user is the event creator or an admin
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));
        boolean isEventCreator = event.getCreatedBy().getId().equals(currentUser.getId());

        if (!isAdmin && !isEventCreator) {
            throw new BadRequestException("You do not have permission to view all registrations");
        }

        // Get registrations for this event with pagination
        Page<Registration> registrations = registrationRepository.findByEventId(eventId, pageable);

        // Convert to DTOs
        return registrations.map(Mapper::mapToRegistrationResponseDto);
    }

    @Override
    public RegistrationResponseDto getRegistrationById(Long registrationId) {
        // Get registration by ID
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException("Registration not found with id: " + registrationId));

        // Get current authenticated user
        String loggedUserUsername = securityUserContext.getCurrentUserUserName();
        User currentUser = userRepository.findByUsername(loggedUserUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loggedUserUsername));

        // Validate user owns this registration, is admin, or is event creator
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));
        boolean isEventCreator = registration.getEvent().getCreatedBy().getId().equals(currentUser.getId());
        boolean isOwner = registration.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isEventCreator && !isOwner) {
            throw new BadRequestException("You do not have permission to view this registration");
        }

        return Mapper.mapToRegistrationResponseDto(registration);
    }

    @Override
    public boolean isCurrentUserRegisteredForEvent(Long eventId) {
        // Get current authenticated user
        String loggedUserUsername = securityUserContext.getCurrentUserUserName();
        User currentUser = userRepository.findByUsername(loggedUserUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loggedUserUsername));

        // Check if user is registered
        return registrationRepository.existsByUserIdAndEventId(currentUser.getId(), eventId);
    }

    /**
     * Validates if an event is available for registration
     */
    private void validateEventForRegistration(Event event) {
        // Check if the event has not already started
        if (event.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Registration is closed: Event has already started");
        }
    }
}
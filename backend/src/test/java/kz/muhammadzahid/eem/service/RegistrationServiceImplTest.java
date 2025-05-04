package kz.muhammadzahid.eem.service;

import kz.muhammadzahid.eem.dto.RegistrationRequestDto;
import kz.muhammadzahid.eem.dto.RegistrationResponseDto;
import kz.muhammadzahid.eem.entity.Event;
import kz.muhammadzahid.eem.entity.EventType;
import kz.muhammadzahid.eem.entity.Registration;
import kz.muhammadzahid.eem.entity.Role;
import kz.muhammadzahid.eem.entity.User;
import kz.muhammadzahid.eem.exception.AlreadyRegisteredException;
import kz.muhammadzahid.eem.exception.EventFullException;
import kz.muhammadzahid.eem.repo.EventRepository;
import kz.muhammadzahid.eem.repo.RegistrationRepository;
import kz.muhammadzahid.eem.repo.UserRepository;
import kz.muhammadzahid.eem.security.SecurityUserContext;
import kz.muhammadzahid.eem.service.impl.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private SecurityUserContext securityUserContext;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private User testUser;
    private Event testEvent;
    private Registration testRegistration;

    @BeforeEach
    public void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .roles(new HashSet<>())
                .active(true)
                .build();

        // Create test event
        testEvent = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Test Event Description")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .eventType(EventType.CONFERENCE)
                .capacity(10)
                .registeredAttendeesCount(0)
                .hasAvailablePlaces(true)
                .publiclyVisible(true)
                .registrationRequired(true)
                .createdBy(testUser)
                .build();

        // Create test registration
        testRegistration = Registration.builder()
                .id(1L)
                .event(testEvent)
                .user(testUser)
                .registrationTime(LocalDateTime.now())
                .status(Registration.RegistrationStatus.CONFIRMED)
                .build();
    }

    @Test
    public void testRegisterForEvent_Success() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        when(securityUserContext.getCurrentUserUserName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(1L, 1L, Registration.RegistrationStatus.CONFIRMED)).thenReturn(false);
        when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);

        // Act
        RegistrationResponseDto responseDto = registrationService.registerForEvent(1L, requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getEventId());
        assertEquals("Test Event", responseDto.getEventTitle());
        assertEquals(1L, responseDto.getUserId());
        assertEquals("CONFIRMED", responseDto.getStatus());
        
        verify(eventRepository).save(any(Event.class));
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    public void testRegisterForEvent_AlreadyRegistered() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        when(securityUserContext.getCurrentUserUserName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(1L, 1L, Registration.RegistrationStatus.CONFIRMED)).thenReturn(true);

        // Act & Assert
        assertThrows(AlreadyRegisteredException.class, () -> {
            registrationService.registerForEvent(1L, requestDto);
        });
        
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    public void testRegisterForEvent_EventFull() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        testEvent.setRegisteredAttendeesCount(10); // Full capacity
        testEvent.setHasAvailablePlaces(false);
        
        when(securityUserContext.getCurrentUserUserName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(1L, 1L, Registration.RegistrationStatus.CONFIRMED)).thenReturn(false);

        // Act & Assert
        assertThrows(EventFullException.class, () -> {
            registrationService.registerForEvent(1L, requestDto);
        });
        
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    public void testCancelRegistration_Success() {
        // Arrange
        when(securityUserContext.getCurrentUserUserName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));

        // Act
        registrationService.cancelRegistration(1L, "Changed my mind");

        // Assert
        verify(registrationRepository).save(any(Registration.class));
        verify(eventRepository).save(any(Event.class));
        assertEquals(Registration.RegistrationStatus.CANCELLED, testRegistration.getStatus());
        assertEquals("Changed my mind", testRegistration.getCancelReason());
    }
}
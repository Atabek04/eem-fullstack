package kz.muhammadzahid.eem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.muhammadzahid.eem.dto.RegistrationRequestDto;
import kz.muhammadzahid.eem.dto.RegistrationResponseDto;
import kz.muhammadzahid.eem.exception.AlreadyRegisteredException;
import kz.muhammadzahid.eem.exception.EventFullException;
import kz.muhammadzahid.eem.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable Spring Security for tests
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;

    private RegistrationRequestDto requestDto;
    private RegistrationResponseDto responseDto;

    @BeforeEach
    public void setUp() {
        // Setup request DTO
        requestDto = RegistrationRequestDto.builder()
                .comments("Looking forward to this event!")
                .build();

        // Setup response DTO
        responseDto = RegistrationResponseDto.builder()
                .id(1L)
                .eventId(1L)
                .eventTitle("Test Event")
                .userId(1L)
                .username("testuser")
                .userFullName("Test User")
                .registrationCode("abcd-1234")
                .registrationTime(LocalDateTime.now())
                .status("CONFIRMED")
                .eventStartDateTime(LocalDateTime.now().plusDays(1))
                .eventEndDateTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .eventLocation("Online: https://example.com/meeting")
                .build();
    }

    @Test
    @WithMockUser
    public void testRegisterForEvent_Success() throws Exception {
        // Arrange
        when(registrationService.registerForEvent(eq(1L), any(RegistrationRequestDto.class)))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/events/1/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.eventId", is(1)))
                .andExpect(jsonPath("$.eventTitle", is("Test Event")))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    @WithMockUser
    public void testRegisterForEvent_AlreadyRegistered() throws Exception {
        // Arrange
        when(registrationService.registerForEvent(eq(1L), any(RegistrationRequestDto.class)))
                .thenThrow(new AlreadyRegisteredException("User is already registered for this event"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/events/1/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    public void testRegisterForEvent_EventFull() throws Exception {
        // Arrange
        when(registrationService.registerForEvent(eq(1L), any(RegistrationRequestDto.class)))
                .thenThrow(new EventFullException("Event is already at full capacity"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/events/1/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testCancelRegistration() throws Exception {
        // Arrange
        Map<String, String> cancelRequest = new HashMap<>();
        cancelRequest.put("reason", "Can't make it");
        doNothing().when(registrationService).cancelRegistration(1L, "Can't make it");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/events/1/registrations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isNoContent());

        verify(registrationService).cancelRegistration(1L, "Can't make it");
    }

    @Test
    @WithMockUser
    public void testGetCurrentUserRegistrations() throws Exception {
        // Arrange
        when(registrationService.getCurrentUserRegistrations())
                .thenReturn(List.of(responseDto));

        // Act & Assert
        mockMvc.perform(get("/api/v1/events/user/registrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventTitle", is("Test Event")))
                .andExpect(jsonPath("$[0].status", is("CONFIRMED")));
    }

    @Test
    @WithMockUser
    public void testGetEventRegistrations() throws Exception {
        // Arrange
        when(registrationService.getEventRegistrations(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(responseDto)));

        // Act & Assert
        mockMvc.perform(get("/api/v1/events/1/registrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].eventTitle", is("Test Event")));
    }

    @Test
    @WithMockUser
    public void testGetRegistrationStatus() throws Exception {
        // Arrange
        when(registrationService.isCurrentUserRegisteredForEvent(1L))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/events/1/registrations/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registered", is(true)));
    }
}
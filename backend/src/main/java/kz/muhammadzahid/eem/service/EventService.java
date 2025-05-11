package kz.muhammadzahid.eem.service;

import kz.muhammadzahid.eem.dto.EventRequestDto;
import kz.muhammadzahid.eem.dto.EventResponseDto;

import java.util.List;

public interface EventService {
    EventResponseDto createEvent(EventRequestDto eventRequestDto);

    List<EventResponseDto> getAllEvents();

    EventResponseDto getEventById(Long id);
    
    EventResponseDto updateEvent(Long id, EventRequestDto eventRequestDto);
    
    void deleteEvent(Long id);
}

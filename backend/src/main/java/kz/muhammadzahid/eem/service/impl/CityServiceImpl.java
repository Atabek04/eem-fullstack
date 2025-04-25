package kz.muhammadzahid.eem.service.impl;

import kz.muhammadzahid.eem.dto.CityDto;
import kz.muhammadzahid.eem.repo.CityRepository;
import kz.muhammadzahid.eem.service.CityService;
import kz.muhammadzahid.eem.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream()
                .map(Mapper::mapToCityDto)
                .toList();
    }
}

package kz.muhammadzahid.eem.repo;

import kz.muhammadzahid.eem.entity.City;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByName(String name);
}

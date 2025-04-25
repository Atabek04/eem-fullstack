package kz.muhammadzahid.eem.util;

import kz.muhammadzahid.eem.entity.City;
import kz.muhammadzahid.eem.entity.Role;
import kz.muhammadzahid.eem.entity.Tag;
import kz.muhammadzahid.eem.entity.User;
import kz.muhammadzahid.eem.repo.CityRepository;
import kz.muhammadzahid.eem.repo.RoleRepository;
import kz.muhammadzahid.eem.repo.TagRepository;
import kz.muhammadzahid.eem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final TagRepository tagRepository;

    @Override
    public void run(String... args) {
        initRoles();
        initializeTags();
        initializeKazakhstanCities();


    }

    private void initializeTags() {
        if (tagRepository.count() == 0) {
            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag("Technology", "Events related to technology", "#FF5733"));
            tags.add(new Tag("Health", "Events related to health", "#33FF57"));
            tags.add(new Tag("Education", "Events related to education", "#3357FF"));
            tags.add(new Tag("Sports", "Events related to sports", "#FF33A1"));
            tags.add(new Tag("Art", "Events related to art", "#FF8C33"));
            tags.add(new Tag("Music", "Events related to music", "#33FFA1"));
            tags.add(new Tag("Food", "Events related to food", "#A133FF"));
            tags.add(new Tag("Travel", "Events related to travel", "#FF33A1"));
            tags.add(new Tag("Business", "Events related to business", "#FF5733"));
            tags.add(new Tag("Environment", "Events related to environment", "#33FF57"));
            tags.add(new Tag("Culture", "Events related to culture", "#3357FF"));
            tags.add(new Tag("Community", "Events related to community", "#FF33A1"));
            tags.add(new Tag("Lifestyle", "Events related to lifestyle", "#FF8C33"));
            tags.add(new Tag("Science", "Events related to science", "#33FFA1"));

            tagRepository.saveAll(tags);
            log.info("Initialized {} tags", tags.size());
        }
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Arrays.stream(Role.RoleName.values())
                    .forEach(roleName -> {
                        Role role = new Role();
                        role.setName(roleName);
                        roleRepository.save(role);
                    });

            log.info("Default roles initialized");
        }

        if (!userRepository.existsByEmail("admin1@gmail.com")) {
            userRepository.save(User.builder()
                    .username("big-boss")
                    .email("admin1@gmail.com")
                    .password(passwordEncoder.encode("Admin12345+"))
                    .firstName("Abu")
                    .lastName("Abdumalik")
                    .active(true)
                    .roles(new HashSet<>(roleRepository.findAll()))
                    .build());
            log.info("Default admin initialized");
        }
    }

    private void initializeKazakhstanCities() {
        if (cityRepository.count() == 0) {
            List<City> cities = new ArrayList<>();
            cities.add(createCity("Almaty", "Almaty Region"));
            cities.add(createCity("Nur-Sultan", "Akmola Region")); // Now Astana
            cities.add(createCity("Shymkent", "South Kazakhstan Region"));
            cities.add(createCity("Karaganda", "Karaganda Region"));
            cities.add(createCity("Aktobe", "Aktobe Region"));
            cities.add(createCity("Taraz", "Zhambyl Region"));
            cities.add(createCity("Pavlodar", "Pavlodar Region"));
            cities.add(createCity("Ust-Kamenogorsk", "East Kazakhstan Region"));
            cities.add(createCity("Semey", "East Kazakhstan Region"));
            cities.add(createCity("Atyrau", "Atyrau Region"));
            cities.add(createCity("Kokshetau", "Akmola Region"));
            cities.add(createCity("Kostanay", "Kostanay Region"));
            cities.add(createCity("Kyzylorda", "Kyzylorda Region"));
            cities.add(createCity("Uralsk", "West Kazakhstan Region"));
            cities.add(createCity("Petropavl", "North Kazakhstan Region"));
            cities.add(createCity("Aktau", "Mangystau Region"));
            cities.add(createCity("Temirtau", "Karaganda Region"));
            cities.add(createCity("Turkestan", "Turkestan Region"));
            cities.add(createCity("Ekibastuz", "Pavlodar Region"));
            cities.add(createCity("Rudny", "Kostanay Region"));
            cities.add(createCity("Zhanaozen", "Mangystau Region"));
            cities.add(createCity("Zhezkazgan", "Karaganda Region"));
            cities.add(createCity("Balkhash", "Karaganda Region"));
            cities.add(createCity("Kentau", "Turkestan Region"));
            cities.add(createCity("Kaskelen", "Almaty Region"));
            cities.add(createCity("Stepnogorsk", "Akmola Region"));
            cities.add(createCity("Talgar", "Almaty Region"));
            cities.add(createCity("Shakhtinsk", "Karaganda Region"));
            cities.add(createCity("Saran", "Karaganda Region"));

            cityRepository.saveAll(cities);
            log.info("Initialized {} Kazakhstan cities", cities.size());
        }
    }

    private City createCity(String name, String region) {
        return City.builder()
                .name(name)
                .region(region)
                .build();
    }
}

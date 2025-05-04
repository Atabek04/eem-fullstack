package kz.muhammadzahid.eem.util;

import kz.muhammadzahid.eem.entity.City;
import kz.muhammadzahid.eem.entity.Event;
import kz.muhammadzahid.eem.entity.EventType;
import kz.muhammadzahid.eem.entity.Role;
import kz.muhammadzahid.eem.entity.Tag;
import kz.muhammadzahid.eem.entity.User;
import kz.muhammadzahid.eem.repo.CityRepository;
import kz.muhammadzahid.eem.repo.EventRepository;
import kz.muhammadzahid.eem.repo.RoleRepository;
import kz.muhammadzahid.eem.repo.TagRepository;
import kz.muhammadzahid.eem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final TagRepository tagRepository;
    private final EventRepository eventRepository;

    @Override
    public void run(String... args) {
        initRoles();
        initializeTags();
        initializeKazakhstanCities();
        initializeEvents();
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

        log.info("Checking the admins existence");
        if (!userRepository.existsByEmail("admin1@gmail.com")) {
            log.info("Creating the default admin");
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

    private void initializeEvents() {
        if (eventRepository.count() == 0) {
            log.info("Initializing sample events");
            
            User admin = userRepository.findByEmail("admin1@gmail.com")
                    .orElseThrow(() -> new RuntimeException("Admin user not found!"));
                    
            // Find Almaty city
            City almaty = cityRepository.findByName("Almaty")
                    .orElseThrow(() -> new RuntimeException("Almaty city not found!"));
                    
            // Find Nur-Sultan (Astana) city
            City nurSultan = cityRepository.findByName("Nur-Sultan")
                    .orElseThrow(() -> new RuntimeException("Nur-Sultan city not found!"));
            
            // Get some tags
            Optional<Tag> techTag = tagRepository.findByName("Technology");
            Optional<Tag> eduTag = tagRepository.findByName("Education");
            Optional<Tag> bizTag = tagRepository.findByName("Business");
            
            Set<Tag> techConfTags = new HashSet<>();
            techTag.ifPresent(techConfTags::add);
            eduTag.ifPresent(techConfTags::add);
            
            Set<Tag> businessMeetupTags = new HashSet<>();
            bizTag.ifPresent(businessMeetupTags::add);
            techTag.ifPresent(businessMeetupTags::add);
            
            // Create first event - Tech conference in Almaty
            Event techConference = Event.builder()
                    .title("Kazakhstan Tech Conference 2025")
                    .description("Join us for the biggest tech conference in Kazakhstan. " +
                            "Leading experts will share insights on AI, blockchain, and cloud technologies. " +
                            "Network with professionals and discover new opportunities in the tech industry.")
                    .startDateTime(LocalDateTime.now().plusMonths(2))
                    .endDateTime(LocalDateTime.now().plusMonths(2).plusDays(2))
                    .city(almaty)
                    .address("Almaty Arena, Koyankus Street")
                    .eventType(EventType.CONFERENCE)
                    .capacity(500)
                    .registeredAttendeesCount(0)
                    .hasAvailablePlaces(true)
                    .createdBy(admin)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .tags(techConfTags)
                    .onlineEvent(false)
                    .publiclyVisible(true)
                    .registrationRequired(true)
                    .build();
            
            // Create second event - Business meetup in Nur-Sultan (online)
            Event businessMeetup = Event.builder()
                    .title("Startup Investors Meetup")
                    .description("Virtual meetup connecting startup founders with potential investors. " +
                            "Pitch your ideas or listen to innovative business proposals. " +
                            "Special focus on fintech and green technology startups.")
                    .startDateTime(LocalDateTime.now().plusMonths(1))
                    .endDateTime(LocalDateTime.now().plusMonths(1).plusHours(3))
                    .city(nurSultan)
                    .eventType(EventType.MEETUP)
                    .capacity(100)
                    .registeredAttendeesCount(0)
                    .hasAvailablePlaces(true)
                    .createdBy(admin)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .tags(businessMeetupTags)
                    .onlineEvent(true)
                    .onlineLink("https://meet.google.com/startup-meetup")
                    .publiclyVisible(true)
                    .registrationRequired(true)
                    .build();
                    
            eventRepository.saveAll(List.of(techConference, businessMeetup));
            log.info("Sample events initialized: {} events created", 2);
        }
    }

    private City createCity(String name, String region) {
        return City.builder()
                .name(name)
                .region(region)
                .build();
    }
}

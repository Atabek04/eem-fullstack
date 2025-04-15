package kz.muhammadzahid.eem.util;

import kz.muhammadzahid.eem.entity.Role;
import kz.muhammadzahid.eem.entity.User;
import kz.muhammadzahid.eem.repo.RoleRepository;
import kz.muhammadzahid.eem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        initRoles();
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

        log.info("Checking the admins existence")
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
        }
    }
}

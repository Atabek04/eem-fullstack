package kz.muhammadzahid.eem.util;

import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvChecker {

    private final Environment env;

    public EnvChecker(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void checkEnv() {
        System.out.println("DB_URL: " + env.getProperty("DB_URL"));
        System.out.println("DB_USERNAME: " + env.getProperty("DB_USERNAME"));
        System.out.println("DB_PASSWORD: " + env.getProperty("DB_PASSWORD"));
    }
}

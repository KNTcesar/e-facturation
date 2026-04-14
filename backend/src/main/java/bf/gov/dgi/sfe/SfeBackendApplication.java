package bf.gov.dgi.sfe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SfeBackendApplication {

    // Point d'entree unique de l'application backend.
    public static void main(String[] args) {
        SpringApplication.run(SfeBackendApplication.class, args);
    }
}

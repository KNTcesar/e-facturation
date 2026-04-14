package bf.gov.dgi.sfe.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SFE Backend API",
                version = "1.0.0",
                description = "API Spring Boot du systeme SFE conforme FEC pour le Burkina Faso",
                contact = @Contact(name = "Equipe projet SFE")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Serveur local")
        }
)
// Point d'entree de la documentation OpenAPI / Swagger.
public class OpenApiConfig {
}
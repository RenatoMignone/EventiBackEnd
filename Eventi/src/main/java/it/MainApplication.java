package it;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.ServerSocket;

@NoArgsConstructor
@SpringBootApplication
@OpenAPIDefinition(
        tags = {
                @Tag(name="widget", description="Widget operations."),
                @Tag(name="gasket", description="Operations related to gaskets")
        },
        info = @Info(
                title="Eventi Api",
                version = "1.0.1",
                contact = @Contact(
                        name = "eventi support",
                        url = "http://exampleurl.com/contact",
                        email = "techsupport@example.com"),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"))
)
public class MainApplication {
    private static ConfigurableApplicationContext appContext;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    public static void start() {
        if ((appContext == null || !appContext.isRunning()) && !isPort8080InUse()) {
            appContext = SpringApplication.run(MainApplication.class);
        } else {
            System.out.println("Server is already running.");
        }
    }

    public static void stop() {
        if (appContext != null && isPort8080InUse()) {
            SpringApplication.exit(appContext, () -> 0);
        } else {
            System.out.println("Server is not running.");
        }
    }

    private static boolean isPort8080InUse() {
        try (ServerSocket ignored = new ServerSocket(8080)) {
            // Port is available
            return false;
        } catch (Exception e) {
            // Port is already in use
            return true;
        }
    }
}

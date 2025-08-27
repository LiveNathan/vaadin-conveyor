package dev.nathanlively;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.net.URI;


@SpringBootApplication
@PageTitle("Vaadin+Conveyor POC")
@Theme(value = "vaadin-conveyor")
public class Application implements AppShellConfigurator, ApplicationListener<ApplicationReadyEvent> {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        context = SpringApplication.run(Application.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Get the actual port the server started on
        ServletWebServerApplicationContext webServerAppContext =
                (ServletWebServerApplicationContext) event.getApplicationContext();
        int port = webServerAppContext.getWebServer().getPort();
        String url = "http://localhost:" + port;

        System.out.println("Application started at: " + url);

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI.create(url));
            } catch (IOException e) {
                System.err.println("Failed to launch browser: " + e.getMessage());
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application...");
            if (context != null) {
                context.close();
            }
        }));
    }
}
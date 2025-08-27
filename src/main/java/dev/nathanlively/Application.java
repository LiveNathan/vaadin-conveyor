package dev.nathanlively;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.Theme;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@PageTitle("Vaadin+Conveyor POC")
@Theme(value = "vaadin-conveyor")
public class Application implements AppShellConfigurator, ApplicationListener<ApplicationReadyEvent> {

    private static ConfigurableApplicationContext context;
    private static final AtomicBoolean browserOpened = new AtomicBoolean(false);

    public static void main(String[] args) {
        // Ensure we can use AWT/Desktop functionality
        System.setProperty("java.awt.headless", "false");

        // Disable Vaadin's automatic browser launch since we'll handle it ourselves
        System.setProperty("vaadin.launch-browser", "false");

        context = SpringApplication.run(Application.class, args);

        // Set up shutdown hook to ensure graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application...");
            if (context != null && context.isActive()) {
                context.close();
            }
        }));
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        if (!browserOpened.compareAndSet(false, true)) {
            return;
        }

        // Get the actual port the server started on
        ServletWebServerApplicationContext webServerAppContext =
                (ServletWebServerApplicationContext) event.getApplicationContext();
        int port = webServerAppContext.getWebServer().getPort();
        String url = "http://localhost:" + port;

        System.out.println("Application started at: " + url);

        // Open the default browser
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(URI.create(url));
                    System.out.println("Browser launched successfully");
                } catch (IOException e) {
                    System.err.println("Failed to launch browser: " + e.getMessage());
                    System.out.println("Please open this URL manually: " + url);
                }
            } else {
                System.out.println("Browse action not supported. Please open: " + url);
            }
        } else {
            System.out.println("Desktop not supported. Please open: " + url);
        }
    }
}
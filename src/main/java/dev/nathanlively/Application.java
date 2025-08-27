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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@PageTitle("Vaadin+Conveyor POC")
@Theme(value = "vaadin-conveyor")
public class Application implements AppShellConfigurator, ApplicationListener<ApplicationReadyEvent> {

    private static ConfigurableApplicationContext context;
    private static final AtomicBoolean browserOpened = new AtomicBoolean(false);
    private static final AtomicLong lastActivity = new AtomicLong(System.currentTimeMillis());
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final long SHUTDOWN_DELAY_MINUTES = 1; // Shut down after 5 minutes of no activity

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        System.setProperty("vaadin.launch-browser", "false");

        context = SpringApplication.run(Application.class, args);

        // Start the shutdown monitor
        startShutdownMonitor();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application...");
            scheduler.shutdown();
            if (context != null && context.isActive()) {
                context.close();
            }
        }));
    }

    public static void updateActivity() {
        lastActivity.set(System.currentTimeMillis());
    }

    private static void startShutdownMonitor() {
        scheduler.scheduleWithFixedDelay(() -> {
            long timeSinceLastActivity = System.currentTimeMillis() - lastActivity.get();
            long shutdownThreshold = SHUTDOWN_DELAY_MINUTES * 60 * 1000;

            if (timeSinceLastActivity > shutdownThreshold) {
                System.out.println("No activity detected for " + SHUTDOWN_DELAY_MINUTES + " minutes. Shutting down...");
                System.exit(0);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        if (!browserOpened.compareAndSet(false, true)) {
            return;
        }

        ServletWebServerApplicationContext webServerAppContext =
                (ServletWebServerApplicationContext) event.getApplicationContext();
        int port = webServerAppContext.getWebServer().getPort();
        String url = "http://localhost:" + port;

        System.out.println("Application started at: " + url);

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(URI.create(url));
                    System.out.println("Browser launched successfully");
                    updateActivity(); // Record initial activity
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
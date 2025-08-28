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
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@PageTitle("Vaadin+Conveyor POC")
@Theme(value = "vaadin-conveyor")
public class Application implements AppShellConfigurator, ApplicationListener<ApplicationReadyEvent> {
//
    private static ConfigurableApplicationContext context;
    private static final AtomicBoolean browserOpened = new AtomicBoolean(false);
    private static final AtomicInteger activeSessions = new AtomicInteger(0);
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final long NO_SESSIONS_SHUTDOWN_DELAY_SECONDS = 30; // Quick shutdown when no sessions

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        System.setProperty("vaadin.launch-browser", "false");

        context = SpringApplication.run(Application.class, args);

        startShutdownMonitor();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application...");
            scheduler.shutdown();
            if (context != null && context.isActive()) {
                context.close();
            }
        }));
    }

    public static void sessionStarted() {
        int count = activeSessions.incrementAndGet();
        System.out.println("Session started. Active sessions: " + count);
    }

    public static void sessionEnded() {
        int count = activeSessions.decrementAndGet();
        System.out.println("Session ended. Active sessions: " + count);
    }

    public static void shutdownRequested() {
        System.out.println("Shutdown requested via page unload");
        System.exit(0);
    }

    private static void startShutdownMonitor() {
        scheduler.scheduleWithFixedDelay(() -> {
            int sessionCount = activeSessions.get();
            if (sessionCount == 0) {
                System.out.println("No active sessions for " + NO_SESSIONS_SHUTDOWN_DELAY_SECONDS + " seconds. Shutting down...");
                System.exit(0);
            }
        }, NO_SESSIONS_SHUTDOWN_DELAY_SECONDS, 10, TimeUnit.SECONDS);
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

        // More defensive desktop integration
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(URI.create(url));
                    System.out.println("Browser launched successfully");
                } else {
                    System.out.println("Browse action not supported. Please open: " + url);
                }
            } else {
                System.out.println("Desktop not supported. Please open: " + url);
            }
        } catch (Exception e) {
            System.err.println("Failed to launch browser (this is normal on some systems): " + e.getMessage());
            System.out.println("Please open this URL manually: " + url);
            // Don't rethrow - browser launch failure shouldn't crash the app
        }
    }
}
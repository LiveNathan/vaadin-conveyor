package dev.nathanlively;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@PageTitle("Vaadin+Conveyor POC")
@Theme(value = "vaadin-conveyor")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        // Set system property to ensure clean shutdown
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(Application.class, args);
    }

    @Component
    public static class DesktopAppConfiguration implements VaadinServiceInitListener {

        @EventListener(ApplicationReadyEvent.class)
        public void onApplicationReady() {
            System.out.println("Vaadin desktop app is ready and browser should open automatically");
        }

        @Override
        public void serviceInit(ServiceInitEvent serviceInitEvent) {
            serviceInitEvent.getSource().addSessionInitListener(_ -> {});
        }
    }
}
package dev.nathanlively;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class SessionTracker implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(_ -> {
            System.out.println("New session started");
            Application.updateActivity();
        });

        event.getSource().addSessionDestroyListener(_ -> {
            System.out.println("Session ended");
            // When the last session ends, the app could shut down immediately
            // For simplicity; we'll let the timeout handle it
        });

        event.getSource().addUIInitListener(e -> e.getUI().addHeartbeatListener(_ -> Application.updateActivity()));
    }
}
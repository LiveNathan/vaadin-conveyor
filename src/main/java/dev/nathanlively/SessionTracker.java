package dev.nathanlively;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class SessionTracker implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(_ -> Application.sessionStarted());
        event.getSource().addSessionDestroyListener(_ -> Application.sessionEnded());
    }
}
This section explains the technical details of application deployment, user sessions, and the UI instance lifecycle. These details aren’t required for writing Vaadin applications but may be useful for understanding how they work and the circumstances where their execution ends.

## <a href="about:blank#application.lifecycle.deployment"></a> Deployment

Before a Vaadin application can be used, it has to be deployed to a Java web server.
The deployment process reads the servlet classes annotated with the `@WebServlet` annotation or the `web.xml` deployment descriptor in the application to register servlets for specific URL paths.
It then loads the classes.
Deployment doesn’t usually run any code yet in the application, although static blocks in classes are executed when they are loaded.

A servlet class should extend the `VaadinServlet` class.
However, there is no need to define your own servlet class if you are using the Servlet 3.1 specification.
It’s only necessary to have at least one class that carries the `@Route` annotation.
A `VaadinServlet` instance is automatically registered for you and Vaadin registers all the required servlets.

### <a href="about:blank#automatic-servlet-registration"></a> Automatic Servlet Registration

On start-up, the Vaadin application tries to register the Vaadin application servlet, mapped to the path `/*`.

This servlet is needed to serve the main application files.

The servlet isn’t registered if any `VaadinServlet` is already registered, or if there is no class annotated with the `@Route` annotation.

A servlet isn’t registered if:

- there is a servlet that has already been mapped to the same path, or
- if the system property `disable.automatic.servlet.registration` is set to `true`.

### <a href="about:blank#application.lifecycle.deployment.redeployment"></a> Undeploying and Redeploying

Applications are undeployed when the server shuts down, during redeployment, and when they are explicitly undeployed.
Undeploying a server-side Vaadin application ends its execution.
All application classes are unloaded and the heap space allocated by the application is freed for garbage collection.

If any user sessions are open at this point, the client-side state of the UIs is left as it was.
An `Out of Sync` error is displayed on the next server request.

## <a href="about:blank#application.lifecycle.servlet-service"></a> Vaadin Servlet and Service

The `VaadinServlet` receives all server requests mapped to it by its URL, as defined in the deployment configuration, and associates them with sessions.
The sessions further associate the requests with particular UIs.

When servicing requests, the Vaadin servlet handles all tasks common to servlets in a `VaadinService`.
It manages sessions, gives access to the deployment configuration information, handles system messages, and does various other tasks.
Any further servlet-specific tasks are handled in the corresponding `VaadinServletService`.
The service acts as the primary low-level customization layer for processing requests.

To add custom functionality related to Vaadin Service, you should use a <a href="/docs/latest/flow/advanced/service-init-listener">`VaadinServiceInitListener`</a>.
This listener allows you, among other things, to add a `RequestHandler`, an `IndexHtmlRequestListener` and a `DependencyFilter`.

## <a href="about:blank#application.lifecycle.session"></a> User Session

A user session begins when a user first makes a request to a Vaadin servlet by opening the URL of a particular `UI`.
All server requests belonging to a particular UI class are processed by the `VaadinServlet` class.
When a new client connects, it creates a new user session, represented by an instance of `VaadinSession`.
User sessions are stored in the HTTP session and tracked using cookies that are stored in the browser.

You can get the `VaadinSession` of a `UI` with `getSession()`, or globally with `VaadinSession.getCurrent()`.
It also provides access to the lower-level session object `HttpSession`, through a `WrappedSession`.
You can also access the deployment configuration through `VaadinSession`.

A user session ends after the last `UI` instance expires or is closed, as described later.

See [Session and UI Listeners](/docs/latest/flow/advanced/session-and-ui-init-listener) to learn how to listen for user session initialization and destruction events.

## <a href="about:blank#application.lifecycle.ui"></a> Loading a UI

When a browser first accesses a URL mapped to the servlet of a particular UI class, the Vaadin servlet generates a loader page.
The page loads the client-side engine (widget set), which in turn loads the UI in a separate request to the Vaadin servlet.

A `UI` instance is created when the client-side engine makes its first request.

After a new UI is created, its `init()` method is called.
The method gets the request as a `VaadinRequest`.

### <a href="about:blank#application.lifecycle.ui.loaderpage"></a> Customizing the Loader Page

The HTML content of the loader page is generated as an HTML `DOM` object, which can be customized by implementing an `IndexHtmlRequestListener` that modifies the `DOM` object.
To do this, you need to extend the `VaadinServlet` and add a `SessionInitListener` to the service object, as outlined in [User Session](about:blank#application.lifecycle.session).
You can then add the bootstrap listener to a user session with the `addIndexHtmlRequestListener()` method when the session is initialized.

Loading the widget set is handled in the loader page with functions defined in a separate `BootstrapHandler.js` script, whose content is included inline in the page.

## <a href="about:blank#application.lifecycle.ui-expiration"></a> UI Expiration

`UI` instances are cleaned up if no communication is received from them after a certain time.
If no other server requests are made, the client side sends "keep alive" heartbeat requests.
A UI is kept alive for as long as requests or heartbeats are received from it.
It expires if three consecutive heartbeats are missed.

Note, however, that the heartbeat mechanism is only effective as long as there is at least one open UI.
This happens because the detection of closed UIs on the server is only done every time there is a Vaadin request to the same session.
Thus, if the user closes the entire browser window, waiting for three missed heartbeats doesn’t automatically free the UIs for garbage collection.
The normal [User Session Expiration](about:blank#application.lifecycle.session-expiration) mechanism covers situations where all UIs have been closed.

The heartbeats occur at an interval of 5 minutes, which can be changed with the `heartbeatInterval` parameter of the servlet.
You can configure the parameter in `application.properties` (if it’s a Spring Boot project) or [using system properties](/docs/latest/flow/configuration/properties#system-properties).

When the UI cleanup happens, a `DetachEvent` is sent to all `DetachListener` objects added to the UI.
When the `UI` is detached from the session, `detach()` is called for it.

## <a href="about:blank#application.lifecycle.ui-closing"></a> Closing UIs Explicitly

You can explicitly close a UI with `close()`.
The method marks the UI to be detached from the session after processing the current request.
For that reason, the method doesn’t invalidate the UI instance and the response is sent as usual.

Detaching a UI doesn’t close the page or browser window in which the UI is running.
Further server requests cause an error.
Typically, you should close the window, reload it, or redirect it to another URL.
If the page is a regular browser window or tab, browsers don’t usually allow them to be closed programmatically.
However, redirection is possible.
You can redirect the window to another URL via JavaScript.

If you close UIs other than the one associated with the current request, they aren’t detached at the end of the current request.
This happens after the next request from the particular UI.
You can make it happen quicker by increasing the UI heartbeat frequency, or immediately by using server push.

## <a href="about:blank#application.lifecycle.session-expiration"></a> User Session Expiration

A user session is kept alive by server requests that are caused by a user interacting with the application, as well as by the UIs heartbeat-monitoring mechanism.
When all UIs have expired, the user session still remains.
It’s removed from the server when the HTTP session timeout configured in the web application elapses, and the whole HTTP session is invalidated.

If there are active UIs in an application, their heartbeat keeps the user session and underlying HTTP session alive indefinitely.
However, you may want to have the user sessions time out if the user is inactive for a certain time.
This is the original purpose of the session timeout setting.

If the closeIdleSessions deployment configuration parameter of the servlet is set to `true`, the closure mechanism works as follows.

The user session and all its UIs are closed when the timeout specified by the session-timeout parameter of the servlet elapses after the last non-heartbeat request.
After the user session is gone, a `Session expired` notification is sent to the browser on the next server request and the client engine handles it based on the `SystemMessages` settings.

By default, it reloads the current page creating a new user session, but it can also be configured to show a notification to the user before reloading the page.
Furthermore, it is possible to customize the notification message and set a URL where to redirect the browser instead of reloading the current page.
For example, setting a URL that points to a static page is useful to prevent the creation of a new user session.

See [Customize System Messages](/docs/latest/flow/advanced/customize-system-messages) to configure user session expiration, and [Configuration Properties](/docs/latest/flow/configuration/properties) for information on setting configuration parameters.

You can handle user session expiration on the server side with a `SessionDestroyListener`, as described in [User Session](about:blank#application.lifecycle.session).

| Note <!-- col-0 --> | User session expiration does not imply the underlying HTTP session being invalidated.
HTTP session invalidation can be performed by invoking `invalidate` on the `WrappedSession` accessible through `VaadinSession`. <!-- col-1 --> |

## <a href="about:blank#application.lifecycle.session-closing"></a> Closing a User Session

You can close a user session by calling `close()` on the `VaadinSession`.
This is typically used when logging a user out, as the user session and all the UIs belonging to the user session should be closed.
Calling the method closes the user session and makes any related objects unavailable.



<!-- <details> -->
<!-- <summary> -->
Source code
<!-- </summary> -->
Java
<!-- <details> -->
<!-- <summary> -->
Java
<!-- </summary> -->

```java
@Route("")
public class MainLayout extends Div {

    protected void onAttach(AttachEvent attachEvent) {
        UI ui = getUI().get();
        Button button = new Button("Logout", event -> {
            // Redirect this page immediately
            ui.getPage().executeJs("window.location.href='logout.html'");

            // Close the session
            ui.getSession().close();
        });

        add(button);

        // Notice quickly if other UIs are closed
        ui.setPollInterval(3000);
    }
}
```

<!-- </details> -->

<!-- </details> -->
When a user session is closed from one UI, any other UIs attached to it are left hanging.
When the client-side engine notices that a UI and the user session are gone on the server side, it reloads the UI or displays a `Session Expired` message and reloads the UI when the message is clicked, according to the System Messages configuration.

`9405AA6C-4F19-4CB6-AF79-C8DCBD0E0C3A`

<!-- <nav> -->
<a href="/docs/latest/flow/advanced">Flow Reference Advanced Topics</a> <a href="/docs/latest/flow/advanced/i18n-localization">Advanced Topics Localization</a>
<!-- </nav> -->

<!-- <footer> -->
<a href="https://github.com/vaadin/docs/commits/v24/articles/flow/advanced/application-lifecycle.adoc">Updated 2024-12-19</a> [Edit this page on GitHub](https://github.com/vaadin/docs/edit/v24/articles/flow/advanced/application-lifecycle.adoc)
<!-- </footer> -->
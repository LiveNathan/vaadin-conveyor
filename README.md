# Vaadin + Conveyor POC

A proof of concept demonstrating how to package a Vaadin web application as a native desktop application using [Hydraulic Conveyor](https://hydraulic.dev/).

## What This Demonstrates

This project shows how to transform a standard Vaadin Spring Boot web application into a desktop application that:

- **Opens automatically**: Launches the default browser when the application starts
- **Behaves like a desktop app**: Automatically shuts down when the browser window is closed
- **Tracks user sessions**: Monitors active sessions and gracefully shuts down when no users are connected
- **Packages natively**: Creates platform-specific installers for Windows, macOS, and Linux
- **Uses minimal JVM distributions**: Leverages jlink for smaller package sizes

## Background

This POC was created while building [Console Whisperer](https://consolewhisperer.com), an AI mixing console assistant. The journey from web app to desktop app involved evaluating multiple packaging solutions including JDeploy, GraalVM Native Image, and Tauri before settling on Conveyor.

For the full story and technical comparison, see the blog post: [From Web App to Desktop: AI mixing console assistant with Vaadin and Conveyor](https://open.substack.com/pub/nathanlively/p/from-web-app-to-desktop-ai-mixing-console-assistant-vaadin-conveyorhtml)

## Key Technical Features

### Desktop Behavior
- Automatically opens the default browser on application startup
- Detects page unload events (browser window closing)
- Graceful shutdown with configurable timeout when no active sessions

### Session Management
- Tracks active Vaadin sessions via `SessionTracker`
- Automatic cleanup when sessions end
- RESTful shutdown endpoint for browser integration

### Packaging
- Uses Conveyor for cross-platform desktop application packaging
- Includes minimal JVM with only required modules
- GitHub Actions workflow for automated releases
- Creates native installers for major platforms

## Requirements

- **Java 24** (configured for Temurin distribution)
- **Maven 3.6+**
- **Conveyor license** (free for open source, $45/month for commercial use)

## Running Locally

```bash
# Development mode with hot reload
./mvnw spring-boot:run

# Production build
./mvnw clean package -Pproduction
java -jar target/vaadin-conveyor-*.jar
```

The application will start on a random available port and automatically open your default browser.

## Building Desktop Applications

### Prerequisites

1. Install [Conveyor](https://hydraulic.dev/)
2. Set up signing keys (optional for development)
3. Configure GitHub secrets for automated builds

### Local Build

```bash
# Build the JAR first
./mvnw clean package -Pproduction

# Create desktop packages
conveyor make copied-site
```

### Automated Release

The project includes a GitHub Actions workflow (`.github/workflows/release.yml`) that automatically builds and releases desktop applications when you push a version tag:

```bash
git tag v1.0.8
git push origin v1.0.8
```

This creates:
- Native installers for Windows, macOS, and Linux
- A download page with automatic OS detection
- GitHub release with all artifacts

## Configuration

### Application Settings

Key configurations in `application.properties`:
- `server.port=0` - Uses random available port
- `vaadin.launch-browser=false` - Disabled since we handle this manually
- `server.shutdown=graceful` - Enables graceful shutdown

### Conveyor Configuration

The `conveyor.conf` file includes:
- JVM module specifications for Spring Boot + Vaadin
- Platform targets (Windows, macOS, Linux)
- Memory settings and JVM options
- GitHub Pages integration for download site

### JVM Modules

The configuration includes all necessary modules for:
- Spring Boot framework
- Vaadin UI framework
- Desktop integration (java.desktop)
- Management and monitoring

## Project Structure

```
src/main/java/dev/nathanlively/
├── Application.java           # Main class with desktop integration
├── SessionTracker.java        # Vaadin session lifecycle management
├── ShutdownController.java    # REST endpoint for shutdown requests
└── views/
    ├── MainLayout.java        # Main UI layout with shutdown detection
    └── helloworld/
        └── HelloWorldView.java # Simple demo view
```

## Lessons Learned

### Why Conveyor?

After evaluating multiple solutions:

- **JDeploy**: Free and handles signing automatically, good for MVPs
- **GraalVM Native Image**: Fast startup, but shows a terminal window on macOS and longer build times
- **Tauri**: Excellent for Rust apps but doesn't handle JVM packaging
- **Conveyor**: Purpose-built for JVM apps with excellent Spring Boot support

### Key Advantages

1. **Fast packaging**: ~30 seconds vs 5+ minutes for GraalVM
2. **Smaller bundles**: ~130MB vs ~180MB for GraalVM
3. **Professional appearance**: No terminal windows or console output
4. **Cross-platform builds**: Build for all platforms from any OS
5. **Integrated distribution**: Automatic download pages and signing

### Trade-offs

- **Cost**: $45/month for commercial use (vs. free alternatives)
- **Web app constraints**: Vaadin isn't designed for desktop, requires workarounds
- **Browser dependency**: Still needs a browser runtime (though bundled)

## Alternative Approaches

For new desktop projects, consider:
- **JavaFX**: Native desktop UI framework with better desktop integration
- **Swing**: Mature desktop framework with extensive ecosystem
- **Tauri + Rust**: For new projects willing to adopt Rust backend

This POC specifically demonstrates retrofitting an existing Vaadin web application for desktop distribution.

## Contributing

This is a proof of concept intended to help other developers facing similar packaging challenges. Feel free to:

- Fork and adapt for your own projects
- Submit issues for questions or problems
- Share your own packaging experiences

## License

This project is released into the public domain under the Unlicense. Use it however you'd like.

## Resources

- [Hydraulic Conveyor Documentation](https://hydraulic.dev/docs/)
- [Vaadin Documentation](https://vaadin.com/docs)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/)
- [Blog post with full technical journey](https://open.substack.com/pub/nathanlively/p/from-web-app-to-desktop-ai-mixing-console-assistant-vaadin-conveyorhtml)
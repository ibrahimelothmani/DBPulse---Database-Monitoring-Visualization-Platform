package com.ibrahim.DBPulse.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Application startup configuration and logging.
 * Displays useful information when the application starts.
 */
@Configuration
@Slf4j
public class ApplicationStartupConfig {

    private final Environment env;

    public ApplicationStartupConfig(Environment env) {
        this.env = env;
    }

    /**
     * Log application startup information.
     * Displays URLs for accessing the application and documentation.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStartup() {
        String protocol = "http";
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Unable to determine host address", e);
        }

        log.info("""

                ----------------------------------------------------------
                \t🚀 Application '{}' is running!
                \t📍 Local:      {}://localhost:{}{}
                \t🌐 External:   {}://{}:{}{}
                \t📚 Swagger UI: {}://localhost:{}{}/swagger-ui.html
                \t📊 API Docs:   {}://localhost:{}{}/v3/api-docs
                \t❤️  Health:     {}://localhost:{}{}/actuator/health
                \t📈 Metrics:    {}://localhost:{}{}/actuator/metrics
                \t📉 Prometheus: {}://localhost:{}{}/actuator/prometheus
                \t🔧 Environment: {}
                ----------------------------------------------------------
                """,
                env.getProperty("spring.application.name", "DBPulse"),
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                protocol, serverPort, contextPath,
                protocol, serverPort, contextPath,
                protocol, serverPort, contextPath,
                protocol, serverPort, contextPath,
                protocol, serverPort, contextPath,
                env.getProperty("spring.profiles.active", "default"));
    }
}

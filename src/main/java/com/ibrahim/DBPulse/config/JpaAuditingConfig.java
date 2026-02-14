package com.ibrahim.DBPulse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * JPA Auditing configuration.
 * Enables automatic tracking of entity creation and modification metadata.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Provides the current auditor (user) for JPA auditing.
     * Currently returns "system" but should be replaced with actual user from
     * security context.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // TODO: Replace with actual user from Spring Security context
            // SecurityContext context = SecurityContextHolder.getContext();
            // Authentication authentication = context.getAuthentication();
            // if (authentication != null && authentication.isAuthenticated()) {
            // return Optional.of(authentication.getName());
            // }
            return Optional.of("system");
        };
    }
}

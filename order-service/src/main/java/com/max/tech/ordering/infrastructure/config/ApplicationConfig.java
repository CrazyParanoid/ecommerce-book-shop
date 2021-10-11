package com.max.tech.ordering.infrastructure.config;

import com.max.tech.ordering.infrastructure.events.OutputBindings;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@EnableAsync
@Configuration
@EnableScheduling
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableBinding(OutputBindings.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ApplicationConfig {
    @Bean
    public JvmThreadMetrics threadMetrics() {
        return new JvmThreadMetrics();
    }

    @Bean
    public MeterRegistry meterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("order");
    }

}

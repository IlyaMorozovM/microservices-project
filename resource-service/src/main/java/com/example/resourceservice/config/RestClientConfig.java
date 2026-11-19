package com.example.resourceservice.config;

import com.example.resourceservice.tracing.TracingConstants;
import org.slf4j.MDC;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    String traceId = MDC.get(TracingConstants.TRACE_ID_MDC_KEY);
                    if (traceId != null) {
                        request.getHeaders().add(TracingConstants.TRACE_ID_HEADER, traceId);
                    }
                    return execution.execute(request, body);
                });
    }
}
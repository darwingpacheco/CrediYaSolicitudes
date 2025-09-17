package co.com.solicitudescrediya.sqs.sender.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
@ConditionalOnMissingBean(SqsAsyncClient.class)
public class SQSSenderConfig {

    @Bean
    public SqsAsyncClient configSqs(SQSSenderProperties properties, MetricPublisher publisher) {
        return SqsAsyncClient.builder()
                .region(Region.of(properties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.accessKey(),
                                        properties.secretKey()
                                )
                        )
                )
                // 👇 Usa el endpoint configurado si existe
                .applyMutation(b -> {
                    if (properties.endpoint() != null && !properties.endpoint().isBlank()) {
                        b.endpointOverride(URI.create(properties.endpoint()));
                    }
                })
                .build();
    }

    private AwsCredentialsProviderChain getProviderChain() {
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }

    private URI resolveEndpoint(SQSSenderProperties properties) {
        if (properties.endpoint() != null) {
            return URI.create(properties.endpoint());
        }
        return null;
    }
}

package yeonjeans.saera.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Value("${spring.cloud.gcp.credentials.location}")
    private String credentialsLocation;
    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    @Bean
    public String MLserverBaseUrl(){
        return "http://34.22.92.172//";
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    public Storage storage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}

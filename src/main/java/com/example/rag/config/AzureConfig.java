package com.example.rag.config;

import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {
    @Bean
    public AzureKeyCredential provideCredentials(@Value("${azure.document.key}") String key){
        return new AzureKeyCredential(key);
    }
}

package com.example.rag.config;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class AzureDocumentIntelligenceProvider {

    @Bean
    public DocumentAnalysisClient provideDocumentAnalysisClient(@Value("${azure.document.endpoint}") String endpoint, AzureKeyCredential credentials) {
        return new DocumentAnalysisClientBuilder()
                .credential(credentials)
                .endpoint(endpoint)
                .buildClient();
    }
}

package com.example.rag.config;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ModelManager {

    @Bean
    public EmbeddingModel createEmbeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public ChatLanguageModel provideChatModel() {

//        return AzureOpenAiChatModel.builder()
//                .baseUrl("<URL>")
//                .apiKey("<API KEY>")
//                .apiVersion("<X>")
//                .build();

        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("mixtral")
                .timeout(Duration.ofSeconds(30))
                .build();

    }
}

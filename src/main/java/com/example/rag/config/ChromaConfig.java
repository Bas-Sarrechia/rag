package com.example.rag.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.amikos.chromadb.Client;

@Configuration
public class ChromaConfig {
    @Bean
    public Client provideChromaConfig(@Value("${chroma.uri}") String chromaUri) {
        return new Client(chromaUri);
    }

    @Bean
    public EmbeddingStore<TextSegment> createEmbeddingStore() {
        return ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .collectionName("embedcollection")
                .build();
    }

}

package com.example.rag;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper;
    private final DocumentSplitter splitter = DocumentSplitters.recursive(
            100,
            10,
            null
    );

    public EmbeddingService(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel, ObjectMapper objectMapper) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
        this.objectMapper = objectMapper;
    }

    public void embed(Embeddable embeddable) {
        Document document = Document.from(embeddable.getEmbeddableContent(), embeddable.getMetaData());
        List<TextSegment> segments = splitter.split(document).stream().map(segPart -> TextSegment.from(
                "%s %s".formatted(embeddable.getRepeatableContent(), segPart.text())
                , segPart.metadata())
        ).toList();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);
    }
}

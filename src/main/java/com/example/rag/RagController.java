package com.example.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.amikos.chromadb.Client;
import tech.amikos.chromadb.Collection;
import tech.amikos.chromadb.handler.ApiException;

import java.util.List;
import java.util.Map;

@RestController
public record RagController(Client client,
                            EmbeddingStore<TextSegment> embeddingStore,
                            EmbeddingModel embeddingModel,
                            PdfConverter pdfConverter,
                            ChatLanguageModel chatModel,
                            EmbeddingService embeddingService) {

    @PostMapping("/pdf")
    public Chemical processPdf(@RequestParam("file") MultipartFile file) {
        PdfData pdfData = pdfConverter.process(file);
        ChemicalExtractor extractor = AiServices.create(ChemicalExtractor.class, chatModel);
        Chemical chemical = extractor.extractChemicalDataFrom(pdfData.content().replaceAll("\n", ""));
        pdfData.setChemical(chemical);
        // add validation step before actually doing this
        embeddingService.embed(pdfData);
        return chemical;
    }

    @PostMapping()
    public ResponseEntity<?> getCollections(@RequestBody ChatRequest chatRequest) throws ApiException {
        Embedding queryEmbedding = embeddingModel.embed(chatRequest.interaction()).content();
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
        if (relevant.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        EmbeddingMatch<TextSegment> embeddingMatch = relevant.getFirst();

        String normalisedRespond = chatModel.generate(
                """
                        please respond as concise and accurate as possible to the following question:
                        %s
                        using the following context:
                        %s
                        """
                        .formatted(chatRequest, embeddingMatch.embedded().text()));
        return ResponseEntity.ok(new ReturnValue(
                embeddingMatch.score(),
                normalisedRespond,
                embeddingMatch.embedded().metadata().asMap()
        ));
    }

    @DeleteMapping("/all")
    public void clearCollections() throws ApiException {
        for (Collection collection : client.listCollections()) {
            collection.deleteWithIds(collection.get().getIds());
        }
    }
}

record ReturnValue(double score, String textresult, Map meta) {
}

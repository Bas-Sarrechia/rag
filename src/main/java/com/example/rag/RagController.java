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

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public record RagController(EmbeddingModel embeddingModel,
                            PdfConverter pdfConverter,
                            ChatLanguageModel chatModel) {

    @PostMapping("/pdf")
    public Chemical processPdf(@RequestParam("file") MultipartFile file) throws IOException {
        pdfConverter.parsePdfByAzureService(file);
        PdfData pdfData = pdfConverter.process(file);
        ChemicalExtractor extractor = AiServices.create(ChemicalExtractor.class, chatModel);
        Chemical chemical = extractor.extractChemicalDataFrom(pdfData.content().replaceAll("\n", ""));
        pdfData.setChemical(chemical);
        // add validation step before actually doing this
        return chemical;
    }
}

record ReturnValue(double score, String textresult, Map meta) {
}

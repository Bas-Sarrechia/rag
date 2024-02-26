package com.example.rag;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentTable;
import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class PdfConverter {
    private final DocumentAnalysisClient documentAnalysisClient;

    public PdfConverter(DocumentAnalysisClient documentAnalysisClient) {
        this.documentAnalysisClient = documentAnalysisClient;
    }

    public String parsePdfByAzureService(MultipartFile file) throws IOException {
        String modelId = "prebuilt-layout";
        BinaryData documentBytes = BinaryData.fromByteBuffer(ByteBuffer.wrap(file.getBytes()));
        SyncPoller<OperationResult, AnalyzeResult> analyzeLayoutResultPoller =
                documentAnalysisClient.beginAnalyzeDocument(modelId, documentBytes);

        DocumentTable table = analyzeLayoutResultPoller.getFinalResult().getTables().getFirst();

        int columns = table.getColumnCount();
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < table.getRowCount() - 1; row++) {
            for (int column = 0; column <= columns; column++) {
                builder.append(table.getCells().get((column * row) % columns).getContent());
                builder.append(" ");
                builder.append(table.getCells().get((column * row) + columns).getContent());
                builder.append(";");
            }
            builder.append("\n");
        }

        return analyzeLayoutResultPoller.getFinalResult().getContent();

    }

    public PdfData process(MultipartFile file) {
        StringBuilder result = new StringBuilder();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File empty");
        }
        if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("Invalid file format. Please upload a PDF file.");
        }

        File tempFile;
        try (InputStream inputStream = file.getInputStream()) {
            tempFile = File.createTempFile("temp", ".pdf");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (PDDocument document = Loader.loadPDF(tempFile)) {
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                PDFTextStripper stripper = new PDFTextStripper();
                result.append(stripper.getText(document));
            }
            document.save("output.pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new PdfData(
                file.getOriginalFilename(), result.toString()
        );
    }

}

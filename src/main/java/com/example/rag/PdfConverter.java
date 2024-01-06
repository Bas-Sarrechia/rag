package com.example.rag;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class PdfConverter {
    private static void addBoundingBoxToText(PDDocument document, PDPage page, String searchText) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700); // Adjust the offset based on your needs
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.COURIER);
        contentStream.setFont(font, 1);
        contentStream.showText(searchText);
        contentStream.endText();

        // Add a bounding box around the text
        contentStream.addRect(100, 700 - 100, 100, 100);
        contentStream.setLineWidth(1f);
        contentStream.stroke();

        // Close the content stream
        contentStream.close();
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
                addBoundingBoxToText(document, document.getPage(page), "environmental");
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

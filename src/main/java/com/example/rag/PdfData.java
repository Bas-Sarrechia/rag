package com.example.rag;

import dev.langchain4j.data.document.Metadata;

import java.util.Objects;

public final class PdfData {
    private final String fileName;
    private final String content;
    private Chemical chemical;

    public PdfData(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String content() {
        return content;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    public void setChemical(Chemical chemical) {
        this.chemical = chemical;
    }
}

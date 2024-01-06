package com.example.rag;

import dev.langchain4j.data.document.Metadata;

import java.util.Objects;

public final class PdfData implements Embeddable {
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

    @Override
    public String getEmbeddableContent() {
        return this.content;
    }

    @Override
    public String getRepeatableContent() {
        return this.chemical.toString();
    }

    @Override
    public Metadata getMetaData() {
        Metadata metadata = new Metadata();
        metadata.add("chemical", this.chemical.toJson());
        metadata.add("file", this.fileName);
        return metadata;
    }

    public void setChemical(Chemical chemical) {
        this.chemical = chemical;
    }
}

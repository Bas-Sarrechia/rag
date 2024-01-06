package com.example.rag;

import dev.langchain4j.data.document.Metadata;

public interface Embeddable {
    String getEmbeddableContent();
    String getRepeatableContent();
    Metadata getMetaData();
}

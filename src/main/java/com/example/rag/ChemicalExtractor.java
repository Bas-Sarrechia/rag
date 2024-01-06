package com.example.rag;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ChemicalExtractor {

    @UserMessage("Extract information about a chemical from {{context}}. Unknown properties can be filled in as NA")
    Chemical extractChemicalDataFrom(@V("context") String text);
}

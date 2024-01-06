package com.example.rag;


import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetWordLocationAndSize extends PDFTextStripper {
    public GetWordLocationAndSize() throws IOException {
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        String wordSeparator = getWordSeparator();
        List<TextPosition> word = new ArrayList<>();
        for (TextPosition text : textPositions) {
            String thisChar = text.getUnicode();
            if (thisChar != null) {
                if (thisChar.length() >= 1) {
                    if (!thisChar.equals(wordSeparator)) {
                        word.add(text);
                    } else if (!word.isEmpty()) {
                        printWord(word);
                        word.clear();
                    }
                }
            }
        }
        if (!word.isEmpty()) {
            printWord(word);
            word.clear();
        }
    }

    void printWord(List<TextPosition> word) {
        Rectangle2D boundingBox = null;
        StringBuilder builder = new StringBuilder();
        for (TextPosition text : word) {
            Rectangle2D box = new Rectangle2D.Float(text.getXDirAdj(), text.getYDirAdj(), text.getWidthDirAdj(), text.getHeightDir());
            if (boundingBox == null)
                boundingBox = box;
            else
                boundingBox.add(box);
            builder.append(text.getUnicode());
        }
        System.out.println(builder.toString() + " [(X=" + boundingBox.getX() + ",Y=" + boundingBox.getY()
                + ") height=" + boundingBox.getHeight() + " width=" + boundingBox.getWidth() + "]");
    }
}


package com.xanbit.education.language.writer;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;
import com.xanbit.education.language.exception.PDFGenerationException;
import com.xanbit.education.language.swedish.dictionary.xml.model.Word;
import com.xanbit.education.language.swedish.lookup.WordLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

@Service
public class PDFGenerator {

    @Autowired
    private WordLookupService lookupService;

    public void generate(List<Word> lookedUpWords, Set<String> notKnownWords, String dest) throws PDFGenerationException, FileNotFoundException {

        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            document.open();

            printWords(document, lookedUpWords);

            printNotFoundWords(notKnownWords, document);

            document.close();

            System.out.println("PDF generated : "+dest);

        }catch (DocumentException ex){
            throw new PDFGenerationException();
        }
    }

    public void generateFlashcards(Map<Integer, Set<String>> wordsByPage, String dest) throws FileNotFoundException, PDFGenerationException {
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            document.open();

            document.addTitle("Flashcards");

            printFlashCards(document, wordsByPage);

            document.close();

            System.out.println("PDF generated : "+dest);

        }catch (DocumentException ex){
            throw new PDFGenerationException();
        }
    }

    private void printFlashCards(Document document, Map<Integer, Set<String>> wordsByPage) {

        wordsByPage.entrySet().forEach(e -> {
            try {
                addPageWordsToDocument(document, e.getKey(), e.getValue());
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void addPageWordsToDocument(Document document, Integer pageNumber, Set<String> pageWords) throws DocumentException {

        double pdfPageCount = Math.ceil(((double)pageWords.size()/8));

        Iterator<String> wordsIterator = pageWords.iterator();

        for (int i = 0; i < pdfPageCount; i++) {

            Paragraph paragraph = new Paragraph("Page : "+pageNumber);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(Chunk.NEWLINE);

            //2X4 table
            PdfPTable pdfPTable = new PdfPTable(2);
            pdfPTable.setTotalWidth(580F);
            pdfPTable.setWidths(new float[]{280F, 280F});

            List<String> wordsForCurrentPage = new ArrayList<>();
            for (int j = 0; j< 8; j++){
                if (wordsIterator.hasNext()){
                    String w = wordsIterator.next();
                    Phrase phrase = new Phrase(w);
                    PdfPCell cell = new PdfPCell(phrase);
                    cell.setFixedHeight(150F);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.BOX);
                    cell.setPaddingLeft(20F);
                    cell.setPaddingRight(20F);
                    cell.setPaddingTop(20F);
                    cell.setPaddingBottom(20F);
                    pdfPTable.addCell(cell);
                    wordsForCurrentPage.add(w);
                }
            }
            pdfPTable.completeRow();
            document.add(pdfPTable);
            document.newPage();
            //add word hints
            addWordHintsToDocument(wordsForCurrentPage, document);

            document.newPage();
        }

    }

    private void addWordHintsToDocument(List<String> wordsForCurrentPage, Document document) throws DocumentException {

        //2X4 table
        PdfPTable pdfPTable = new PdfPTable(2);
        pdfPTable.setTotalWidth(580F);
        pdfPTable.setWidths(new float[]{280F, 280F});

        for (String w : wordsForCurrentPage){
            Word lookedUpWord = lookupService.lookup(w);
            String wordHint = lookedUpWord != null ? lookedUpWord.getTranslationsString().replaceAll("\\s*,\\s*$", "") : w;
            Phrase phrase = new Phrase(wordHint);
            PdfPCell cell = new PdfPCell(phrase);
            cell.setFixedHeight(150F);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);
            }
            document.add(pdfPTable);
    }

    private void printWords(Document document, List<Word> words) throws DocumentException {
        for (Word word : words) {
            try {
                addWordToDocument(document, word);
                document.add(Paragraph.getInstance(""));
                document.add( Chunk.NEWLINE );
            }catch (Exception e){
                System.out.println("Problem adding the word to pdf : "+word+"  : "+e.getMessage());
                e.printStackTrace();
            }

        }
    }

    private void printNotFoundWords(Set<String> notKnownWords, Document document) throws DocumentException {
        document.add(Paragraph.getInstance("Words not available in Dictionary : "+notKnownWords));
    }

    private void addWordToDocument(Document document, Word word) throws DocumentException {

        PdfPTable outerTable = new PdfPTable(new float[]{580});
        outerTable.setTotalWidth(580F);
        outerTable.setLockedWidth(true);

        //add headers
        outerTable.addCell(word.getValue() +" ("+word.getWordClass()+")   :-   "+word.getTranslationsString());

        float[] columnWidths = {120, 460};
        PdfPTable detailsTable = new PdfPTable(columnWidths);
        detailsTable.setTotalWidth(580F);
        detailsTable.setLockedWidth(true);
        addNestedTable(detailsTable, word);

        outerTable.addCell(detailsTable);

        document.add(outerTable);
    }

    private void addNestedTable(PdfPTable outerTable, Word word) {
        PdfPTable inflectionsTable = new PdfPTable(1);
        PdfPCell cell;
        //add inflections
        word.getParadigmInflections().stream().forEach(inf -> inflectionsTable.addCell(inf));
        outerTable.addCell(inflectionsTable);

        float[] columnWidths = {460};
        PdfPTable rightOuterTable = new PdfPTable(columnWidths);
        rightOuterTable.setTotalWidth(460F);
        rightOuterTable.setLockedWidth(true);

        //add definition
        word.getDefinitionValues().stream().forEach(def -> rightOuterTable.addCell(def));

        //add synonyms
        if ( ! word.getSynonymsString().isEmpty())
            rightOuterTable.addCell("Synonyms  :-  "+word.getSynonymsString());

        //add examples
        word.getExamplesTranslations().forEach(ex -> rightOuterTable.addCell("Ex. "+ex));

        outerTable.addCell(rightOuterTable);
        cell = new PdfPCell();
        cell.setColspan(14);
        outerTable.addCell(cell);
    }

    private void createDirectory(String path) {
        File file = new File(path);
        file.mkdirs();
    }
}

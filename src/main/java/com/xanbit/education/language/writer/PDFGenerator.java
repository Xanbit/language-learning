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

import javax.xml.stream.events.Characters;
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
        document.setMargins(4f, 4f, 4f, 4f);

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

            //2X4 table
            PdfPTable pdfPTable = new PdfPTable(2);
            pdfPTable.setWidthPercentage(99.0f);

            List<String> wordsForCurrentPage = new ArrayList<>();
            for (int j = 0; j< 8; j++){
                if (wordsIterator.hasNext()){
                    String w = wordsIterator.next();
                    Font wordFont = new Font(Font.FontFamily.TIMES_ROMAN);
                    wordFont.setSize(30f);
                    Phrase phrase = new Phrase(w, wordFont);
                    PdfPCell cell = new PdfPCell(phrase);
                    cell.setFixedHeight(207F);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.BOX);
                    cell.setPaddingLeft(20F);
                    cell.setPaddingRight(20F);
                    cell.setPaddingTop(20F);
                    cell.setPaddingBottom(20F);
                    cell.setBorderWidth(15f);
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
        pdfPTable.setWidthPercentage(100f);
        List<PdfPCell> eightCellls = new ArrayList<>();

        for (int i = 0; i < 8; i++){
            Word lookedUpWord = wordsForCurrentPage.size() > i ? lookupService.lookup(wordsForCurrentPage.get(i)) : null;
            PdfPCell cell = lookedUpWord == null ? new PdfPCell(new Phrase("")) : getWordHints(lookedUpWord);
            cell.setFixedHeight(207F);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidth(15f);
            eightCellls.add(cell);
        }
        /*
        for (String w : wordsForCurrentPage){
            Word lookedUpWord = lookupService.lookup(w);
            PdfPCell cell = lookedUpWord == null ? new PdfPCell(new Phrase("")) : getWordHints(lookedUpWord);
            cell.setFixedHeight(207F);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidth(15f);
            pdfPTable.addCell(cell);
            }*/

        pdfPTable.addCell(eightCellls.get(1));
        pdfPTable.addCell(eightCellls.get(0));
        pdfPTable.addCell(eightCellls.get(3));
        pdfPTable.addCell(eightCellls.get(2));
        pdfPTable.addCell(eightCellls.get(5));
        pdfPTable.addCell(eightCellls.get(4));
        pdfPTable.addCell(eightCellls.get(7));
        pdfPTable.addCell(eightCellls.get(6));

        document.add(pdfPTable);
    }

    private PdfPCell getWordHints(Word lookedUpWord) {

        Font wordClassFont = new Font(Font.FontFamily.TIMES_ROMAN);
        wordClassFont.setStyle(Font.BOLD);

        Paragraph p1 = new Paragraph(lookedUpWord.getWordClass() != null ? lookedUpWord.getWordClass().toUpperCase()+"  " : "");
        p1.setAlignment(Element.ALIGN_CENTER);
        p1.setFont(wordClassFont);

        Paragraph p2 = new Paragraph(lookedUpWord.getTranslationsString().replaceAll("\\s*,\\s*$", ""));
        p2.setAlignment(Element.ALIGN_CENTER);
        Font translationFont = new Font(Font.FontFamily.HELVETICA);
        translationFont.setStyle(Font.ITALIC);
        p2.setFont(translationFont);

        Font inflectionsFont = new Font(Font.FontFamily.COURIER);
        inflectionsFont.setStyle(Font.BOLDITALIC);
        Paragraph p3 = new Paragraph(lookedUpWord.getInflectionsString().replaceAll("\\s*,\\s*$", ""));
        p3.setAlignment(Element.ALIGN_CENTER);
        p3.setFont(inflectionsFont);

        PdfPCell cell = new PdfPCell();
        cell.addElement(p1);
        cell.addElement(Chunk.NEWLINE);
        cell.addElement(p2);
        cell.addElement(Chunk.NEWLINE);
        cell.addElement(p3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return cell;
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

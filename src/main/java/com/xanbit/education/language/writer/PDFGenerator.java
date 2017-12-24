package com.xanbit.education.language.writer;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.xanbit.education.language.exception.PDFGenerationException;
import com.xanbit.education.language.swedish.dictionary.xml.model.Word;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

@Service
public class PDFGenerator {

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

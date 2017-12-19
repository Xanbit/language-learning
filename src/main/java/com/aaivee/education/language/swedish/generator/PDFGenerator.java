package com.aaivee.education.language.swedish.generator;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFGenerator {

    public static final String DEST = "results/tables/nested_tables.pdf";
    public static void main(String[] args) throws IOException, DocumentException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        System.out.println(file.getAbsolutePath());
        new PDFGenerator().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        //float[] columnWidths = {183, 31, 88, 49, 35, 25, 35, 35, 35, 32, 32, 33, 35, 60, 46, 26 };
        printWord(document);
        printWord(document);
        printWord(document);
        document.close();
    }

    private void printWord(Document document) throws DocumentException {
        float[] columnWidths = {120, 460};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setTotalWidth(580F);
        table.setLockedWidth(true);
        buildNestedTables(table);
        document.add(table);
        document.add(Paragraph.getInstance(""));
        document.add( Chunk.NEWLINE );
    }

    private void buildNestedTables(PdfPTable outerTable) {
        PdfPTable inflectionsTable = new PdfPTable(1);
        PdfPCell cell;
        inflectionsTable.addCell("Inflection 1");
        inflectionsTable.addCell("Inflection 2");
        inflectionsTable.addCell("Inflection 3");
        inflectionsTable.addCell("Inflection 4");
        inflectionsTable.addCell("Inflection 5");
        outerTable.addCell(inflectionsTable);

        float[] columnWidths = {460};
        PdfPTable rightOuterTable = new PdfPTable(columnWidths);
        rightOuterTable.setTotalWidth(460F);
        rightOuterTable.setLockedWidth(true);

        rightOuterTable.addCell("gör ingenting spännande, hoppa över väggen");
        rightOuterTable.addCell("do, make, produce | (syn) skappa, (syn) hoppa, (ant) sova");
        rightOuterTable.addCell("Ex. gör nånting intressant : do something interesting");
        rightOuterTable.addCell("Ex. hoppa på säng : jump om bed");
        /*
        PdfPTable definitions = new PdfPTable(1);
        definitions.addCell("gör ingenting spännande, hoppa över väggen");
        rightOuterTable.addCell(definitions);

        PdfPTable meanings = new PdfPTable(1);
        meanings.addCell("do, make, produce");
        rightOuterTable.addCell(meanings);

        PdfPTable relatedWords = new PdfPTable(1);
        relatedWords.addCell("(syn) skappa, (syn) hoppa, (ant) sova");
        rightOuterTable.addCell(relatedWords);

        PdfPTable example1 = new PdfPTable(1);
        example1.addCell("Ex. gör nånting intressant : do something interesting");
        rightOuterTable.addCell(example1);

        PdfPTable example2 = new PdfPTable(1);
        example2.addCell("Ex. hoppa på säng : jump om bed");
        rightOuterTable.addCell(example2);
        */
        outerTable.addCell(rightOuterTable);
        cell = new PdfPCell();
        cell.setColspan(14);
        outerTable.addCell(cell);
    }

    private void buildNestedTables2(PdfPTable outerTable) {
        PdfPTable innerTable1 = new PdfPTable(1);
        innerTable1.setWidthPercentage(100);
        PdfPTable innerTable2 = new PdfPTable(2);
        innerTable2.setWidthPercentage(100);
        PdfPCell cell;
        innerTable1.addCell("Cell 1");
        innerTable1.addCell("Cell 2");
        cell = new PdfPCell(innerTable1);
        outerTable.addCell(cell);
        innerTable2.addCell("Cell 3");
        innerTable2.addCell("Cell 4");
        cell = new PdfPCell(innerTable2);
        outerTable.addCell(cell);
        cell = new PdfPCell();
        cell.setColspan(14);
        outerTable.addCell(cell);
    }

    private void buildNestedTables3(PdfPTable outerTable) {
        PdfPTable innerTable1 = new PdfPTable(1);
        innerTable1.setWidthPercentage(100);
        PdfPTable innerTable2 = new PdfPTable(2);
        innerTable2.setWidthPercentage(100);
        PdfPCell cell;
        innerTable1.addCell("Cell 1");
        innerTable1.addCell("Cell 2");
        cell = new PdfPCell();
        cell.addElement(innerTable1);
        outerTable.addCell(cell);
        innerTable2.addCell("Cell 3");
        innerTable2.addCell("Cell 4");
        cell = new PdfPCell();
        cell.addElement(innerTable2);
        outerTable.addCell(cell);
        cell = new PdfPCell();
        cell.setColspan(14);
        outerTable.addCell(cell);
    }
}

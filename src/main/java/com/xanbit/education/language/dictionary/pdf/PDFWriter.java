package com.xanbit.education.language.dictionary.pdf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

import com.xanbit.education.language.dictionary.xml.model.Word;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFWriter {

	private static final String OUTPUT_FILE = "/Users/markiv/sv-en-output.pdf";
	
	public void write(List<Word> words, Set<String> unfoundWords) throws FileNotFoundException, DocumentException{
		 Document document = new Document(PageSize.A4.rotate());
	        PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_FILE));
	        document.open();
	        float[] columnWidths = {183, 31, 88, 49, 35, 25, 35, 35, 35, 32, 32, 33, 35, 60, 46, 26 };
	        PdfPTable table = new PdfPTable(columnWidths);
	        table.setTotalWidth(770F);
	        table.setLockedWidth(true);
	        buildNestedTables(table);
	        document.add(new Paragraph("Add table straight to another table"));
	        document.add(table);
	        document.close();
		
		
	}
	
	private void buildNestedTables(PdfPTable outerTable) {
        PdfPTable innerTable1 = new PdfPTable(1);
        PdfPTable innerTable2 = new PdfPTable(2);
        PdfPCell cell;
        innerTable1.addCell("Cell 1");
        innerTable1.addCell("Cell 2");
        outerTable.addCell(innerTable1);
        innerTable2.addCell("Cell 3");
        innerTable2.addCell("Cell 4");
        outerTable.addCell(innerTable2);
        cell = new PdfPCell();
        cell.setColspan(14);
        outerTable.addCell(cell);
   }
	
}

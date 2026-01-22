package org.speiser.haggadah;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;

public class AssembleContent {

    public static void main(String... args) {
        try {
            File cover = new File("../assets/title-dedication.pdf");
            File content = new File("../Haggadah.pdf");
            File extra = File.createTempFile("blank", ".pdf"); //blank page at end
            File out = new File("../Content-Pages.pdf");

            // Create a PDF with a single blank page to add at the end
            PDDocument blank = new PDDocument();
            blank.addPage(new PDPage());
            blank.save(extra);
            blank.close();

            PDDocumentInformation info = new PDDocumentInformation();
            info.setTitle("Haggadah");
            info.setAuthor("Baruch Speiser");

            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationDocumentInformation(info);
            merger.setDestinationFileName(out.getAbsolutePath());
            merger.addSource(cover);
            merger.addSource(content);
            merger.addSource(extra);
            merger.mergeDocuments(null);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}

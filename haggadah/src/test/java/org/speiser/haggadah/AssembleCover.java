package org.speiser.haggadah;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;

public class AssembleCover {

    public static void main(String... args) {
        try {
            File front = new File("../assets/front-cover-A5.pdf");
            File back = new File("../assets/back-cover-A5.pdf");
            File out = new File("../Covers-Front-Back-A5.pdf");

            PDDocumentInformation info = new PDDocumentInformation();
            info.setTitle("Haggadah");
            info.setAuthor("Baruch Speiser");

            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationDocumentInformation(info);
            merger.setDestinationFileName(out.getAbsolutePath());
            merger.addSource(front);
            merger.addSource(back);
            merger.mergeDocuments(null);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}

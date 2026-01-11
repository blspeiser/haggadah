package org.speiser.haggadah;

import java.io.File;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;

public class AddCoverPage {

  public static void main(String... args) {
    try {
      File front = new File("../assets/front-cover.pdf");
      File cover = new File("../assets/title-dedication.pdf");
      File content = new File("../Haggadah.pdf");
      File extra = File.createTempFile("blank", ".pdf"); //blank page at end
      File back = new File("../assets/back-cover.pdf");
      File out = new File("../PrintableHaggadah.pdf");
      
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
      merger.addSource(front);
      merger.addSource(cover);
      merger.addSource(content);
      merger.addSource(extra);
      merger.addSource(back);
      merger.mergeDocuments(null);
      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}

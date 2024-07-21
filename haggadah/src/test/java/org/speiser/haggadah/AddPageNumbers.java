package org.speiser.haggadah;

import java.awt.Color;
import java.io.File;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.util.Matrix;

public class AddPageNumbers {

  public static void main(String... args) {
    try {
      File in = new File("../Haggadah-Google-Fonts.pdf");
      File out = new File("../numbered.pdf");
      if(out.exists()) out.delete();
      
      try(PDDocument doc = Loader.loadPDF(in)) {
        PDFont font = new PDType1Font(FontName.TIMES_ROMAN);
        float fontSize = 11.0f;
        int pageNum = 1;
        for(PDPage page : doc.getPages()) {
          PDRectangle pageSize = page.getMediaBox();
          String message = Integer.toString(pageNum++);
          float stringWidth = font.getStringWidth(message) * fontSize / 1000f;
          // calculate to center of the page
          int rotation = page.getRotation();
          boolean rotate = rotation == 90 || rotation == 270;
          float pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
          float pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
          float centerX = rotate ? pageHeight / 2f : (pageWidth - stringWidth) / 2f;
          float bottomY = 15f; //bottom of page is 0, top of page is ~840

          // append the content to the existing stream
          try(PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true)) {
            contentStream.beginText();
            // set font and font size
            contentStream.setFont(font, fontSize);
            // set text color to red
            contentStream.setNonStrokingColor(Color.BLACK);
            if(rotate) {
              // rotate the text according to the page rotation
              contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, centerX, bottomY));
            } else {
              contentStream.setTextMatrix(Matrix.getTranslateInstance(centerX, bottomY));
            }
            contentStream.showText(message);
            contentStream.endText();
          }
        }
        doc.save(out);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}

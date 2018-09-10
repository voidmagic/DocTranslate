package cn.ac.ia.cip;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
/**
 * 将页面文字替换成翻译后的文字，保留页面背景
 *
 * @author jingnianxueren
 */
public class PdfTextProcess
{
    public static void main(String[] args) throws IOException
    {
//        String infile = "example/cn.pdf";
//        String outfile = "example/cn1.pdf";
//        PdfTextProcess rw = new PdfTextProcess();
//        //rw.replace(infile, outfile);
//        rw.removePDFText(infile, outfile);
    }

    public void removePDFText(String infile,String outfile)
    {        
        try(PDDocument document=PDDocument.load(new File(infile)))
        {
            document.setAllSecurityToBeRemoved(true);
            for (PDPage page : document.getPages())
            {
                removePageText(page, document);
            }
            document.save(outfile);
        } catch (IOException ex)
        {
            Logger.getLogger(PdfTextProcess.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void removePageText(PDPage page, PDDocument document) throws IOException
    {
        PDFStreamParser parser = new PDFStreamParser(page);
        parser.parse();
        List<Object> tokens = parser.getTokens();
        List<Object> newTokens = new ArrayList<Object>();
        for (Object token : tokens)
        {
            if (token instanceof Operator)
            {
                String opname = ((Operator) token).getName();
                if ("TJ".equals(opname) || "Tj".equals(opname))
                {
                    // remove the one argument to this operator
                    newTokens.remove(newTokens.size() - 1);
                    continue;
                }
            }
            newTokens.add(token);
        }
        PDStream newContents = new PDStream(document);
        OutputStream out = newContents.createOutputStream(COSName.FLATE_DECODE);
        ContentStreamWriter writer = new ContentStreamWriter(out);
        writer.writeTokens(newTokens);
        out.close();
        page.setContents(newContents);
        processResources(page.getResources());
    }

    private void processResources(PDResources resources) throws IOException
    {
        Iterable<COSName> names = resources.getXObjectNames();
        for (COSName name : names)
        {
            PDXObject xobject = resources.getXObject(name);
            if (xobject instanceof PDFormXObject)
            {
                removeAllText((PDFormXObject) xobject);
            }
        }
    }
    private void removeAllText(PDFormXObject xobject) throws IOException
    {
        PDStream stream = xobject.getContentStream();
        PDFStreamParser parser = new PDFStreamParser(xobject);
        parser.parse();
        List<Object> tokens = parser.getTokens();
        List<Object> newTokens = new ArrayList<Object>();
        for (Object token : tokens)
        {
            if (token instanceof Operator)
            {
                Operator op = (Operator) token;
                if ("TJ".equals(op.getName()) || "Tj".equals(op.getName())
                        || "'".equals(op.getName()) || "\"".equals(op.getName()))
                {
                    // remove the one argument to this operator
                    newTokens.remove(newTokens.size() - 1);
                    continue;
                }
            }
            newTokens.add(token);
        }
        OutputStream out = stream.createOutputStream(COSName.FLATE_DECODE);
        ContentStreamWriter writer = new ContentStreamWriter(out);
        writer.writeTokens(newTokens);
        out.close();
        processResources(xobject.getResources());
    }
    private void addText(String oldFile, String newFile)
    {
        try
        {
            //PDDocument newdoc = new PDDocument();
            PDDocument doc = PDDocument.load(new File(oldFile));
            PDFont font = PDType1Font.HELVETICA_BOLD;
            int count = 0;
            for (PDPage page : doc.getPages())
            {
               
                PDPage newpage = new PDPage(PDRectangle.A4);
                doc.addPage(newpage);
               
                
                PDPageContentStream contentStream = new PDPageContentStream(
                        doc, newpage, AppendMode.APPEND, true, true);
                
                //contentStream.drawForm(form);
                //contentStream.saveGraphicsState();
              
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Go to Document->File Attachments to View Embedded Files");
                contentStream.endText();
                contentStream.close();
            }
            doc.save(newFile);
        } catch (IOException ex)
        {
            Logger.getLogger(PdfTextProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

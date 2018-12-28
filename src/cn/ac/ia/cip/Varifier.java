package cn.ac.ia.cip;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Varifier extends PDFStreamEngine {

    private int imgCount;
    private float imgArea;
    private float imgPosition;

    public Varifier() {
        // preparing PDFStreamEngine
        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());

    }

    public List<Integer> illegalPages(String file) throws IOException {
        PDDocument document = PDDocument.load(new File(file));

        List<Integer> picPageNumbers = new ArrayList<>();
        int totalNumber = document.getNumberOfPages();

        for(int i = 0; i < document.getNumberOfPages(); ++i) {
            PDPage page = document.getPage(i);
            this.imgCount = 0;

            this.processPage(page);
            if (imgCount != 1) continue;

            float pageArea = page.getMediaBox().getHeight() * page.getMediaBox().getWidth();
            if (this.imgPosition < 2.0 && this.imgArea / pageArea > 0.8) {
                picPageNumbers.add(i);
            }
        }

        document.close();
        return picPageNumbers;
    }


    protected void processOperator( Operator operator, List<COSBase>operands) throws IOException {
        String operation = operator.getName();
        if( "Do".equals(operation) ) {
            COSName objectName = (COSName) operands.get( 0 );
            // get the PDF object
            PDXObject xobject = getResources().getXObject( objectName );

            // check if the object is an image object
            if( xobject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject)xobject;
                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                float imageXScale = ctmNew.getScalingFactorX();
                float imageYScale = ctmNew.getScalingFactorY();
                this.imgArea = imageXScale * imageYScale;
                this.imgPosition = ctmNew.getTranslateX() + ctmNew.getTranslateY();
                this.imgCount += 1;
            }
            else if(xobject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject)xobject;
                showForm(form);
            }
        } else {
            super.processOperator( operator, operands );
        }
    }
}

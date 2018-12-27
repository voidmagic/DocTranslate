package cn.ac.ia.cip.reader;

import com.itextpdf.kernel.geom.Rectangle;
import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFTextLocationStripper extends PDFTextStripper {

    private List<LineText> lineTextList;

    public static final String EN = "EN";
    public static final String CN = "CN";
    public static final String JP = "JP";

    // 不是行尾，但是包含终结符的词语，如u.s. mr. 等
    private List<String> specialWord = new ArrayList<>();
    // 可以作为行尾的词语
    private List<String> endChar = new ArrayList<>();

    private String language;


    public PDFTextLocationStripper(String filename, int startPage, int endPage, String language) throws IOException {

        addOperator(new SetStrokingColorSpace());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
        addOperator(new SetStrokingDeviceRGBColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetStrokingDeviceGrayColor());
        addOperator(new SetStrokingColor());
        addOperator(new SetStrokingColorN());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetNonStrokingColorN());

        this.language = language;
        lineTextList = new ArrayList<>();
        this.initEndSymbol();

        PDDocument document = PDDocument.load(new File(filename));
        this.setSortByPosition(true);
        this.setStartPage(startPage);
        this.setEndPage(endPage);
        this.getText(document);
        document.close();
    }

    private void initEndSymbol() {
        switch (this.language) {
            case PDFTextLocationStripper.CN:
                this.endChar.add("。");
                this.endChar.add("：");
                this.endChar.add("；");
                break;
            case PDFTextLocationStripper.EN:
                this.endChar.add(".");
                this.endChar.add(";");
                this.endChar.add(":");
                this.specialWord.add("u.s.");
                break;
            case PDFTextLocationStripper.JP:
                this.endChar.add("。");
                this.endChar.add("：");
                this.endChar.add("；");
                break;
        }
    }


    public List<LineText> getTextWithRectangle() {
        // 行内合并，把字符合并成行
        lineTextList = mergeRectanglesInLine();

        // 删掉无关行
        lineTextList.removeIf(lineText -> escape(lineText.getText()));
        // 行间合并，上下行合并成段落块
        return mergeTextRectangle();
    }

    private TextPosition textPosition;

    @Override
    protected void processTextPosition(TextPosition text) {

        PDColor nonStrokingColor = getGraphicsState().getNonStrokingColor();

        float pageHeight = text.getPageHeight();
        float x = text.getXDirAdj();
        float y = pageHeight - text.getYDirAdj();
        float h = text.getFontSizeInPt();
        float w = text.getWidthDirAdj();

        float t = 0;
        try {
            t = text.getFont().getFontDescriptor().getAscent() - text.getFont().getFontDescriptor().getDescent();
        } catch (NullPointerException ignored) {
        }

        float actualY;
        if (Math.abs(t) < 0.1) {
            actualY = y - h / 4;
        } else {
            actualY = y + (h / t) * text.getFont().getFontDescriptor().getDescent() - 1;
        }

        Rectangle rectangle = new Rectangle(x, actualY, w, h + 3);

        if (this.language.equals(PDFTextLocationStripper.EN)) w = (float) (h * 0.7);

        try {
            lineTextList.add(new LineText(text.getUnicode(), rectangle, y, w, h, nonStrokingColor.toRGB()));
        } catch (IOException e) {
            lineTextList.add(new LineText(text.getUnicode(), rectangle, y, w, h, 0));
        }
    }

    private List<LineText> mergeRectanglesInLine() {
        List<LineText> lineTextBlock = new ArrayList<>();

        if (lineTextList.size() == 0) return lineTextBlock;

        int currentIndex = 0;
        lineTextBlock.add(lineTextList.get(currentIndex));

        for (int i = 1; i < lineTextList.size(); ++i) {
            LineText currentLineText = lineTextBlock.get(currentIndex);
            LineText thisLineText = lineTextList.get(i);

            // 行内合并规则
            // 1. 两个字符的间隔应该小于字符高度（因为宽度会不一样），且后一个字符在前一个字符的右边。
            float marginBoundary = (float) (Math.max(thisLineText.getCharHeightMin(), currentLineText.getCharHeightMin()) * 1.5);
            boolean margin = Math.abs(thisLineText.getLeft() - currentLineText.getRight()) < marginBoundary;
            // 2. 上下有重叠，可以应对上下标的情况
            boolean sameLine = overlapVertical(currentLineText.getRectangle(), thisLineText.getRectangle());
            // 3. 连续的空格应该切分
            boolean consecutiveSpace = false; // (currentLineText.getText().endsWith(" ") || currentLineText.getText().endsWith("　")) && (thisLineText.getText().startsWith(" ") || thisLineText.getText().startsWith("　"));

//            boolean sameColor = currentLineText.getTextColor() == thisLineText.getTextColor();
            if (margin && sameLine && (!consecutiveSpace) /* && sameColor */ ) {
                // 如果间隔过大，手动添加一个空格
                if (Math.abs(thisLineText.getLeft() - currentLineText.getRight()) > Math.max(thisLineText.getCharHeightMin(), currentLineText.getCharHeightMin()) * 0.2)
                    thisLineText.setText(" " + thisLineText.getText());
                // 如果当前是个标点符号或者空格，字体高度设为前一个的字体高度
                if (thisLineText.getText().matches("\\pP|( )|(　)"))
                    thisLineText.setCharHeightMin(currentLineText.getCharHeightMin());
                currentLineText.mergeWith(thisLineText);
            } else {
                lineTextBlock.add(thisLineText);
                currentIndex++;
            }
        }

        return lineTextBlock;
    }

    private List<LineText> mergeTextRectangle() {

        List<LineText> resultLineText = new ArrayList<>();
        if (lineTextList.size() == 0) return resultLineText;

        resultLineText.add(lineTextList.get(0));

        int currentIndex = 0;
        // 开始逐行合并
        for (int i = 1; i < lineTextList.size(); ++i) {
            LineText currentLineText = resultLineText.get(currentIndex);
            LineText thisLineText = lineTextList.get(i);

            // 左右有没有重叠
            boolean overlap = overlapHorizontal(currentLineText.getRectangle(), thisLineText.getRectangle());
            // 字体大小是否相同，如果上下有重叠，则不需要字体大小相同，因为可能出现某个字母占多行的情况
            boolean sameFont = overlap(currentLineText.getCharHeightMin(), currentLineText.getCharHeightMax(), thisLineText.getCharHeightMin(), thisLineText.getCharHeightMax()) || overlapVertical(currentLineText.getRectangle(), thisLineText.getRectangle());
            // 行间距小于字体高，如果上下有重叠同上
            boolean margin = (
                    (currentLineText.getBottom() - thisLineText.getTop() < currentLineText.getLastLineMargin()
                    || currentLineText.getBottom() - thisLineText.getTop() < currentLineText.getCharHeightMax() * 0.5)
                    && (currentLineText.getTop() - thisLineText.getBottom() > -10))
                    || overlapVertical(currentLineText.getRectangle(), thisLineText.getRectangle());
            // 是否到一段的结束，根据句号等规则
            boolean lineEnd = currentLineText.isLineEnd(this.specialWord, this.endChar);
            // 是否应该新行
            boolean newLine = false; // newLine(currentLineText.getLastLineWidth(), thisLineText.getLastLineWidth(), currentLineText.getCharWidth(), thisLineText.getText());
            // 开始为项目标号
            boolean itemStart = thisLineText.getText().startsWith("•") || thisLineText.getText().trim().matches("\\d+(\\.) ([^\\d]+)");

            boolean sameColor = thisLineText.getTextColor() == currentLineText.getTextColor();
            if (overlap && margin && sameFont && !lineEnd && !newLine && !itemStart && sameColor) {
                currentLineText.mergeWithLine(thisLineText);
            } else {
                resultLineText.add(thisLineText);
                currentIndex++;
            }
        }

        return resultLineText;
    }

    /**
     * 根据一行的文字判断这一行是否应该删去
     * @param text 文字
     * @return 是否删去
     */
    private boolean escape(String text) {
        text = text.trim();
        while (text.startsWith("　")) text = text.substring(1, text.length()).trim();
        while (text.endsWith("　")) text = text.substring(0, text.length() - 1).trim();
        return text.equals("\uF06E") || text.length() == 0 || text.matches("\\d+(.)?") || text.equals("•");
    }

    private boolean overlapHorizontal(Rectangle aRect, Rectangle bRect) {
        // 判断两个上下行的矩形是否为同一个block，判断依据：宽度重叠部分。
        float aWidth = aRect.getWidth();
        float bWidth = bRect.getWidth();
        float a = aRect.getX(), b = aRect.getX() + aWidth, c = bRect.getX(), d = bRect.getX() + bWidth;
        float overlap = Math.min(b, d) - Math.max(a, c);
        return overlap / aWidth > 0.3 || overlap / bWidth > 0.3;
    }

    private boolean overlapVertical(Rectangle aRect, Rectangle bRect) {
        // 判断两个上下行的矩形是否为同一个block，判断依据：高度重叠部分。
        float aHeight = aRect.getHeight();
        float bHeight = bRect.getHeight();
        float a = aRect.getY(), b = aRect.getY() + aHeight, c = bRect.getY(), d = bRect.getY() + bHeight;
        float overlap = Math.min(b, d) - Math.max(a, c);
        return overlap / aHeight > 0.3 || overlap / bHeight > 0.3;
    }

    private boolean newLine(float aWidth, float bWidth, float charWidth, String text) {
        // 判断下行的矩形是不是新行，判断依据是下一行比上一行长很多
        // aWidth: 上一行宽度
        // bWidth: 下一行宽度

        if (this.language.equals(PDFTextLocationStripper.CN) || this.language.equals(PDFTextLocationStripper.JP)) {
            return aWidth + 3 * charWidth < bWidth;
        }

        // 考虑英文中因为单词过长而换行
        float append = text.split(" ")[0].length() * charWidth;
        return aWidth + append + 3 * charWidth  < bWidth;
    }

    private boolean overlap(float a, float b, float c, float d) {
        // a~b, c~d
        float overlap = Math.min(b, d) - Math.max(a, c);
        return overlap >= 0;
    }

}

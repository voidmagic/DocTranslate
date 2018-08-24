package cn.ac.ia.cip.reader;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字和对应的位置
 */
public class TextWithRectangle {

    private String text;
    private List<Rectangle> rectangles;
    private PdfFont font;
    private float charSpacing;
    private float singleSpaceWidth;
    private float leading;
    private float fontHeight;
    private float lineBase;

    public TextWithRectangle(String text, Rectangle rectangle) {
        this.text = text;
        this.rectangles = new ArrayList<>();
        this.rectangles.add(rectangle);
    }

    public TextWithRectangle(String text,
                             Rectangle rectangle,
                             PdfFont font,
                             float charSpacing,
                             float singleSpaceWidth,
                             float leading,
                             float fontHeight, float lineBase) {
        this.text = text;
        this.rectangles = new ArrayList<>();
        this.rectangles.add(rectangle);
        this.font = font;
        this.charSpacing = charSpacing;
        this.singleSpaceWidth = singleSpaceWidth;
        this.leading = leading;
        this.fontHeight = fontHeight;
        this.lineBase = lineBase;
    }

    public void mergeWith(TextWithRectangle other) {
        StringBuilder newString = new StringBuilder();
        if (this.text.endsWith("-") && !other.text.startsWith(" ")) {
            // 去掉行尾的连词符
            newString.append(this.text.substring(0, this.text.length()-1)).append(other.text);
        } else if (!this.text.endsWith(" ") && !other.text.startsWith(" ")) {
            // 添加空格
            newString.append(this.text).append(" ").append(other.text);
        } else {
            newString.append(this.text).append(other.text);
        }

        this.text = newString.toString();
        this.rectangles.addAll(other.rectangles);
    }

    public float getCharSpacing() {
        return charSpacing;
    }

    public float getLeading() {
        return leading;
    }

    public float getSingleSpaceWidth() {
        return singleSpaceWidth;
    }

    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    public PdfFont getFont() {
        return font;
    }

    public String getText() {
        return text;
    }

    public float getFontHeight() {
        return fontHeight;
    }

    public float getLineBase() {
        return lineBase;
    }

    private float getTotalLineWidth() {
        float totalWidth = 0;
        for (Rectangle rectangle: rectangles) {
            totalWidth += rectangle.getWidth();
        }
        // 比实际的宽度缩小一点，防止字体过大
        return totalWidth * (float) 0.9;
    }

    public float getEstimateFontSize() {
        float totalWidth = getTotalLineWidth();
        float originWidthWithFont = font.getWidth(getText(), getFontHeight());
        float scale = totalWidth / originWidthWithFont;
        return getFontHeight() * scale;
    }

    public List<String> getSplitTextForEachRectangle() {
        List<String> stringList = new ArrayList<>();
        float fontSize = getEstimateFontSize();
        PdfFont font = getFont();
        String text = this.getText();

        for (Rectangle rectangle : rectangles) {
            StringBuilder builder = new StringBuilder();
            for (char c : text.toCharArray()) {
                if (font.getWidth(builder.toString(), fontSize) > rectangle.getWidth() - fontSize / 2)
                    break;
                builder.append(c);
            }
            String current = builder.toString();
            stringList.add(current);
            text = text.substring(current.length());
        }

        return stringList;
    }

    public float getLeft() {
        float left = 100000;
        for (Rectangle rectangle: rectangles) {
            left = Math.min(left, rectangle.getLeft());
        }
        return left;
    }

    public float getRight() {
        float right = 0;
        for (Rectangle rectangle: rectangles) {
            right = Math.max(right, rectangle.getRight());
        }
        return right;
    }

    public float getBottom() {
        float bottom = 100000;
        for (Rectangle rectangle: rectangles) {
            bottom = Math.min(bottom, rectangle.getBottom());
        }
        return bottom;
    }

    public float getTop() {
        float top = 0;
        for (Rectangle rectangle: rectangles) {
            top = Math.max(top, rectangle.getTop());
        }
        return top;
    }
}



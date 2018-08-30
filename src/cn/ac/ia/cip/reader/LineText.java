package cn.ac.ia.cip.reader;

import com.itextpdf.kernel.geom.Rectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;

import java.util.List;

public class LineText {

    private String text;
    private Rectangle rectangle;
    private float baseLine;

    private float charWidth;
    private float charHeightMin;
    private float charHeightMax;
    private float lastLineWidth;
    private float lastLineMargin;

    private int textColor;

    public LineText(String text, Rectangle rectangle, float baseLine, float charWidth, float charHeight, int color) {
        this.baseLine = baseLine;
        this.text = text;
        this.charWidth = charWidth;
        this.rectangle = rectangle;
        this.charHeightMin = charHeight;
        this.charHeightMax = charHeight;
        this.lastLineWidth = rectangle.getWidth();

        this.lastLineMargin = charHeight * (float) 0.7;

        this.textColor = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public float getBaseLine() {
        return baseLine;
    }

    public void setBaseLine(float baseLine) {
        this.baseLine = baseLine;
    }

    public float getLeft() {
        return rectangle.getLeft();
    }

    public float getRight() {
        return rectangle.getRight();
    }

    public float getWidth() {
        return rectangle.getWidth();
    }

    public float getTop() {
        return rectangle.getTop();
    }

    public float getBottom() {
        return rectangle.getBottom();
    }

    public float getHeight() {
        return rectangle.getHeight();
    }

    public void mergeWith(LineText other) {
        // 行内合并，右边的合并到左边
        this.text += other.getText();
        this.charWidth = Math.max(this.charWidth, other.charWidth);
        this.charHeightMin = Math.min(this.charHeightMin, other.charHeightMin);
        this.charHeightMax = Math.max(this.charHeightMax, other.charHeightMax);

        this.lastLineWidth = rectangle.getWidth();

        float width = Math.max(this.getRight(), other.getRight()) - Math.min(this.getLeft(), other.getLeft());
        float height = Math.max(this.getTop(), other.getTop()) - Math.min(this.getBottom(), other.getBottom());
        this.rectangle.setX(Math.min(this.getLeft(), other.getLeft()));
        this.rectangle.setY(Math.min(this.getBottom(), other.getBottom()));
        this.rectangle.setWidth(width);
        this.rectangle.setHeight(height);

    }

    private void expandRectangle(Rectangle other) {

    }

    public void mergeWithLine(LineText other) {
        // 行间合并，下面的合并到上面
        this.text += other.getText();
        this.charWidth = Math.max(this.charWidth, other.charWidth);
        this.charHeightMin = Math.min(this.charHeightMin, other.charHeightMin);
        this.charHeightMax = Math.max(this.charHeightMax, other.charHeightMax);

        this.lastLineWidth = other.lastLineWidth;
        this.lastLineMargin = Math.abs(this.getBottom() - other.getTop()) * (float) 1.3;

        float width = Math.max(this.getRight(), other.getRight()) - Math.min(this.getLeft(), other.getLeft());
        float height = Math.max(this.getTop(), other.getTop()) - Math.min(this.getBottom(), other.getBottom());
        this.rectangle.setX(Math.min(this.getLeft(), other.getLeft()));
        this.rectangle.setY(Math.min(this.getBottom(), other.getBottom()));
        this.rectangle.setWidth(width);
        this.rectangle.setHeight(height);

    }

    public float getCharWidth() {
        return charWidth;
    }

    public void setCharWidth(float charWidth) {
        this.charWidth = charWidth;
    }

    public float getCharHeightMin() {
        return charHeightMin;
    }

    public void setCharHeightMin(float charHeightMin) {
        this.charHeightMin = charHeightMin;
    }

    public boolean isLineEnd(List<String> specialWord, List<String> endChar) {
        String text = this.text.trim();
        while (text.endsWith("　")) text = text.substring(0, text.length() - 1).trim();

        for (String c: specialWord)
            if (text.toLowerCase().endsWith(c)) return false;

        for (String c: endChar)
            if (text.toLowerCase().endsWith(c)) return true;

        return false;
    }

    public float getLastLineWidth() {
        return lastLineWidth;
    }

    public float getCharHeightMax() {
        return charHeightMax;
    }

    public void setCharHeightMax(float charHeightMax) {
        this.charHeightMax = charHeightMax;
    }


    public float getLastLineMargin() {
        return lastLineMargin;
    }

    public void setLastLineMargin(float lastLineMargin) {
        this.lastLineMargin = lastLineMargin;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}

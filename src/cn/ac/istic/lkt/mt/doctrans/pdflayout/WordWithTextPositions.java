/**
 * 初始代码来自PDFTextStripper的WordWithTextPositions类
 */
package cn.ac.istic.lkt.mt.doctrans.pdflayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.font.PDFont;

import cn.ac.istic.lkt.mt.utils.StringHelper;

/**
 * @author SHI Chongde
 *
 */
/**
 * Internal class that maps strings to lists of {@link TextPosition} arrays. Note that the number of entries in that
 * list may differ from the number of characters in the string due to normalization.
 *
 * @author Axel D枚rfler
 */
public class WordWithTextPositions
{
    String text;
    String trans = "";

	List<TextPosition> textPositions;
	
    PDFont font;
    float fontSize;
    float fontSizeInPt;
    HashMap<PDFont, Integer> fontFreq = new HashMap<PDFont, Integer>();
    HashMap<Float, Integer> fontSizeFreq = new HashMap<Float, Integer>();
    
    float Xstart = 1000000000f;
    float Ystart = 0f;
    float Width  = 0f;
	float Height = 0f;
    
    WordWithTextPositions(String word, List<TextPosition> positions)
    {
    	init(word,positions);
    }
    
    /* 取自WordWithTextPositions(String word, List<TextPosition> positions)，为保证兼容性，不可修改 */
    public void init(String word, List<TextPosition> positions) {
    	text = word;
        textPositions = positions;
        
        assert(positions.size()>0);
        TextPosition firstP = positions.get(0);
        //TextPosition lastP = positions.get(positions.size()-1);
        float xend = 0f;
        float ytop = 1000000000f;
        for(TextPosition tp : positions) {
        	//fontSizeInPt = fontSizeInPt > tp.getFontSizeInPt() ? fontSizeInPt: tp.getFontSizeInPt();
        	fontSizeInPt = fontSizeInPt > tp.getYScale() ? fontSizeInPt: tp.getYScale();
        	Xstart = Xstart < tp.getXDirAdj() ? Xstart : tp.getXDirAdj();
        	//Ystart = Math.max(Ystart, tp.getYDirAdj());
        	Ystart = Ystart > tp.getYDirAdj() ? Ystart : tp.getYDirAdj();
        	xend   = xend > (tp.getXDirAdj()+tp.getWidthDirAdj())  ? xend   : (tp.getXDirAdj()+tp.getWidthDirAdj());
        	//ytop = ytop < (tp.getYDirAdj()-tp.getHeightDir()) ? ytop : (tp.getYDirAdj()-tp.getHeightDir());
        	ytop = ytop < (tp.getYDirAdj()-tp.getYScale()) ? ytop : (tp.getYDirAdj()-tp.getYScale());
        	PDFont f = tp.getFont();
        	if (fontFreq.containsKey(f)) {
        		fontFreq.replace(f, fontFreq.get(f)+1);
        	}else {
        		fontFreq.put(f, 1);
        	}
        	float fs = fontSizeInPt;
        	if (fontSizeFreq.containsKey(fs)) {
        		fontSizeFreq.replace(fs, fontSizeFreq.get(fs)+1);
        	}else {
        		fontSizeFreq.put(fs, 1);
        	}
        }
        //Ystart += 1;
        //Yend -= 1;
        Width = xend - Xstart;
        Height = Ystart - ytop;
        font = positions.get(0).getFont();
        int freq = 0;
        for (Entry<PDFont, Integer> en :fontFreq.entrySet()) {
        	if (freq < en.getValue()) {
        		font = en.getKey();
        		freq = en.getValue();
        	}
        }
        freq = 0;
        for (Entry<Float, Integer> en :fontSizeFreq.entrySet()) {
        	if (freq < en.getValue()) {
        		fontSizeInPt = en.getKey();
        		freq = en.getValue();
        	}
        }
        
    }
    
    
    // 垂直合并
    public boolean appendV(WordWithTextPositions w, float maxWidth) {
    	String currt = text.trim();
		String next = w.getText().trim();
		if (next.length()==0) {
			return true;
		}
		
    	if(currt.length()==0) {
    		init(w.text,w.textPositions);
    		return true;
    	}
    	
    	// 行间距过小
    	if (w.Ystart - this.Ystart < this.getFontSizeInPt() - 2) {
    	//if (w.Ystart - this.Ystart < 0) {
    		System.err.println("WordWithTextPosition::appendV() Failed!\n"
    				+ "\tCurr:"+ this.text + "\tNext:"+ w.text);
    		return false;
    	}
    	
		
		float charw = textPositions.get(textPositions.size()-1).getWidthDirAdj();
		char lastChar = currt.charAt(currt.length()-1);
		char firstChar = next.charAt(0);
		
		/*
		if (lastChar == '.' || lastChar == '。' || StringHelper.isUpCase(firstChar)) {
			next = "\n"+ next;
		}
		*/
		/*
		if ((Xstart+Width - (w.Xstart+w.Width))>(charw*3) ) { // 上一行未完成整行，视为一个段落结束
			next = next+"\n";
		}else if((w.Xstart-Xstart)>2*charw){ // 有缩进
			next = "\n" + next;
		}else if (Width < 0.9*maxWidth) {
			next = "\n" + next;
		}else if (w.Width < 0.9*maxWidth) {
			next = next + "\n";
		}*/
		
		
		if (StringHelper.isEnglish(firstChar)) { // 英语加空格
			if (lastChar == '-') {
				currt = currt.substring(0, currt.length()-1);					
			}else {
				next = " "+ next;
			}
		}
		
		text = (currt + next).replaceAll("\n\n", "\n");
		this.textPositions.addAll(w.textPositions);
		for(TextPosition tp : w.textPositions) {
			PDFont f = tp.getFont();
			if (fontFreq.containsKey(f)) {
        		fontFreq.replace(f, fontFreq.get(f)+1);
        	}else {
        		fontFreq.put(f, 1);
        	}
			float fs = tp.getYScale();
        	if (fontSizeFreq.containsKey(fs)) {
        		fontSizeFreq.replace(fs, fontSizeFreq.get(fs)+1);
        	}else {
        		fontSizeFreq.put(fs, 1);
        	}
		}
		int freq = 0;
		for (Entry<PDFont, Integer> en :fontFreq.entrySet()) {
			if (freq < en.getValue()) {
        		font = en.getKey();
        		freq = en.getValue();
        	}
        }
		freq = 0;
        for (Entry<Float, Integer> en :fontSizeFreq.entrySet()) {
        	if (freq < en.getValue()) {
        		fontSizeInPt = en.getKey();
        		freq = en.getValue();
        	}
        }
		float ytop  = Ystart - Height;
		float ytopw = w.Ystart - w.Height;
			
		Width  = Math.max(Xstart+Width, w.Xstart+w.Width) - Math.min(Xstart, w.Xstart);
		Xstart = Xstart < w.Xstart ? Xstart: w.Xstart;
		Ystart = Ystart > w.Ystart ? Ystart: w.Ystart;			
		//Height = Height > w.Height ? Height: w.Height;
		Height = Ystart - Math.min(ytop, ytopw);
		return true;
    }
    
    // 水平合并
    public boolean appendH(WordWithTextPositions w) {
    	String currt = text.trim();
		String next = w.getText().trim();
		if (next.length()==0) {
			return true;
		}
		
    	if(currt.length()==0) {
    		init(w.text,w.textPositions);
    		return true;
    	}
    	
    	// 不在一行？
    	if (this.getYstart()-w.getYstart()>2){
    		return false;
    	}
    	
		TextPosition lastp = textPositions.get(textPositions.size()-1);
		//String lasts = lastp.getUnicode().trim();
		//char lastc = lasts.charAt(lasts.length()-1);
		//float charw = lastp.getWidthDirAdj();
		float spacewidth = textPositions.get(0).getWidthOfSpace();
		// 间隔过大，不认为应该合并到一个字符串里
		/*
		 * 调用端已经处理
		if ((w.Xstart - (Xstart+Width))>(spacewidth*3) ) { 
			return false;
		}*/
		
		text = currt + " "+ next;
		this.textPositions.addAll(w.textPositions);
		for(TextPosition tp : w.textPositions) {
			PDFont f = tp.getFont();
			if (fontFreq.containsKey(f)) {
        		fontFreq.replace(f, fontFreq.get(f)+1);
        	}else {
        		fontFreq.put(f, 1);
        	}
			float fs = tp.getYScale();
        	if (fontSizeFreq.containsKey(fs)) {
        		fontSizeFreq.replace(fs, fontSizeFreq.get(fs)+1);
        	}else {
        		fontSizeFreq.put(fs, 1);
        	}
		}
		int freq = 0;
		for (Entry<PDFont, Integer> en :fontFreq.entrySet()) {
			if (freq < en.getValue()) {
        		font = en.getKey();
        		freq = en.getValue();
        	}
        }
		freq = 0;
        for (Entry<Float, Integer> en :fontSizeFreq.entrySet()) {
        	if (freq < en.getValue()) {
        		fontSizeInPt = en.getKey();
        		freq = en.getValue();
        	}
        }
		float ytop  = Ystart - Height;
		float ytopw = w.Ystart - w.Height;
			
		Width  = Math.max(Xstart+Width, w.Xstart+w.Width) - Math.min(Xstart, w.Xstart);
		Xstart = Xstart < w.Xstart ? Xstart: w.Xstart;
		Ystart = Ystart > w.Ystart ? Ystart: w.Ystart;			
		//Height = Height > w.Height ? Height: w.Height;
		Height = Ystart - Math.min(ytop, ytopw);	
		return true;
	}
    
    public String getColorInfo() {
    	TextPosition first = textPositions.get(0);
    	if (textPositions.size()==1) {
    		return first.getColorInfo();
    	}
    	TextPosition last = textPositions.get(textPositions.size()-1);
    	if (first.isSameColor(last)) {
    		return first.getColorInfo();
    	}else {
    		return last.getColorInfo();   // 不太准确，为了省事
    	}
    }
    
    public String getTrans() {
		return trans;
	}

	public void setTrans(String trans) {
		this.trans = trans;
	}
	
    public String getText()
    {
        return text;
    }

    public List<TextPosition> getTextPositions()
    {
        return textPositions;
    }
    public PDFont getFont() {
		return font;
	}

	public float getFontSizeInPt() {
		return fontSizeInPt;
	}

	public float getXstart() {
		return Xstart;
	}

	public float getYstart() {
		return Ystart;
	}
	
	public float getWidth() {
		return Width;
	}

	public float getHeight() {
		return Height;
	}

    public String toString() {
    	return "[WordWithTextPositions]: " + text + "\t<" + Xstart + ", " + Ystart
    			+ ", " + Width +", " + Height +"> <" + font.getName() +", " + fontSize + ", "
    			+ fontSizeInPt + "> "+ textPositions.size()+ " " + getColorInfo();
    }
    
}

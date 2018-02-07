package cn.ac.istic.lkt.mt.doctrans.pdflayout;

import java.util.List;

public class WordWithTextPositionsCN extends WordWithTextPositions{
	public WordWithTextPositionsCN(String word, List<TextPosition> positions) {
		super(word, positions);
	}

	@Override
	public boolean appendV(WordWithTextPositions w, float maxWidth) {
		// 待完善
		return true;
	}
	
	@Override
	public boolean appendH(WordWithTextPositions w) {
		// 待完善
		return true;
	}
}

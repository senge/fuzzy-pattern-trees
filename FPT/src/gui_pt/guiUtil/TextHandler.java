package gui_pt.guiUtil;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

public class TextHandler {
	
	private String text;
	private Color color;
	private int fontSize;
	
	public TextHandler(String text, Color color, int fontSize){
		
		this.text = text;
		this.color = color;
		this.fontSize = fontSize;
	}
	
	public static MutableAttributeSet setTextStyle(MutableAttributeSet aSet, Color color, int fontSize){
		StyleConstants.setForeground(aSet, color);
		StyleConstants.setFontSize(aSet, fontSize);
		
		return aSet;
	}
	
	public static void appendText(JTextPane textPane, String text, AttributeSet set) throws BadLocationException{
		
		textPane.getDocument().insertString(
				textPane.getDocument().getLength(), text, set);
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
}

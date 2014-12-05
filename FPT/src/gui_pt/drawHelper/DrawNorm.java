package gui_pt.drawHelper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.Serializable;

public class DrawNorm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4929493688354754890L;
	
	public static final int STRING = 0;
	public static final int SYMBOL = 1;
	public static final int CUSTOM = 2;
	
	public static final int TNORM = 10;
	public static final int TCONORM = 11; 
	public static final int AVERAGENORM = 12; 
	
	private int normRep = DrawNorm.STRING;
	private String customText = "";
	private Color normColor = Color.white;
	private Font normFont;
	private Image normImage;
	
	public DrawNorm(){}
	
	public DrawNorm(int normRep, Color normColor, Font normFont, Image normIamge)
	{
		this.normRep = normRep;
		this.normColor = normColor;
		this.normFont = normFont;
		this.normImage = normImage;
	}
	
	// GET and SET #######################################################################

	public int getNormRep() {
		return normRep;
	}

	public void setNormRep(int normRep) {
		this.normRep = normRep;
	}

	public Color getNormColor() {
		return normColor;
	}

	public void setNormColor(Color normColor) {
		this.normColor = normColor;
	}

	public Font getNormFont() {
		return normFont;
	}

	public void setNormFont(Font normFont) {
		this.normFont = normFont;
	}

	public Image getNormImage() {
		return normImage;
	}

	public void setNormImage(Image normImage) {
		this.normImage = normImage;
	}

	public String getCustomText() {
		return customText;
	}

	public void setCustomText(String customText) {
		this.customText = customText;
	}

}

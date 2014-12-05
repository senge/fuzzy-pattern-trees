package gui_pt.io;

import gui_pt.drawHelper.DrawNorm;
import gui_pt.drawObjects.DrawDetail;
import gui_pt.drawObjects.Triangle;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;

public class Settings implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1997852045087309933L;
	
	public double leafShapeSize = 60;
	public double leafDistance = 40;
	public double layerDistance = 100;
	
	public boolean nodeMidEdge = true;
	public boolean showDetails = true;
	
	public TreeSet<Integer> detailsToShow = new TreeSet<Integer>();
	public Integer[] allDetails = {DrawDetail.ATTRIBUTE_NAME, DrawDetail.FUZZYSET, DrawDetail.PERFORMANCE, DrawDetail.OUTPUT};
	
	public DrawNorm tNormDraw = new DrawNorm(DrawNorm.STRING, new Color(255,150,150), new Font("Dialog.plain", 0, 12), null);
	public DrawNorm tCoNormDraw = new DrawNorm(DrawNorm.STRING, new Color(150,255,150), new Font("Dialog.plain", 0, 12), null);
	public DrawNorm averageDraw = new DrawNorm(DrawNorm.STRING, new Color(255,255,83), new Font("Dialog.plain", 0, 12), null);
	
	public DrawNorm leafDraw = new DrawNorm(DrawNorm.SYMBOL, new Color(215,235,255), new Font("Dialog.plain", 0, 12), null);
	
	public int maxLength;
	
	public boolean alaising = true;
	public boolean edgeRect = false;
	public boolean paperView = false;
	
	public double scaleFactorX = 1;
	public double scaleFactorY = 1;
	
	public int offsetX;
	public int offsetY;
	public int transX = 0;
	public int transY = 0;
	
	public ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	public AffineTransform transformation = new AffineTransform();
	
	public Settings(){
		
		for(int i=0; i<allDetails.length; i++)
		{
			detailsToShow.add(allDetails[i]);
		}
	}

}

package gui_pt.drawObjects;

import gui_pt.DefaultPTV.MouseListenerAction;
import gui_pt.gui.DrawPanel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class DrawDetail implements MouseListenerAction{
	
	public static final int ATTRIBUTE_NAME	= 0;
	public static final int FUZZYSET 		= 1;
	public static final int PERFORMANCE 	= 2;
	public static final int OUTPUT 			= 3;
	
	private DetailValue 			value;
	private int						type;
	
	private Point2D 				location;
	private final int 				defaultX;
	private final int 				defaultY;
	private AffineTransform 		transformation = new AffineTransform();
	private Color					color = Color.black;
	
	//mouselistener stuff
	int mouseX1;
	int mouseY1;
	int mouseX2;
	int mouseY2;
	
	//################################################################################################
	// Constructor
	//################################################################################################
	
	public DrawDetail(Point2D location, int x, int y, int type){
		
		this.location = location;
		this.defaultX = x;
		this.defaultY = y;
		
		this.type = type;
	}
	
	//################################################################################################
	// Methods
	//################################################################################################
	
	public String toString(){
		
		return value.detailToString();
	}
	
	public int getX(){
		
		return (int)(location.getX()+defaultX+transformation.getTranslateX());
	}
	
	public int getY(){
		
		return (int)(location.getY()+defaultY+transformation.getTranslateY());
	}
	
	//################################################################################################
	// Methods
	//################################################################################################

	@Override
	public void action(MouseEvent me, int eventType, Object stuff) {
		
		DrawPanel dp = (DrawPanel)stuff;
		
		if(eventType == MouseEvent.MOUSE_PRESSED)
		{			
			mouseX1 = me.getX();
			mouseY1 = me.getY();
			dp.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}
		else if(eventType == MouseEvent.MOUSE_DRAGGED)
		{
			mouseX2 = me.getX();
			mouseY2 = me.getY();
			
			double scaledX = (mouseX2-mouseX1)/dp.getSettings().scaleFactorX;
			double scaledY = (mouseY2-mouseY1)/dp.getSettings().scaleFactorY;
			
			this.transformation.translate(scaledX, scaledY);
			
			mouseX1 = mouseX2;
			mouseY1 = mouseY2;
		}
		
	}
	
	public void setToDefault()
	{
		transformation.setToIdentity();
	}
	
	//################################################################################################
	// GET and SET
	//################################################################################################

	public AffineTransform getTransformation() {
		return transformation;
	}

	public void setTransformation(AffineTransform transformation) {
		this.transformation = transformation;
	}

	public DetailValue getValue() {
		return value;
	}

	public void setValue(DetailValue value) {
		this.value = value;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getType() {
		return type;
	}


}

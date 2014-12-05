package gui_pt.DefaultPTV;

import gui_pt.drawObjects.DrawNode;
import gui_pt.gui.DrawPanel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class NodeInteraction {
	
	private DrawPanel dp;
	private DrawNode dn;
	
	private int x = -20;
	private int y = -20;
	private int width = 20;
	private int height = 20;
	
	private ArrayList<InteractionArea> ia_List = new ArrayList<InteractionArea>();
	
	//##################################################################################################
	//Constructor
	//##################################################################################################
	
	public NodeInteraction(){
		
		InteractionArea ia_0 = new InteractionMoveNode();
		ia_0.setId(0);
		
		InteractionArea ia_2 = new InteractionEditFS();
		ia_2.setId(2);
		
		InteractionArea ia_6 = new InteractionExpand();
		ia_6.setId(6);
		
		ia_List.add(ia_0);
		ia_List.add(ia_2);
		ia_List.add(ia_6);
	}
	
	//##################################################################################################
	// Methode
	//##################################################################################################
		
	public void paintNodeInteraction(Graphics2D g2D){
		
		if(dn != null)
		{
			//Transformation t-t-t-t-t
			g2D.setTransform(dp.getSettings().transformation);
			g2D.transform(dp.getDrawTree().getLayerTrans().get(dn.getLayer()));
			g2D.transform(dn.getTransform());
		
			for(InteractionArea ia: ia_List)
			{
				ia.paintArea(g2D);
			}		
		}		
	}
	
	public abstract class InteractionArea implements MouseListenerAction{
		
		int id;
		
		public abstract void paintArea(Graphics2D g2D);

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}		
	}
	
	public class InteractionMoveNode extends InteractionArea{

		int mouseX1;
		int mouseX2;
		int mouseY1;
		int mouseY2;
		
		boolean isPressed = false;
		
		@Override
		public void action(MouseEvent me, int eventType, Object stuff) {
			
			
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
				
				dn.getTransform().translate(scaledX, scaledY);
				
				mouseX1 = mouseX2;
				mouseY1 = mouseY2;
			}
		}

		@Override
		public void paintArea(Graphics2D g2D) {
			
			int xStart = x+(int)dn.getLocation().getX() 
						+ (dn.getWidth()+2*width)/3*(this.getId()%3)
						+ ((dn.getWidth()+2*width)/3-20)*(this.getId()%3)/2;
			int yStart = y+(int)dn.getLocation().getY() 
						+ (dn.getHeight()+2*height)/3*(this.getId()/3)
						+ ((dn.getHeight()+2*height)/3-20)*(this.getId()/3)/2;
			
			Image image = dp.getToolkit().getImage("res/icons/move.PNG");
			
			g2D.drawImage(image
					, xStart
					, yStart
					, 21
					, 21
					, dp);
		}		
	}
	
	public class InteractionEditFS extends InteractionArea{

		@Override
		public void action(MouseEvent me, int eventType, Object stuff) {
			
			if(eventType == MouseEvent.MOUSE_PRESSED)
			{
				if(dn.getType()== DrawNode.LEAF)
				{					
					FSE_PostEditorDialog fse_PED = new FSE_PostEditorDialog(dp.getdPTV().getOwner(), dn);
					
					fse_PED.setLocation(me.getX(), me.getY());
					fse_PED.setSize(800,500);
					fse_PED.setVisible(true);
				}
				else
				{
					PostAGGRSelectDialog pasd = new PostAGGRSelectDialog(dp.getdPTV().getOwner(), dn, dp);
					pasd.setLocation(me.getX(), me.getY());
					pasd.setSize(150,300);
					pasd.setVisible(true);
				}
			}				
		}

		@Override
		public void paintArea(Graphics2D g2D) {
			
			int xStart = x+(int)dn.getLocation().getX() 
						+ (dn.getWidth()+2*width)/3*(this.getId()%3)
						+ ((dn.getWidth()+2*width)/3-20)*(this.getId()%3)/2;
			int yStart = y+(int)dn.getLocation().getY() 
						+ (dn.getHeight()+2*height)/3*(this.getId()/3)
						+ ((dn.getHeight()+2*height)/3-20)*(this.getId()/3)/2;
			
			Image image = dp.getToolkit().getImage("res/icons/bauhelm.PNG");
			
			g2D.drawImage(image
					, xStart
					, yStart
					, 21
					, 21
					, dp);			
		}		
	}
	
	public class InteractionExpand extends InteractionArea{

		@Override
		public void action(MouseEvent me, int eventType, Object stuff) {
			
			if(eventType == MouseEvent.MOUSE_PRESSED)
			{
				if(dn.getType() == DrawNode.INNER_NODE)
				{
					dn.expand(!dn.isExpendedRoot());
				}				
			}
		}

		@Override
		public void paintArea(Graphics2D g2D) {

			int xStart = x+(int)dn.getLocation().getX() 
						+ (dn.getWidth()+2*width)/3*(this.getId()%3)
						+ ((dn.getWidth()+2*width)/3-20)*(this.getId()%3)/2;
			int yStart = y+(int)dn.getLocation().getY() 
						+ (dn.getHeight()+2*height)/3*(this.getId()/3)
						+ ((dn.getHeight()+2*height)/3-20)*(this.getId()/3)/2;
			
			int[] pX = {xStart+10
					, xStart
					, xStart+20};
			int[] pY = {yStart
					,yStart+20
					,yStart+20};
			
			Polygon polygon = new Polygon(pX,pY,3);
			
			g2D.setColor(Color.blue);
			g2D.fillPolygon(polygon);
			g2D.setColor(Color.black);
			g2D.drawPolygon(polygon);		
		}	
	}
	
	//###################################################################################################
	// GET and SET
	//###################################################################################################

	public DrawPanel getDp() {
		return dp;
	}

	public void setDp(DrawPanel dp) {
		this.dp = dp;
	}

	public DrawNode getDn() {
		return dn;
	}

	public void setDn(DrawNode dn) {
		this.dn = dn;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public ArrayList<InteractionArea> getIa_List() {
		return ia_List;
	}

	public void setIa_List(ArrayList<InteractionArea> ia_List) {
		this.ia_List = ia_List;
	}

}

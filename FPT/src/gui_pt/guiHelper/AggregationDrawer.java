package gui_pt.guiHelper;

import gui_pt.drawObjects.DrawNode;

import java.awt.Graphics2D;

public class AggregationDrawer {
	
	public static void drawTnorm(DrawNode dn, Graphics2D g2D){
		
		g2D.drawLine((int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0)
				, (int)dn.getLocation().getY()+8
				, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0)
				, (int)dn.getLocation().getY()+dn.getHeight()-4);
		g2D.drawLine((int)dn.getLocation().getX()+6
				, (int)dn.getLocation().getY()+8
				, (int)dn.getLocation().getX()+(dn.getWidth()-6)
				, (int)dn.getLocation().getY()+8);
	}
	
	public static void drawTConorm(DrawNode dn, Graphics2D g2D){
		
		g2D.drawLine((int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0)
				, (int)dn.getLocation().getY()+4
				, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0)
				, (int)dn.getLocation().getY()+dn.getHeight()-8);
		g2D.drawLine((int)dn.getLocation().getX()+6
				, (int)dn.getLocation().getY()+dn.getHeight()-8
				, (int)dn.getLocation().getX()+(dn.getWidth()-6)
				, (int)dn.getLocation().getY()+dn.getHeight()-8);		
	}
	
	public static void drawAverage(DrawNode dn, Graphics2D g2D){
		
		g2D.drawLine((int)dn.getLocation().getX()+8
				, (int)dn.getLocation().getY()+6
				, (int)dn.getLocation().getX()+(dn.getWidth()-8)
				, (int)dn.getLocation().getY()+6);
		g2D.drawLine((int)dn.getLocation().getX()+8
				, (int)dn.getLocation().getY()+6
				, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0)
				, (int)dn.getLocation().getY()+(int)(dn.getHeight()/2.0));
		g2D.drawLine((int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0)
				, (int)dn.getLocation().getY()+(int)(dn.getHeight()/2.0)
				, (int)dn.getLocation().getX()+8
				, (int)dn.getLocation().getY()+dn.getHeight()-6);
		g2D.drawLine((int)dn.getLocation().getX()+8
				, (int)dn.getLocation().getY()+dn.getHeight()-6
				,(int)dn.getLocation().getX()+(dn.getWidth()-8)
				, (int)dn.getLocation().getY()+dn.getHeight()-6);		
	}

}

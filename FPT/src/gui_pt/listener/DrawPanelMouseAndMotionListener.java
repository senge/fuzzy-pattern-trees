package gui_pt.listener;

import gui_pt.DefaultPTV.NodeInteraction;
import gui_pt.drawObjects.DrawDetail;
import gui_pt.drawObjects.DrawNode;
import gui_pt.drawObjects.Triangle;
import gui_pt.gui.DrawPanel;
import gui_pt.gui.DrawPanelPopupMenu;
import gui_pt.util.PropertiesTrader;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

public class DrawPanelMouseAndMotionListener implements MouseListener, MouseMotionListener{

	
	DrawPanel dp;
	Triangle triangle;
	boolean pressed = false;
	boolean button1 = false;
	boolean button2 = false;
	boolean button3 = false;
	boolean button4 = false;
	
	boolean ddFound = false;
	boolean dnFound = false;
	DrawDetail foundDD = null;
	
	int startX = 0;
	int startY = 0;
	
	public DrawPanelMouseAndMotionListener(DrawPanel dp)
	{
		this.dp = dp;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * 
	 */
	public void mousePressed(MouseEvent arg0) {

		//reTransform Cursor
		Point2D p2D = new Point2D.Double(arg0.getX(),arg0.getY());
		
		try {
			dp.getTransformation().inverseTransform(p2D, p2D);
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(arg0.getButton() == MouseEvent.BUTTON3)
		{			
			DrawPanelPopupMenu.getInstence(arg0.getXOnScreen(), arg0.getYOnScreen(), dp);
		}
		else if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			if(dp.getCursor().getName().equals("move"))
			{
				Cursor c = dp.getToolkit().createCustomCursor(
						new ImageIcon(PropertiesTrader.getProperties().getProperty("IconPath") + "closedhand2.png").getImage(),
						new Point(16,16), "moveNow");
				this.dp.setCursor(c);
				
				this.dp.refreshOffsetX(arg0.getX());
				this.dp.refreshOffsetY(arg0.getY());
			}
			else if(dp.getCursor().getType() == Cursor.DEFAULT_CURSOR)
			{				
				Point2D transLoc;				
				boolean oneSelected = false;
				for(DrawNode dn: dp.getDrawTree().getTree())
				{
					transLoc = new Point2D.Double(dn.getLocation().getX()
													,dn.getLocation().getY());
					dn.getTransform().transform(transLoc, transLoc);
					dp.getDrawTree().getLayerTrans()
						.get(dn.getLayer()).transform(transLoc, transLoc);
					
					if(p2D.getX() > transLoc.getX()
                          && p2D.getX() < transLoc.getX()+dn.getWidth()
                          && p2D.getY() > transLoc.getY()
                          && p2D.getY() < transLoc.getY()+dn.getHeight())
					{
						dp.setSelectedNodeID(dn.getId());
						dp.getTcp().getAp().buildAnalyseFrame(dn.getAccNode());
						
						dp.repaint();
						
						oneSelected = true;
					}
				}
				if(!oneSelected)
				{
					dp.setSelectedNodeID(0);
					dp.repaint();
				}
				
				/** LayerControl ####################################################**/
			
				if(this.triangle != null)
				{
					Point2D pT2D1;
					pT2D1 = new Point2D.Double(this.triangle.getX2()
							,this.triangle.getY2());
					this.triangle.getTransform().transform(pT2D1, pT2D1);
					
					//Y-Scale Ausgleich
					int yScaleToTrans = (int)(triangle.getTransform().getTranslateY()
							*dp.getTransformation().getScaleY());
					yScaleToTrans = yScaleToTrans - (int)triangle.getTransform().getTranslateY();
					yScaleToTrans += dp.getTransformation().getTranslateY();

					
					if(arg0.getY()-yScaleToTrans > pT2D1.getY()-16
							&& arg0.getY()-yScaleToTrans < pT2D1.getY()-6
							&& arg0.getX() > pT2D1.getX()-20
							&& arg0.getX() < pT2D1.getX())
					{					
						if(triangle.isFixed())
						{
							triangle.setFixed(false);
							triangle.setColor(Color.LIGHT_GRAY);
						}
						else
						{
							triangle.setFixed(true);
							triangle.setColor(Color.red);
						}
					}
					else if(arg0.getY()-yScaleToTrans > pT2D1.getY()-6
							&& arg0.getY()-yScaleToTrans < pT2D1.getY()+6
							&& arg0.getX() > pT2D1.getX()-20
							&& arg0.getX() < pT2D1.getX())
					{					
						this.pressed = true;
						this.button2 = true;
					} 
					else if(arg0.getY()-yScaleToTrans > pT2D1.getY()+6
							&& arg0.getY()-yScaleToTrans < pT2D1.getY()+16
							&& arg0.getX() > pT2D1.getX()-20
							&& arg0.getX() < pT2D1.getX()-10)
					{					
						this.pressed = true;
						this.button3 = true;
						this.startX = arg0.getX();
						this.startY = arg0.getY();
					}
					else if(arg0.getY()-yScaleToTrans > pT2D1.getY()+6
							&& arg0.getY()-yScaleToTrans < pT2D1.getY()+16
							&& arg0.getX() > pT2D1.getX()-10
							&& arg0.getX() < pT2D1.getX())
					{					
						this.pressed = true;
						this.button4 = true;
						this.startX = arg0.getX();
						this.startY = arg0.getY();
					}
				}
				
				/** NodeInteraction **/
				if(dp.getnInteraction().getDn() != null)
				{					
					DrawNode dn = dp.getnInteraction().getDn();
					NodeInteraction ni = dp.getnInteraction();
				
					transLoc = new Point2D.Double(dn.getLocation().getX()
							,dn.getLocation().getY());
					dn.getTransform().transform(transLoc, transLoc);
					dp.getDrawTree().getLayerTrans()
							.get(dn.getLayer()).transform(transLoc, transLoc);
					
					for(NodeInteraction.InteractionArea ia: ni.getIa_List())
					{
						int x = ni.getX()+(int)transLoc.getX() + (dn.getWidth()+2*ni.getWidth())/3*(ia.getId()%3);
						int y = ni.getY()+(int)transLoc.getY() + (dn.getHeight()+2*ni.getHeight())/3*(ia.getId()/3);					
						
						if(p2D.getX() > x
								&& p2D.getX() < x + (dn.getWidth()+2*ni.getWidth())/3
								&& p2D.getY() > y 
					 	&& p2D.getY() < y +(dn.getHeight()+2*ni.getHeight())/3)
						{
							ia.action(arg0, MouseEvent.MOUSE_PRESSED, null);
						}
					}				
				}
			}			
			else if(foundDD != null)
			{
				foundDD.action(arg0, MouseEvent.MOUSE_PRESSED, dp);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			if(dp.getCursor().getName().equals("moveNow"))
			{
				Cursor c = dp.getToolkit().createCustomCursor(
						new ImageIcon(PropertiesTrader.getProperties().getProperty("IconPath") + "openhand2.png").getImage(),
						new Point(16,16), "move");
				this.dp.setCursor(c);
				
				this.dp.setOffsetX(-arg0.getX()+this.dp.getOffsetX());
				this.dp.setOffsetY(-arg0.getY()+this.dp.getOffsetY());
			}
			else if(dp.getCursor().getType() == Cursor.MOVE_CURSOR)
			{
				dp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}		
		this.pressed = false;
		this.button1 = false;
		this.button2 = false;
		this.button3 = false;
		this.button4 = false;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		if(dp.getCursor().getName().equals("moveNow"))
		{
			dp.setTransX(arg0.getX() - dp.getOffsetX());
			dp.setTransY(arg0.getY() - dp.getOffsetY());
			
			dp.repaint();
		}
		else if(dp.getCursor().getType() == Cursor.MOVE_CURSOR)
		{
			/** NodeInteraction **/
			if(dp.getnInteraction().getDn() != null)
			{
				dp.getnInteraction().getIa_List().get(0).action(arg0, MouseEvent.MOUSE_DRAGGED, null);
			}
			else if(foundDD != null) //Details
			{
				foundDD.action(arg0, MouseEvent.MOUSE_DRAGGED, dp);
			}
		}
	
		
		/** Layer-Control **/
		if(this.button2)
		{
			//transform Triangle
			this.triangle.getTransform().setToTranslation(0
				, (arg0.getY()-(this.dp.getTransY()+45))/this.dp.getScaleFactorY());
			
		}
		else if(this.button3)
		{			
			for(int i= this.triangle.getLayer(); i<dp.getDrawTree().getNumLayer(); i++)
			{
				//transform Triangle				
				dp.getTriangles().get(i).getTransform().translate(0
				, (arg0.getY()-this.startY)/dp.getTransformation().getScaleY());
			}
			this.startY = arg0.getY();
		}
		else if(this.button4)
		{
			AffineTransform transform = new AffineTransform();
			
			for(int i= this.triangle.getLayer(); i<dp.getDrawTree().getNumLayer(); i++)
			{
				//transform Triangle
				transform.translate(0, (arg0.getY()-this.startY)/dp.getTransformation().getScaleY());
				
				dp.getTriangles().get(i).getTransform().translate(0
				, transform.getTranslateY());
			}
			this.startY = arg0.getY();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

		Point2D pT2D1;
				
		if(this.triangle == null)
		{			
			for(int i=0; i<this.dp.getTriangles().size(); i++)
			{
				pT2D1 = new Point2D.Double(this.dp.getTriangles().get(i).getX2()
								,this.dp.getTriangles().get(i).getY2());
				this.dp.getTriangles().get(i).getTransform().transform(pT2D1, pT2D1);
				
				//Y-Scale Ausgleich
				int yScaleToTrans = (int)(dp.getDrawTree().getLayerTrans().get(i).getTranslateY()
						*dp.getTransformation().getScaleY());
				yScaleToTrans = yScaleToTrans - (int)dp.getDrawTree().getLayerTrans().get(i).getTranslateY();
				yScaleToTrans += dp.getTransformation().getTranslateY();
								
				if(arg0.getY()-yScaleToTrans > pT2D1.getY()-3
						&& arg0.getY()-yScaleToTrans < pT2D1.getY()+3
						&& arg0.getX() > pT2D1.getX()-20
						&& arg0.getX() < pT2D1.getX())
				{
					this.triangle = this.dp.getTriangles().get(i);
					triangle.setSelected(true);
				}
			}
		}
		else
		{	
			pT2D1 = new Point2D.Double(this.triangle.getX2()
					,this.triangle.getY2());
			this.triangle.getTransform().transform(pT2D1, pT2D1);
			
			//Y-Scale Ausgleich
			int yScaleToTrans = (int)(triangle.getTransform().getTranslateY()
					*dp.getTransformation().getScaleY());
			yScaleToTrans = yScaleToTrans - (int)triangle.getTransform().getTranslateY();
			yScaleToTrans += dp.getTransformation().getTranslateY();
			
			if((arg0.getY()-yScaleToTrans < pT2D1.getY()-17
					|| arg0.getY()-yScaleToTrans > pT2D1.getY()+17
					|| arg0.getX() < pT2D1.getX()-20
					|| arg0.getX() > pT2D1.getX())
					&& !this.pressed)
			{
				triangle.setSelected(false);
				triangle = null;
			}
		}		
		
		//reTransform Cursor
		Point2D p2D = new Point2D.Double(arg0.getX(),arg0.getY());
		
		try {
			dp.getTransformation().inverseTransform(p2D, p2D);
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Point2D transLoc;

		// Details ------------------------------------------------------------------
		
		if(!dnFound && dp.getCursor().getType() == Cursor.DEFAULT_CURSOR || dp.getCursor().getType() == Cursor.MOVE_CURSOR)
		{
			ddFound = false;
			foundDD = null;
			for(DrawNode dn: dp.getDrawTree().getTree())
			{					
				for(DrawDetail dd: dn.getDetails())
				{
					transLoc = new Point2D.Double(dd.getX()
									,dd.getY());
					dn.getTransform().transform(transLoc, transLoc);
					dp.getDrawTree().getLayerTrans()
						.get(dn.getLayer()).transform(transLoc, transLoc);
					
					if(p2D.getX() > transLoc.getX()			
			                  && p2D.getX() < transLoc.getX()+50
			                  && p2D.getY() > transLoc.getY()-15
			                  && p2D.getY() < transLoc.getY())
					{
						dp.setCursor(new Cursor(Cursor.MOVE_CURSOR));
						ddFound = true;
						foundDD = dd;
						break;
					}
				}
				if(ddFound) break;
			}
			if(dp.getCursor().getType()== Cursor.MOVE_CURSOR && !ddFound)
			{
				dp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
		// Details ende -------------------------------------------------------------
		
		//Node ToolTip---------------------------------------------------------------		
				
		if(!ddFound && dp.getCursor().getType() == Cursor.DEFAULT_CURSOR)
		{
			dnFound = false;
			if(dp.getnInteraction().getDn() == null)
			{
				for(DrawNode dn: dp.getDrawTree().getTree())
				{
					if(dn.isExpended())
					{
						transLoc = new Point2D.Double(dn.getLocation().getX()
														,dn.getLocation().getY());
						dn.getTransform().transform(transLoc, transLoc);
						dp.getDrawTree().getLayerTrans()
							.get(dn.getLayer()).transform(transLoc, transLoc);
						
						if(p2D.getX() > transLoc.getX()
			                  && p2D.getX() < transLoc.getX()+dn.getWidth()
			                  && p2D.getY() > transLoc.getY()
			                  && p2D.getY() < transLoc.getY()+dn.getHeight())
						{
							dp.getnInteraction().setDn(dn);
							dnFound = true;
						}
					}
				}
			}
			else
			{
				transLoc = new Point2D.Double(dp.getnInteraction().getDn().getLocation().getX()
						,dp.getnInteraction().getDn().getLocation().getY());
				dp.getnInteraction().getDn().getTransform().transform(transLoc, transLoc);
				dp.getDrawTree().getLayerTrans()
				.get(dp.getnInteraction().getDn().getLayer()).transform(transLoc, transLoc);
				
				if(p2D.getX() > transLoc.getX() - dp.getnInteraction().getWidth()
				&& p2D.getX() < transLoc.getX()+dp.getnInteraction().getDn().getWidth() + dp.getnInteraction().getWidth()
				&& p2D.getY() > transLoc.getY() - dp.getnInteraction().getHeight()
				&& p2D.getY() < transLoc.getY()+dp.getnInteraction().getDn().getHeight() + dp.getnInteraction().getHeight())
				{	
					dnFound = true;
				}
			}	
			if(!dnFound) dp.getnInteraction().setDn(null);
		}
	}
}

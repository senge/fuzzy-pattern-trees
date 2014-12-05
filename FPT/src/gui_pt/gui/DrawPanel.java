package gui_pt.gui;

import gui_pt.DefaultPTV.NodeInteraction;
import gui_pt.accessLayer.util.AccessNode;
import gui_pt.drawHelper.DrawNorm;
import gui_pt.drawHelper.DrawTree;
import gui_pt.drawHelper.DrawTreeBuilder;
import gui_pt.drawHelper.TreeClassPack;
import gui_pt.drawObjects.DrawDetail;
import gui_pt.drawObjects.DrawNode;
import gui_pt.drawObjects.Triangle;
import gui_pt.drawUtil.TransformationStack;
import gui_pt.guiHelper.AggregationDrawer;
import gui_pt.guiHelper.FuzzySetDrawer;
import gui_pt.io.Settings;
import gui_pt.listener.DrawPanelMouseAndMotionListener;
import gui_pt.stream.StreamView;
import gui_pt.visualisation.DefaultPTV;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.RepaintManager;

public class DrawPanel extends JPanel implements Printable, Runnable, ComponentListener{
	
	private DefaultPTV dPTV;

	private StreamView sv = null;
	private boolean streamMode = false;
	
	public int ID;	//TESTER
	
	private TreeClassPack tcp;
	private AccessNode accRoot;
	private DrawTree drawTree;

	private Image dbImage;					//double-buffer image
	private Graphics2D dbG2D;
	private int countL = 0;
	private float phase;
	
	//settings
	private Settings settings;
	
	Image imageLCB1;
	Image imageLCB2;
	Image imageLCB3;
	Image imageLCB4;
	
	NodeInteraction nInteraction;
	
	int selectedNodeID;
	
	private final AffineTransform identity = new AffineTransform();
	
	
	//#######################################################################################
	// CONSTRUCTOR ##########################################################################
	//#######################################################################################

	public DrawPanel(Settings settings, DrawTree drawTree, int index, TreeClassPack tcp)
	{
		this.accRoot = tcp.getAccessPT().getAccessTrees()[index];
		this.tcp = tcp;
		this.drawTree = drawTree;
		this.drawTree.setTcp(tcp);
		this.selectedNodeID = 0;
		
		this.settings = settings;
		nInteraction = new NodeInteraction();
		this.nInteraction.setDp(this);
	
		updateDrawTree();
		
		this.imageLCB1 = this.getToolkit().createImage("res/icons/square.png");
		this.imageLCB2 = this.getToolkit().createImage("res/icons/balken.png");
		this.imageLCB3 = this.getToolkit().createImage("res/icons/dreieck.png");
		this.imageLCB4 = this.getToolkit().createImage("res/icons/dreieck2.png");
			
		DrawPanelMouseAndMotionListener dpm 
				= new DrawPanelMouseAndMotionListener(this);
		
		this.addMouseListener(dpm);
		this.addMouseMotionListener(dpm);
		
		this.addComponentListener(this);
		
		this.setDoubleBuffered(false);
		this.setBackground(Color.white);
		this.setVisible(true);
		
		Thread th = new Thread(this);
		th.start();
		
	}
	
	//########################################################################################
	// METHODS ###############################################################################
	//########################################################################################
	public void updateDrawTree(){
		
		//clear old stuff
		this.drawTree.clearDrawTree();
		DrawTreeBuilder dtb = new DrawTreeBuilder();
		
		dtb.buildDrawTree(accRoot, this.drawTree);
		this.calcLayerNew();
		dtb.setNodeLocations(this.drawTree, this);		
	}
	
	public void calcLayerNew(){
		
		this.drawTree.getLayerTrans().clear();
		DrawTreeBuilder.calcLayerLocations(this.drawTree, this.settings.layerDistance);
		this.settings.triangles.clear();
		
		for(int i=0; i<this.drawTree.getNumLayer(); i++)
		{
			settings.triangles.add(new Triangle(5
					, -5
					, 20
					, 0
					, 5
					, +5
					, 0
					, 0));
			settings.triangles.get(i).setTransform(this.drawTree.getLayerTrans().get(i));
			settings.triangles.get(i).setLayer(i);
		}
	}
	
	/**
	 * PAINTCOMPONENT
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		if((sv != null
				&& sv.synRepaintUpdate(true))
			|| !streamMode)
		{
			//upcast
			Graphics2D graphic = (Graphics2D)g;
					
			this.setBackground(Color.white);
			
			// Antialiasing
			if(settings.alaising)
			{
				graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING
					, RenderingHints.VALUE_ANTIALIAS_ON );
			}
			
			this.identity.setTransform(graphic.getTransform());
			settings.transformation.setTransform(this.identity);
			//centering
				//Tree Width
			int treeWidth = (int)(accRoot.getLeafs()*settings.leafShapeSize
								+accRoot.getLeafs()*settings.leafDistance);
			
			settings.transformation.translate(10+this.getWidth()/2-treeWidth/2,50);
			settings.transformation.translate(settings.transX, settings.transY);
			settings.transformation.scale(settings.scaleFactorX, settings.scaleFactorY);
				
			//TreeInfo
			this.paintTreeInfo(graphic);
			//StreamInfo
			if(this.sv != null)
			{
				this.paintStreamInfo(graphic);
			}		
			//Draw Tree
			this.paintDrawTree(graphic);
			//DrawLayerControler
			graphic.setTransform(this.identity);
			this.paintLayerControler(graphic);
			//Draw SubtreeControler
			graphic.setTransform(this.identity);
			this.paintSubtreeControler(graphic);
			//Draw NodeInteraction
			graphic.setTransform(this.identity);
			nInteraction.paintNodeInteraction(graphic);
			
			//Layer Line
			for(int i=0; i<drawTree.getNumLayer(); i++)
			{			
				if(this.settings.triangles.get(i).isSelected()
						|| this.settings.triangles.get(i).isFixed())				
				{
					graphic.setTransform(this.identity);				
					graphic.translate(0, settings.transformation.getTranslateY());
					graphic.scale(1, settings.transformation.getScaleY());
					graphic.transform(drawTree.getLayerTrans().get(i));
					
					float[] f = {3.0f,3.0f};
					BasicStroke s = new BasicStroke(1f,0,0, 1, f, 0);
					graphic.setStroke(s);
					graphic.drawLine(20, 0, this.getWidth(), 0);
				}
			}
			if(streamMode) sv.synRepaintUpdate(false);
		}	
	}
	//####################################################################################
	/**
	 * 
	 */
	public void calcMaxStringLength()
	{
		Graphics g = this.getGraphics();
		
		this.settings.maxLength = Integer.MIN_VALUE;
		
		for(DrawNode dn: this.drawTree.getTree())
		{
			if(dn.getType() == DrawNode.INNER_NODE)
			{
				if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TNORM)
				{					
					g.setFont(this.settings.tNormDraw.getNormFont());
				}
				else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TCONORM)
				{
					g.setFont(this.settings.tCoNormDraw.getNormFont());
				}
				else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.AVERAGE)
				{
					g.setFont(this.settings.averageDraw.getNormFont());
				}
				
				String aggrName = dn.getAggr();
				FontMetrics fm = g.getFontMetrics();
				int aNlength = fm.stringWidth(aggrName);
				
				if(this.settings.maxLength < aNlength)
				{
					this.settings.maxLength = aNlength;
				}
			}
		}
	}
	
	//#####################################################################
	/**
	 * 
	 * @param size
	 */
	public void resizeINodes(int size)
	{
		for(DrawNode dn: this.drawTree.getTree())
		{
			if(dn.getType() == DrawNode.INNER_NODE)
			{
				dn.setWidth(size);
				dn.setHeight(size);
			}
		}
		DrawTreeBuilder dtb = new DrawTreeBuilder();
		dtb.setNodeLocations(this.drawTree, this);
	}
	
	// PaintMethods #######################################################
	
	/**
	 * DrawTree is painted here
	 * 
	 * 
	 */
	public void paintDrawTree(Graphics2D graphic)
	{			
		for(DrawNode dn: drawTree.getTree())
		{
			if(dn.isExpended() && dn.isExpendedRoot())
			{
	//			//Draw Edge .....................................................
					graphic.setTransform(settings.transformation);
					
					Point2D pointParent = new Point2D.Double();
					AffineTransform transP = new AffineTransform(
							drawTree.getLayerTrans().get(dn.getLayer()));
					transP.concatenate(dn.getTransform());
					transP.transform(dn.getLocation(), pointParent);
					
					//Edge-Color
					graphic.setColor(Color.black);
					
					if(settings.edgeRect)
					{								
						if(dn.getChildren()[0] != null)
						{
							Point2D pointC0 = new Point2D.Double();
							AffineTransform trans = new AffineTransform(
									drawTree.getLayerTrans().get(dn.getLayer()+1));
							trans.concatenate(dn.getChildren()[0].getTransform());
							trans.transform(dn.getChildren()[0].getLocation(), pointC0);
							
							//parent bottom center
							int pBC = (int)pointParent.getX()
								       + dn.getWidth()/2;
							//child top center
							int cTC = (int)pointC0.getX()
				                         + dn.getChildren()[0].getWidth()/2;
							
							// parent to knickPunkt
							graphic.drawLine(pBC
											, (int)pointParent.getY()+dn.getHeight()							
											, cTC
											, (int)pointParent.getY()+dn.getHeight());
												
							// knickPunkt to child
							graphic.drawLine(cTC
									, (int)pointParent.getY()+dn.getHeight()							
									, cTC
									, (int)pointC0.getY());
						}
						if(dn.getChildren()[1] != null)
						{
							Point2D pointC1 = new Point2D.Double();
							AffineTransform trans = new AffineTransform(
									drawTree.getLayerTrans().get(dn.getLayer()+1));
							trans.concatenate(dn.getChildren()[1].getTransform());
							trans.transform(dn.getChildren()[1].getLocation(), pointC1);
							
							//parent bottom center
							int pBC = (int)pointParent.getX()
								       + dn.getWidth()/2;
							//child top center
							int cTC = (int)pointC1.getX()
				                         + dn.getChildren()[1].getWidth()/2;
							
							// parent to knickPunkt
							graphic.drawLine(pBC
											, (int)pointParent.getY()+dn.getHeight()
											, cTC
											, (int)pointParent.getY()+dn.getHeight());
		//					
							// knickPunkt to child
							graphic.drawLine(cTC
									, (int)pointParent.getY()+dn.getHeight()
									, cTC
									,(int) pointC1.getY());
		              }
					}
					else
					{
						if(dn.getChildren()[0] != null)
						{
							Point2D pointC0 = new Point2D.Double();
							AffineTransform trans = new AffineTransform(
									drawTree.getLayerTrans().get(dn.getLayer()+1));
							trans.concatenate(dn.getChildren()[0].getTransform());
							trans.transform(dn.getChildren()[0].getLocation(), pointC0);
							
							//parent bottom center
							int pBC = (int)pointParent.getX()
								       + dn.getWidth()/2;
							//child top center
							int cTC = (int)pointC0.getX()
				                         + dn.getChildren()[0].getWidth()/2;
							graphic.drawLine(pBC
											, (int)pointParent.getY()+dn.getHeight()
											, cTC
											, (int)pointC0.getY());
						}
						if(dn.getChildren()[1] != null)
						{
							Point2D pointC1 = new Point2D.Double();
							AffineTransform trans = new AffineTransform(
									drawTree.getLayerTrans().get(dn.getLayer()+1));
							trans.concatenate(dn.getChildren()[1].getTransform());
							trans.transform(dn.getChildren()[1].getLocation(), pointC1);
							
							//parent bottom center
							int pBC = (int)pointParent.getX()
								       + dn.getWidth()/2;
							//child top center
							int cTC = (int)pointC1.getX()
				                         + dn.getChildren()[1].getWidth()/2;
							graphic.drawLine(pBC
											, (int)pointParent.getY()+dn.getHeight()
											, cTC
											, (int)pointC1.getY());                                               
						}
					}
					graphic.transform(drawTree.getLayerTrans().get(dn.getLayer()));
					graphic.transform(dn.getTransform());
					
					if(dn.getId() == selectedNodeID)
					{
						float[] f = {3.0f,3.0f};
						BasicStroke s = new BasicStroke(1f,0,0, 1, f, phase);
						graphic.setStroke(s);
						graphic.drawRect((int)dn.getLocation().getX()-2
		                        , (int)dn.getLocation().getY()-2
		                        , dn.getWidth()+4
		                        , dn.getHeight()+4);
						s = new BasicStroke(1f,0,0);
						graphic.setStroke(s);
					}
				
				//Draw edges end :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				
				//Draw Nodes ................................................................
			
				//Transformation t-t-t-t-t
				graphic.setTransform(settings.transformation);
				graphic.transform(drawTree.getLayerTrans().get(dn.getLayer()));
				graphic.transform(dn.getTransform());
							
				if(dn.getType() == DrawNode.LEAF)
				{				
					graphic.setColor(this.settings.leafDraw.getNormColor());
					graphic.fillRect((int)dn.getLocation().getX()
			                         , (int)dn.getLocation().getY()
			                         , dn.getWidth()
			                         , dn.getHeight());
					graphic.setColor(Color.black);
					graphic.drawRect((int)dn.getLocation().getX()
			                         , (int)dn.getLocation().getY()
			                         , dn.getWidth()
			                         , dn.getHeight());
					
	
					//DRAW FuzzySet
					FuzzySetDrawer.drawCPLFS(dn, graphic, 1.0);
						
					graphic.setFont(this.settings.leafDraw.getNormFont());
//					FontMetrics fm = graphic.getFontMetrics();
//					int aNHeight = fm.getAscent();				
					graphic.setColor(Color.black);
//					TreeMap<String,StringBuffer> details = dn.detailsToString();
				}
				else if(dn.getType() == DrawNode.INNER_NODE)
				{
					if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TNORM)
					{
						graphic.setColor(this.settings.tNormDraw.getNormColor());
					}
					else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TCONORM)
					{
						graphic.setColor(this.settings.tCoNormDraw.getNormColor());
					}
					else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.AVERAGE)
					{
						graphic.setColor(this.settings.averageDraw.getNormColor());
					}
					
					graphic.fillOval((int)dn.getLocation().getX()
			                         , (int)dn.getLocation().getY()
			                         , dn.getWidth()
			                         , dn.getHeight());
					graphic.setColor(Color.black);
					
					if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TNORM)
					{
						if(this.settings.tNormDraw.getNormRep() == DrawNorm.SYMBOL)
						{
							AggregationDrawer.drawTnorm(dn, graphic);
						}
						else if(this.settings.tNormDraw.getNormRep() == DrawNorm.STRING)
						{
							graphic.setFont(settings.tNormDraw.getNormFont());
							String[] helpSplit = dn.getAggr().split("_");
							String aggrName = helpSplit[helpSplit.length-1];
							FontMetrics fm = graphic.getFontMetrics();
							int aNlength = fm.stringWidth(aggrName);
							int aNHeight = fm.getAscent();
						
							graphic.drawString(aggrName
									, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0-aNlength/2.0)
					                , (int)dn.getLocation().getY()+(int)(dn.getHeight()/2.0+aNHeight/2.0));
	
						}
						else if(this.settings.tNormDraw.getNormRep() == DrawNorm.CUSTOM)
						{
							graphic.setFont(settings.tNormDraw.getNormFont());
							FontMetrics fm = graphic.getFontMetrics();
							
							graphic.drawImage(settings.tNormDraw.getNormImage()
									, (int)dn.getLocation().getX()
									, (int)dn.getLocation().getY()
									, dn.getWidth()
									, dn.getHeight()
									, this);
							
							String[] sRows = this.settings.tNormDraw.getCustomText().toString().split("\n");
							int aNHeight = sRows.length*fm.getAscent();
							int defHeight = fm.getAscent();
							
							for(int i=0; i< sRows.length; i++)
							{
								int aNlength = fm.stringWidth(sRows[i]);
												
								graphic.drawString(sRows[i]
									, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0-aNlength/2.0)
					                , (int)dn.getLocation().getY()+(int)(dn.getHeight()/2.0+aNHeight/2.0-(sRows.length-(i+1))*defHeight));
							}						
						}					
					}
					else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TCONORM)
					{
						if(this.settings.tCoNormDraw.getNormRep() == DrawNorm.SYMBOL)
						{
							AggregationDrawer.drawTConorm(dn, graphic);
						}
						else if(this.settings.tCoNormDraw.getNormRep() == DrawNorm.STRING)
						{
							graphic.setFont(settings.tCoNormDraw.getNormFont());
							String[] helpSplit = dn.getAggr().split("_");
							String aggrName = helpSplit[helpSplit.length-1];
							FontMetrics fm = graphic.getFontMetrics();
							int aNlength = fm.stringWidth(aggrName);
							int aNHeight = fm.getAscent();
							
							graphic.drawString(aggrName
									, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0-aNlength/2.0)
					                , (int)dn.getLocation().getY()+(int)(dn.getHeight()/2.0+aNHeight/2.0));
						}
						else if(this.settings.tCoNormDraw.getNormRep() == DrawNorm.CUSTOM)
						{
							graphic.setFont(settings.tCoNormDraw.getNormFont());
							FontMetrics fm = graphic.getFontMetrics();
							
							graphic.drawImage(settings.tCoNormDraw.getNormImage()
									,(int) dn.getLocation().getX()
									, (int)dn.getLocation().getY()
									, dn.getWidth()
									, dn.getHeight()
									, this);
							
							String[] sRows = this.settings.tCoNormDraw.getCustomText().toString().split("\n");
							int aNHeight = sRows.length*fm.getAscent();
							int defHeight = fm.getAscent();
							
							for(int i=0; i< sRows.length; i++)
							{
								int aNlength = fm.stringWidth(sRows[i]);
												
								graphic.drawString(sRows[i]
									, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0-aNlength/2.0)
					                , (int)dn.getLocation().getY()+(int)(dn.getHeight()/2.0+aNHeight/2.0-(sRows.length-(i+1))*defHeight));
							}					
						}
					}
					else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.AVERAGE)
					{
						if(this.settings.averageDraw.getNormRep() == DrawNorm.SYMBOL)
						{
							AggregationDrawer.drawAverage(dn, graphic);
						}
						else if(this.settings.averageDraw.getNormRep() == DrawNorm.STRING)
						{
							graphic.setFont(settings.averageDraw.getNormFont());
							String[] helpSplit = dn.getAggr().split("_");
							String aggrName = helpSplit[helpSplit.length-1];
							FontMetrics fm = graphic.getFontMetrics();
							int aNlength = fm.stringWidth(aggrName);
							int aNHeight = fm.getAscent();
							
							graphic.drawString(aggrName
									, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0-aNlength/2.0)
					                , (int)dn.getLocation().getY()+(int)(dn.getHeight()/2.0+aNHeight/2.0));
						}
						else if(this.settings.averageDraw.getNormRep() == DrawNorm.CUSTOM)
						{
							graphic.setFont(settings.averageDraw.getNormFont());
							FontMetrics fm = graphic.getFontMetrics();
							
							graphic.drawImage(settings.averageDraw.getNormImage()
									, (int)dn.getLocation().getX()
									, (int)dn.getLocation().getY()
									, dn.getWidth()
									, dn.getHeight()
									, this);
							
							String[] sRows = this.settings.averageDraw.getCustomText().toString().split("\n");
							int aNHeight = sRows.length*fm.getAscent();
							int defHeight = fm.getAscent();
							
							for(int i=0; i< sRows.length; i++)
							{
								int aNlength = fm.stringWidth(sRows[i]);
												
								graphic.drawString(sRows[i]
									, (int)dn.getLocation().getX()+(int)(dn.getWidth()/2.0-aNlength/2.0)
					                , (int)dn.getLocation().getY()+(int)(dn.getHeight()/2.0+aNHeight/2.0-(sRows.length-(i+1))*defHeight));
							}						
						}
					}
					//paint Circle
					graphic.setColor(Color.black);
					graphic.drawOval((int)dn.getLocation().getX()
			                         , (int)dn.getLocation().getY()
			                         , dn.getWidth()
			                         , dn.getHeight());
				}
				
				//DrawDetails -----------------------------------------
				
				if(settings.showDetails)
				{
					for(DrawDetail dd: dn.getDetails())
					{
							graphic.setColor(dd.getColor());
							graphic.drawString(dd.toString()
								, dd.getX()
		                        , dd.getY());
					}
				}
				else
				{
					for(DrawDetail dd: dn.getDetails())
					{
						if(settings.detailsToShow.contains(dd.getType()))
						{
							graphic.setColor(dd.getColor());
							graphic.drawString(dd.toString()
								, dd.getX()
		                        , dd.getY());
						}					
					}
				}
				//DrawDetails end -----------------------------------------
				
			}//if expended end
			if(!dn.isExpendedRoot() && dn.isExpended())
			{
				//Transformation t-t-t-t-t
				graphic.setTransform(settings.transformation);
				graphic.transform(drawTree.getLayerTrans().get(dn.getLayer()));
				graphic.transform(dn.getTransform());
				
				int[] pX = {(int)(dn.getLocation().getX()+dn.getWidth()/2)
						,(int)(dn.getLocation().getX()+dn.getWidth())
						,(int)(dn.getLocation().getX())};
				int[] pY = {(int)(dn.getLocation().getY())
						,(int)(dn.getLocation().getY()+dn.getHeight())
						,(int)(dn.getLocation().getY()+dn.getHeight())};
				
				Polygon polygon = new Polygon(pX,pY,3);
				
				graphic.setColor(Color.blue);
				graphic.fillPolygon(polygon);
				graphic.setColor(Color.black);
				graphic.drawPolygon(polygon);
				
				if(dn.getId() == selectedNodeID)
				{
					float[] f = {3.0f,3.0f};
					BasicStroke s = new BasicStroke(1f,0,0, 1, f, phase);
					graphic.setStroke(s);
					graphic.drawRect((int)dn.getLocation().getX()-2
	                        , (int)dn.getLocation().getY()-2
	                        , dn.getWidth()+4
	                        , dn.getHeight()+4);
					s = new BasicStroke(1f,0,0);
					graphic.setStroke(s);
				}
			}
		}		
	}
	//###################################################################################
	
	
	/**
	 * 
	 * @param graphic
	 */
	public void paintSubtreeControler(Graphics2D graphic)
	{		
		graphic.setColor(new Color(240,240,255));
		graphic.fillRect(20, 0, this.getWidth(), 20);
		graphic.drawRect(20, 0, this.getWidth(), 20);
		graphic.setColor(Color.black);
		
		for(int i=1; i< this.getWidth()/10; i++)
		{
			int width = 5;
			if(i%5 == 0)
			{
				width = 10;
			}
			graphic.drawLine(20+i*10
					, 20-width
					, 20+i*10
					, 20);
		}
		
		//upperleft Edge
		graphic.setColor(new Color(240,240,255));
		graphic.fillRect(0, 0, 20, 20);
		graphic.setColor(Color.gray);
		graphic.drawRect(0, 0, 20, 20);
	}
	//####################################################################################
	/**
	 * 
	 */
	public void paintLayerControler(Graphics2D graphics)
	{
		TransformationStack transStack = new TransformationStack();
		
		graphics.setColor(new Color(240,240,255));
		graphics.fillRect(0, 20, 20, this.getHeight());
		graphics.drawRect(0, 20, 20, this.getHeight());
		graphics.setColor(Color.black);
		
		for(int i=1; i< this.getHeight()/10; i++)
		{
			int width = 5;
			if(i%5 == 0)
			{
				width = 10;
			}
			graphics.drawLine(20-width
					, 20+i*10
					, 20
					, 20+i*10);
		}
		
		transStack.push(AffineTransform.getTranslateInstance(this.identity.getTranslateX()
				, this.settings.transformation.getTranslateY()));
		
		Triangle selectedTri = null;
		int helpYScaleToTrans = 0;
		for(int i=0; i<this.drawTree.getNumLayer(); i++)
		{ 
			int yScaleToTrans = (int)(this.drawTree.getLayerTrans().get(i).getTranslateY()
										*this.settings.transformation.getScaleY());
			yScaleToTrans = yScaleToTrans - (int)this.drawTree.getLayerTrans().get(i).getTranslateY();
		
			transStack.push(settings.triangles.get(i).getTransform());
			transStack.push(AffineTransform.getTranslateInstance(0
					, yScaleToTrans));
			
			graphics.setTransform(transStack.pop());
		
			graphics.setColor(settings.triangles.get(i).getColor());
			graphics.fillPolygon(settings.triangles.get(i).getPolygon());
			graphics.setColor(Color.black);
			graphics.drawPolygon(settings.triangles.get(i).getPolygon());
			
			if(settings.triangles.get(i).isSelected())
			{
				selectedTri = settings.triangles.get(i);
				helpYScaleToTrans = yScaleToTrans;
			}			
			transStack.pop();
		}
		if(selectedTri != null)
		{
			transStack.push(selectedTri.getTransform());
			transStack.push(AffineTransform.getTranslateInstance(0
					, helpYScaleToTrans));

			graphics.setTransform(transStack.pop());
			this.paintLCButtons(graphics, selectedTri);

			transStack.pop();
		}

		transStack.pop();
		graphics.setTransform(transStack.peek());
	}
	//####################################################################################
	/**
	 * 
	 * @param graphic
	 * @param tri
	 */
	public void paintLCButtons(Graphics2D graphic, Triangle tri)
	{
//		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
//		AlphaComposite ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
//		
//		graphic.setComposite(ac);

		graphic.drawImage(this.imageLCB1
				,tri.getButton1()[0]
				,tri.getButton1()[1]
				,tri.getButton1()[2]
				,tri.getButton1()[3]
				,this);
		graphic.drawImage(this.imageLCB2
				,tri.getButton2()[0]
				,tri.getButton2()[1]
				,tri.getButton2()[2]
				,tri.getButton2()[3]
				,this);
		graphic.drawImage(this.imageLCB3
				,tri.getButton3()[0]
				,tri.getButton3()[1]
				,tri.getButton3()[2]
				,tri.getButton3()[3]
				,this);
		graphic.drawImage(this.imageLCB4
				,tri.getButton4()[0]
				,tri.getButton4()[1]
				,tri.getButton4()[2]
				,tri.getButton4()[3]
				,this);
//		graphic.setComposite(ac2);
	}
	
	/**
	 * 
	 * @param graphic
	 */
	private void paintStreamInfo(Graphics2D graphic){
		
		graphic.setColor(Color.black);
		graphic.drawString("STREAM-MODE: "
				, 30
				, 55);
		graphic.drawString("SELECTED TREE: "
				, 30
				, 70);
		
		graphic.setColor(Color.red);
		graphic.drawString(""+this.sv.getUpdateCount()
		, 140
		, 55);
		graphic.drawString(""+this.dPTV.getWatchedIndex()
		, 140
		, 70);
		
	}
	
	private void paintTreeInfo(Graphics2D graphic){
		
		graphic.setColor(Color.black);
		graphic.drawString("CLASS: "
				, 30
				, 40);
		graphic.setColor(Color.blue);
		graphic.drawString(""+this.drawTree.getClassName()
				, 80
				, 40);
		
	}
	//####################################################################################
	/**
	 * 
	 */
	public void update(Graphics g)
	{
		//initialize Double-Buffer
		if(dbImage == null)
		{
			dbImage = createImage(this.getWidth()
					, this.getHeight());
			
			Graphics graphic = dbImage.getGraphics();
			dbG2D = (Graphics2D)graphic;
		}
		
		//Delete Background
		dbG2D.setColor(this.getBackground());
		dbG2D.fillRect(0
				,0
				,this.getWidth()
				,this.getHeight());
		
		//Draw Foreground
		dbG2D.setColor(this.getForeground());

		paintComponent(dbG2D);
		g.drawImage(dbImage,0,0,this);

	}
	


	@Override
	public int print(Graphics arg0, PageFormat arg1, int arg2)
			throws PrinterException {
				
		int x = (int)arg1.getImageableX() + 1;
		int y = (int)arg1.getImageableY() + 1;
		arg0.translate(x,y);
		RepaintManager currentManager = RepaintManager.currentManager(this);
		currentManager.setDoubleBufferingEnabled(false);
		this.paintForPrint(arg0);
		currentManager.setDoubleBufferingEnabled(true);
		
		return PAGE_EXISTS;
	}
	
	public void paintForPrint(Graphics g)
	{
		//upcast
		Graphics2D graphic = (Graphics2D)g;
		
		// Antialiasing
		if(settings.alaising)
		{
			graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING
				, RenderingHints.VALUE_ANTIALIAS_ON );
		}
		
		settings.transformation.setToIdentity();
		//centering
			//Tree Width
		int treeWidth = (int)(accRoot.getLeafs()*settings.leafShapeSize
							+accRoot.getLeafs()*settings.leafDistance);
		settings.transformation.translate(this.getWidth()/2-treeWidth/2,0);
		settings.transformation.translate(settings.transX, settings.transY);
		settings.transformation.scale(settings.scaleFactorX, settings.scaleFactorY);
				
		//Draw Tree
		this.paintDrawTree(graphic);
	}
	//##################################################################################
	
	private int[] calcBB()
	{
		int[] bb = new int[4];
		
		bb[0] = Integer.MAX_VALUE;
		bb[1] = Integer.MIN_VALUE;
		bb[2] = Integer.MAX_VALUE;
		bb[3] = Integer.MIN_VALUE;
		
		for(DrawNode dn: drawTree.getTree())
		{	
			Point2D p2D = new Point2D.Double();
			
			int yOffset = 300;
			
			dn.getTransform().transform(dn.getLocation(), p2D);
			drawTree.getLayerTrans().get(dn.getLayer()).transform(p2D, p2D);
							
			if(bb[0] > p2D.getX()) 									bb[0] = (int)p2D.getX();
			if(bb[1] < ((int)p2D.getX()+dn.getWidth())) 			bb[1] = (int)p2D.getX()+dn.getWidth();
			if(bb[2] > (yOffset-(int)p2D.getY())) 					bb[2] = (yOffset-(int)p2D.getY());
			if(bb[3] < (yOffset-((int)p2D.getY()+dn.getHeight()))) 	bb[3] = (yOffset-((int)p2D.getY()+dn.getHeight()));
			
			Point2D p2DD = new Point2D.Double();
			dn.getTransform().transform(new Point2D.Double(0,0), p2DD);
			drawTree.getLayerTrans().get(dn.getLayer()).transform(p2DD, p2DD);
			int fontsize = settings.tNormDraw.getNormFont().getSize();
	
			for(DrawDetail dd: dn.getDetails())
			{
				if(bb[0] > p2DD.getX()+dd.getX()) 						bb[0] = (int)p2DD.getX()+dd.getX();
				if(bb[1] < (int)(p2DD.getX()+dd.getX() + fontsize))	bb[1] = (int)(p2DD.getX()+dd.getX() + fontsize);
				if(bb[2] > (yOffset-((int)(p2DD.getY()+dd.getY()+1))))	bb[2] = (yOffset-((int)(p2DD.getY()+dd.getY()+1)));
				if(bb[3] < (yOffset-((int)(p2DD.getY()+dd.getY()-fontsize))))	bb[3] = (yOffset-((int)(p2DD.getY()+dd.getY()-fontsize)));
			}
			
		}	
		return bb;
	}
	
	/**
	 * 
	 */
	public String toLatex(double scaleFactor)
	{		
		int[] bb = calcBB();
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\\documentclass{article}\n");
		sb.append("\\usepackage{pst-all}\n");
		sb.append("\\usepackage{pst-pdf}\n");
		sb.append("\\begin{document}\n");
		sb.append("%###################################################\n");
		sb.append("%Here the Tree can be scaled \n");
		sb.append("\\psscalebox{1.0}{\n");
		sb.append("%###################################################\n");
		sb.append("\\psset{xunit=1pt,yunit=1pt,runit=1pt}\n");
		sb.append("%###################################################\n");
		sb.append("%Here the picture can be cliped \n");
		sb.append("\\begin{pspicture}(" + bb[0] + "," + bb[2] + ")(" + bb[1] + "," + bb[3] + ")\n");
		sb.append("%###################################################\n");
		
		float[] rgb = new float[3];
		
		sb.append("%Here you can change the colors\n");
		this.settings.leafDraw.getNormColor().getRGBColorComponents(rgb);
		sb.append("\\newrgbcolor{leafcolor}{"+rgb[0]+" "+rgb[1]+" "+rgb[2]+" }\n");
		
		this.settings.tNormDraw.getNormColor().getRGBColorComponents(rgb);
		sb.append("\\newrgbcolor{tnormcolor}{"+rgb[0]+" "+rgb[1]+" "+rgb[2]+"}\n");

		this.settings.tCoNormDraw.getNormColor().getRGBColorComponents(rgb);
		sb.append("\\newrgbcolor{tconormcolor}{"+rgb[0]+" "+rgb[1]+" "+rgb[2]+"}\n");
		
		this.settings.averageDraw.getNormColor().getRGBColorComponents(rgb);
		sb.append("\\newrgbcolor{averagecolor}{"+rgb[0]+" "+rgb[1]+" "+rgb[2]+"}\n");
		
		Color color = new Color(160, 190, 255);
		color.getRGBColorComponents(rgb);
		sb.append("\\newrgbcolor{fuzzysetfillcolor}{"+rgb[0]+" "+rgb[1]+" "+rgb[2]+"}\n");
		
		color = new Color(0,0,255);
		color.getRGBColorComponents(rgb);
		sb.append("\\newrgbcolor{fuzzysetlinecolor}{"+rgb[0]+" "+rgb[1]+" "+rgb[2]+"}\n");
				
		for(DrawNode dn: drawTree.getTree())
		{															
			Point2D p2D = new Point2D.Double();
			
			int yOffset = 300;
			
			dn.getTransform().transform(dn.getLocation(), p2D);
			drawTree.getLayerTrans().get(dn.getLayer()).transform(p2D, p2D);
			
			//Here the Leafs are painted
			if(dn.getType() == DrawNode.LEAF)
			{				
				sb.append("%NODE - LEAF " + dn.getId() + " ##################################################################\n");
				sb.append("\\psframe[linewidth=1pt," +
						"fillstyle=solid," +
						"fillcolor= leafcolor]" +
						"("+(int)p2D.getX()+
						","+(yOffset-(int)p2D.getY())+")"+
						"("+((int)p2D.getX()+dn.getWidth())+
						","+(yOffset-((int)p2D.getY()+dn.getHeight()))+")");
				sb.append("\n");
								
				if(dn.getFuzzySet().equals("RO"))
				{
					sb.append("%fuzzyset - RO\n");
					this.fuzzySetToLatex(sb, p2D, dn, scaleFactor, yOffset);
				}
				else if(dn.getFuzzySet().equals("LO"))
				{									
					sb.append("%fuzzyset - LO\n");
					this.fuzzySetToLatex(sb, p2D, dn, scaleFactor, yOffset);
				}
				else if(dn.getFuzzySet().equals("TRI"))
				{			
					sb.append("%fuzzyset - TRI\n");
					this.fuzzySetToLatex(sb, p2D, dn, scaleFactor, yOffset);
				}
				else if(dn.getFuzzySet().equals("NTRI"))
				{			
					sb.append("%fuzzyset - NTRI\n");
					this.fuzzySetToLatex(sb, p2D, dn, scaleFactor, yOffset);
				}
				else if(dn.getFuzzySet().equals("TRA"))
				{			
					sb.append("%fuzzyset - TRA\n");
					this.fuzzySetToLatex(sb, p2D, dn, scaleFactor, yOffset);
				}
				else if(dn.getFuzzySet().equals("INT"))
				{			
					sb.append("%fuzzyset - INT\n");
					this.fuzzySetToLatex(sb, p2D, dn, scaleFactor, yOffset);
				}
				else if(dn.getFuzzySet().equals("CPLFS"))
				{
					sb.append("%fuzzyset - CPLFS\n");
					this.fuzzySetToLatex(sb, p2D, dn, scaleFactor, yOffset);
				}
				
				//Draw Axis
				sb.append("%Axis\n");
				sb.append("\\psline[linewidth=1pt]{}");
				sb.append("("+(p2D.getX()+3)+","
						+(yOffset-(p2D.getY()+3))+")");
				sb.append("("+(p2D.getX()+3)+","
						+(yOffset-(p2D.getY()+(int)(dn.getHeight()*scaleFactor-3)))+")");
				sb.append("("+(p2D.getX()+(int)(dn.getWidth()*scaleFactor-3))
						+","+(yOffset-(p2D.getY()+(int)(dn.getHeight()*scaleFactor-3)))+")");
				sb.append("\n");					
			}
			else if(dn.getType() == DrawNode.INNER_NODE)
			{
				String iNodeColor = "";
				
				if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TNORM)
				{
					iNodeColor = "tnormcolor";
				}
				else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TCONORM)
				{
					iNodeColor = "tconormcolor";
				}
				else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.AVERAGE)
				{
					iNodeColor = "averagecolor";
				}
				
				sb.append("%NODE - InnerNODE " + dn.getId() + " ###################################################################\n");
				sb.append("\\pscircle[linewidth=1pt," +
						"fillstyle=solid," +
						"fillcolor="+ iNodeColor + "]" +
						"(" + ((int)(p2D.getX()+dn.getWidth()/2d)) +
						"," + (yOffset-((int)(p2D.getY()+dn.getHeight()/2d))) + ")"+
						"{" + (int)dn.getWidth()/2d + "}");
				sb.append("\n");
				
				if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TNORM)
				{
					if(this.settings.tNormDraw.getNormRep() == DrawNorm.STRING)
					{
						int fontsize = settings.tNormDraw.getNormFont().getSize();
						sb.append("\\fontsize{" +
								fontsize +
								"}{15}");
						sb.append("\n");
						sb.append("\\selectfont");
						sb.append("\n");
						String[] helpSplit = dn.getAggr().split("_");
						String aggrName = helpSplit[helpSplit.length-1];
						sb.append("\\pstextpath[c](0 ,0)" +
								"{\\psline[linewidth=0pt, linestyle = none, linecolor = tnormcolor]" +
								"{}" +
								"(" + ((int)(p2D.getX()+dn.getWidth()/2d)-(int)dn.getWidth()/4d-1) +
								"," + (yOffset-((int)(p2D.getY()+dn.getHeight()/2d))-(fontsize*3/8)) + ")" +
								"(" + ((int)(p2D.getX()+dn.getWidth()/2d)+(int)dn.getWidth()/4d+1) +
								"," + (yOffset-((int)(p2D.getY()+dn.getHeight()/2d))-(fontsize*3/8)) + ")}" +
								"{" + aggrName + "}");
						sb.append("\n");						
					}					
				}
				else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.TCONORM)
				{
					if(this.settings.tCoNormDraw.getNormRep() == DrawNorm.STRING)
					{
						String[] helpSplit = dn.getAggr().split("_");
						String aggrName = helpSplit[helpSplit.length-1];

						int fontsize = settings.tNormDraw.getNormFont().getSize();
						sb.append("\\fontsize{" +
								fontsize +
								"}{15}");
						sb.append("\n");
						sb.append("\\selectfont");
						sb.append("\n");
						sb.append("\\pstextpath[c](0 ,0)" +
								"{\\psline[linewidth=0pt, linestyle = none, linecolor = tconormcolor]" +
								"{}" +
								"(" + ((int)(p2D.getX()+dn.getWidth()/2d)-(int)aggrName.length()*fontsize/3) +
								"," + (yOffset-((int)(p2D.getY()+dn.getHeight()/2d))-(fontsize*3/8)) + ")" +
								"(" + ((int)(p2D.getX()+dn.getWidth()/2d)+(int)aggrName.length()*fontsize/3) +
								"," + (yOffset-((int)(p2D.getY()+dn.getHeight()/2d))-(fontsize*3/8)) + ")}" +
								"{" + aggrName + "}");
						sb.append("\n");
					}
				}
				else if(dn.getAccNode().getAggrType() == AccessNode.AggrType.AVERAGE)
				{
					if(this.settings.averageDraw.getNormRep() == DrawNorm.STRING)
					{
						int fontsize = settings.averageDraw.getNormFont().getSize();
						sb.append("\\fontsize{" +
								fontsize +
								"}{15}");
						sb.append("\n");
						sb.append("\\selectfont");
						sb.append("\n");
						String[] helpSplit = dn.getAggr().split("_");
						String aggrName = helpSplit[helpSplit.length-1];
						sb.append("\\pstextpath[c](0 ,0)" +
								"{\\psline[linewidth=0pt, linestyle = none, linecolor = tnormcolor]" +
								"{}" +
								"(" + ((int)(p2D.getX()+dn.getWidth()/2d)-(int)dn.getWidth()/4d-1) +
								"," + (yOffset-((int)(p2D.getY()+dn.getHeight()/2d))-(fontsize*3/8)) + ")" +
								"(" + ((int)(p2D.getX()+dn.getWidth()/2d)+(int)dn.getWidth()/4d+1) +
								"," + (yOffset-((int)(p2D.getY()+dn.getHeight()/2d))-(fontsize*3/8)) + ")}" +
								"{" + aggrName + "}");
						sb.append("\n");
					}
				}
			}
			
			//DrawDetails -----------------------------------------
			
			Point2D p2DD = new Point2D.Double();
			dn.getTransform().transform(new Point2D.Double(0,0), p2DD);
			drawTree.getLayerTrans().get(dn.getLayer()).transform(p2DD, p2DD);		
			
			String detail = null;
			if(settings.showDetails)
			{
				for(DrawDetail dd: dn.getDetails())
				{
					detail = dd.toString().replaceAll("_", "\\_");
						int fontsize = settings.tNormDraw.getNormFont().getSize();
						sb.append("\\fontsize{" +
								fontsize +
								"}{15}");
						sb.append("\n");
						sb.append("\\selectfont");
						sb.append("\n");
						sb.append("\\pstextpath[l](0 ,0)" +
								"{\\psline[linewidth=0pt, linestyle = none, linecolor = tnormcolor]" +
								"{}" +
								"(" + ((int)p2DD.getX()+dd.getX()) +
								"," + (yOffset-((int)(p2DD.getY()+dd.getY()))) + ")" +
								"(" + (int)(p2DD.getX()+dd.getX() + 20*fontsize ) +
								"," + (yOffset-((int)(p2DD.getY()+dd.getY()))) + ")}" +
								"{" + detail + "}");
						sb.append("\n");
				}
			}
			else
			{
				for(DrawDetail dd: dn.getDetails())
				{					
					if(settings.detailsToShow.contains(dd.getType()))
					{
						detail = dd.toString().replaceAll("_", "\\_");
						int fontsize = settings.tNormDraw.getNormFont().getSize();
						sb.append("\\fontsize{" +
								fontsize +
								"}{15}");
						sb.append("\n");
						sb.append("\\selectfont");
						sb.append("\n");
						sb.append("\\pstextpath[l](0 ,0)" +
								"{\\psline[linewidth=0pt, linestyle = none, linecolor = tnormcolor]" +
								"{}" +
								"(" + ((int)p2DD.getX()+dd.getX()) +
								"," + (yOffset-((int)(p2DD.getY()+dd.getY()))) + ")" +
								"(" + (int)(p2DD.getX()+dd.getX() + 20*fontsize ) +
								"," + (yOffset-((int)(p2DD.getY()+dd.getY()))) + ")}" +
								"{" + detail + "}");
						sb.append("\n");
					}					
				}
			}
			//DrawDetails end -----------------------------------------
						
			//Draw Edge .....................................................
			if(settings.edgeRect)
			{				
				if(dn.getChildren()[0] != null)
				{					
					Point2D p2DC0 = new Point2D.Double();
					dn.getChildren()[0].getTransform().transform(dn.getChildren()[0].getLocation(), p2DC0);
					drawTree.getLayerTrans().get(dn.getLayer()+1).transform(p2DC0, p2DC0);
					
					//parent bottom center
					int pBC = (int)dn.getLocation().getX()
						       + dn.getWidth()/2;
					//child top center
					int cTC = (int)dn.getChildren()[0].getLocation().getX()
		                         + dn.getChildren()[0].getWidth()/2;
					
					// parent to knickPunkt to child
					sb.append("\\psline[linewidth=1pt]{}");
					sb.append("("+pBC+","+(yOffset-(p2D.getY()+dn.getHeight()))+")");
					sb.append("("+cTC+","+(yOffset-(p2D.getY()+dn.getHeight()))+")");
					sb.append("("+cTC+","+(yOffset-(p2DC0.getY()))+")");
					sb.append("\n");
					
				}
				if(dn.getChildren()[1] != null)
				{
				
					Point2D p2DC1 = new Point2D.Double();
					dn.getChildren()[1].getTransform().transform(dn.getChildren()[1].getLocation(), p2DC1);
					drawTree.getLayerTrans().get(dn.getLayer()+1).transform(p2DC1, p2DC1);
					
					//parent bottom center
					int pBC = (int)dn.getLocation().getX()
						       + dn.getWidth()/2;
					//child top center
					int cTC = (int)p2DC1.getX()
		                         + dn.getChildren()[1].getWidth()/2;
					
					// parent to knickPunkt to child
					sb.append("\\psline[linewidth=1pt]{}");
					sb.append("("+pBC+","+(yOffset-(p2D.getY()+dn.getHeight()))+")");
					sb.append("("+cTC+","+(yOffset-(p2D.getY()+dn.getHeight()))+")");
					sb.append("("+cTC+","+(yOffset-(p2DC1.getY()))+")");
					sb.append("\n");
				}
			}
			else
			{
				if(dn.getChildren()[0] != null)
				{
					Point2D p2DC0 = new Point2D.Double();
					dn.getChildren()[0].getTransform().transform(dn.getChildren()[0].getLocation(), p2DC0);
					drawTree.getLayerTrans().get(dn.getLayer()+1).transform(p2DC0, p2DC0);
					
					//parent bottom center
					int pBC = (int)dn.getLocation().getX()
						       + dn.getWidth()/2;
					//child top center
					int cTC = (int)dn.getChildren()[0].getLocation().getX()
		                         + dn.getChildren()[0].getWidth()/2;
					
					// parent to child
					sb.append("\\psline[linewidth=1pt]{}");
					sb.append("("+pBC+","+(yOffset-(p2D.getY()+dn.getHeight()))+")");
					sb.append("("+cTC+","+(yOffset-(p2DC0.getY()))+")");
					sb.append("\n");
				}
				if(dn.getChildren()[1] != null)
				{
					Point2D p2DC1 = new Point2D.Double();
					dn.getChildren()[1].getTransform().transform(dn.getChildren()[1].getLocation(), p2DC1);
					drawTree.getLayerTrans().get(dn.getLayer()+1).transform(p2DC1, p2DC1);
					
					//parent bottom center
					int pBC = (int)dn.getLocation().getX()
						       + dn.getWidth()/2;
					//child top center
					int cTC = (int)dn.getChildren()[1].getLocation().getX()
		                         + dn.getChildren()[1].getWidth()/2;
					
					// parent to child
					sb.append("\\psline[linewidth=1pt]{}");
					sb.append("("+pBC+","+(yOffset-(p2D.getY()+dn.getHeight()))+")");
					sb.append("("+cTC+","+(yOffset-(p2DC1.getY()))+")");
					sb.append("\n");                                              
				}
			}			
		}
		
		sb.append("\\end{pspicture}\n");
		sb.append("}\n");
		sb.append("\\end{document}");
		
		return sb.toString();
	}
	
	private void fuzzySetToLatex(StringBuffer sb, Point2D p2D, DrawNode dn, double scaleFactor,int yOffset)
	{					
		//Draw Polygon to fill
		sb.append("\\pspolygon*[linearc=0.2,linewidth=2pt,linecolor=fuzzysetfillcolor]");
		
		double[] prev = dn.getPoints().get(0);
		
		int prevX = (int)p2D.getX()+3
						+(int)((dn.getWidth()-6)
						*scaleFactor
						*prev[0]);
		
		int yZero = (int)p2D.getY()+2+(int)((dn.getHeight()-4)*scaleFactor);
		
		sb.append("("+prevX+","
				+(yOffset-yZero)+")");
		
		for(int i=0; i< dn.getPoints().size(); i++)
		{
			int currX = (int)p2D.getX()+3
									+(int)((dn.getWidth()-6)
									*scaleFactor
									*dn.getPoints().get(i)[0]);
			
			int currY = (int)p2D.getY()+3
									+(int)((dn.getHeight()-6)
									*(1-dn.getPoints().get(i)[1])
									*scaleFactor);			
			
			sb.append("("+currX+","
					+(yOffset-currY)+")");
			
		}
		
		prev = dn.getPoints().get(dn.getPoints().size()-1);
		
		prevX = (int)p2D.getX()+3
						+(int)((dn.getWidth()-6)
						*scaleFactor
						*prev[0]);
		
		sb.append("("+prevX+","
				+(yOffset-yZero)+")");
		
		sb.append("\n");
		
		//Draw line ------------------------------------------------------------
		
		sb.append("\\psline[linewidth=1pt, linecolor=fuzzysetlinecolor]{}");
		
		for(int i=0; i< dn.getPoints().size(); i++)
		{
			int currX = (int)p2D.getX()+3
						+(int)((dn.getWidth()-6)
						*scaleFactor
						*dn.getPoints().get(i)[0]);			
			
			int currY = (int)p2D.getY()+3
						+(int)((dn.getHeight()-6)
						*(1-dn.getPoints().get(i)[1])
						*scaleFactor);
				
			sb.append("("+currX+","
					+(yOffset-currY)+")");
		}
		sb.append("\n");
	}
	
	/**
	 * 
	 */
	public void zoomIN(){
		
		this.settings.scaleFactorX = this.settings.scaleFactorX*1.1;
		this.settings.scaleFactorY = this.settings.scaleFactorY*1.1;
	}
	
	public void zoomOut(){
		
		this.settings.scaleFactorX = this.settings.scaleFactorX/1.1;
		this.settings.scaleFactorY = this.settings.scaleFactorY/1.1;
	}

	@Override
	public void run() {
		
		while(true){
			countL = (countL+1)%1000;
			phase = (phase+0.8f)%1000;
	
			try {
				Thread.sleep(100);
			}
			catch(InterruptedException e)
			{
				//leer
			}
			this.repaint();
		}
	}
	
	//#######################################################################################
	// ComponentListener
	//#######################################################################################
	
	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent arg0) {

		this.dbImage = createImage(this.getWidth(), this.getHeight());
		Graphics graphic = dbImage.getGraphics();
		dbG2D = (Graphics2D)graphic;
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
			
		//update AnalysePanel & FuzzyMap
		this.tcp.getAp().buildAnalyseFrame(this.drawTree.getTree().get(this.selectedNodeID).getAccNode());
	}
	//####################################################################################
	//Get and SET ########################################################################
	//####################################################################################
	
	public void setTriangles(ArrayList<Triangle> triangles) {
		this.settings.triangles = triangles;
	}

	public double getScaleFactorX() {
		return settings.scaleFactorX;
	}

	public void setScaleFactorX(double scaleFactorX) {
		this.settings.scaleFactorX = scaleFactorX;
	}

	public double getScaleFactorY() {
		return settings.scaleFactorY;
	}

	public void setScaleFactorY(double scaleFactorY) {
		this.settings.scaleFactorY = scaleFactorY;
	}

	public boolean isAlaising() {
		return settings.alaising;
	}

	public void setAlaising(boolean alaising) {
		this.settings.alaising = alaising;
	}

	public int getOffsetX() {
		return settings.offsetX;
	}

	public void setOffsetX(int currentX) {
		this.settings.offsetX = currentX;
	}
	
	public void refreshOffsetX(int x)
	{
		this.settings.offsetX += x;
	}

	public int getOffsetY() {
		return settings.offsetY;
	}

	public void setOffsetY(int currentY) {
		this.settings.offsetY = currentY;
	}
	
	public void refreshOffsetY(int y)
	{
		this.settings.offsetY += y;
	}

	public int getTransX() {
		return settings.transX;
	}

	public void setTransX(int transX) {
		this.settings.transX = transX;
	}

	public int getTransY() {
		return settings.transY;
	}

	public void setTransY(int transY) {
		this.settings.transY = transY;
	}

	public DrawTree getDrawTree() {
		return drawTree;
	}

	public void setDrawTree(DrawTree drawTree) {
		this.drawTree = drawTree;
		DrawTreeBuilder dtb = new DrawTreeBuilder();
		dtb.setNodeLocations(drawTree, this);
	}

	public AffineTransform getTransformation() {
		return settings.transformation;
	}

	public void setTransformation(AffineTransform transformation) {
		this.settings.transformation = transformation;
	}

	public int getSelectedNodeID() {
		return selectedNodeID;
	}

	public void setSelectedNodeID(int selectedNodeID) {
		this.selectedNodeID = selectedNodeID;
	}

	public boolean isEdgeRect() {
		return settings.edgeRect;
	}

	public void setEdgeRect(boolean edgeRect) {
		this.settings.edgeRect = edgeRect;
	}

	public DrawNorm getLeafDraw() {
		return settings.leafDraw;
	}

	public void setLeafDraw(DrawNorm leafDraw) {
		this.settings.leafDraw = leafDraw;
	}
	
	public double getLeafShapeSize() {
		return settings.leafShapeSize;
	}

	public void setLeafShapeSize(double leafShapeSize) {
		this.settings.leafShapeSize = leafShapeSize;
	}

	public double getLeafDistance() {
		return settings.leafDistance;
	}

	public void setLeafDistance(double leafDistance) {
		this.settings.leafDistance = leafDistance;
	}

	public double getLayerDistance() {
		return settings.layerDistance;
	}

	public void setLayerDistance(double layerDistance) {
		this.settings.layerDistance = layerDistance;
	}

	public boolean isNodeMidEdge() {
		return settings.nodeMidEdge;
	}

	public void setNodeMidEdge(boolean nodeMidEdge) {
		this.settings.nodeMidEdge = nodeMidEdge;
	}

	public boolean isPaperView() {
		return settings.paperView;
	}

	public void setPaperView(boolean paperView) {
		this.settings.paperView = paperView;
	}

	public DrawNorm gettNormDraw() {
		return settings.tNormDraw;
	}

	public void settNormDraw(DrawNorm tNormDraw) {
		this.settings.tNormDraw = tNormDraw;
	}

	public DrawNorm gettCoNormDraw() {
		return settings.tCoNormDraw;
	}

	public void settCoNormDraw(DrawNorm tCoNormDraw) {
		this.settings.tCoNormDraw = tCoNormDraw;
	}

	public DrawNorm getAverageDraw() {
		return settings.averageDraw;
	}

	public void setAverageDraw(DrawNorm averageDraw) {
		this.settings.averageDraw = averageDraw;
	}


	public int getMaxLength() {
		return settings.maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.settings.maxLength = maxLength;
	}

	public ArrayList<Triangle> getTriangles() {
		return settings.triangles;
	}

	public TreeSet<Integer> getDetailsToShow() {
		return settings.detailsToShow;
	}

	public void setDetailsToShow(TreeSet<Integer> detailsToShow) {
		this.settings.detailsToShow = detailsToShow;
	}

	public boolean isShowDetails() {
		return settings.showDetails;
	}

	public void setShowDetails(boolean showDetails) {
		this.settings.showDetails = showDetails;
		
	}

	public AffineTransform getIdentity() {
		return identity;
	}

	public TreeClassPack getTcp() {
		return tcp;
	}

	public void setTcp(TreeClassPack tcp) {
		this.tcp = tcp;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public AccessNode getAccRoot() {
		return accRoot;
	}

	public void setAccRoot(AccessNode accRoot) {
		this.accRoot = accRoot;
	}

	public StreamView getSv() {
		return sv;
	}

	public void setSv(StreamView sv) {
		this.sv = sv;
	}

	public boolean isStreamMode() {
		return streamMode;
	}

	public void setStreamMode(boolean streamMode) {
		this.streamMode = streamMode;
	}
	
	public DefaultPTV getdPTV() {
		return dPTV;
	}

	public void setdPTV(DefaultPTV dPTV) {
		this.dPTV = dPTV;
	}

	public NodeInteraction getnInteraction() {
		return nInteraction;
	}

	public void setnInteraction(NodeInteraction nInteraction) {
		this.nInteraction = nInteraction;
	}
}

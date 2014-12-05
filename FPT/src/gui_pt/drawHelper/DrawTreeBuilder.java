package gui_pt.drawHelper;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.drawObjects.DrawNode;
import gui_pt.gui.DrawPanel;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class DrawTreeBuilder {
	
	private int idCounter = 0;
	int offsetRight = 0;
	
	public void buildDrawTree(AccessNode accRoot, DrawTree dt)
	{	
		LinkedList<DrawNode> returnList = new LinkedList<DrawNode>();	
		LinkedList<DrawNode> helpQueue = new LinkedList<DrawNode>();
		dt.initPoints_A(accRoot);
		DrawNode dNode = new DrawNode(accRoot, dt);
		helpQueue.add(dNode);
		dt.setRoot(dNode);
		
		dNode.setLayer(0);
		
		//build tree and determine layer-count
		//check root is Leaf
		if(dNode.getType() == DrawNode.LEAF)
		{
			dt.setNumLayer(1);
		}
		while(!helpQueue.isEmpty())
		{
			DrawNode dn = helpQueue.poll();
					
			returnList.add(dn);
			dn.setId(this.idCounter++);
			
			
			if(dn.getType() == DrawNode.INNER_NODE)
			{				
				DrawNode dChild1 = new DrawNode(dn.getAccNode().getChild1(), dt);
				DrawNode dChild2 = new DrawNode(dn.getAccNode().getChild2(), dt);
				dChild1.setLayer(dn.getLayer()+1);
				dChild2.setLayer(dn.getLayer()+1);
				dt.setNumLayer(dn.getLayer()+2);
				DrawNode[] dChildren = {dChild1, dChild2};
				
				dn.setChildren(dChildren);
				
				helpQueue.add(dChild1);
				helpQueue.add(dChild2);				
			}									
		}
		dt.setTree(returnList);
	}
	
	public void setNodeLocations(DrawTree dt, DrawPanel dp)
	{
		offsetRight = 0;
		
		DrawNode dn = dt.getTree().getFirst();
		
		LinkedList<DrawNode> helpQueue = new LinkedList<DrawNode>();
		helpQueue.add(dn);
		
		if(dp.isNodeMidEdge() && dn.getType() == DrawNode.INNER_NODE)
		{
			recNodeInMiddleDraw(dn, dp);
		}
		else
		{
			while(!helpQueue.isEmpty())
			{
				dn = helpQueue.poll();
				
				if(dn.getType() == DrawNode.INNER_NODE)
				{
					double leafsC1 = dn.getChildren()[0].getAccNode().getLeafs();
				
					int locX = (int)(leafsC1*dp.getLeafShapeSize())
								+(int)((leafsC1-0.5)*dp.getLeafDistance())
								- (int)(dn.getWidth()/2)
								+ dn.getLocOffset();
				
//					int locY = 10+dn.getLayer()*(int)dp.getLayerDistance();
					int locY = 0;
					
					int offsetX = (int)(leafsC1*dp.getLeafShapeSize())
									+(int)((leafsC1)*dp.getLeafDistance()
									+ dn.getLocOffset());
					dn.getChildren()[1].setLocOffset(offsetX);
					dn.getChildren()[0].setLocOffset(dn.getLocOffset());
									
					Point2D location = new Point2D.Double(locX,locY);
//					dn.setLocation(location);
					dn.getLocation().setLocation(location);
					
					helpQueue.add(dn.getChildren()[0]);
					helpQueue.add(dn.getChildren()[1]);
				}
				else
				{
					int locX = dn.getLocOffset();
//					int locY = 10+dn.getLayer()*(int)dp.getLayerDistance();
					int locY = 0;
					
					Point2D location = new Point2D.Double(locX, locY);
//					dn.setLocation(location);
					dn.getLocation().setLocation(location);
				}					
			}
		}
//		this.calcLayerLocations(dt);
	}
	
	private int recNodeInMiddleDraw(DrawNode dn, DrawPanel dp)
	{
		double midLeft = 0;
		double midRight = 0;

		assert dn.getType() == 0;
		if(dn.getChildren()[0].getType() == DrawNode.LEAF)
		{
			midLeft = offsetRight + (dp.getLeafShapeSize()/2.0);
			
			//set leaf loc
			int leafLocX = offsetRight;
			offsetRight = offsetRight + (int)(dp.getLeafShapeSize()+dp.getLeafDistance());
//			int leafLocY = 10+dn.getChildren()[0].getLayer()
//								*(int)dp.getLayerDistance();
			int leafLocY = 0;
			
//			Point2D leafLoc = new Point2D.Double(leafLocX, leafLocY);
			dn.getChildren()[0].getLocation().setLocation(leafLocX, leafLocY);
		}
		else
		{
			midLeft = recNodeInMiddleDraw(dn.getChildren()[0], dp);
			
//			double leafsC1 = dn.getChildren()[0].getAccNode().getLeafs(); TODO delete
//			offsetRight = (int)(leafsC1*dp.getLeafShapeSize())
//							+(int)((leafsC1)*dp.getLeafDistance()
//							+ dn.getLocOffset());
		}
	
		if(dn.getChildren()[1].getType() == DrawNode.LEAF)
		{
			
			midRight = offsetRight + (int)(dp.getLeafShapeSize()/2.0);

			
			//set leaf loc
			int leafLocX = offsetRight;
			
			offsetRight = offsetRight + (int)(dp.getLeafShapeSize()+dp.getLeafDistance());
//			int leafLocY = 10+dn.getChildren()[1].getLayer()
//								*(int)dp.getLayerDistance();
			int leafLocY = 0;
			
//			Point2D leafLoc = new Point2D.Double(leafLocX, leafLocY);
//			dn.getChildren()[1].setLocation(leafLoc);
			dn.getChildren()[1].getLocation().setLocation(leafLocX, leafLocY);
		}
		else
		{			
			midRight = recNodeInMiddleDraw(dn.getChildren()[1], dp);
			
		}
		
		int locX = (int)((midRight+midLeft)/2.0);
//		int locY = 10+dn.getLayer()*(int)dp.getLayerDistance();
		int locY = 0;
		
//		Point2D location = new Point2D.Double(locX-(int)(dn.getWidth()/2.0), locY);
//		dn.setLocation(location);
		dn.getLocation().setLocation(locX-(int)(dn.getWidth()/2.0), locY);
		
		return locX;		
	}
	
//	public void calcLayerLocations(DrawTree dt)
//	{
//		int nodeCounter = 0;
//		for(int i=0; i<dt.getNumLayer(); i++)
//		{
//			while(dt.getTree().get(nodeCounter).getLayer()!=i)
//			{
//				nodeCounter++;
//			}
//			dt.getLayerLocations().add(dt.getTree().get(nodeCounter).getLocation());
//		}
//	}
	
	public static void calcLayerLocations(DrawTree dt, double layerDis)
	{
		for(int i=0; i<dt.getNumLayer(); i++)
		{
			AffineTransform trans = new AffineTransform();
			trans.translate(0, 10+i*layerDis);
			dt.getLayerTrans().add(trans);
		}
	}
	
}

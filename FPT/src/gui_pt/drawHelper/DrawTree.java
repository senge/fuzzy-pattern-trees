package gui_pt.drawHelper;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.drawObjects.DrawNode;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class DrawTree implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1413456248524475655L;
	
	private DrawNode root;
	private LinkedList<DrawNode> Tree = new LinkedList<DrawNode>();
	private int numLayer;
	private ArrayList<AffineTransform> layerTrans = new ArrayList<AffineTransform>();
	private String className;
	private PointsWrapper[][] points_A;
	private TreeClassPack tcp;
	

	public void clearDrawTree(){
		
		this.Tree.clear();
		this.layerTrans.clear();
	}
	
	public void initPoints_A(AccessNode accNode)
	{
//		points_A = new PointsWrapper[accNode.getAttrSize()][accNode.getTermSize()];

		points_A = new PointsWrapper
				[tcp.getAccessPT().getFuzzySets()[accNode.getClass_Index()].length][];
		
		for(int a=0; a < points_A.length; a++)
		{
			points_A[a] = new PointsWrapper[tcp.getAccessPT().getFuzzySets()[accNode.getClass_Index()][a].length];
		}
		
		for(int i=0; i<points_A.length; i++)
		{
			for(int j=0; j<points_A[i].length; j++)
			{
				points_A[i][j] = new PointsWrapper();
			}
		}
	}
	
	public void setPoints(int attr, int term, ArrayList<double[]> points)
	{
		points_A[attr][term].points = points;
	}
	
	public ArrayList<double[]> getPoints(int attr, int term){
		
		return points_A[attr][term].points;
	}
	
	public void setPointsSelection(int attr, int term, ArrayList<ArrayList<double[]>> pointsSelection){
		
		points_A[attr][term].pointsSelection = pointsSelection;
	}
	
	public ArrayList<ArrayList<double[]>> getPointsSelection(int attr, int term){
		
		return points_A[attr][term].pointsSelection;
	}
	
	private class PointsWrapper{
		
		public ArrayList<double[]> points;
		public ArrayList<ArrayList<double[]>>  pointsSelection = new ArrayList<ArrayList<double[]>>();
	}
	
		
	//GET and SET #####################################################################

	public LinkedList<DrawNode> getTree() {
		return Tree;
	}

	public void setTree(LinkedList<DrawNode> tree) {
		Tree = tree;
	}

	public DrawNode getRoot() {
		return root;
	}

	public void setRoot(DrawNode root) {
		this.root = root;
	}

	public int getNumLayer() {
		return numLayer;
	}

	public void setNumLayer(int numLayer) {
		this.numLayer = numLayer;
	}

	public ArrayList<AffineTransform> getLayerTrans() {
		return layerTrans;
	}

	public void setLayerTrans(ArrayList<AffineTransform> layerTrans) {
		this.layerTrans = layerTrans;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public PointsWrapper[][] getPoints_A() {
		return points_A;
	}

	public void setPoints_A(PointsWrapper[][] points_A) {
		this.points_A = points_A;
	}

	public TreeClassPack getTcp() {
		return tcp;
	}

	public void setTcp(TreeClassPack tcp) {
		this.tcp = tcp;
	}

//	public ArrayList<Point2D> getLayerLocations() {
//		return layerLocations;
//	}
//
//	public void setLayerLocations(ArrayList<Point2D> layerLocations) {
//		this.layerLocations = layerLocations;
//	}

}

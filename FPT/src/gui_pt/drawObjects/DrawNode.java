package gui_pt.drawObjects;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.drawHelper.DrawTree;
import gui_pt.guiHelper.FuzzySetDrawer;
import gui_pt.pt.Calculations;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class DrawNode implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -1724422590312323934L;
	
	public static final int LEAF = 0;
	public static final int INNER_NODE = 1;
	
//	public static final String ATTRIBUTE_NAME = "attributname";
//	public static final String FUZZYSET = "fuzzyset";
//	public static final String PERFORMANCE = "performance";
	
	private AccessNode					accNode;
	
	private int								id;	
	private boolean 						expended = true;
	private boolean							expendedRoot = true;
	private boolean 						selected = false;
	private int								type;
	private Point2D							location = new Point2D.Double(0,0); //redundant with transform
	private AffineTransform					transform = new AffineTransform();
	private int								locOffset = 0;
	private int								width, height;
	private int 							layer;
	private String 							fuzzySet;
	private int								selectedPoints = 0;
	private String							aggr;
	private String							attributeName;
	private int								attributeIndex;
	private int								termIndex;
	private double							attMin = 0;
	private double							attMax = 1;
	private double							fak;
	
	private ArrayList<DrawDetail>			details = new ArrayList<DrawDetail>();
	
	private DrawNode[] 						children = new DrawNode[2];
	private DrawTree						drawTree;
	
	//Default Constructor
	public DrawNode(){}
	
	public DrawNode(AccessNode accNode, DrawTree dt)
	{		
		this.accNode = accNode;
		this.drawTree = dt;
		
		if(accNode.getNodeType() == AccessNode.INNER_NODE)
		{
			this.type = DrawNode.INNER_NODE;
			this.width = 30;
			this.height = 30;
			this.aggr = accNode.getAggregation();
		}
		else
		{
			this.type = DrawNode.LEAF;
			this.width = 60;
			this.height = 30;
					
			this.attributeName = accNode.getAttribute().name();
			this.attributeIndex = accNode.getAttributeIndex();
			this.termIndex		= accNode.getTermIndex();
			
			this.fuzzySet = FuzzySetDrawer.getFuzzySetName(accNode.getAccPT().getFuzzySets()
					[accNode.getClass_Index()]
						[accNode.getAttributeIndex()]
							[accNode.getTermIndex()]);
			
			extractMinMax(attributeIndex);
		
			fak = attMax-attMin;

			drawTree.setPoints(attributeIndex, termIndex, convertToDrawPoints(
					FuzzySetDrawer.getFuzzySetPoints(accNode.getAccPT().getFuzzySets()
										[accNode.getClass_Index()]
											[accNode.getAttributeIndex()]
												[accNode.getTermIndex()])
						, fak));
			

			
			if(drawTree.getPointsSelection(attributeIndex, termIndex).size() == 0)
			{
				drawTree.getPointsSelection(attributeIndex, termIndex).add(FuzzySetDrawer
						.getFuzzySetPoints(accNode.getAccPT()
								.getFuzzySets()
									[accNode.getClass_Index()]
										[accNode.getAttributeIndex()]
											[accNode.getTermIndex()]));
			}		
		}
		
		addDefaultDetails();
	}
	private void extractMinMax(int attributeIndex){
		
		if(accNode.getAttribute().isNumeric())
		{
			this.attMin = Calculations.calcMin(accNode.getAccPT().getData(), attributeIndex);
			this.attMax = Calculations.calcMax(accNode.getAccPT().getData(), attributeIndex);
		}
		else
		{
			this.attMax = accNode.getAccPT().getData().attribute(attributeIndex).numValues()-1;
		}
		
	}
	
	public void expand(boolean expand){
		
		this.expendedRoot = expand;
		
		if(children[0] != null)
		{
			children[0].recursiveExpand(expand);
		}
		if(children[1] != null)
		{
			children[1].recursiveExpand(expand);
		}
	}
	
	public void recursiveExpand(boolean expand){
		
		this.expended = expand;
		
		if(this.isExpendedRoot())
		{
			if(children[0] != null)
			{
				children[0].recursiveExpand(expand);
			}
			if(children[1] != null)
			{
				children[1].recursiveExpand(expand);
			}
		}
	}
	
	public ArrayList<double[]> convertToDrawPoints(ArrayList<double[]> exPoints, double fak){
		
		ArrayList<double[]> returnPoints = new ArrayList<double[]>();
	
		if(this.fuzzySet.equals("LO"))
		{
			double[] point = new double[2];
			point[0] = 0;
			point[1] = 1;
			
			returnPoints.add(point);
		}
		
		for(int i=0; i<exPoints.size(); i++)
		{
			double[] cp = exPoints.get(i);
			
			if(cp[0] < attMin)
			{
				cp[1] = gui_pt.util.Math.calcLinFunc(cp, exPoints.get(i+1), attMin);
				cp[0] = attMin;
			}
			
			if(cp[0] > attMax)
			{
				cp[1] = gui_pt.util.Math.calcLinFunc(exPoints.get(i-1), cp, attMax);
				cp[0] = attMax;
			}
			
			double[] point = new double[2];
			
			point[0] = (cp[0]-attMin)/fak;
			point[1] = cp[1];
			
			returnPoints.add(point);
		}
		
		if(this.fuzzySet.equals("RO"))
		{
			double[] point = new double[2];
			point[0] = (attMax-attMin)/fak;
			point[1] = 1;
			
			returnPoints.add(point);
		}
		
		return returnPoints;
	}
	
	public void addDefaultDetails(){
		
		if(this.type == DrawNode.LEAF)
		{
			int offset = 15;
			
			// AttributeName
			StaticValue sv1 = new StaticValue();
			sv1.setValue(this.attributeName);
			
			DrawDetail dd1 = new DrawDetail(location, 0, height+ offset, DrawDetail.ATTRIBUTE_NAME);
			dd1.setValue(sv1);
			
			this.details.add(dd1);
			
			// FuzzySet
			StaticValue sv2 = new StaticValue();
			sv2.setValue(fuzzySetToString());
								
			DrawDetail dd2 = new DrawDetail(location, 0, height+ offset*2, DrawDetail.FUZZYSET);
			dd2.setValue(sv2);
			
			this.details.add(dd2);
			
			// Performance
			StaticValue sv3 = new StaticValue();
			sv3.setValue("[P = " + ((int)this.accNode.getPerformance()*100)/100d + "]");
								
			DrawDetail dd3 = new DrawDetail(location, 0, height+ offset*3, DrawDetail.PERFORMANCE);
			dd3.setValue(sv3);
			
			this.details.add(dd3);
			
			// Output
			OutputValue ov = new OutputValue(this);
			
			DrawDetail dd = new DrawDetail(location, 0, -15, DrawDetail.OUTPUT);
			dd.setValue(ov);
			dd.setColor(Color.red);
			
			this.details.add(dd);
		}
		else
		{
			
			int offset = 10;
			// Performance
			StaticValue sv1 = new StaticValue();
			sv1.setValue("[P = " + ((int)this.accNode.getPerformance()*100)/100d + "]");
			
			DrawDetail dd1 = new DrawDetail(location, width + offset, height*2/3, DrawDetail.PERFORMANCE);
			dd1.setValue(sv1);
			
			this.details.add(dd1);
			
			// Output
			OutputValue ov = new OutputValue(this);
			
			DrawDetail dd = new DrawDetail(location, 0, -15, DrawDetail.OUTPUT);
			dd.setValue(ov);
			dd.setColor(Color.red);
			
			this.details.add(dd);
		}				
	}
	
	public String fuzzySetToString(){
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(fuzzySet);
		
//		if(this.fuzzySet.equals("LO")
//				|| this.fuzzySet.equals("RO")
//				|| this.fuzzySet.equals("INT"))
//		{
//			sb.append("[");
//			sb.append(((int)(this.accNode.getA()*100)/100d)+";");
//			sb.append(((int)(this.accNode.getB()*100)/100d)+"]");
//		}
//		else if(this.fuzzySet.equals("TRI")
//				|| this.fuzzySet.equals("NTRI"))
//		{
//			sb.append("[");
//			sb.append(((int)(this.accNode.getA()*100)/100d)+";");
//			sb.append(((int)(this.accNode.getB()*100)/100d)+";");
//			sb.append(((int)(this.accNode.getC()*100)/100d)+"]");
//		}
//		else {
//			sb.append("[");
//			sb.append(((int)(this.accNode.getA()*100)/100d)+";");
//			sb.append(((int)(this.accNode.getB()*100)/100d)+";");
//			sb.append(((int)(this.accNode.getC()*100)/100d)+";");
//			sb.append(((int)(this.accNode.getD()*100)/100d)+"]");
//		}
		
		return sb.toString();
	}
	
//	public TreeMap<String, StringBuffer> detailsToString()
//	{
//		TreeMap<String,StringBuffer> returnMap = new TreeMap<String, StringBuffer>();
//		
//		StringBuffer[] sb = null;
//		
//		if(this.type == DrawNode.LEAF)
//		{
//			sb = new StringBuffer[3];
//			sb[0] = new StringBuffer();
//			sb[0].append(this.attributeName);
//			returnMap.put(DrawNode.ATTRIBUTE_NAME, sb[0]);
//			
//			sb[1] = new StringBuffer();
//			sb[1].append(this.fuzzySet);
//			sb[1].append("\n");
//			sb[1].append(fuzzySetToString());
//			
//			returnMap.put(DrawNode.FUZZYSET, sb[1]);
//			
//			sb[2] = new StringBuffer();
//			sb[2].append("[P = ");
//			sb[2].append(((int)this.accNode.getPerformance()*100)/100d);
//			sb[2].append("]");
//			returnMap.put(DrawNode.PERFORMANCE, sb[2]);
//		}
//		else
//		{
//			sb = new StringBuffer[1];
//			sb[0] = new StringBuffer();
//			sb[0].append("[P = ");
//			sb[0].append(((int)this.accNode.getPerformance()*100)/100d);
//			sb[0].append("]");
//			returnMap.put(DrawNode.PERFORMANCE, sb[0]);
//		}
//		return returnMap;
//	}
	
	public void resetTransformation(){
		
		this.getTransform().setToIdentity();
		
		for(DrawDetail dd: details)
		{
			dd.setToDefault();
		}
		
		if(this.children[0] != null)
		{
			this.children[0].resetTransformation();
		}
		if(this.children[1] != null)
		{
			this.children[1].resetTransformation();
		}
	}
	
	
	//GET and SET ########################################################################
	
	public boolean isExpended() {
		return expended;
	}
	public void setExpended(boolean expended) {
		this.expended = expended;
	}
	public boolean isExpendedRoot() {
		return expendedRoot;
	}

	public void setExpendedRoot(boolean expendedRoot) {
		this.expendedRoot = expendedRoot;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getFuzzySet() {
		return fuzzySet;
	}

	public void setFuzzySet(String fuzzySet) {
		this.fuzzySet = fuzzySet;
	}

	public DrawNode[] getChildren() {
		return children;
	}

	public void setChildren(DrawNode[] children) {
		this.children = children;
	}

	public int getLocOffset() {
		return locOffset;
	}

	public void setLocOffset(int locOffset) {
		this.locOffset = locOffset;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}

	public Point2D getLocation() {
		return location;
	}

	public void setLocation(Point2D location) {
		this.location = location;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public int getAttributeIndex() {
		return attributeIndex;
	}

	public void setAttributeIndex(int attributeIndex) {
		this.attributeIndex = attributeIndex;
	}

	public AccessNode getAccNode() {
		return accNode;
	}

	public void setAccNode(AccessNode accNode) {
		this.accNode = accNode;
	}

	public String getAggr() {
		return aggr;
	}

	public void setAggr(String aggr) {
		this.aggr = aggr;
	}

	public ArrayList<double[]> getPoints() {
//		return points;
		
		return drawTree.getPoints(attributeIndex, termIndex);
	}

	public void setPoints(ArrayList<double[]> points) {
//		this.points = points;
		
		drawTree.setPoints(attributeIndex, termIndex, points);
	}

	public double getAttMin() {
		return attMin;
	}

	public void setAttMin(double attMin) {
		this.attMin = attMin;
	}

	public double getAttMax() {
		return attMax;
	}

	public void setAttMax(double attMax) {
		this.attMax = attMax;
	}

	public ArrayList<ArrayList<double[]>> getPointSelection() {
		return drawTree.getPointsSelection(attributeIndex, termIndex);
	}

	public void setPointSelection(ArrayList<ArrayList<double[]>> pointSelection) {
		drawTree.setPointsSelection(attributeIndex, termIndex, pointSelection);
	}

	public double getFak() {
		return fak;
	}

	public void setFak(double fak) {
		this.fak = fak;
	}

	public int getSelectedPoints() {
		return selectedPoints;
	}

	public void setSelectedPoints(int selectedPoints) {
		this.selectedPoints = selectedPoints;
	}

	public ArrayList<DrawDetail> getDetails() {
		return details;
	}

	public void setDetails(ArrayList<DrawDetail> details) {
		this.details = details;
	}

	public DrawTree getDrawTree() {
		return drawTree;
	}

	public void setDrawTree(DrawTree drawTree) {
		this.drawTree = drawTree;
	}


}

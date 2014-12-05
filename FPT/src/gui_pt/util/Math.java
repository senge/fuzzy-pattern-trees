package gui_pt.util;

public class Math {
	
	public static double calcLinFunc(double[] point_1, double[] point_2, double x)
	{
		double m = (point_2[1] - point_1[1])/(point_2[0] - point_1[0]);
System.out.println(m);
		
		double b = point_1[1] - m * point_1[0];
System.out.println(b);
System.out.println("x" + x);
		return m * x + b;
	}

}

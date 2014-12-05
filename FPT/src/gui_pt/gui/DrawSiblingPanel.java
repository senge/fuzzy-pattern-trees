package gui_pt.gui;

import gui_pt.drawHelper.TreeClassPack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class DrawSiblingPanel extends JPanel{
	
	TreeClassPack tcp;
	
	public DrawSiblingPanel(TreeClassPack tcp)
	{
		this.tcp = tcp;
		this.setSize(0, 100);
		this.setBackground(Color.white);
		this.setVisible(true);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);	
		//upcast
		Graphics2D graphic = (Graphics2D)g;
		
		paintSiblingTrees(graphic);
		
	}
	
	public void paintSiblingTrees(Graphics2D graphic)
	{	
		
		//calc output
		double[] output = new double[this.tcp.getAccRootPack().length];
		double maxDouble = Double.MIN_VALUE;
		int maxIndex = -1;
		for(int i=0; i< this.tcp.getAccRootPack().length; i++)
		{
			output[i] = this.tcp.getAccRootPack()[i].fire(this.tcp.getProtoInstance());
			if(output[i] > maxDouble)
			{
				maxDouble = output[i];
				maxIndex = i;
			}
		}
		
		//calc longest ClassName
		int length_longest_name = 0;
		for(int i=0; i< tcp.getClass_Names().length; i++)
		{
			if(length_longest_name < tcp.getClass_Names()[i].length())
			{
				length_longest_name = tcp.getClass_Names()[i].length();
			}
		}
		
		for(int i=0; i< this.tcp.getAccRootPack().length; i++)
		{
			if(i == maxIndex)
			{
				graphic.setColor(Color.red);
			}
		
			graphic.fillRect((int)(length_longest_name*6/2d)+length_longest_name*6*i
					, 20 + (int)(60 - 60*output[i])
					, 20
					, (int)(60*output[i]));
			
			graphic.drawString(tcp.getClass_Names()[i]
					, 10 + length_longest_name*6*i
					, 95);
			
			graphic.drawString(Double.toString((int)(output[i]*1000)/1000d)
			        , (int)(length_longest_name*6/2d)+length_longest_name*6*i
			        , 15);
			
			graphic.setColor(Color.black);
			graphic.drawRect((int)(length_longest_name*6/2d)+length_longest_name*6*i
					, 20
					, 20
					, 59);
		}
	}

}

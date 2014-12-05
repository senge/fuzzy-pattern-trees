package gui_pt.guiUtil;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class RolloverToggleButton extends JToggleButton implements MouseListener{
	
	private boolean isRollover = false;
//	private float alpha = 0.0f;
	
	//#######################################################################
	// CONSTRUCTOR
	//#######################################################################
	
	public RolloverToggleButton(ImageIcon icon){
		
		super(icon);
		
		this.addMouseListener(this);
		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setOpaque(false);
		this.setBackground(new Color(0,0,0,0));
	}
	
	public RolloverToggleButton(String name){		
		super(name);
		
		this.addMouseListener(this);
		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setOpaque(false);
		this.setBackground(new Color(0,0,0,0));
	}
	
	//#######################################################################
	// METHODS
	//#######################################################################
	
//	public void paint(Graphics g){
//		super.paint(g);
//		
//			Graphics2D g2D = (Graphics2D)g;
//			g2D.setColor(new Color(0.0f,0.0f,0.0f,alpha));
//			g2D.drawRect(0
//			, 0
//			, this.getWidth()-1
//			, this.getHeight()-1);	
//	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		this.setBorderPainted(true);

	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		if(!this.isSelected()){
			this.setBorderPainted(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
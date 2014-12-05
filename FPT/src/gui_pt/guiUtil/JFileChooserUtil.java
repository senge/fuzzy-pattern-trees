package gui_pt.guiUtil;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.plaf.FileChooserUI;

public class JFileChooserUtil {
	
	public static boolean enableApproveButton(JFileChooser fc, boolean enable)
	{
		FileChooserUI ui = fc.getUI();
		String buttonText = ui.getApproveButtonText(fc);
		
		return JFileChooserUtil.enableButton(fc, buttonText, enable);
	
	}
	
	public static boolean enableButton(Component comp, String buttonText, boolean enable)
	{	
		if(comp instanceof JButton)
		{
			if(((JButton)comp).getText() !=null
					&& ((JButton)comp).getText().equals(buttonText))
			{
				((JButton)comp).setEnabled(enable);
				return true;
			}
		}
		else if(comp instanceof Container)
		{
			for(int i=0; i< ((Container)comp).getComponentCount(); i++)
			{
				Component c = ((Container)comp).getComponent(i);
				JFileChooserUtil.enableButton(c, buttonText, enable);
			}
		}		
		return false;
	}

}

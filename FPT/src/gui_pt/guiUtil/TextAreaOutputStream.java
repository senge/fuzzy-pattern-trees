package gui_pt.guiUtil;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream{
	
	private JTextArea textArea;
	
	public TextAreaOutputStream(JTextArea textArea)
	{
		this.textArea = textArea;
	}
	

	@Override
	public void write(int arg0) throws IOException 
	{
		textArea.append(Character.toString((char)arg0));	
	}

}

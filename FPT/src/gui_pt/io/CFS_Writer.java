package gui_pt.io;

import gui_pt.fse.CFS_IdentityWraper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CFS_Writer {
	
	File m_dir;
	String m_fileName;
	
	public CFS_Writer(File dir, String fileName){
		
		m_dir = dir;
		m_fileName = fileName;
	}
	
	public void write(CFS_IdentityWraper cfs_IW) throws IOException{
		
		File file = new File(m_dir.toString()+"/"+m_fileName+".fso");
		
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		oos.writeObject(cfs_IW);
		
		oos.close();
		fos.close();
		
	}

}

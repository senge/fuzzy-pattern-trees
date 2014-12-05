package gui_pt.io;

import gui_pt.fse.FuzzySetProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import weka.core.Instances;

public class FS_ProjectWriter {
	
	File m_dir;
	String m_fileName;
	
	public FS_ProjectWriter(File dir, String fileName){
		
		m_dir = dir;
		m_fileName = fileName;
	}
	
	public void write(FuzzySetProject fs_Pro) throws IOException{
		
		File proDir = new File(m_dir.toString()+"/"+m_fileName);
		
		if(!proDir.exists())
		{
			proDir.mkdir();
		}
		File file = new File(proDir.toString()+"/project.fspr");
		
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		oos.writeObject(fs_Pro);
		
		oos.close();
		fos.close();		
	}
	
	/**
	 * 
	 * @param fs_Pro
	 * @param data
	 * @throws IOException
	 */
	public void write(FuzzySetProject fs_Pro, Instances data) throws IOException{
		
		File proDir = new File(m_dir.toString()+"/"+m_fileName);
		
		if(!proDir.exists())
		{
			proDir.mkdir();
		}
		File file = new File(proDir.toString()+"/project.fspr");
		
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		oos.writeObject(fs_Pro);
		
		oos.close();
		fos.close();
		
		int numAttr = data.numAttributes();
//		if(data.classAttribute() != null)
//		{
//			numAttr--;
//		}
		
		for(int c=0; c<data.numClasses(); c++)
		{
//			File classDir = new File(proDir.toString()+"/"+data.classAttribute().value(c));

System.out.println("numClasses: "+fs_Pro.getClasses().length);
System.out.println(fs_Pro.getClasses()[c].getClassName());
			
			File classDir = new File(proDir.toString()+"/"+fs_Pro.getClasses()[c].getClassName());

			
			if(!classDir.exists())
			{
				classDir.mkdir();
			}
			
			for(int a=0; a<numAttr; a++)
			{
				File attributeDir = new File(classDir.toString()+"/"+data.attribute(a).name());
				
				if(!attributeDir.exists())
				{
					attributeDir.mkdir();
				}				
			}
		}
	}

}

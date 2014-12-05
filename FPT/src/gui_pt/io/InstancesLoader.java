package gui_pt.io;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class InstancesLoader {
	
	public static Instances loadInstances(File file)
	{
		Instances structure = null;
		
		try {
			ArffLoader al = new ArffLoader();
			al.setFile(file);
			structure = al.getDataSet();
			structure.setClassIndex(structure.numAttributes()-1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return structure;
	}
	
	public static Instances loadInstances(String sfile)
	{
		Instances structure = null;
		
		File file = new File(sfile);
		
		try {
			ArffLoader al = new ArffLoader();
			al.setFile(file);
			structure = al.getDataSet();
			structure.setClassIndex(structure.numAttributes()-1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return structure;
	}

}

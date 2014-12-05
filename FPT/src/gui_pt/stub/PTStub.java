package gui_pt.stub;

import gui_pt.stream.RunAsStreamAble;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Observable;

import weka.classifiers.trees.pt.nodes.AbstractNode;

public class PTStub extends Observable implements RunAsStreamAble{
	
	AbstractNode[][] rootPack;
	
//	public static void createRootPack(String des, String[] dataUrl) throws Exception{
//		
//		Instances[] data = new Instances[dataUrl.length];
//		for(int i=0; i<dataUrl.length; i++)
//		{
//			data[i] = InstancesLoader.loadInstances(dataUrl[i]);
//		}
		
			
//		for(int	public static void createRootPack(String des, String[] dataUrl) throws Exception{
//		
//		Instances[] data = new Instances[dataUrl.length];
//		for(int i=0; i<dataUrl.length; i++)
//		{
//			data[i] = InstancesLoader.loadInstances(dataUrl[i]);
//		}
//		
//			
//		for(int i = 0; i<data.length; i++)
//		{
//			String[] options = {"-C",i+"","-E","0.0025",""};
//			
//			
//			PTTD pttd = new PTTD();
//			
////			pttd.setOptions(options);
//			
//			pttd.setUseCustomSets(false);
//			try {
//				pttd.buildClassifier(data[i]);
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//					
//			AbstractNode[] root = new AbstractNode[pttd.getModel().numInducers()];
//			
//			for(int j=0; j<pttd.getModel().numInducers(); j++)
//			{
//				root[j] = pttd.getModel().getCandidates(j)[0];
//			}
//			
//			File desDir = new File(des);
//			desDir.mkdirs();
//			
//			FileOutputStream fos = new FileOutputStream(new File(desDir+"/tree"+"_"+i+".ano"));
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			
//			oos.writeObject(root);
//			
//			oos.close();
//			fos.close();
//		}		
//	} i = 0; i<data.length; i++)
//		{
//			String[] options = {"-C",i+"","-E","0.0025",""};
//			
//			
//			PTTD pttd = new PTTD();
//			
////			pttd.setOptions(options);
//			
//			pttd.setUseCustomSets(false);
//			try {
//				pttd.buildClassifier(data[i]);
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//					
//			AbstractNode[] root = new AbstractNode[pttd.getModel().numInducers()];
//			
//			for(int j=0; j<pttd.getModel().numInducers(); j++)
//			{
//				root[j] = pttd.getModel().getCandidates(j)[0];
//			}
//			
//			File desDir = new File(des);
//			desDir.mkdirs();
//			
//			FileOutputStream fos = new FileOutputStream(new File(desDir+"/tree"+"_"+i+".ano"));
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			
//			oos.writeObject(root);
//			
//			oos.close();
//			fos.close();
//		}		
//	}
	
	
	/**
	 * 
	 * @param url
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadRootPack(String url) throws IOException, ClassNotFoundException{
		
		File dir = new File(url);
		
		String[] trees = dir.list();
		
		rootPack = new AbstractNode[trees.length][];
		
		for(int i=0; i<trees.length; i++)
		{
			FileInputStream fis = new FileInputStream(new File(dir+"/"+trees[i]));
			ObjectInputStream ois = new ObjectInputStream(fis);
									
			rootPack[i] = (AbstractNode[])ois.readObject();
		
			ois.close();
			fis.close();
		}
	}
	
	public String toString(){
		
		return "PTStub";
	}
	
	//#################################################################################################
	//RunAsStreamAble
	//#################################################################################################
	
	@Override
	public void startStream(){
		
		try {
			this.loadRootPack("././TestFiles/patternTrees");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int i=0; i<rootPack.length; i++)
		{
			try {
System.out.println("go to sleep");
				Thread.sleep(5000);
System.out.println("awake");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.setChanged();
System.out.println(rootPack[i]);
			this.notifyObservers(rootPack[i]);
		}	
	}

	@Override
	public Observable getObservalbe() {

		return this;
	}
}

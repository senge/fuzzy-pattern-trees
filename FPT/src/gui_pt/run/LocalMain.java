package gui_pt.run;

import gui_pt.gui.StartWindow;

import javax.swing.SwingUtilities;

public class LocalMain { 

	
	public static void main(String[] args) throws Exception
	{			
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				
				StartWindow sw = new StartWindow();
			}
		});
		
//		String[] data = {"C:/Dokumente und Einstellungen/Sascha/Desktop/iris/5/5/TRAIN.arff"
//				,"C:/Dokumente und Einstellungen/Sascha/Desktop/iris/5/3/TRAIN.arff"
//				,"C:/Dokumente und Einstellungen/Sascha/Desktop/iris/5/5/TRAIN.arff"
//				,"C:/Dokumente und Einstellungen/Sascha/Desktop/iris/5/3/TRAIN.arff"
//				,"C:/Dokumente und Einstellungen/Sascha/Desktop/iris/5/5/TRAIN.arff"};
////		
//		PTStub.createRootPack("C:/Dokumente und Einstellungen/Sascha/Desktop/patternTrees"
//								, data);
		
//		try {
//			try {
//				Classifier cl = (Classifier) Class.forName("weka.classifiers.trees.PTTD").newInstance();
//
//System.out.println(cl.toString());
//			} catch (InstantiationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
}

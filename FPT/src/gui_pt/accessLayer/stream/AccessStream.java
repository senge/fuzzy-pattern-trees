package gui_pt.accessLayer.stream;

import gui_pt.accessLayer.loader.PTLoader;
import gui_pt.accessLayer.util.AccessPT;

import java.util.Observable;
import java.util.Observer;

import weka.classifiers.trees.AbstractPT;

public class AccessStream extends Observable implements Observer{

	@Override
	public void update(Observable arg0, Object arg1) {
		
		
		
		AbstractPT abstractPT = (AbstractPT)arg1;
		
		AccessPT accPT = PTLoader.wrapTree(abstractPT);
		
		this.setChanged();
		this.notifyObservers(accPT);
	}
}

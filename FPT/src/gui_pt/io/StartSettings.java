package gui_pt.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeMap;

public class StartSettings implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4859558182027622534L;
	
	private String workspacePath = "./PTV/workspacePT";
	private String lastStorePath = null;
	private boolean showWorkspaceLuncher = true;
	
	//stream commands
	private String defaultCommand = null;
			
	private TreeMap<String, String> command_Map = null;	
	
	public void writeToFile(File file) throws IOException{
		
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		
		oos.close();
		fos.close();
	}
	
	public void addCommand(String key, String command)
	{
		if(command_Map == null)
		{
			command_Map = new TreeMap<String, String>();
		}
		command_Map.put(key, command);
	}
	
	public void deleteCommand(String key)
	{
		command_Map.remove(key);
	}
	
	public String[] getCommandKeys()
	{
		if(command_Map != null)
		{
			String[] s_A = new String[command_Map.size()];
			return command_Map.keySet().toArray(s_A);
		}
		else
		{
			return null;
		}
	}
	
	public String getCommand(String key)
	{
		return command_Map.get(key);
	}
	
	public String getDefaultCommand()
	{
		return this.defaultCommand;
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public boolean isShowWorkspaceLuncher() {
		return showWorkspaceLuncher;
	}

	public void setShowWorkspaceLuncher(boolean showWorkspaceLuncher) {
		this.showWorkspaceLuncher = showWorkspaceLuncher;
	}

	public String getLastStorePath() {
		return lastStorePath;
	}

	public void setLastStorePath(String lastStorePath) {
		this.lastStorePath = lastStorePath;
	}	
}

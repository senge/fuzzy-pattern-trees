package gui_pt.guiUtil;

public class UserObjectWrapper {
	
	public Object obj;
	private boolean isOpen = false;
	private String text;
	
	public UserObjectWrapper(Object obj, String toStringText){
		
		this.obj = obj;
		this.text = toStringText;
	}
	
	public String toString(){
		
		return text;
	}
	
	//##############################################################################################
	// GET and SET
	//##############################################################################################

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
}

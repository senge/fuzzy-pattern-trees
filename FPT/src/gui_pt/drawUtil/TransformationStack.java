package gui_pt.drawUtil;

import java.awt.geom.AffineTransform;
import java.util.Stack;

public class TransformationStack {
	
	private Stack<AffineTransform> stack = new Stack<AffineTransform>();
	
	public TransformationStack()
	{
		AffineTransform trans = new AffineTransform();
		stack.push(trans);
	}
	
	public void push(AffineTransform trans)
	{
		AffineTransform newTrans = new AffineTransform(stack.peek());
		newTrans.concatenate(trans);
		stack.push(newTrans);
	}
	
	public AffineTransform pop()
	{
		if(stack.size() > 1)
		{
			return stack.pop();
		}
		return stack.peek();
	}
	
	public AffineTransform peek()
	{
		return stack.peek();
	}
	
	public void load(AffineTransform trans)
	{
		stack.push(trans);
	}

}

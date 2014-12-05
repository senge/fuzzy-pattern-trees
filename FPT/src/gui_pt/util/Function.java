package gui_pt.util;

import java.util.LinkedList;

public class Function {
	
	private int arraySize = 5;
	
	public String parseFunction(String func)
	{
		return parseFunction(func, 0);
	}
	
	private String parseFunction(String func, int depth)
	{
		int brackedCount = 0;
		
		
		for(int i=0; i<func.length(); i++)
		{
			//1. Auflösen der Klammern
			if(func.charAt(i) == ('('))
			{
				brackedCount++;
			}
			else if(brackedCount > 0)
			{
				Token token = new Token();
				StringBuffer subFunc = new StringBuffer();
				int j=1;
				while(brackedCount > 0)
				{
					if(func.charAt(i+j) == ')')
					{
						brackedCount--;
					}
					else if(func.charAt(i+j) == '(')
					{
						brackedCount++;
					}
					assert brackedCount < 0;
					
					if(brackedCount >0) subFunc.append(func.charAt(i+j));
					
					j++;
				}
				//Wenn operatoren im token enthalten sind
				if(subFunc.toString().contains("+")
						|| subFunc.toString().contains("-")
						|| subFunc.toString().contains("*")
						|| subFunc.toString().contains("/")
						|| subFunc.toString().contains("^")
						|| subFunc.toString().contains("exp")
						|| subFunc.toString().contains("log")
						|| subFunc.toString().contains("cos")
						|| subFunc.toString().contains("sin")
						|| subFunc.toString().contains("tan")
						|| subFunc.toString().contains("_"))
				{
					token.sb.append(parseFunction(subFunc.toString(),depth+1));
				}				
				i = i+j;
			}
			else
			{
				/*if(func.charAt(i) == ('+')
						|| func.charAt(i) == ('-')
						|| func.charAt(i) == ('*')
						|| func.charAt(i) == ('/')
						|| func.charAt(i) == ('^')
						|| func.charAt(i) == ('_')
						|| func.charAt(i) == ('exp')
						|| func.charAt(i) == ('log')
						|| func.charAt(i) == ('cos')
						|| func.charAt(i) == ('sin')
						|| func.charAt(i) == ('tan'))
				{
					Token token = new Token();
					token.sb.append(func.charAt(i));
				}*/
			}
		}
		
		
		return null;
	}
	
	private class TokenNode {
		
		public LinkedList<Token> tokenList = new LinkedList<Token>();
		
	}
	
	private class Token{
		
		public StringBuffer sb = new StringBuffer();
	}

}

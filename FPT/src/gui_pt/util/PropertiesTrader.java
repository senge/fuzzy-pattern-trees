package gui_pt.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesTrader {
	
	private static Properties properties = null;
	
	private PropertiesTrader(){}
	
	public static Properties getProperties()
	{
		if(properties == null)
		{
			try {
				properties = new Properties();
				FileInputStream fis = new FileInputStream("res/properties/gui.properties");
				BufferedInputStream stream = new BufferedInputStream(fis);
			
				properties.load(stream);
				stream.close();
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return properties;
		}
		else
		{
			return properties;
		}
	}

}

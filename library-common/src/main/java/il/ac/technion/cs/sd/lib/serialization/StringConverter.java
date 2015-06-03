package il.ac.technion.cs.sd.lib.serialization;

import com.thoughtworks.xstream.XStream;

public class StringConverter {


	public static String convertToString(Object data){
		if (data == null) 
			throw new IllegalArgumentException("data cannot be null");

		XStream xstream = new XStream();
		return xstream.toXML(data);
		
	}

	public static Object convertFromString(String data){
		
		if (data == null) 
			throw new IllegalArgumentException("data cannot be null");

		XStream xstream = new XStream();
		return xstream.fromXML(data);
	}
	
	
}

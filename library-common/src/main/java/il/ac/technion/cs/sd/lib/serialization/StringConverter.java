package il.ac.technion.cs.sd.lib.serialization;


import com.google.gson.Gson;

public class StringConverter {


	public static String convertToString(Object data){
		if (data == null) 
			throw new IllegalArgumentException("data cannot be null");

		Gson gson = new Gson();
		return gson.toJson(data); 
		
	}

	public static <T> T convertFromString(String data, Class<T> classOfT){
		
		if (data == null) 
			throw new IllegalArgumentException("data cannot be null");

		Gson gson = new Gson();
        return gson.fromJson(data, classOfT);
	}
	
}

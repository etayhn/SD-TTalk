package il.ac.technion.cs.sd.lib.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.google.gson.Gson;

public class FileHandler {


	public static void writeToFile(Object data, String fileName) throws IOException{
		
		if(fileName == null || data == null){
			throw new IllegalArgumentException("null parameters for write to file");
		}
		Gson gson = new Gson();
		String dataAsJson = gson.toJson(data);
		
		FileOutputStream outputStream = new FileOutputStream(fileName);
		ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
		objectStream.writeObject(dataAsJson);
		objectStream.close();
		outputStream.close();
		
	}

	public static <T> T readFromFile(String fileName, Class<T> classOfT) throws IOException{
		
		if(fileName == null ){
			throw new IllegalArgumentException("null parameters for read from file");
		}

		FileInputStream input = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(input);
        
        String receivedData = null;
        
        // shouldn't happen.
		try {
			receivedData = (String) in.readObject();

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("couldn't find Serializable. WTF???");
		}finally{
			in.close();
			input.close();
		}
		Gson gson = new Gson();
        return gson.fromJson(receivedData, classOfT);
	}
	
}

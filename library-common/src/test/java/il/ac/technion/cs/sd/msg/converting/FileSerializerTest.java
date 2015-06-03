package il.ac.technion.cs.sd.msg.converting;
import static org.junit.Assert.assertEquals;
import il.ac.technion.cs.sd.lib.serialization.FileHandler;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;


public class FileSerializerTest {

	private static final String FILE_NAME = System.getProperty("user.dir") + "/test.txt";

	@Test
	public void testWriteSimpleObjectToFile() throws IOException, ClassNotFoundException {

		String data = "data";
		FileHandler.writeToFile(data, FILE_NAME);
		String readFromFileString = (String) FileHandler.readFromFile(FILE_NAME);
		assertEquals(data, readFromFileString);

	}
	
	@After
	public void clean(){
		
		File file = new File(FILE_NAME);
		file.delete();
	}

	@Test
	public void testConvertComplexObjectToString() throws IOException, ClassNotFoundException {

		ComplexObject data = new ComplexObject(10, 5.5, "Avner");
		FileHandler.writeToFile(data, FILE_NAME);
		ComplexObject convertedBackObject = (ComplexObject) FileHandler.readFromFile(FILE_NAME);
		assertEquals(data, convertedBackObject);

	}

}


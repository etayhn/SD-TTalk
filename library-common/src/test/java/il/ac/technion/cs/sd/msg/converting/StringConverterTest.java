package il.ac.technion.cs.sd.msg.converting;

import static org.junit.Assert.assertEquals;
import il.ac.technion.cs.sd.lib.serialization.StringConverter;

import java.awt.Container;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

public class StringConverterTest {
	
	@Test
	public void testConvertSimpleObjectToString() throws IOException, ClassNotFoundException {

		String data = "data";
		String dataAsString = StringConverter.convertToString(data);
		String convertedBackString = StringConverter.convertFromString(dataAsString, String.class);
		assertEquals(data, convertedBackString);

	}

	@Test
	public void testConvertComplexObjectToString() throws IOException, ClassNotFoundException {

		ComplexObject data = new ComplexObject(10, 5.5, "Avner");
		String dataAsString = StringConverter.convertToString(data);
		ComplexObject convertedBackObject = StringConverter.convertFromString(dataAsString, ComplexObject.class);
		assertEquals(data, convertedBackObject);

	}
	
	@Test
	public void testConvertContainerSimpleObjectToString() throws IOException, ClassNotFoundException {

		List<String> list = new ArrayList<>();
		list.add("Avner");
		list.add("Itay");
		String dataAsString = StringConverter.convertToString(list);
		List<String> listAfterParse = StringConverter.convertFromString(dataAsString, List.class);
		System.out.println(listAfterParse.toString());
		assertEquals(list, listAfterParse);

	}

	@Test
	public void testConvertContainerComplexObjectToString() throws IOException, ClassNotFoundException {

		List<ComplexObject> list = new ArrayList<>();
		list.add(new ComplexObject(1, 1.1, "Avner"));
		list.add(new ComplexObject(2, 2.2, "Itay"));
		String dataAsString = StringConverter.convertToString(list);
		List<ComplexObject> listAfterParse = Arrays.asList(StringConverter.convertFromString(dataAsString, ComplexObject[].class));
		System.out.println(listAfterParse.get(0).getClass());
//		List<ComplexObject> parsedComplexObjectList = new ArrayList<>();
//		listAfterParse.forEach(s -> parsedComplexObjectList.add(StringConverter.convertFromString(s, ComplexObject.class)));
		System.out.println(listAfterParse);
		assertEquals(list, listAfterParse);

	}

}

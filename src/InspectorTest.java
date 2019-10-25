import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

/**
 * Uses ClassA to test the public static methods
 * @author Benedict Mendoza
 *
 */
public class InspectorTest {
	ClassA testObj = new ClassA();

	
	@Test
	public void test_for_method_names() {
		String result = Inspector.get_declared_methods_info(testObj.getClass());
		
		// methods should be run(), getVal(), setVal(), toString() and printSomething()
		List<String> function_names_list = Arrays.asList("run", "getVal", "setVal", "toString", "printSomething");
		HashSet<String> function_names = new HashSet<String>();
		function_names.addAll(function_names_list);
		
		// split result so the head of each index is the function name
		String[] result_parsed = result.trim().split("Method Name:");
		
		// index at 1 since index at 0 is an empty string
		for (int i=1; i<result_parsed.length; i++) {
			String function_name = result_parsed[i].split("\n")[0].trim();
//			System.out.println(function_name);
			assertTrue(function_names.contains(function_name));
		}
	}
	
	@Test
	public void test_for_method_modifiers() {
		Method[] dec_methods = testObj.getClass().getDeclaredMethods();
		HashMap<String, String> method_to_modifiers = new HashMap<String, String>();
		method_to_modifiers.put("run", "public");
		method_to_modifiers.put("toString", "public");
		method_to_modifiers.put("setVal", "public");
		method_to_modifiers.put("getVal", "public");
		method_to_modifiers.put("printSomething", "private");


		for (Method aMethod : dec_methods) {
			String modifiers = Inspector.get_modifiers(aMethod);
			String correct_modifiers = method_to_modifiers.get(aMethod.getName());
			assertTrue(correct_modifiers.equals(modifiers));
		}
	}

	@Test
	public void test_for_constructor_names() {
		String result = Inspector.get_constructors_info(testObj.getClass());
		
		// constructor should return two contructor names both called ClassA
		String correct_result = "ClassA";
		String[] result_parsed = result.trim().split("Constructor Name:");
		
		// index at 1 since index at 0 is an empty string
		for (int i=1; i<result_parsed.length; i++) {
			String construc_name = result_parsed[i].split("\n")[0].trim();
//			System.out.println(construc_name);
			assertTrue(correct_result.equals(construc_name));
		}
	}
	
	
	
	@Test
	public void test_for_field_names() {
		Field[] obj_fields = testObj.getClass().getDeclaredFields();
		// fields should be val, val2, val3
		List<String> field_names_list = Arrays.asList("val", "val2", "val3");
		HashSet<String> field_names = new HashSet<String>();
		field_names.addAll(field_names_list);
		
		for (Field aField : obj_fields) {
			String result = Inspector.get_field_info(testObj, aField);
			
			// split result so the head of each index is the function name
			String[] result_parsed = result.trim().split("\n");
			
			// first index must contain the index name
			String field_name = result_parsed[0].trim().split("Field Name:")[1].trim(); // account for first index being blank
//			System.out.println(field_name);
			// check for field name is correct
			assertTrue(field_names.contains(field_name));
			
		}
	}
	
	@Test
	public void test_for_field_modifiers() {
		Field[] dec_fields = testObj.getClass().getDeclaredFields();
		HashMap<String, String> field_to_modifiers = new HashMap<String, String>();
		field_to_modifiers.put("val", "private");
		field_to_modifiers.put("val2", "private");
		field_to_modifiers.put("val3", "private");


		for (Field aField : dec_fields) {
			String modifiers = Inspector.get_modifiers(aField);
			String correct_modifiers = field_to_modifiers.get(aField.getName());
			assertTrue(correct_modifiers.equals(modifiers));
		}
	}
	


	@Test
	public void test_for_classArray_to_string_conversion() {
		Class[] testClassArray = {String.class, Integer.class, Float.class, Double.class};
		String class_array_string = Inspector.classArrayToString(testClassArray, ", ");
		
		// check that the type names in the array are correct
		String[] class_array_parsed = class_array_string.split(", ");
		for (int i=0; i<class_array_parsed.length; i++) {
			
			assertTrue(testClassArray[i].getTypeName().equals(class_array_parsed[i]));
		}
	}

}

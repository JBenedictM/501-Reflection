import java.io.File;
import java.lang.reflect.Method;

public class DriverBonus {
	
	
	public static void main(String[] args) {
		
		// ensure all arguments are present
		if (args.length != 3) {
			System.out.printf("Usage: java DriverBonus [Class Path with inspect(Object, boolean) method] [Class to inspect] [true/false]\n");
			System.exit(-1);
		}
		
		String inspect_class_name = args[0];
		String argument_class_name = args[1];
		String boolean_string = args[2];
		
		// ensure boolean argument is correct
		if (!boolean_string.equalsIgnoreCase("true") && !boolean_string.equalsIgnoreCase("false")) {
			System.out.println("Incorrect boolean argument, should be {true, false}");
			System.exit(-1);
		}
		
		// create Inspector class
		Class inspect_class = getClassObject(inspect_class_name);
		if (inspect_class == null) System.exit(-1);
		
		// create Inspector instance
		Object inspect_obj = getClassInstance(inspect_class);
		if (inspect_obj == null) System.exit(-1);

		
		// create argument class object
		Class argument_class = getClassObject(argument_class_name);
		if (argument_class == null) System.exit(-1);

		Object argument_obj = getClassInstance(argument_class);
		if (argument_obj == null) System.exit(-1);

		
		// invoke inspect(Object, boolean)
		Method inspect_method = null;
		try {
			String method_name = "inspect";
			Class[] parameter = {Object.class, boolean.class};
			inspect_method = inspect_class.getDeclaredMethod(method_name, parameter);

		} catch (NoSuchMethodException nm) {
			System.out.printf("Failed to access inspect(Object, boolean) method\n");
			System.exit(-1);
		}
		
		boolean recursive = Boolean.parseBoolean(boolean_string);
		Object[] parameter_objs = {argument_obj, recursive};
		
		try {
			// should have no returns
			inspect_method.invoke(inspect_obj, parameter_objs);
			
		} catch (IllegalAccessException ia) {
			System.out.printf("Illegal access to method occured\n");
			System.exit(-1);
			
		} catch (IllegalArgumentException ia) {
			System.out.printf("Illegal arguments given to method\n");
			System.exit(-1);
			
		} catch (Exception e) {
			System.out.printf("Unexpected exception occured during %s invokation\n", inspect_method.getName());
			System.exit(-1);
		}
		
	}
	
	public static Class getClassObject(String className) {
		
		Class class_obj = null;
		try {
			class_obj = Class.forName(className);

		} catch (ClassNotFoundException ex) {
			System.out.printf("Failed to get class object of %s\n", className);
			
		} catch (Exception e) {
			System.out.printf("Failed to get class object of %s\n", className);
			e.printStackTrace();
		}
		
		System.out.flush();
		return class_obj;
	}
	
	public static Object getClassInstance(Class class_obj) {
		
		Object class_instance = null;
		try {
			class_instance = class_obj.newInstance();

		} catch (InstantiationException ie) {
			System.out.printf("Failed to instantiate %s\n", class_obj.getName());
			
		} catch (IllegalAccessException ia) {
			System.out.printf("Failed to access class to instantiate %s\n", class_obj.getName());
		
		}
		
		System.out.flush();
		return class_instance;
	}
	
	
}

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Inspector {
	
	// tells recursion calls how many tabs they should use
	private int tab_level = 0;
	public void inspect(Object obj, boolean recursive) {
		
		inspect_with_tab_level(obj, recursive, 0);
			
	}
	
	private void inspect_with_tab_level(Object obj, boolean recursive, int tab_level) {
		// Base case we have reached past the Object Class
		if (obj == null) {
			return;
		}
				
		Class obj_class = null;
		if (obj instanceof Class) {
			obj_class = (Class)obj;
		} else {
			obj_class = obj.getClass();
		}
				
		String indent = new String(new char[tab_level]).replace("\0", "\t");
		// print class name
		System.out.println(indent + "Class name: " + obj_class.getName() + "\n");
				
		// recurse on super class
		Class superclass = obj_class.getSuperclass();
		System.out.println(indent + "Super-Class");
		inspect_with_tab_level(superclass, recursive, tab_level++);
		
		// recurse on interfaces
		System.out.println(indent + "Interfaces");
		Class[] interfaces = obj_class.getInterfaces();
		for (Class anInterface : interfaces) {
			inspect_with_tab_level(anInterface, recursive, tab_level++);
		}

				
		// print constructors
		System.out.println(indent + "Constructors");
		display_constructors(obj_class);
		
		// print methods
		System.out.println(indent + "Methods");
		display_declared_methods(obj_class);
		
		// print fields
		System.out.println(indent + "Fields");
		display_fields(obj, recursive);
		
		// check for array
		if (obj_class.isArray()) {
			// loop through the array
			for (int i=0; i<Array.getLength(obj); i++) {
				Object array_obj = Array.get(obj, i);
				inspect_with_tab_level(array_obj, recursive, tab_level++);
			}
		} 
	}	
		
	
	private String display_interfaces(Class[] interfaces, boolean recursive) {
		if (interfaces.length == 0) 
			return "";
		
		
		StringBuffer interfaces_list = new StringBuffer(classArrayToString(interfaces, ", "));
		
		// recursive step
		for (Class anInterface : interfaces) {
			String other_interfaces = display_interfaces(anInterface.getInterfaces(), recursive);
			
			if (!other_interfaces.isEmpty()) {
				interfaces_list.append(", ");
				interfaces_list.append(other_interfaces);
			}
		}
		
		return interfaces_list.toString();
		
		
	}
	
	
	private void display_fields(Object obj, boolean recursive) {
		Class class_obj = obj.getClass();
		Field[] class_fields = class_obj.getDeclaredFields();
		
		for (Field aField : class_fields) {
			
			if (!aField.isAccessible()) {
				aField.setAccessible(true);
			}
			
			// print name
			String field_name = aField.getName();
			System.out.printf("Field Name: %s\n", field_name);
			
			// print type
			String field_type = aField.getType().getTypeName();
			System.out.printf("Field Type : %s\n", field_type);
			
			// print modifiers
			String field_modifiers = get_modifiers(aField);
			System.out.printf("Field Modifiers: %s\n", field_modifiers);
			
			// print current value
			String value = null;
			try {
				Object field_value = aField.get(obj);
				value = field_value.toString();

			} catch (IllegalAccessException iae) {
				value = "IllegalAccessException thrown";

			} catch (IllegalArgumentException iae) {
				value = "IllegalArgumentException thrown";

			} catch (NullPointerException npe) {
				value = "NullPointerException thrown";
			}
			System.out.printf("Current Value: %s\n\n", value);

			
		}
	}
	
	/**
	 * prints the constructors of a class obj
	 * @param class_obj : Class object containing the constructors
	 */
	private void display_constructors(Class class_obj) {
		Constructor[] class_constructors = class_obj.getDeclaredConstructors();
		
		for (Constructor aConstructor : class_constructors) {
			// set accessible if constructor is not accessible
			if (!aConstructor.isAccessible()) {
				aConstructor.setAccessible(true);
			}
			
			// print name
			String construc_name = aConstructor.getName();
			System.out.printf("Constructor Name: %s\n",  construc_name);
						
			// print parameter types
			String parameter_types = get_constructor_parameters(aConstructor);
		
			System.out.printf("Parameter Types: %s\n", parameter_types);
			
			// print possible modifiers
			String modifiers = get_modifiers(aConstructor);
			System.out.printf("Modifers: %s\n\n", modifiers);
			
		}
		
	}
	
	private void display_declared_methods(Class class_obj) {
		Method[] class_methods = class_obj.getDeclaredMethods();
		
		for (Method aMethod : class_methods) {
			// set accessible if method is not accessible
			if (!aMethod.isAccessible()) {
				aMethod.setAccessible(true);
			}
			
			// print name
			String method_name = aMethod.getName();
			System.out.printf("Method Name: %s\n",  method_name);
			
			
			// print exceptions thrown
			String exceptions = classArrayToString(aMethod.getExceptionTypes(), ", ");
			if (exceptions.isEmpty()) {
				System.out.println("Exceptions Thrown: None");
			} else {
				System.out.printf("Exceptions Thrown: %s\n", exceptions);
			}
			
			// print parameter types
			String parameters = classArrayToString(aMethod.getParameterTypes(), ", ");
			if (parameters.isEmpty()) {
				System.out.println("Parameter Types: None");
			} else {
				System.out.printf("Parameter Types: %s\n", parameters);
			}
			
			// print return types
			String return_type = aMethod.getReturnType().getTypeName();
			if (return_type.isEmpty()) {
				System.out.println("Return Type: Void");
			} else {
				System.out.printf("Return Type: %s\n", return_type);
			}

			// print modifiers
			String modifiers = get_modifiers(aMethod);
			System.out.printf("Modifiers: %s\n\n", modifiers);
			
		}
	}
	
	private String classArrayToString(Class[] class_array, String separator) {
		StringBuilder sb = new StringBuilder();
		
		if (class_array.length > 0) {
			
			sb.append(class_array[0].getTypeName());
			
			for (int i=1; i<class_array.length; i++) {
				sb.append(separator);
				sb.append(class_array[i].getTypeName());
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param class_obj : object representing a Class, Constructor
	 * @return : returns a string of paramters like: "param1, param2,...., paramN"
	 * 
	 */
	private String get_constructor_parameters(Constructor constructor_obj) {
		StringBuffer param_buff = new StringBuffer();
		Class[] parameters = constructor_obj.getParameterTypes();
		
		if (parameters.length > 0) {
			param_buff.append(parameters[0].getTypeName());
			
			for (int i=1; i<parameters.length; i++) {
				param_buff.append(", " + parameters[i].getTypeName());
			}
		}
		
		return param_buff.toString();
	}
	
	/**
	 * 
	 * @param obj : must be of type Class, Constructor
	 * @return : modifiers in string like so "modifier1, modifier2, ..., modifierN"
	 * 
	 */
	private String get_modifiers(Object class_obj) {
		StringBuffer modifiers =  new StringBuffer();
		
		int construc_modifier = class_obj.getClass().getModifiers();
		
		if (Modifier.isPublic(construc_modifier))
			modifiers.append("public, ");
		
		if (Modifier.isPrivate(construc_modifier))
			modifiers.append("private, ");
		
		if (Modifier.isProtected(construc_modifier)) 
			modifiers.append("protected, ");
		
		if (Modifier.isStrict(construc_modifier)) 
			modifiers.append("strict, ");
		
		if (Modifier.isStatic(construc_modifier)) 
			modifiers.append("static, ");
		
		if (Modifier.isFinal(construc_modifier)) 
			modifiers.append("final, ");
		
		if (Modifier.isAbstract(construc_modifier)) 
			modifiers.append("abstract, ");
		
		if (Modifier.isInterface(construc_modifier)) 
			modifiers.append("interface, ");
			
		if (Modifier.isNative(construc_modifier)) 
			modifiers.append("native, ");
		
		if (Modifier.isSynchronized(construc_modifier)) 
			modifiers.append("synchronized, ");
		
		if (Modifier.isTransient(construc_modifier)) 
			modifiers.append("transient, ");
		
		if (Modifier.isVolatile(construc_modifier)) 
			modifiers.append("volatile, ");
		
		// remove the ", " from the string buffer
		int buff_length = modifiers.length();
		if (buff_length != 0) 
			modifiers.delete(buff_length-2, buff_length);
		
		
		return modifiers.toString();
	}
	
	
	public static void main(String[] args) {
		Inspector ins = new Inspector();
		String[] temp = {"hello", "world", "bye"};
		
		ins.inspect(temp, false);
		
		
	}
}

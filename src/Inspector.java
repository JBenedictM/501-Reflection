import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Inspector {
	
	public void inspect(Object obj, boolean recursive) {
		
		inspect_with_tab_level(obj, obj.getClass(), recursive, 0);
			
	}
	
	private void inspect_with_tab_level(Object obj, Class obj_class, boolean recursive, int tab_level) {
		
		// Base case we have reached past the Object Class
		if (obj == null || obj_class == null) {
			return;
		}
		
				
		String indent = new String(new char[tab_level]).replace("\0", "\t");
		// print class name
		System.out.println(indent + "Class name: " + obj_class.getName() + "\n");
				
		// recurse on super class
		Class superclass = obj_class.getSuperclass();
		System.out.println(indent + "Super-Class");
		if (superclass != null) {
			inspect_with_tab_level(obj, superclass, recursive, tab_level+1);
		} else {
			System.out.println(indent + "None\n");
		}
		
		// recurse on interfaces
		System.out.println(indent + "Interfaces");
		Class[] interfaces = obj_class.getInterfaces();
		if (interfaces.length > 0) {
			for (Class anInterface : interfaces) {
				inspect_with_tab_level(anInterface, anInterface, recursive, tab_level+1);
			}	
		} else {
			System.out.println(indent + "None\n");
		}
		
		// print constructors 
		System.out.println(indent + "Constructors");
		String constructors_info = get_constructors_info(obj_class);
		if (!constructors_info.isEmpty()) {
			constructors_info = constructors_info.replaceAll("(?m)^", indent); 		// adds in the indentation
			System.out.print(constructors_info);
		} else {
			System.out.println(indent + "None\n");
		}
		
		
		// print methods 
		System.out.println(indent + "Methods");
		String methods_info = get_declared_methods_info(obj_class);
		if (!methods_info.isEmpty()) {
			methods_info = methods_info.replaceAll("(?m)^", indent);				// adds in the indentation
			System.out.print(methods_info);
		} else {
			System.out.println(indent + "None\n");
		}
		
		// print fields 
		System.out.println(indent + "Fields");
		String field_info = get_field_info(obj, obj_class, recursive, tab_level);
		
		
		// check for array
		if (obj_class.isArray()) {
			System.out.println(indent + "Array Contents");
			// loop through the array
			for (int i=0; i<Array.getLength(obj); i++) {
				Object array_obj = Array.get(obj, i);
				System.out.printf(indent + "\t" + "Object at index: %d\n", i);
				if (array_obj != null) {
					inspect_with_tab_level(array_obj, array_obj.getClass(), recursive, tab_level+1);
				} else {
					System.out.printf(indent + "\t" + "null\n\n");
				}
			}
		} 
		
	}	
	
	
	private String get_field_info(Object obj, Class obj_class, boolean recursive, int tab_level) {
		
		Field[] obj_fields = obj_class.getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		
		String indent = new String(new char[tab_level]).replace("\0", "\t");
		for (Field aField : obj_fields) {
			
			if (!aField.isAccessible()) {
				aField.setAccessible(true);
			}
			
			// print name
			String field_name = aField.getName();
			sb.append(String.format(indent + "Field Name: %s\n", field_name));
			
			// print type
			Class field_type = aField.getType();
			sb.append(String.format(indent + "Field Type : %s\n", field_type.getTypeName()));
				
			// print modifiers
			String field_modifiers = get_modifiers(aField);
			sb.append(String.format(indent + "Field Modifiers: %s\n", field_modifiers));
			
			// print current value
			String value = null;
			Object field_value = null;
			boolean except_flag = false;
			try {
				field_value = aField.get(obj);
				
			} catch (IllegalAccessException iae) {
				value = "IllegalAccessException thrown";
			} catch (IllegalArgumentException iae) {
				value = "IllegalArgumentException thrown";

			} catch (NullPointerException npe) {
				value = "NullPointerException thrown";

			}
			if (field_value != null ) {
				if (!field_type.isPrimitive()) {
					sb.append(String.format(indent + "Current Value: %s\n", field_value.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(field_value))));
				} else {
					sb.append(String.format(indent + "Current Value: %s\n", field_value));
				}
			} else {
				sb.append(String.format(indent + "Current Value: %s\n", value));	
			}
			System.out.println(sb.toString());
			sb = new StringBuilder();
			
			// recurse
			if (recursive  && field_value != null) {
				
				if (!field_type.isPrimitive()) {
					Class field_value_class = null;
					if (field_value instanceof Class) {
						field_value_class = (Class)field_value;;
					} else {
						field_value_class = field_value.getClass();
					}
					inspect_with_tab_level(field_value, field_value_class, recursive, tab_level+1);
				}
				
			}
			
		}
		
		if (obj_fields.length == 0) {
			System.out.println(indent + "None\n");
		}
		

		return sb.toString();
	}
	
	/**
	 * prints the constructors of a class obj
	 * @param class_obj : Class object containing the constructors
	 */
	private String get_constructors_info(Class class_obj) {
		Constructor[] class_constructors = class_obj.getDeclaredConstructors();
		StringBuilder sb = new StringBuilder();
		
		for (Constructor aConstructor : class_constructors) {
			// set accessible if constructor is not accessible
			if (!aConstructor.isAccessible()) {
//				try {
					aConstructor.setAccessible(true);
//				} catch (SecurityException se) {
//					// ignore
//					// exception is most likely due to Class class being accessed
//					continue;
//				}
			}
			
			// print name
			String construc_name = aConstructor.getName();
			sb.append(String.format("Constructor Name: %s\n",  construc_name));
						
			// print parameter types
			String parameter_types = get_constructor_parameters(aConstructor);
		
			sb.append(String.format("Parameter Types: %s\n", parameter_types));
			
			// print possible modifiers
			String modifiers = get_modifiers(aConstructor);
			sb.append(String.format("Modifers: %s\n\n", modifiers));
			
		}
		
		return sb.toString();
		
	}
	
	private String get_declared_methods_info(Class class_obj) {
		Method[] class_methods = class_obj.getDeclaredMethods();
		StringBuilder sb = new StringBuilder();
		
		for (Method aMethod : class_methods) {
			// set accessible if method is not accessible
			if (!aMethod.isAccessible()) {
				aMethod.setAccessible(true);
			}
			
			// print name
			String method_name = aMethod.getName();
			sb.append(String.format("Method Name: %s\n",  method_name));
			
			
			// print exceptions thrown
			String exceptions = classArrayToString(aMethod.getExceptionTypes(), ", ");
			if (exceptions.isEmpty()) {
				sb.append("Exceptions Thrown: None\n");
			} else {
				sb.append(String.format("Exceptions Thrown: %s\n", exceptions));
			}
			
			// print parameter types
			String parameters = classArrayToString(aMethod.getParameterTypes(), ", ");
			if (parameters.isEmpty()) {
				sb.append("Parameter Types: None\n");
			} else {
				sb.append(String.format("Parameter Types: %s\n", parameters));
			}
			
			// print return types
			String return_type = aMethod.getReturnType().getTypeName();
			if (return_type.isEmpty()) {
				sb.append("Return Type: Void\n");
			} else {
				sb.append(String.format("Return Type: %s\n", return_type));
			}
			
			// print modifiers
			String modifiers = get_modifiers(aMethod);
			sb.append(String.format("Modifiers: %s\n\n", modifiers));
			
		}
		
		return sb.toString();
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
	
	
//	public static void main(String[] args) {
//		Inspector ins = new Inspector();
//		String[] temp = {"hello", "world", "bye"};
//		char[][] sample2 = {{'a', 'b'}, {'c', 'd'}};
//		String sample3 = "hi";
//		int sample4 = 123;
//		
//		ins.inspect(sample3, false);
//		
//		
//	}
}

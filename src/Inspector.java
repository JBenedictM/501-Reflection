import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class Inspector {
	public void inspect(Object obj, boolean recursive) {
		Class obj_class = obj.getClass();
		
		System.out.println("Constructors");
		display_constructors(obj_class);
		
		
		
	
		
	}
	
	/**
	 * prints the constructors of a class obj
	 * @param class_obj : Class object containing the constructors
	 */
	private void display_constructors(Class class_obj) {
		Constructor[] class_constructors = class_obj.getDeclaredConstructors();
		
		for (Constructor construc : class_constructors) {
			// set accessible if constructor is not accessible
			if (!construc.isAccessible()) {
				construc.setAccessible(true);
			}
			
			// print name
			String construc_name = construc.getName();
			System.out.printf("Name: %s\n",  construc_name);
			
			get_modifiers(construc);
			
			// print parameter types
			construc.getTypeParameters();
			
			// print possible modifiers
			String modifiers = get_modifiers(construc);
			System.out.printf("Modifers: %s\n\n", modifiers);
			
			
			
			
			
		}
		
	}
	
	/**
	 * 
	 * @param obj : must of type Class, Constructor
	 * @return : modifiers in string like so modifier1, modifier2, ..., modifierN
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
		String temp = "slajflkjs";
		
		ins.inspect(temp, false);
		
		
	}
}

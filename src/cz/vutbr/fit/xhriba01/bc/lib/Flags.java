package cz.vutbr.fit.xhriba01.bc.lib;

public class Flags {
	
	public static int ACC_PUBLIC = 0x0001; // class, field, method
	public static int ACC_PRIVATE = 0x0002; // class, field, method
	public static int ACC_PROTECTED = 0x0004; // class, field, method
	public static int ACC_STATIC = 0x0008; // field, method
	public static int ACC_FINAL = 0x0010; // class, field, method, parameter
	public static int ACC_SUPER = 0x0020; // class
	public static int ACC_SYNCHRONIZED = 0x0020; // method
	public static int ACC_VOLATILE = 0x0040; // field
	public static int ACC_BRIDGE = 0x0040; // method
	public static int ACC_VARARGS = 0x0080; // method
	public static int ACC_TRANSIENT = 0x0080; // field
	public static int ACC_NATIVE = 0x0100; // method
	public static int ACC_INTERFACE = 0x0200; // class
	public static int ACC_ABSTRACT = 0x0400; // class, method
	public static int ACC_STRICT = 0x0800; // method
	public static int ACC_SYNTHETIC = 0x1000; // class, field, method, parameter
	public static int ACC_ANNOTATION = 0x2000; // class
	public static int ACC_ENUM = 0x4000; // class(?) field inner
	public static int ACC_MANDATED = 0x8000; // parameter
    
    public static boolean isPublic(int access) {
    	return (access & ACC_PUBLIC) != 0;
    }
    
    public static boolean isPrivate(int access) {
    	return (access & ACC_PRIVATE) != 0;
    }
    
    public static boolean isProtected(int access) {
    	return (access & ACC_PROTECTED) != 0;
    }
    
    public static boolean isStatic(int access) {
    	return (access & ACC_STATIC) != 0;
    }
    
    public static boolean isFinal(int access) {
    	return (access & ACC_FINAL) != 0;
    }
    
    public static boolean isSuper(int access) {
    	return (access & ACC_SUPER) != 0;
    }
    
    public static boolean isSynchronized(int access) {
    	return (access & ACC_SYNCHRONIZED) != 0;
    }
    
    public static boolean isVolatile(int access) {
    	return (access & ACC_VOLATILE) != 0;
    }
    
    public static boolean isBridge(int access) {
    	return (access & ACC_BRIDGE) != 0;
    }
    
    public static boolean isVarArgs(int access) {
    	return (access & ACC_VARARGS) != 0;
    }
    
    public static boolean isTransient(int access) {
    	return (access & ACC_TRANSIENT) != 0;
    }
    
    public static boolean isNative(int access) {
    	return (access & ACC_NATIVE) != 0;
    }
    
    public static boolean isInterface(int access) {
    	return (access & ACC_INTERFACE) != 0;
    }
    
    public static boolean isAbstract(int access) {
    	return (access & ACC_ABSTRACT) != 0;
    }
    
    public static boolean isStrict(int access) {
    	return (access & ACC_STRICT) != 0;
    }
    
    public static boolean isSynthetic(int access) {
    	return (access & ACC_SYNTHETIC) != 0;
    }
    
    public static boolean isAnnotation(int access) {
    	return (access & ACC_ANNOTATION) != 0;
    }
	
    public static boolean isEnum(int access) {
    	return (access & ACC_ENUM) != 0;
    }
    
    public static boolean isMandated(int access) {
    	return (access & ACC_MANDATED) != 0;
    }
}

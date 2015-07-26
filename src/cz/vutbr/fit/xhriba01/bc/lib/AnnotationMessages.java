package cz.vutbr.fit.xhriba01.bc.lib;

public class AnnotationMessages extends Container<Annotation> {
	
	public static String TYPE_ERROR = "error";
	
	public static String TYPE_WARNING = "warrning";
	
	public static String TYPE_INFO = "info";
	
	public static String TYPE_GENERAL = "general";
 	
	public void addMessage(String type, String message, int offset, int length) {
		
		add(new Annotation(type, message, new Position(offset, length)));
	}
	
	public void addMessage(String type, String message) {
		
		add(new Annotation(type, message, null));
	}
	
	public void addErrorMessage(String message, int offset, int length) {
		addMessage(TYPE_ERROR, message, offset, length);
	}
	
	public void addInfoMessage(String message, int offset, int length) {
		addMessage(TYPE_INFO, message, offset, length);
	}
	
	public void addGeneralMessage(String message, int offset, int length) {
		addMessage(TYPE_GENERAL, message, offset, length);
	}
	
	public void addWarrningMessage(String message, int offset, int length) {
		addMessage(TYPE_WARNING, message, offset, length);
	}
	
	
	public void addErrorMessage(String message) {
		addMessage(TYPE_ERROR, message);
	}
	
	public void addInfoMessage(String message ) {
		addMessage(TYPE_INFO, message);
	}
	
	public void addGeneralMessage(String message) {
		addMessage(TYPE_GENERAL, message);
	}
	
	public void addWarrningMessage(String message) {
		addMessage(TYPE_WARNING, message);
	}
	
}

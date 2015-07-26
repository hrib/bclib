package cz.vutbr.fit.xhriba01.bc.lib;

public class JavaSourceResult {
	
	private String fText;
	
	private String fFilename;
	
	public JavaSourceResult(String text, String filename) {
		fText = text;
		fFilename = filename;
	}
	
	public JavaSourceResult() {
		
	}
	
	public void setFilename(String filename) {
		 fFilename = filename;
	}
	
	public String getFilename() {
		return fFilename;
	}
	
	public void setText(String text) {
		fText = text;
	}
	
	public String getText() {
		return fText;
	}
	
}
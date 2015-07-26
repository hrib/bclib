package cz.vutbr.fit.xhriba01.bc.lib;


public class Result {
	
	private JavaSourceResult fJavaSourceResult;
	
	private ClassSourceResult fClassSourceResult;
	
	public Result(JavaSourceResult javaSourceResult, ClassSourceResult classSourceResult) {
		fJavaSourceResult = javaSourceResult;
		fClassSourceResult = classSourceResult;
	}
	
	public JavaSourceResult getJavaSourceResult() {
		return fJavaSourceResult;
	}
	
	public ClassSourceResult getClassSourceResult() {
		return fClassSourceResult;
	}
}
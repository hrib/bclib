package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.Collections;
import java.util.Comparator;

public abstract class Node extends Tree<Node> implements Comparator<Node> {
	
	public static enum TYPE {
		CLASS,
		METHOD,
		FIELD,
		INSTRUCTION,
		ROOT,
	}
	
	private int fStartLine = Utils.INVALID_LINE;
	
	private int fSourceOffset = Utils.INVALID_OFFSET;
	
	protected TYPE fType;
	
	public AnnotationMessages fAnnotationMessages = new AnnotationMessages();
	
	public int getSourceLine() {
		return fStartLine;
	}
	
	public void setSourceLine(int startLine) {
		fStartLine = startLine;
	}
	
	public int getSourceOffset() {
		return fSourceOffset;
	}
	
	public void setSourceOffset(int sourceOffset) {
		fSourceOffset = sourceOffset;
	}
	
	public TYPE getType() {
		return fType;
	}
	
	public boolean isMethod() {
		return fType == TYPE.METHOD;
	}
	
	@Override
	public int compare(Node arg0, Node arg1) {
		
		int arg0Line = arg0.getSourceLine();
		int arg1Line = arg1.getSourceLine();
		
		if (arg0Line == -1 && arg0Line == -1) {
			return 0;
		}
		
		int lineDiff = arg0Line - arg1Line;
		
		if (lineDiff != 0) return lineDiff;
		
		int sourceOffsetDif = arg0.getSourceOffset() - arg1.getSourceOffset();
		
		if (sourceOffsetDif > 0) {
			return sourceOffsetDif;
		}
		
		return lineDiff;
	}
	
	public void sort() {
		if (hasChilds()) Collections.sort(getChilds(), this);
	}
}

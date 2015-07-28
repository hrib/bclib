package cz.vutbr.fit.xhriba01.bc.lib;

public class Tree<T extends Tree<T>> extends Container<T>  {

	private T fParent;
	
	public void setParent(T parent) {
		fParent = parent;
	}
	
	public T getParent() {
		return fParent;
	}
	
	public void add(T child) {
		super.add(child);
		child.setParent((T)this);
	}
	
	public void prepend(T child) {
		super.prepend(child);
		child.setParent((T)this);
	}
	
}

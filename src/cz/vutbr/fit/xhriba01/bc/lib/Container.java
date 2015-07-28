package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Container<T> implements Iterable<T> {
	
	private List<T> fChilds;
	
	public void add(T child) {
		
		lazyList();
		
		fChilds.add(child);
		
	}
	
	public void prepend(T child) {
		
		lazyList();
		
		((LinkedList<T>)fChilds).addFirst(child);
	}
	
	public List<T> getChilds() {
		
		lazyList();
		
		return fChilds;
	}
	
	private void lazyList() {
		if (fChilds == null) {
			fChilds = new LinkedList<>();
		}
	}
	
	public boolean hasChilds() {
		return fChilds != null && fChilds.size() != 0;
	}
	
	@Override
	public Iterator<T> iterator() {
		
		if (fChilds == null) {
			return Collections.emptyIterator();
		}
		
		return fChilds.iterator();
	}

}

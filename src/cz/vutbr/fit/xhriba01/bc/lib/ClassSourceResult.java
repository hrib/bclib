package cz.vutbr.fit.xhriba01.bc.lib;


public class ClassSourceResult {
	
	Node fRootNode;
	
	public ClassSourceResult(Node rootNode) {
		fRootNode = rootNode;
	}
	
	public Node getResultNode() {
		return fRootNode;
	}
	
	public void setResultNode(Node node) {
		fRootNode = node;
	}
}

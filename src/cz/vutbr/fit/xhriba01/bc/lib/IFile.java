package cz.vutbr.fit.xhriba01.bc.lib;

import java.io.InputStream;

public interface IFile {
	
	InputStream getContent();
	
	String getFilename();
	
}

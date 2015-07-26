package cz.vutbr.fit.xhriba01.bc.lib;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteFile implements IFile2 {
	
	private byte[] fBytes;
	
	private String fFilename;
	
	public ByteFile(byte[] bytes, String filename) {
		fBytes = bytes;
		fFilename = filename;
	}
	
	public ByteFile(byte[] bytes) {
		this(bytes, null);
	}
	
	@Override
	public InputStream getContent() {
		return new ByteArrayInputStream(fBytes);
	}

	@Override
	public String getFilename() {
		return fFilename;
	}

}

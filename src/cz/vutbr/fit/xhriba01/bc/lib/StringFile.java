package cz.vutbr.fit.xhriba01.bc.lib;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class StringFile implements IFile2 {
	
	private String fContent;
	
	private String fFilename;
	
	public StringFile(String content, String filename) {
		fContent = content;
		fFilename = filename;
	}
	
	public StringFile(String content) {
		this(content, null);
	}
	
	@Override
	public InputStream getContent() {
		// TODO Auto-generated method stub
		try {
			
			return new ByteArrayInputStream(fContent.getBytes("UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public String getFilename() {
		// TODO Auto-generated method stub
		return fFilename;
	}

}

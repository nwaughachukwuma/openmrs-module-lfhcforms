package org.openmrs.module.lfhcforms.utils;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader {

	InputStream getResourceAsStream(String resourcePath);
	
	String getResourceAsString(String resourcePath, String encoding) throws IOException;
}
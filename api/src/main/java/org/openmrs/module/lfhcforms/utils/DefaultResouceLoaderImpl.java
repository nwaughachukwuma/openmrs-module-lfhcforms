package org.openmrs.module.lfhcforms.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class DefaultResouceLoaderImpl implements ResourceLoader {

	@Override
	public InputStream getResourceAsStream(String resourcePath) {
		return getClass().getClassLoader().getResourceAsStream(resourcePath);
	}

	@Override
	public String getResourceAsSting(String resourcePath, String encoding) throws IOException {
		return IOUtils.toString(this.getResourceAsStream(resourcePath), encoding); 
	}
}

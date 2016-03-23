package org.openmrs.module.lfhcforms.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * This resource loader's implementation can be used in unit tests or on the API side.
 * Refer to {@link OmodResouceLoaderImpl} for omod side resource loading.
 * 
 * @author Dimitri Renault
 */
public class DefaultResouceLoaderImpl implements ResourceLoader {

	@Override
	public InputStream getResourceAsStream(String resourcePath) {
		return getClass().getClassLoader().getResourceAsStream(resourcePath);
	}

	@Override
	public String getResourceAsString(String resourcePath, String encoding) throws IOException {
		return IOUtils.toString(this.getResourceAsStream(resourcePath), encoding); 
	}
}

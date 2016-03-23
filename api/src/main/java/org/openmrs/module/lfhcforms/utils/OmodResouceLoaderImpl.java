package org.openmrs.module.lfhcforms.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jfree.util.Log;
import org.openmrs.ui.framework.resource.ResourceFactory;

/**
 * Encapsulation of OpenMRS's {@link ResourceFactory#getInstance()} to load omod resources.
 * Use this implementation of {@link ResourceLoader#} when loading resources from the omod part of the project.
 * 
 * @author Dimitri Renault
 */
public class OmodResouceLoaderImpl implements ResourceLoader {

	private String providerName = "";
	final private ResourceFactory resourceFactory = ResourceFactory.getInstance();
	
	public OmodResouceLoaderImpl(String providerName) {
		this.providerName = providerName;
	}
	
	@Override
	public InputStream getResourceAsStream(String resourcePath) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream( resourceFactory.getResource(providerName, resourcePath) );
		} catch (FileNotFoundException e) {
			Log.error("Could not find resource " + providerName + ":" + resourcePath, e);
		}
		return inputStream;
	}

	@Override
	public String getResourceAsString(String resourcePath, String encoding) throws IOException {
		return resourceFactory.getResourceAsString(providerName, resourcePath);
	}
}

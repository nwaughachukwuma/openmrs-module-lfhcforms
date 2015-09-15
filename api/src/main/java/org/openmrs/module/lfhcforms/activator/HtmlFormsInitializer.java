package org.openmrs.module.lfhcforms.activator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.ui.framework.resource.ResourceProvider;

/**
 * Sets up the HFE forms
 */
public class HtmlFormsInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(HtmlFormsInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting HFE forms for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);

		final ResourceFactory resourceFactory = ResourceFactory.getInstance();
		
		final String providerName = "lfhcforms";
		final String formsPath = "htmlforms/";
		final String prefixedFormsPath = providerName + ":" + formsPath;
		
		final ResourceProvider resourceProvider = resourceFactory.getResourceProviders().get(providerName);
		final File formsDir = resourceProvider.getResource(formsPath);
		if(formsDir == null || formsDir.isDirectory() == false) {
			log.error("No HTML forms could be retrieved from the provided folder: " + prefixedFormsPath);
			return;
		}
		
		final List<String> formPaths = new ArrayList<String>();
		for(File file : formsDir.listFiles()) {
			String name = file.getName();
			String resourcePath = formsPath + name;
			String prefixedResourcePath = prefixedFormsPath + name;
			String xml = "";
			try {
				xml = resourceFactory.getResourceAsString(providerName, resourcePath);
			} catch (IOException e) {
				log.error("The following resource file could not be flattened as a string: " + prefixedResourcePath, e);
				continue;
			}
			// TODO: Validate that the XML is indeed a <htmlform> = PARSE the XLM with the new attributes and all
			formPaths.add(prefixedResourcePath);
		}
		
		final FormService formService = Context.getFormService();
		final HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
		for (String formPath : formPaths) {
			try {
				HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, formPath);
			} catch (IOException e) {
				final String errMsg = "Could not generate HTML form from the following resource file: " + formPath;
				log.error(errMsg, e);
			}
		}
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
		//TODO: Perhaps disable the forms?
	}
}

package org.openmrs.module.lfhcforms.activator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.formentryapp.FormEntryAppService;
import org.openmrs.module.formentryapp.FormManager;
import org.openmrs.module.formentryapp.page.controller.forms.ExtensionForm;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.utils.ExtensionFormUtil;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.ui.framework.resource.ResourceProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Sets up the HFE forms
 */
public class HtmlFormsInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(HtmlFormsInitializer.class);

	protected static final String providerName = "lfhcforms";
	protected static final String formsPath = "htmlforms/";
	
	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting HFE forms for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);

		final ResourceFactory resourceFactory = ResourceFactory.getInstance();
		final ResourceProvider resourceProvider = resourceFactory.getResourceProviders().get(providerName);
		
		final List<String> formPaths = new ArrayList<String>();
		final File formsDir = resourceProvider.getResource(formsPath); // The ResourceFactory can't return File instances, hence the ResourceProvider need
		if(formsDir == null || formsDir.isDirectory() == false) {
			log.error("No HTML forms could be retrieved from the provided folder: " + providerName + ":" + formsPath);
			return;
		}
		for(File file : formsDir.listFiles()) 
			formPaths.add(formsPath + file.getName());
		
		final FormManager formManager = Context.getRegisteredComponent("formManager", FormManager.class);
		final FormEntryAppService hfeAppService = Context.getRegisteredComponent("formEntryAppService", FormEntryAppService.class);
		final FormService formService = Context.getFormService();
		final HtmlFormEntryService hfeService = Context.getService(HtmlFormEntryService.class);
		for (String formPath : formPaths) {
			HtmlForm htmlForm = null;
			try {
				htmlForm = HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, hfeService, providerName, formPath);
			} catch (IOException e) {
				final String errMsg = "Could not generate HTML form from the following resource file: " + formPath;
				log.error(errMsg, e);
				continue;
			}
			ExtensionForm extForm = null;
			try {
				extForm = ExtensionFormUtil.getExtensionFormFromForm(resourceFactory, providerName, formPath, hfeAppService, formManager, htmlForm.getForm());
			} catch (Exception e) {
				final String errMsg = "The form was created but its extension point could not be created in Configure Metadata: " + formPath;
				log.error(errMsg, e);
				continue;
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

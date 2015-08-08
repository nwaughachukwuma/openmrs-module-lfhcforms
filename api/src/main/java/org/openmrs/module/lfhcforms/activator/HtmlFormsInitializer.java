package org.openmrs.module.lfhcforms.activator;

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
		
		try {
			ResourceFactory resourceFactory = ResourceFactory.getInstance();
			FormService formService = Context.getFormService();
			HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

			List<String> htmlforms = Arrays.asList(
					"lfhcforms:htmlforms/clinicalnotes.html",
					"lfhcforms:htmlforms/diagnosis.html",
					"lfhcforms:htmlforms/diagnostic-imaging.html",
					"lfhcforms:htmlforms/discharge.html",
					"lfhcforms:htmlforms/dispense-med.html",
					"lfhcforms:htmlforms/doctor.html",
					"lfhcforms:htmlforms/fluidbalance.html",
					"lfhcforms:htmlforms/history.html",
					"lfhcforms:htmlforms/immunity.html",
					"lfhcforms:htmlforms/ipd-nurse.html",
					"lfhcforms:htmlforms/lab-test-order.html",
					"lfhcforms:htmlforms/lines-tubes.html",
					"lfhcforms:htmlforms/med-order.html",
					"lfhcforms:htmlforms/opd-nurse.html",
					"lfhcforms:htmlforms/phys-exam.html",
					"lfhcforms:htmlforms/treatment.html",
					"lfhcforms:htmlforms/triage.html",
					"lfhcforms:htmlforms/vitals-pews.html",
					"lfhcforms:htmlforms/wound.html"
					);

			for (String htmlform : htmlforms) {
				HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, htmlform);
			}
		}
		catch (Exception e) {
			// this is a hack to get component test to pass until we find the proper way to mock this
			if (ResourceFactory.getInstance().getResourceProviders() == null) {
				log.error("Unable to load HTML forms--this error is expected when running component tests");
			}
			else {
				log.error(e.getMessage(), e);
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

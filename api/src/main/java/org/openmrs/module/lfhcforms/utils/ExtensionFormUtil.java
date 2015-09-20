package org.openmrs.module.lfhcforms.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jfree.util.Log;
import org.openmrs.Form;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.formentryapp.FormEntryAppService;
import org.openmrs.module.formentryapp.FormManager;
import org.openmrs.module.formentryapp.page.controller.forms.ExtensionForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Equivalent of HtmlFormUtil for ExtensionForm objects.
 * 
 * @author Dimitri Renault
 *
 */
public class ExtensionFormUtil {
	
	public static final String DEFAULT_UILOCATION = "patientDashboard.overallActions";
	
	/**
	 * Returns the ExtensionForm corresponding to a Form instance AND saves it.
	 * @param providerName Eg. "lfhcforms".
	 * @param formPath The relative form path under webapp/resources (eg. "htmlforms/myform.xml")
	 * @param form The Form instance
	 */
	public static ExtensionForm getExtensionFormFromUIResourceAndForm(ResourceFactory resourceFactory, String providerName, String formPath, FormEntryAppService hfeAppService, FormManager formManager, Form form) throws Exception {

		final String xml = resourceFactory.getResourceAsString(providerName, formPath);
		
		final ExtensionForm extensionForm = getExtensionFormFromXML(xml);
		extensionForm.setId("");
		extensionForm.setLabel(form.getName());
		
		// If this form is already saved on the specified UI location, we remove it first
		final List<Extension> extensions = hfeAppService.getFormExtensions(form);
		for(Extension extension : extensions) {
			final ExtensionForm runningExtensionForm = new ExtensionForm();
			runningExtensionForm.copyFrom(extension);
			if(runningExtensionForm.getUiLocation().equals(extensionForm.getUiLocation())) {
				hfeAppService.purgeFormExtension(form, extension);
				break;
			}
		}
		
		// Then if the provided UI location is valid, we add it to that one
		final Set<String> allowedUiiLocations = new HashSet<String>( formManager.getUILocations(form) );
		if(allowedUiiLocations.contains(extensionForm.getUiLocation())) {
			if(extensionForm.getOrder() != null) {	// This must be satisfied when the XML has been parsed (a bit clunky)
				final Extension extension = new Extension();
				extensionForm.copyTo(extension);
				hfeAppService.saveFormExtension(form, extension);
			}
		}
		
		return extensionForm;
	}
	
	/**
	 * @return A <i>partially</i> filled ExtensionForm from the XML representation of a form.
	 */
	protected static ExtensionForm getExtensionFormFromXML(String formXlm) throws Exception {
		
		Document doc = HtmlFormEntryUtil.stringToDocument(formXlm);
        Node htmlFormNode = HtmlFormEntryUtil.findChild(doc, "htmlform");

        String processFlag = getAttributeStringValue(htmlFormNode, "formAddMetadata", "");
        processFlag = processFlag.trim().toLowerCase();
        boolean doProcess = processFlag.equalsIgnoreCase("yes") || processFlag.equalsIgnoreCase("true") || processFlag.equalsIgnoreCase("1");

        final ExtensionForm extensionForm = new ExtensionForm();
        if(doProcess) {
        	extensionForm.setOrder( getAttributeIntegerValue(htmlFormNode, "formOrder", 0) );
        	extensionForm.setUiLocation( getAttributeStringValue(htmlFormNode, "formUILocation", DEFAULT_UILOCATION) );
        	extensionForm.setIcon( getAttributeStringValue(htmlFormNode, "formIcon", "icon-file") );
        	extensionForm.setDisplayStyle( getAttributeStringValue(htmlFormNode, "formDisplayStyle", "Simple") );
	        extensionForm.setShowIf( getAttributeStringValue(htmlFormNode, "formShowIf", "") );
        }
        
        return extensionForm;
	}
	
    protected static String getAttributeStringValue(Node htmlForm, String attributeName, String defaultValue) {
        Node item = htmlForm.getAttributes().getNamedItem(attributeName);
        return item == null ? defaultValue : item.getNodeValue();
    }
    
    protected static int getAttributeIntegerValue(Node htmlForm, String attributeName, int defaultValue) {
    	String stringValue = getAttributeStringValue(htmlForm, attributeName, (new Integer(defaultValue)).toString());
    	int val = defaultValue;
    	try {
    		val = Integer.parseInt(stringValue);
    	} catch(NumberFormatException e) {
    		Log.error(stringValue + "could not be parsed to an integer while parsing\n" + htmlForm.toString(), e);
    	}
    	return val;
    }
	
}

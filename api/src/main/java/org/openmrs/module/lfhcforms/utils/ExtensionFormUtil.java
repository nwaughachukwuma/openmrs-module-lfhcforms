package org.openmrs.module.lfhcforms.utils;

import java.util.List;

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

public class ExtensionFormUtil {

	public static ExtensionForm getExtensionFormFromUIResourceAndForm(ResourceFactory resourceFactory, String providerName, String formPath, FormEntryAppService hfeAppService, FormManager formManager, Form form) throws Exception {

		String xml = resourceFactory.getResourceAsString(providerName, formPath);
		final ExtensionForm extensionForm = getExtensionFormFromXML(xml);
		
		if(extensionForm != null) {
		
			extensionForm.setId("");
			extensionForm.setLabel(form.getName());
			
			// If this form is already hooked to an extension, we don't do anything.
			final List<Extension> extensions = hfeAppService.getFormExtensions(form);
			if(extensions.isEmpty()) {
				final Extension extension = new Extension();
				extensionForm.copyTo(extension);
				hfeAppService.saveFormExtension(form, extension);
			}
		}
		
		return extensionForm;
	}
	
	public static ExtensionForm getExtensionFormFromXML(String xml) throws Exception {
		
		Document doc = HtmlFormEntryUtil.stringToDocument(xml);
        Node htmlFormNode = HtmlFormEntryUtil.findChild(doc, "htmlform");

        String processFlag = getAttributeStringValue(htmlFormNode, "formAddMetadata", "");
        processFlag = processFlag.trim().toLowerCase();
        boolean doProcess = processFlag.equalsIgnoreCase("yes") || processFlag.equalsIgnoreCase("true") || processFlag.equalsIgnoreCase("1");

        final ExtensionForm extensionForm = new ExtensionForm();
        if(doProcess) {
        	extensionForm.setOrder( getAttributeIntegerValue(htmlFormNode, "formIcon", 1) );
        	extensionForm.setIcon( getAttributeStringValue(htmlFormNode, "formIcon", "icon-file") );
        	extensionForm.setDisplayStyle( getAttributeStringValue(htmlFormNode, "formDisplayStyle", "Simple") );
	        extensionForm.setShowIf( getAttributeStringValue(htmlFormNode, "formShowIf", "") );
        }
        
        return extensionForm;
	}
	
    private static String getAttributeStringValue(Node htmlForm, String attributeName, String defaultValue) {
        Node item = htmlForm.getAttributes().getNamedItem(attributeName);
        return item == null ? defaultValue : item.getNodeValue();
    }
    
    private static int getAttributeIntegerValue(Node htmlForm, String attributeName, int defaultValue) {
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

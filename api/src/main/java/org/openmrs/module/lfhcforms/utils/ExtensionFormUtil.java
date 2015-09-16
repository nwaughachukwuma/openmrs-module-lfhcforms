package org.openmrs.module.lfhcforms.utils;

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

	public static ExtensionForm getExtensionFormFromForm(ResourceFactory resourceFactory, String providerName, String formPath, FormEntryAppService hfeAppService, FormManager formManager, Form form) throws Exception {

		String xml = resourceFactory.getResourceAsString(providerName, formPath);
		final ExtensionForm extensionForm = getExtensionFormFromXML(xml);
		
		if(extensionForm != null) {
		
			extensionForm.setId("");
			extensionForm.setLabel(form.getName());
			
			final Extension extension = new Extension();
			extensionForm.copyTo(extension);
			hfeAppService.saveFormExtension(form, extension);
		}
		
		return extensionForm;
	}
	
	public static ExtensionForm getExtensionFormFromXML(String xml) throws Exception {
		
		Document doc = HtmlFormEntryUtil.stringToDocument(xml);
        Node htmlFormNode = HtmlFormEntryUtil.findChild(doc, "htmlform");

        String processFlag = getAttributeValue(htmlFormNode, "formAddMetadata");
        if(processFlag == null)
        	return null;
        if(false == processFlag.trim().equalsIgnoreCase("yes"))
        	return null;

        ExtensionForm extForm = new ExtensionForm();
        String formIcon = getAttributeValue(htmlFormNode, "formIcon");
        if(formIcon != null)
        	extForm.setIcon(formIcon);

        return extForm;
	}
	
    private static String getAttributeValue(Node htmlForm, String attributeName) {
        Node item = htmlForm.getAttributes().getNamedItem(attributeName);
        return item == null ? null : item.getNodeValue();
    }
	
}

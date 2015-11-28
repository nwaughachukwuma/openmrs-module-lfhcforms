/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.lfhcforms.fragment.controller.visit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.template.TemplateFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.lfhcforms.utils.Utils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller to return the active visit with its location attributes
 * 
 */
public class ActiveVisitStatusWithLocationFragmentController {

	protected static final Log log = LogFactory.getLog(ActiveVisitStatusWithLocationFragmentController.class);

	private String colorUuid = "5a35eb84-dbc4-4cd0-8a3c-58dc6a7a10ae";
	private String shortNameUuid = "f31de69c-ca4f-4613-aec6-2a3f28cdc973";

	public void controller(FragmentConfiguration config, @RequestParam("patientId") Patient patient,
			FragmentModel model, UiUtils ui, UiSessionContext sessionContext,
			@SpringBean("adtService") AdtService adtService, @InjectBeans PatientDomainWrapper wrapper) {

		// checks if the config passes "activeVisit" attribute
		VisitDomainWrapper activeVisit = (VisitDomainWrapper) config.getAttribute("activeVisit");

		// if not, retrieve activeVisit from the adtService
		if (activeVisit == null) {
			try {
				Location visitLocation = adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
				activeVisit = adtService.getActiveVisit(wrapper.getPatient(), visitLocation);
			} catch (IllegalArgumentException ex) {
				// location does not support visits
			}
		}

		model.addAttribute("activeVisitStartDatetime", null);
		model.addAttribute("activeLocationAttr", null);

		if (activeVisit != null) {
			model.addAttribute("activeVisit", activeVisit);
			model.addAttribute("activeVisitStartDatetime",
					DateFormatUtils.format(activeVisit.getStartDatetime(), "dd MMM yyyy hh:mm a", Context.getLocale()));

			// Retrieve attributes of visit location
			LocationAttributeType colorAttrType = Context.getLocationService()
					.getLocationAttributeTypeByUuid(colorUuid);
			LocationAttributeType shortNameAttrType = Context.getLocationService()
					.getLocationAttributeTypeByUuid(shortNameUuid);

			LocationAttribute colorAttr = Utils.getMostRecentAttribute(activeVisit.getVisit().getLocation(),
					colorAttrType);
			if (colorAttr == null) {
				// default the value of this attribute if no attribute has been set yet
				colorAttr = new LocationAttribute();
				colorAttr.setAttributeType(colorAttrType);
				colorAttr.setValue("grey");
			}
			log.warn("There is no Attribute for Location Attribute Type \"" + colorAttrType.getName() + "\". Using \""
					+ colorAttr.getValue() + "\" as default value");

			LocationAttribute shortNameAttr = Utils.getMostRecentAttribute(activeVisit.getVisit().getLocation(),
					shortNameAttrType);
			if (shortNameAttr == null) {
				// default the value of this attribute if no attribute has been set yet
				shortNameAttr = new LocationAttribute();
				shortNameAttr.setAttributeType(shortNameAttrType);
				shortNameAttr.setValue(activeVisit.getVisit().getLocation().getName());
			}
			log.warn("There is no Attribute for Location Attribute Type \"" + shortNameAttrType.getName()
					+ "\". Using \"" + shortNameAttr.getValue() + "\" as default value");

			Map<String, Object> visitLocAttr = new HashMap<String, Object>();

			visitLocAttr.put("color", colorAttr.getValue());
			visitLocAttr.put("shortName", shortNameAttr.getValue());

			model.addAttribute("visitLocAttr", visitLocAttr);
		}
	}
}
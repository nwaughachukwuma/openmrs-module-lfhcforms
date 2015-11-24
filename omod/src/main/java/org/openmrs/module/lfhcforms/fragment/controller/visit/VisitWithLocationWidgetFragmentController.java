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

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.template.TemplateFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Supports the containing PageModel having an "app" property whose config
 * defines a "visitUrl" property
 */
public class VisitWithLocationWidgetFragmentController {

	private String colorUuid = "5a35eb84-dbc4-4cd0-8a3c-58dc6a7a10ae";
	private String shortNameUuid = "f31de69c-ca4f-4613-aec6-2a3f28cdc973";

	public void controller(FragmentConfiguration config, PageModel pageModel,
			@RequestParam("patientId") Patient patient, FragmentModel model, UiUtils ui,
			UiSessionContext sessionContext, @SpringBean("appframeworkTemplateFactory") TemplateFactory templateFactory,
			@InjectBeans PatientDomainWrapper patientWrapper) {

		AppContextModel contextModel = sessionContext.generateAppContextModel();
		patientWrapper.setPatient(patient);
		contextModel.put("patient", patient);

		AppDescriptor app = (AppDescriptor) pageModel.get("app");
		String visitUrl = null;
		String visitsUrl = null;
		if (app != null) {
			try {
				visitUrl = app.getConfig().get("visitUrl").getTextValue();
			} catch (Exception ex) {
			}
			try {
				visitsUrl = app.getConfig().get("visitsUrl").getTextValue();
			} catch (Exception ex) {
			}
		}
		if (visitUrl == null) {
			visitUrl = "coreapps/patientdashboard/patientDashboard.page?patientId={{patient.uuid}}&visitId={{visit.id}}#visits";
		}
		visitUrl = "/" + ui.contextPath() + "/" + visitUrl;
		if (visitsUrl == null) {
			visitsUrl = "coreapps/patientdashboard/patientDashboard.page?patientId={{patient.uuid}}#visits";
		}
		visitsUrl = "/" + ui.contextPath() + "/" + visitsUrl;
		model.addAttribute("visitsUrl", templateFactory.handlebars(visitsUrl, contextModel));

		List<VisitDomainWrapper> recentVisits = patientWrapper.getAllVisitsUsingWrappers();
		if (recentVisits.size() > 5) {
			recentVisits = recentVisits.subList(0, 5);
		}

		Map<Integer, Map<String, Object>> recentVisitsWithAttr = new LinkedHashMap<Integer, Map<String, Object>>();
		Map<VisitDomainWrapper, String> recentVisitsWithLinks = new LinkedHashMap<VisitDomainWrapper, String>();
		for (VisitDomainWrapper recentVisit : recentVisits) {
			contextModel.put("visit", new VisitContextModel(recentVisit));

			// Retrieve attributes of visit location
			LocationAttributeType colorAttrType = Context.getLocationService()
					.getLocationAttributeTypeByUuid(colorUuid);
			LocationAttributeType shortNameAttrType = Context.getLocationService()
					.getLocationAttributeTypeByUuid(shortNameUuid);

			LocationAttribute colorAttr = getMostRecentAttribute(recentVisit.getVisit().getLocation(), colorAttrType);
			LocationAttribute shortNameAttr = getMostRecentAttribute(recentVisit.getVisit().getLocation(),
					shortNameAttrType);
			Map<String, Object> visitLocAttr = new HashMap<String, Object>();

			visitLocAttr.put("color", colorAttr.getValue());
			visitLocAttr.put("shortName", shortNameAttr.getValue());

			recentVisitsWithAttr.put(recentVisit.getVisitId(), visitLocAttr);
			recentVisitsWithLinks.put(recentVisit, templateFactory.handlebars(visitUrl, contextModel));
		}
		model.addAttribute("recentVisitsWithAttr", recentVisitsWithAttr);
		model.addAttribute("recentVisitsWithLinks", recentVisitsWithLinks);
	}

	/**
	 * 
	 * Returns the most recent location attribute for a given location and
	 * location attribute type
	 * 
	 * @param location
	 * @param attrType
	 * @return attributeMap
	 */
	private LocationAttribute getMostRecentAttribute(Location location, LocationAttributeType attrType) {

		List<LocationAttribute> allAttr = location.getActiveAttributes(attrType);
		NavigableMap<Date, LocationAttribute> attrMap = new TreeMap<Date, LocationAttribute>();
		for (LocationAttribute attr : allAttr) {
			attrMap.put(attr.getDateCreated(), attr);
		}
		return attrMap.lastEntry().getValue();
	}
}
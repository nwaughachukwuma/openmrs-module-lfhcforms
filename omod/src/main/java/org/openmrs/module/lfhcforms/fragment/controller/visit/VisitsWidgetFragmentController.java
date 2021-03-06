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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.template.TemplateFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.lfhcforms.utils.VisitTypeHelper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;

/**
 * Supports the containing PageModel having an "app" property whose config
 * defines a "visitUrl" property
 */
public class VisitsWidgetFragmentController {

	protected static final Log log = LogFactory.getLog(VisitsWidgetFragmentController.class);

	public void controller(FragmentConfiguration config,
			PageModel pageModel,
			FragmentModel model,
			UiUtils ui,
			UiSessionContext sessionContext,
			@SpringBean("appframeworkTemplateFactory") TemplateFactory templateFactory,
			@SpringBean("visitTypeHelper") VisitTypeHelper visitTypeHelper,
			@InjectBeans PatientDomainWrapper patientWrapper) {

		// Patient ID is passed through config when the fragment is included
		patientWrapper.setPatient((Context.getPatientService().getPatient((Integer) config.get("patientId") )));
		
		AppContextModel contextModel = sessionContext.generateAppContextModel();
		contextModel.put("patient", new PatientContextModel(patientWrapper.getPatient()));
		
		AppDescriptor app = (AppDescriptor) pageModel.get("app");
		String visitUrl = null;
		String visitsUrl = null;
		if (app != null) {
			try {
				visitUrl = app.getConfig().get("visitUrl").getTextValue();
			} catch (Exception ex) { }
			try {
				visitsUrl = app.getConfig().get("visitsUrl").getTextValue();
			} catch (Exception ex) { }
		}
		if (visitUrl == null) {
			visitUrl = "coreapps/patientdashboard/patientDashboard.page?patientId={{patient.patientId}}&visitId={{visit.id}}";
		}
		visitUrl = "/" + ui.contextPath() + "/" + visitUrl;
		if (visitsUrl == null) {
			visitsUrl = "coreapps/patientdashboard/patientDashboard.page?patientId={{patient.patientId}}";
		}
		visitsUrl = "/" + ui.contextPath() + "/" + visitsUrl;
		model.addAttribute("visitsUrl", templateFactory.handlebars(visitsUrl, contextModel));

		List<VisitDomainWrapper> recentVisits = patientWrapper.getAllVisitsUsingWrappers();
		if (recentVisits.size() > 5) {
			recentVisits = recentVisits.subList(0, 5);
		}

		Map<VisitDomainWrapper, String> recentVisitsWithLinks = new LinkedHashMap<VisitDomainWrapper, String>();

		for (VisitDomainWrapper recentVisit : recentVisits) {
			contextModel.put("visit", new VisitContextModel(recentVisit));
			recentVisitsWithLinks.put(recentVisit, templateFactory.handlebars(visitUrl, contextModel));
		}
		
		Map<Integer, Map<String, Object>> recentVisitsWithAttr = visitTypeHelper.getVisitColorAndShortName(recentVisits);

		model.addAttribute("recentVisitsWithAttr", recentVisitsWithAttr);
		model.addAttribute("recentVisitsWithLinks", recentVisitsWithLinks);
	}

	
}

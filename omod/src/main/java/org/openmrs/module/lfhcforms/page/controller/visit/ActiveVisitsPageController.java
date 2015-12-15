/**
 * Version 1.0 (the "License"); you may not use this file except in
 * The contents of this file are subject to the OpenMRS Public License
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
package org.openmrs.module.lfhcforms.page.controller.visit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.VisitType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.lfhcforms.utils.VisitHelper;
import org.openmrs.module.lfhcforms.utils.VisitTypeHelper;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ActiveVisitsPageController {

	public String get(UiSessionContext sessionContext, PageModel model,
			@SpringBean AdtService service,
			@SpringBean("locationService") LocationService locationService,
			@RequestParam("app") AppDescriptor app
			) {

		Location sessionLocation = sessionContext.getSessionLocation();
		if (sessionLocation == null) {
			return "redirect:login.htm";
		}
		Location visitLocation = null;
		if (sessionLocation != null) {
			visitLocation = service.getLocationThatSupportsVisits(sessionLocation);
		}
		if (visitLocation == null) {
			throw new IllegalStateException("Configuration required: no visit location found based on session location");
		}

		model.addAttribute("visitSummaries", service.getActiveVisits(visitLocation));
		String patientPageUrl = app.getConfig().get("patientPageUrl").getTextValue();
		model.addAttribute("patientPageUrl", patientPageUrl);

		// used to determine whether or not we display a link to the patient in the results list
		model.addAttribute("canViewVisits", Context.hasPrivilege(CoreAppsConstants.PRIVILEGE_PATIENT_VISITS));

		// retrieve color and short names
		VisitTypeHelper visitTypeHelper = new VisitTypeHelper();
		Map<Integer, Map<String, Object>> activeVisitsWithAttr = visitTypeHelper.getVisitColorAndShortName(service.getActiveVisits(visitLocation));
		model.addAttribute("visitsWithAttr", activeVisitsWithAttr);

		// retrieve all different visit types, with their color and short name
		List<VisitDomainWrapper> activeVisits = service.getActiveVisits(visitLocation);
		Map <VisitType, Object> visitTypesWithAttr = new HashMap<VisitType, Object>();
		for (VisitDomainWrapper vdw : activeVisits) {
			Map <String, Object> typeAttr = visitTypeHelper.getVisitTypeColorAndShortName(vdw.getVisit().getVisitType());
			visitTypesWithAttr.put(vdw.getVisit().getVisitType(), typeAttr);
		}
		model.addAttribute("visitTypesWithAttr", visitTypesWithAttr);

		return null;
	}

}

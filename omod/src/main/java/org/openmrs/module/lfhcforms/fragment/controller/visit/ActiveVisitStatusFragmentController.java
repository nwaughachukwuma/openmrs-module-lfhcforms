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

import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.lfhcforms.utils.VisitHelper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller to return the active visit with its location attributes
 * 
 */
public class ActiveVisitStatusFragmentController {

	protected static final Log log = LogFactory.getLog(ActiveVisitStatusFragmentController.class);

	public void controller(FragmentConfiguration config, @RequestParam("patientId") Patient patient,
			FragmentModel model, UiUtils ui, UiSessionContext sessionContext,
			@SpringBean("adtService") AdtService adtService,
			@SpringBean("visitHelper") VisitHelper visitHelper,
			@InjectBeans PatientDomainWrapper wrapper) {

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
		model.addAttribute("activeVisitAttr", null);

		if (activeVisit != null) {
			model.addAttribute("activeVisit", activeVisit);
			model.addAttribute("activeVisitStartDatetime",
					DateFormatUtils.format(activeVisit.getStartDatetime(), "dd MMM yyyy hh:mm a", Context.getLocale()));

			// Retrieve color and short name of visit type
			Map<String, Object> activeVisitAttr = visitHelper.getVisitColorAndShortName(activeVisit);

			model.addAttribute("activeVisitAttr", activeVisitAttr);
		}
	}
}
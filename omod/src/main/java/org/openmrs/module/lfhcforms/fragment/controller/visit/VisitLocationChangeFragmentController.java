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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.appui.AppUiConstants;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.lfhcforms.utils.Utils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@Transactional
public class VisitLocationChangeFragmentController {

	protected static final Log log = LogFactory.getLog(VisitLocationChangeFragmentController.class);

	/**
	 * 
	 * Controller to let user change the visit location
	 * 
	 * @param model
	 * @param ui
	 * @param adtService
	 * @param sessionContext
	 */
	public void controller(FragmentModel model,
			UiUtils ui,
			@SpringBean("adtService") AdtService adtService, UiSessionContext sessionContext) {

		model.addAttribute("locationList", null);
		model.addAttribute("userVisitLocation", null);

		List<Location> visitLocations = adtService.getAllLocationsThatSupportVisits();

		// get the user's visit location
		Location userVisitLocation = adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
		model.addAttribute("userVisitLocation", userVisitLocation);

		// model.addAttribute("visitLocation", visitLocation);
		model.addAttribute("locationList", visitLocations);
	}

	/**
	 *  Set the visit with a location provided by the view
	 *  
	 * @param adtService
	 * @param visitService
	 * @param patient
	 * @param selectedLocation
	 * @param visit
	 * @param uiUtils
	 * @param request
	 * @return
	 */
	public FragmentActionResult change(@SpringBean("adtService") AdtService adtService,
			@SpringBean("visitService") VisitService visitService,
			@RequestParam("patientId") Patient patient,
			@RequestParam("selectedLocation") Location selectedLocation,
			@RequestParam("visitId") Visit visit,
			UiUtils uiUtils, UiSessionContext context, HttpServletRequest request) {

		Location previousLocation = visit.getLocation();
		visit.setLocation(selectedLocation);
		visitService.saveVisit(visit);

		Utils.setAdmissionBasedOnLocation(visit, previousLocation);

		if (!(visit.getLocation().equals(selectedLocation))) {
			log.error("The location \""+selectedLocation+"\" could not be set for visit "+visit);
			return new FailureResult(uiUtils.message("lfhcforms.app.visit.changelocation.fail"));
		}
		
		context.setSessionLocation(selectedLocation);
		
		request.getSession().setAttribute(AppUiConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
				uiUtils.message("lfhcforms.app.visit.changelocation.success", uiUtils.format(patient)));
		request.getSession().setAttribute(AppUiConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");

		return new SuccessResult();
	}
}

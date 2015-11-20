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
package org.openmrs.module.lfhcforms.fragment.controller;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.appui.AppUiConstants;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@Transactional
public class StartVisitWithLocationFragmentController {

	public void controller(	FragmentModel model, @RequestParam("patientId") Patient patient
			, UiUtils ui
			,	@SpringBean("adtService") AdtService adtService
			)
	{
		model.addAttribute("locationList", null);
		model.addAttribute("activeVisitList", null);

		List<Location> visitLocations = adtService.getAllLocationsThatSupportVisits();
		
		// send active visits to the view, if any.
		ArrayList<Visit> activeVisits = getActiveVisits(patient, adtService);

		model.addAttribute("activeVisitList", activeVisits);
		model.addAttribute("locationList" , visitLocations);
	}


	public FragmentActionResult create(@SpringBean("adtService") AdtService adtService,
			@RequestParam("patientId") Patient patient,
			@RequestParam("selectedLocation") Location location, UiUtils uiUtils,
			UiSessionContext emrContext,
			HttpServletRequest request) {
		
		// Do not save if patient already has active visit, in any location
		if (getActiveVisits(patient, adtService).size() != 0) {
			return new FailureResult(uiUtils.message("coreapps.activeVisits.alreadyExists"));
		}

		adtService.ensureVisit(patient, new Date(), location);

		request.getSession().setAttribute(AppUiConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
				uiUtils.message("coreapps.visit.createQuickVisit.successMessage", uiUtils.format(patient)));
		request.getSession().setAttribute(AppUiConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");

		return new SuccessResult();
	}

	/**
	 * 
	 * Checks if the patient has Active Visit in any location
	 * 
	 * @param patient
	 * @param adtService
	 * @return boolean
	 */
	private ArrayList <Visit> getActiveVisits (Patient patient, AdtService adtService) {

		ArrayList <Visit> activeVisits = new ArrayList<Visit>();
		List <Location> visitLocations = adtService.getAllLocationsThatSupportVisits();
		for(Iterator<Location> it = visitLocations.iterator(); it.hasNext();) {
			Location loc = it.next();
			VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, loc);
			if ( activeVisit != null) {
				activeVisits.add(activeVisit.getVisit());
			};
		}

		return activeVisits;

	}
}

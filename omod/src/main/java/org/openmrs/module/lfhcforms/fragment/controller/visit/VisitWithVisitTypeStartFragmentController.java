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
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.AppUiConstants;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Transactional
public class VisitWithVisitTypeStartFragmentController {

	protected static final Log log = LogFactory.getLog(VisitWithVisitTypeStartFragmentController.class);

	/**
	 * 
	 * Controller to let user start a visit with a specific visit type 
	 * 
	 */
	public void controller(FragmentModel model, @RequestParam("patientId") Patient patient, UiUtils ui,
			@SpringBean("adtService") AdtService adtService, UiSessionContext sessionContext) {
		
		model.addAttribute("visitTypes", null);
		model.addAttribute("activeVisits", null);
		model.addAttribute("currentVisitType", null);

		VisitService vs = Context.getVisitService();
		List<VisitType> visitTypes = vs.getAllVisitTypes(false);

		// send active visits to the view, if any.
		ArrayList<Visit> activeVisits = getActiveVisits(patient, adtService);

		// get the current visit's visit type, if any active visit, IN the current location
		VisitDomainWrapper activeVisitWrapper = adtService.getActiveVisit(patient, sessionContext.getSessionLocation());
		if (activeVisitWrapper != null) {
			VisitType currentVisitType = activeVisitWrapper.getVisit().getVisitType();
			model.addAttribute("currentVisitType", currentVisitType);
		}
		
		model.addAttribute("activeVisits", activeVisits);
		model.addAttribute("visitTypes", visitTypes);
	}

	/**
	 * 
	 * Saves visit with a visit type provided by the view
	 * 
	 */
	public FragmentActionResult create(@SpringBean("adtService") AdtService adtService,
			@RequestParam("patientId") Patient patient,
			@RequestParam("selectedType") VisitType selectedType, 
			UiUtils uiUtils, UiSessionContext uiContext, HttpServletRequest request) {
		
		// Do not save if patient already has active visit, in any location
		ArrayList<Visit> activeVisits = getActiveVisits(patient, adtService);
		if (activeVisits.size() != 0) {
			log.warn("Patient already has active visits. " + activeVisits.toString());
			return new FailureResult(uiUtils.message("coreapps.activeVisits.alreadyExists"));
		}
		
		// create the visit
		Visit visit = adtService.ensureVisit(patient, new Date(), uiContext.getSessionLocation());
		// set the visit type
		visit.setVisitType(selectedType);
		Context.getVisitService().saveVisit(visit);
		
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
	private ArrayList<Visit> getActiveVisits(Patient patient, AdtService adtService) {

		ArrayList<Visit> activeVisits = new ArrayList<Visit>();
		List<Location> visitLocations = adtService.getAllLocationsThatSupportVisits();
		for (Iterator<Location> it = visitLocations.iterator(); it.hasNext();) {
			Location loc = it.next();
			VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, loc);
			if (activeVisit != null) {
				activeVisits.add(activeVisit.getVisit());
			}
			;
		}
		return activeVisits;
	}
}

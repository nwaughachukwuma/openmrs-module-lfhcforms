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
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.AppUiConstants;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
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
public class VisitTypeChangeFragmentController {

	protected static final Log log = LogFactory.getLog(VisitTypeChangeFragmentController.class);

	/**
	 * 
	 * Controller to let user change the visit type
	 * 
	 */
	public void controller(FragmentModel model,
			UiUtils ui,
			@SpringBean("adtService") AdtService adtService, UiSessionContext sessionContext) {

		model.addAttribute("visitTypes", null);

		VisitService vs = Context.getVisitService();
		List<VisitType> visitTypes = vs.getAllVisitTypes(false);

		model.addAttribute("visitTypes", visitTypes);
	}

	/**
	 *  Set the visit with a visit type provided by the view
	 *  
	 */
	public FragmentActionResult change(@SpringBean("adtService") AdtService adtService,
			@SpringBean("visitService") VisitService visitService,
			@RequestParam("patientId") Patient patient,
			@RequestParam("selectedType") VisitType selectedType,
			@RequestParam("visitId") Visit visit,
			UiUtils uiUtils, UiSessionContext context, HttpServletRequest request) {

		// VisitType previousType = visit.getVisitType();
		visit.setVisitType(selectedType);
		visitService.saveVisit(visit);

		// Utils.setAdmissionBasedOnLocation(visit, previousType);

		if (!(visit.getVisitType().equals(selectedType))) {
			log.error("The visit type \""+selectedType+"\" could not be set for visit "+visit);
			return new FailureResult(uiUtils.message("lfhcforms.app.visit.visittype.change.fail"));
		}

		request.getSession().setAttribute(AppUiConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
				uiUtils.message("lfhcforms.app.visit.visittype.change.success", uiUtils.format(selectedType)));
		request.getSession().setAttribute(AppUiConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");

		return new SuccessResult();
	}
}
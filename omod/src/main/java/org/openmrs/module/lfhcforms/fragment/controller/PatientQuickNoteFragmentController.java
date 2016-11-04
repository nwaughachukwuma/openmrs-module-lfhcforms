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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * 
 * @author Romain Buisson
 *
 */

public class PatientQuickNoteFragmentController {

	protected final Log log = LogFactory.getLog(getClass());

	protected final String QUICK_NOTE_CONCEPT_MAPPING = "CIEL:9999";

	public void controller(
			FragmentModel model,
			@FragmentParam("patient") PatientDomainWrapper patientWrapper,
			@SpringBean("conceptService") ConceptService conceptService,
			UiUtils ui
			)

	{
		Map<String, Object> jsonConfig = new HashMap<String, Object>();
		Patient patient = patientWrapper.getPatient();

		String[] splitMapping = QUICK_NOTE_CONCEPT_MAPPING.split(":");

		jsonConfig.put("patient", ConversionUtil.convertToRepresentation(patient, Representation.REF));
		jsonConfig.put("concept", ConversionUtil.convertToRepresentation(
				conceptService.getConceptByMapping(splitMapping[1], splitMapping[0]), Representation.REF));

		model.addAttribute("patient", patient);
		model.addAttribute("jsonConfig", ui.toJson(jsonConfig));
	}
}
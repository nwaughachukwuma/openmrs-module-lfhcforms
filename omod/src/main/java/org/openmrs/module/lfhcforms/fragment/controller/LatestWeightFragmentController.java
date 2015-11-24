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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.springframework.web.bind.annotation.RequestParam;

public class LatestWeightFragmentController {
	
	private static Log log = LogFactory.getLog(LatestWeightFragmentController.class);
	
	public void controller(	UiUtils ui,	FragmentConfiguration config, 
							@SpringBean("conceptService")	ConceptService conceptService,
							@SpringBean("obsService")		ObsService obsService,
							@RequestParam("patientId")		Patient patient
	                      )
	{
		String weightString = getLatestWeight(conceptService, obsService, patient);
		
        config.addAttribute("patientWeight", weightString);
        config.addAttribute("patientWeightLabel", ui.message("lfhcforms.latestweight.patientWeightLabel"));
	}
	
	protected void setLogger(Log log) {
		this.log = log;
	}
	
	protected String getWeightConceptCode() {
		return "5089";
	}

	protected String getWeightConceptSource() {
		return "CIEL";
	}
	
	protected String getLatestWeight(ConceptService conceptService, ObsService obsService, Patient patient) {
		
		final String conceptMapping = getWeightConceptSource() + ":" + getWeightConceptCode();
		Concept conceptWeight = conceptService.getConceptByMapping(getWeightConceptCode(), getWeightConceptSource());
		if(conceptWeight == null) {
			log.error("The concept " + conceptMapping + " could not be fetched. No concept could be used to obtain the latest weight.");
			return "";
		}
		ConceptNumeric conceptWeightNumeric = conceptService.getConceptNumeric(conceptWeight.getId());
		String units = "";
		if(conceptWeightNumeric == null) {
			log.error("The concept " + conceptMapping + " is not of the 'numeric' class, its units won't be retrieved.");
		}
		else {
			units = conceptWeightNumeric.getUnits();
		}
		
		String weightString = "";
		List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, conceptWeight);
		if(obsList.isEmpty() == false) {
			Obs obs = obsList.get(0);
			Double weight = obs.getValueNumeric();
			if(weight == null) {
				log.error("The latest obs for concept " + conceptMapping + " is not of the 'numeric' type, no weight was obtained.");
			}
			else {
				weightString = String.valueOf(weight) + units;
			}
		}
			
		return weightString;
	}
}
package org.openmrs.module.lfhcforms.fragment.controller;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class MyViewFragmentController {

	public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,  @SpringBean("conceptService") ConceptService conceptService
			, @SpringBean("obsService") ObsService obsService
			/*, @SpringBean("allergyService") PatientService patientService*/) {


		Concept concept = conceptService.getConcept("5089");
		
		List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, concept);
		
		model.addAttribute("myObs", null);
		if(!obsList.isEmpty()) {
			// The last obs
			Obs obs = obsList.get(obsList.size() - 1);
			model.addAttribute("myObs", obs.getValueNumeric());
		}
	}

}

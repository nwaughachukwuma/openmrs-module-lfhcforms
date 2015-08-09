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

public class FluidBalanceFragmentController {

	public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,  @SpringBean("conceptService") ConceptService conceptService
			, @SpringBean("obsService") ObsService obsService
			/*, @SpringBean("allergyService") PatientService patientService*/) {

		Concept concept = conceptService.getConcept("5089"); // CIEL weight
		List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, concept);
		if(!obsList.isEmpty()) {
			// The last obs
			Obs obs = obsList.get(obsList.size() - 1);
			double weight = obs.getValueNumeric();
		}
		
		model.addAttribute("fluidBalance", null);
		model.addAttribute("lastFluidBalance", null);
		model.addAttribute("avgUrineOutput", null);
		model.addAttribute("avgUrineOutputPerKg", null);
		
//		model.addAttribute("fluidBalance", 100);
//		model.addAttribute("lastFluidBalance", 102);
//		model.addAttribute("avgUrineOutput", 55);
//		model.addAttribute("avgUrineOutputPerKg", 1);
	}

}

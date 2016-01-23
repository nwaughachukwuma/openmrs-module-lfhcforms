package org.openmrs.module.lfhcforms.fragment.controller;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class FluidBalanceFragmentController {

	public void controller(	FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,	@SpringBean("conceptService") ConceptService conceptService
			,	@SpringBean("obsService") ObsService obsService
																)
	{
		model.addAttribute("fluidBalance", null);
		model.addAttribute("lastFluidBalance", null);
		model.addAttribute("avgUrineOutput", null);
		model.addAttribute("avgUrineOutputPerKg", null);
		
		//
		//	Fetching the last weight obs
		//
		double weight = 0;
		{
			List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, conceptService.getConceptByMapping("5089", "CIEL") );
			if(!obsList.isEmpty()) {
				// The last obs
				Obs obs = obsList.get(obsList.size() - 1);
				weight = obs.getValueNumeric();
			}
		}
		
		DateTime now = DateTime.now();
		DateTime today6am = now.withTimeAtStartOfDay().plusHours(6);
		if(today6am.isAfter(now))
			today6am = today6am.minusDays(1);
		DateTime yesterday6am = today6am.minusDays(1);

		//
		//	Urine output
		//
		Period period = new Period(today6am, now);
		double currentHours = period.getHours() + (double) period.getMinutes() / 60;
		
		Concept urineConcept = conceptService.getConceptByMapping("1168", LFHCFormsConstants.LFHC_CONCEPT_SOURCE);
		double urineOutput = sumObservations(obsService, patient, today6am, now, Arrays.asList(urineConcept));
		double averageUrineOutput = 0;
		if(urineOutput > 0)
			averageUrineOutput = urineOutput / currentHours;
		double averageUrineOutputPerKg = 0;
		if(weight > 0)
			averageUrineOutputPerKg = averageUrineOutput / weight;

		averageUrineOutput = Math.round(averageUrineOutput);
		model.addAttribute("avgUrineOutput", averageUrineOutput + " (ml/h)");
		averageUrineOutputPerKg = Math.round(10 * averageUrineOutputPerKg) / 10;
		model.addAttribute("avgUrineOutputPerKg", averageUrineOutputPerKg + " (ml/h/kg)");
		
		//
		//	Total fluid balances
		//
		List<Concept> inputConcepts = Arrays.asList(
				conceptService.getConceptByMapping("1004", LFHCFormsConstants.LFHC_CONCEPT_SOURCE),
				conceptService.getConceptByMapping("1005", LFHCFormsConstants.LFHC_CONCEPT_SOURCE),
				conceptService.getConceptByMapping("1171", LFHCFormsConstants.LFHC_CONCEPT_SOURCE),
				conceptService.getConceptByMapping("1172", LFHCFormsConstants.LFHC_CONCEPT_SOURCE),
				conceptService.getConceptByMapping("1006", LFHCFormsConstants.LFHC_CONCEPT_SOURCE)
				);
		List<Concept> outputConcepts = Arrays.asList(
				conceptService.getConceptByMapping("1168", LFHCFormsConstants.LFHC_CONCEPT_SOURCE),
				conceptService.getConceptByMapping("1169", LFHCFormsConstants.LFHC_CONCEPT_SOURCE),
				conceptService.getConceptByMapping("1001", LFHCFormsConstants.LFHC_CONCEPT_SOURCE),
				conceptService.getConceptByMapping("1002", LFHCFormsConstants.LFHC_CONCEPT_SOURCE),
				conceptService.getConceptByMapping("1170", LFHCFormsConstants.LFHC_CONCEPT_SOURCE)
				);
		
		double lastInput = sumObservations(obsService, patient, yesterday6am, today6am, inputConcepts);
		double currentInput = sumObservations(obsService, patient, today6am, now, inputConcepts);
		double lastOutput = sumObservations(obsService, patient, yesterday6am, today6am, outputConcepts);
		double currentOutput = sumObservations(obsService, patient, today6am, now, outputConcepts);
		
		double currentBalance = currentInput - currentOutput;
		model.addAttribute("fluidBalance", currentBalance + " (ml)");
		
		double lastBalance = lastInput - lastOutput;
		model.addAttribute("lastFluidBalance", lastBalance + " (ml)");
	}
	
	protected double sumObservations(final ObsService obsService, Person whom, DateTime fromDate, DateTime toDate, List<Concept> concepts) {
		
		List<Obs> obsList = obsService.getObservations(Arrays.asList(whom), null, concepts,
		        null, null, null, null,
		        null, null, fromDate.toDate(), toDate.toDate(), false);
		
		if(obsList.isEmpty())
			return 0;
		
		double sum = 0;
		for(Obs obs : obsList) {
			sum += obs.getValueNumeric();
		}
		return sum;
	}
}

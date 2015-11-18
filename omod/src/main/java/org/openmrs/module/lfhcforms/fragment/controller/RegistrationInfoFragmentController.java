package org.openmrs.module.lfhcforms.fragment.controller;

import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.VisitService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Fields and actions to provide registrationInfo view.
 * 
 * @author Romain Buisson
 */
public class RegistrationInfoFragmentController {

	private String fatherNameUuid= "51c5e4f4-7e13-11e5-8bcf-feff819cdc9f";	
	private String fatherOccupationUuid= "51c5e88c-7e13-11e5-8bcf-feff819cdc9f";	
	private String motherNameUuid= "8d871d18-c2cc-11de-8d13-0010c6dffd0f";	
	private String insuranceDetailsUuid = "5c6ee7c2-7e1b-11e5-8bcf-feff819cdc9f";


	public void controller(	FragmentModel model, @RequestParam("patientId") Patient patient,
			UiUtils ui
			)

	{
		model.addAttribute("fatherName", null);
		model.addAttribute("fatherOccupation", null);
		model.addAttribute("motherName", null);
		model.addAttribute("insuranceDetails", null);
		model.addAttribute("village", null);

		Map<String, PersonAttribute> attributes = patient.getPerson().getAttributeMap();
		for ( Entry<String, PersonAttribute> attr : attributes.entrySet() ) {
			if (attr.getValue().getAttributeType().getUuid().equals(fatherNameUuid)) {
				model.addAttribute("fatherName", attr.getValue());
			}

			if (attr.getValue().getAttributeType().getUuid().equals(fatherOccupationUuid)) {
				model.addAttribute("fatherOccupation", attr.getValue());
			}
			
			if (attr.getValue().getAttributeType().getUuid().equals(motherNameUuid)) {
				model.addAttribute("motherName", attr.getValue());
			}

			if (attr.getValue().getAttributeType().getUuid().equals(insuranceDetailsUuid)) {
				model.addAttribute("insuranceDetails", attr.getValue());
			}
		}
		
		String village = patient.getPersonAddress().getCityVillage();
		model.addAttribute("village", village);
	}
}





package org.openmrs.module.lfhcforms.fragment.controller;

import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Fields and actions to provide registrationInfo view.
 * 
 * @author Romain Buisson
 */
public class RegistrationInfoFragmentController {

	private String fatherNameUuid= LFHCFormsConstants.FATHER_NAME_UUID;	
	private String fatherOccupationUuid= LFHCFormsConstants.FATHER_OCCUPATION_UUID;	
	private String motherNameUuid= LFHCFormsConstants.MOTHER_NAME_UUID;	
	private String insuranceDetailsUuid = LFHCFormsConstants.INSURANCE_DETAILS_UUID;


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





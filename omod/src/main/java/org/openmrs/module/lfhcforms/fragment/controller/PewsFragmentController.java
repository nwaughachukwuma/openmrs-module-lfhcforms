package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.resource.ResourceFactory;

public class PewsFragmentController {

//	protected static final String LFHC_SOURCE = "LFHC";
	
	public void controller(	FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,	@SpringBean("conceptService") ConceptService conceptService
			,	@SpringBean("obsService") ObsService obsService
																)
	{
		ResourceFactory resourceFactory = ResourceFactory.getInstance();
		String json = "";
		try {
			json = resourceFactory.getResourceAsString("lfhcforms", "pews/boundaries.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HashMap<String,Object> result = null;
		try {
			result = new ObjectMapper().readValue(json, HashMap.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String pews = "";
		if(result != null) {
			pews = "Min. value: " + result.get("minValue") + ", max. value: " + result.get("maxValue");
		}
		model.addAttribute("pews", pews);
	}
}

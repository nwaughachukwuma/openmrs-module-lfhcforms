package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.resource.ResourceFactory;

public class PewsScoreFragmentController {

	/*
	 * This is a Jackson friendly class to hold the limits of a component of the PEWS calculation.
	 * Such as the limits for the respiratory rate, blood pressure... etc.
	 */
	public static class Boundaries {
		
		public Boundaries() {};
		
		private String description = "";
		private String conceptMapping = "";
		private List<Double> lows = new ArrayList<Double>();
		private List<Double> highs = new ArrayList<Double>();
		private List<String> whenAnswers = new ArrayList<String>();
		
		public boolean isNumeric() {
			return whenAnswers.size() == 0;
		}
		
		public List<String> getWhenAnswers() {
			return whenAnswers;
		}

		public void setWhenAnswers(List<String> whenAnswers) {
			this.whenAnswers = whenAnswers;
		}

		public String getDescription() {
			return description;
		}

		public List<Double> getHighs() {
			return highs;
		}

		public void setHighs(List<Double> highs) {
			this.highs = highs;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getConceptMapping() {
			return conceptMapping;
		}

		public void setConceptMapping(String conceptMapping) {
			this.conceptMapping = conceptMapping;
		}

		public List<Double> getLows() {
			return lows;
		}

		public void setLows(List<Double> lows) {
			this.lows = lows;
		}
	}
	
	
	public void controller(	FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,	@SpringBean("conceptService") ConceptService conceptService
			,	@SpringBean("obsService") ObsService obsService
																)
	{
		ResourceFactory resourceFactory = ResourceFactory.getInstance();
		String json = "";
		try {
			json = resourceFactory.getResourceAsString("lfhcforms", "pewsScore/boundaries.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, PewsScoreFragmentController.Boundaries.class);
		HashMap<String, PewsScoreFragmentController.Boundaries> result = null;
		
		try {
			result = mapper.readValue(json, mapType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String pews = "";
		if(result != null) {
			Boundaries bound = result.get("heartRate");
			pews = "Min. value: " + bound.getLows().get(0) + ", max. value: " + bound.getHighs().get(0);
		}
		model.addAttribute("pews", pews);
	}
}

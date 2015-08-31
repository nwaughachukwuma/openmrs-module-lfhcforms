package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.jfree.util.Log;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.lfhcforms.utils.OmodResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;
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
		private String parentId = "";
		private List<Double> lows = new ArrayList<Double>();
		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		private List<Double> highs = new ArrayList<Double>();
		private List<String> whenAnswers = new ArrayList<String>();
		
		public boolean isNumeric() {
			return whenAnswers.size() == 0;
		}
		
		public boolean hasParent() {
			return getParentId().length() == 0;
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
	
	protected String getBoundariesJson(ResourceLoader resourceLoader, String resourcePath) {

		String json = "";
		try {
			json = resourceLoader.getResourceAsSting(resourcePath, "UTF-8");
		} catch (IOException e) {
			Log.error("There was an error loading " + resourcePath, e);
		}
		return json;
	}
	
	protected Map<String, Boundaries> getBoundariesMapFromJson(String json) {
		
		final ObjectMapper mapper = new ObjectMapper();
		
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Boundaries.class);
		
		HashMap<String, Boundaries> boundariesMap = null;
		try {
			boundariesMap = mapper.readValue(json, mapType);
		} catch (Exception e) {
			Log.error("There was an error unmarshalling JSON data: \n" + json, e);
		}
		
		return boundariesMap;
	}
		
	public void controller(	FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,	@SpringBean("conceptService") ConceptService conceptService
			,	@SpringBean("obsService") ObsService obsService
			,	ResourceLoader loader
																)
	{
		String json = getBoundariesJson(new OmodResouceLoaderImpl("lfhcforms"), "pewsScore/boundaries.json");
		
		Map<String, Boundaries> boundariesMap = getBoundariesMapFromJson(json);
				
		String pews = "";
		if(boundariesMap != null) {
			Boundaries bound = boundariesMap.get("CIEL:5087"); // heart rate & blood pressure
			pews = "Min. value: " + bound.getLows().get(0) + ", max. value: " + bound.getHighs().get(0);
		}
		model.addAttribute("pews", pews);
	}
}

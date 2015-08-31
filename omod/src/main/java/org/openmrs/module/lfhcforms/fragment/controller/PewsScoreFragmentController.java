package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.jfree.util.Log;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.lfhcforms.utils.OmodResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class PewsScoreFragmentController {

	/*
	 * This is a Jackson friendly class to hold the limits of a component of the PEWS calculation.
	 * Such as the limits for the respiratory rate, blood pressure... etc.
	 */
	public static class Boundaries {
		
		public Boundaries() {};
		
		private Double increment = 0.0;
		private String description = "";
		private String conceptMapping = "";
		private String parentId = "";
		private List<Double> lows = new ArrayList<Double>();
		
		private List<Double> highs = new ArrayList<Double>();
		private List<String> whenAnswers = new ArrayList<String>();
		
		public boolean isNumeric() {
			return whenAnswers.size() == 0;
		}
		
		public boolean hasParent() {
			return getParentId().length() == 0;
		}

		public Double getIncrement() {
			return increment;
		}

		public void setIncrement(Double increment) {
			this.increment = increment;
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
		
		public List<String> getWhenAnswers() {
			return whenAnswers;
		}

		public void setWhenAnswers(List<String> whenAnswers) {
			this.whenAnswers = whenAnswers;
		}
		
		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
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
																)
	{	
		int ageInMonths = (int) getPatientAgeInMonths(patient.getBirthdate()); 
		
		String json = getBoundariesJson(new OmodResouceLoaderImpl("lfhcforms"), "pewsScore/boundaries.json");
		
		Map<String, Boundaries> boundariesMap = getBoundariesMapFromJson(json);
				
		Map<String, Double> pewsComponents = new HashMap<String, Double>();
		
		String err = "";
		for(String conceptMapping : boundariesMap.keySet()) {
			
			String[] split = conceptMapping.split(":");
			Concept concept = conceptService.getConceptByMapping(split[1], split[0]);

			Obs obs = getLastObs(obsService, patient, concept);
			if(obs == null) {
				//TODO Report on the widget that the PEWS score cannot be calculated.
				err = concept.getName(Locale.ENGLISH).getName() + " required for PEWS score has no obs recorded.";
				break;
			}
			
			// TODO Maybe check that all obs date-time are within a window (and recorded the oldest date-time as the PEWS timestamp
			
			String component = conceptMapping;
			Boundaries boundaries = boundariesMap.get(conceptMapping);
			if(boundaries.hasParent()) {
				component = boundaries.getParentId();
			}
			if(!pewsComponents.containsKey(component))
				pewsComponents.put(component, 0.0);

			Double updatedComponent = pewsComponents.get(component) + getPewsComponentIncrement(conceptService, obs, boundaries, ageInMonths);
			pewsComponents.put(component, updatedComponent);
		}
		
		String pews = "";
		if(boundariesMap != null) {
			Boundaries bound = boundariesMap.get("CIEL:5087"); // heart rate & blood pressure
			pews = "Min. value: " + bound.getLows().get(0) + ", max. value: " + bound.getHighs().get(0);
		}
		model.addAttribute("pews", pews);
	}

	private double getPatientAgeInMonths(Date birthdate) {
		
		DateTime now = DateTime.now();
		DateTime then = new DateTime(birthdate);
		
		Period period = new Period(then, now);

		return ((double) period.getDays()) / (365/12);
	}

	protected int getIndexBasedOnAge(int patientAge) {
		if(patientAge < 12)
			return 0;
		if(patientAge < 59)
			return 1;
		return 2;
	}
	
	protected Double getPewsComponentIncrement(final ConceptService conceptService, Obs obs, Boundaries boundaries, int patientAge) {
		
		Double increment = 0.0;
		if(boundaries.isNumeric()) {
			int ageIdx = getIndexBasedOnAge(patientAge);
			if(boundaries.getHighs().get(ageIdx) <= obs.getValueNumeric() || boundaries.getLows().get(ageIdx) >= obs.getValueNumeric())
				increment = boundaries.getIncrement();	
		}
		else {
			Concept answer = obs.getValueCoded();
			for(String whenAnswer : boundaries.getWhenAnswers()) {
				String[] split = whenAnswer.split(":");
				Concept concept = conceptService.getConceptByMapping(split[1], split[0]);
				if(answer.getId() == concept.getId()) {
					increment = boundaries.getIncrement();
					break;
				}
			}
		}
		
		return increment;
	}

	private Obs getLastObs(ObsService obsService, Patient patient, Concept concept) {
		
		Obs obs = null;
		List<Obs> obsList = obsService.getObservations(Arrays.asList((Person) patient), null, Arrays.asList(concept),
		        null, null, null, null,
		        1, null, null, null, false);
		
		if(!obsList.isEmpty())
			obs = obsList.get(0);
		
		return obs;
	}
}

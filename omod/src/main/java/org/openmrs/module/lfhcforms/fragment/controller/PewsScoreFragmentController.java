package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.jfree.util.Log;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.lfhcforms.utils.OmodResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Populates the patient's page PEwS score widget's view.
 * 
 * @author Dimitri Renault
 */
public class PewsScoreFragmentController {
	
	// Simple configuration where each PEWS score component is capped.
	final protected static double PEWS_COMPONENT_CAP = 1.0;

	/*
	 * This is a Jackson friendly class to hold the limits of a component of the PEWS calculation.
	 * Such as the limits for the respiratory rate, blood pressure... etc.
	 */
	public static class Boundaries {
		
		public Boundaries() {};
		
		private Boolean isActive = true;

		private Double increment = 0.0;
		private String description = "";
		private String conceptMapping = "";
		private String parentId = "";
		private List<Double> lows = new ArrayList<Double>();
		
		private List<Double> highs = new ArrayList<Double>();
		private List<String> whenAnswers = new ArrayList<String>();
		
		public boolean isActive() {
			return getIsActive();
		}
		
		public boolean isNumeric() {
			return whenAnswers.size() == 0;
		}
		
		public boolean hasParent() {
			return !getParentId().isEmpty();
		}

		public Boolean getIsActive() {
			return isActive;
		}

		public void setIsActive(Boolean isActive) {
			this.isActive = isActive;
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
	
	/**
	 * @param resourceLoader
	 * @param resourcePath
	 * @return The JSON String defining the PEWS limits (boundaries).
	 */
	protected String getBoundariesJson(final ResourceLoader resourceLoader, String resourcePath) {

		String json = "";
		try {
			json = resourceLoader.getResourceAsSting(resourcePath, "UTF-8");
		} catch (IOException e) {
			Log.error("There was an error loading " + resourcePath, e);
		}
		return json;
	}
	
	/**
	 * Unmarshalls the boundaries JSON into a map of {@link Boundaries#}.
	 * @param json
	 */
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
			,	@SpringBean("patientService") PatientService patientService
			,	@SpringBean("conceptService") ConceptService conceptService
			,	@SpringBean("obsService") ObsService obsService
																)
	{	
		patient = patientService.getPatient(patient.getId());
		int ageInMonths = (int) (Days.daysBetween(new DateTime(patient.getBirthdate()), DateTime.now()).getDays() / (365.0/12)); // Patient age truncation in months
		
		String json = getBoundariesJson(new OmodResouceLoaderImpl("lfhcforms"), "pewsScore/boundaries.json");
		
		Map<String, Boundaries> boundariesMap = getBoundariesMapFromJson(json);
				
		Map<String, Double> pewsComponents = new HashMap<String, Double>();
		
		String viewErrMsg = "";		
		for(String conceptMapping : boundariesMap.keySet()) {
			
			Boundaries boundaries = boundariesMap.get(conceptMapping);
			if(!boundaries.isActive())
				continue;
			
			String[] splitMapping = conceptMapping.split(":");
			Concept concept = conceptService.getConceptByMapping(splitMapping[1], splitMapping[0]);

			Obs obs = getLastObs(obsService, patient, concept);
			if(obs == null) {
				viewErrMsg = "'" + concept.getName(Locale.ENGLISH).getName() + "' required for PEWS score has no obs recorded.";
				break;
			}
			
			// TODO Maybe check that all obs date-time are within a window (and record the oldest date-time as the PEWS timestamp
			
			String component = conceptMapping;			
			// initialization
			if(boundaries.hasParent()) {
				component = boundaries.getParentId();
			}
			if(!pewsComponents.containsKey(component))
				pewsComponents.put(component, 0.0);
			// PEWS component calculation/update
			Double updatedComponent = pewsComponents.get(component) + getPewsComponentIncrement(conceptService, obs, boundaries, ageInMonths);
			updatedComponent = Math.min(PEWS_COMPONENT_CAP, updatedComponent);
			pewsComponents.put(component, updatedComponent);
		}
		
		String pews = "";
		if(viewErrMsg.isEmpty()) {
			// Summing up the PEWS components.
			Double pewsScore = 0.0;
			for(Double val : pewsComponents.values())
				pewsScore += val;
			pews = "PEWS score = " + pewsScore;
		}
		else {
			pews = viewErrMsg;
		}
			
		model.addAttribute("pews", pews);
	}
	
	/**
	 * @param patientAge, in months
	 * @return The index to use in 'highs' and 'lows' based on the patient's age.
	 * Example: 0 for 0-11m, 1 for 12m to 4y, 2 for 5y+
	 */
	protected int getIndexBasedOnAge(int patientAge) {
		if(patientAge < 12)
			return 0;
		if(patientAge < 59)
			return 1;
		return 2;
	}
	
	/**
	 * From on the concept's boundaries, this returns the PEWS score increment from the answered obs.
	 * @param conceptService
	 * @param obs The answer to the concept question framed by boundaries.
	 * @param boundaries The {@link Boundaries#} to check the answered obs against.
	 * @param patientAge in months
	 * @return The PEWS score increment.
	 */
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

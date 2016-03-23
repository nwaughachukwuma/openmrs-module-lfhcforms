package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;
import org.openmrs.module.lfhcforms.fragment.controller.visit.VisitStartFragmentController;
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

	protected static final Log log = LogFactory.getLog(PewsScoreFragmentController.class);

	/*
	 * This is a Jackson friendly class to hold the limits of a component of the PEWS calculation.
	 * Such as the limits for the respiratory rate, blood pressure... etc.
	 */
	public static class Boundaries {

		public Boundaries() {};

		private Boolean isActive = true;

		private Boolean isConfig = false; // Used for parents only, to configure a set of children
		private Double increment = 0.0;
		private String description = "";
		private String label = "";
		private String conceptMapping = "";
		private String parentId = "";
		private List<Double> lows = new ArrayList<Double>();

		private List<Double> highs = new ArrayList<Double>();
		private List<String> whenAnswers = new ArrayList<String>();

		public boolean isActive() {
			return getIsActive();
		}

		public boolean isNumeric() {
			return getWhenAnswers().size() == 0 && !isConfig();
		}

		public boolean hasParent() {
			return !getParentId().isEmpty();
		}

		public boolean isConfig() {
			return getIsConfig();
		}

		public Boolean getIsConfig() {
			return isConfig;
		}

		public void setIsConfig(Boolean isConfig) {
			this.isConfig = isConfig;
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

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
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

	/*
	 * Convenience structured type to send the PEWS score components to the view
	 */
	public static class Component {
		public String label = "";
		public Double value = 0.0;

		public Component(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Double getValue() {
			return value;
		}

		public void setValue(Double value) {
			this.value = value;
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
			json = resourceLoader.getResourceAsString(resourcePath, "UTF-8");
		} catch (IOException e) {
			log.error("There was an error loading " + resourcePath, e);
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

		HashMap<String, Boundaries> boundariesMap = new HashMap<String, Boundaries>();
		try {
			boundariesMap = mapper.readValue(json, mapType);
		} catch (Exception e) {
			log.error("There was an error unmarshalling JSON data: \n" + json, e);
		}

		return boundariesMap;
	}

	public void controller(	FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,	@SpringBean("adminService") AdministrationService adminService
			,	@SpringBean("patientService") PatientService patientService
			,	@SpringBean("conceptService") ConceptService conceptService
			,	@SpringBean("obsService")	  ObsService obsService
			)
	{
		Date now = new Date();	// Use this when 'now' is needed throughout the code
		patient = patientService.getPatient(patient.getId());
		int ageInMonths = (int) (Days.daysBetween(new DateTime(patient.getBirthdate()), new DateTime(now)).getDays() / (365.0/12)); // Patient age truncation in months

		long timeWindowInMin = getTimeWindowInMin(adminService);
		long expiryInMin = getExpiryInMin(adminService);

		String json = getBoundariesJson(new OmodResouceLoaderImpl("lfhcforms"), "pewsScore/boundaries.json");
		Map<String, Boundaries> boundariesMap = getBoundariesMapFromJson(json);
		if(boundariesMap.isEmpty())
			log.warn("The JSON boundaries file returned an empty map, please check the file as no boundaries are defined.");

		Map<String, Component> pewsComponents = new HashMap<String, Component>();

		String modelErrMsg = "";	
		Date earliestObsDate = now;
		boolean modelMissingConcept = false;
		
		for(String conceptMapping : boundariesMap.keySet()) {

			Boundaries boundaries = boundariesMap.get(conceptMapping);
			if(!boundaries.isActive() || boundaries.isConfig())
				continue;

			String[] splitMapping = conceptMapping.split(":");
			Concept concept = conceptService.getConceptByMapping(splitMapping[1], splitMapping[0]);

			Obs obs = getLastObs(obsService, patient, concept);

			if (concept != null && obs == null) {
				modelErrMsg = ui.message("lfhcforms.pewsscore.error.obsmissing", concept.getName(Locale.ENGLISH).getName());
				break;
			}

			if (concept == null) {
				log.error("The concept " + splitMapping[0] + ":" + splitMapping[1] +" can not be found. Not displaying PEWS widget");
				modelMissingConcept = true;
				break;
			}

			// Checking the PEWS obs time window
			final Date obsDate = obs.getObsDatetime(); 
			if(obsDate.before(earliestObsDate))
				earliestObsDate = obsDate;

			long ageInMin = TimeUnit.MILLISECONDS.toMinutes( now.getTime() - earliestObsDate.getTime() );
			if(ageInMin > expiryInMin) {
				modelErrMsg = ui.message("lfhcforms.pewsscore.error.expiry", expiryInMin);
				break;
			}

			long obsSpanInMin = TimeUnit.MILLISECONDS.toMinutes( obsDate.getTime() - earliestObsDate.getTime() );
			if(obsSpanInMin > timeWindowInMin) {
				modelErrMsg = ui.message("lfhcforms.pewsscore.error.timewindow", timeWindowInMin);
				break;
			}

			String componentId = conceptMapping;			
			// initialization
			if(boundaries.hasParent()) {
				componentId = boundaries.getParentId();
			}
			if(!pewsComponents.containsKey(componentId)) {
				String label = ui.message(boundariesMap.get(componentId).getLabel());
				pewsComponents.put(componentId, new Component(label));
			}
			// PEWS component calculation/update
			final Component component = pewsComponents.get(componentId);
			component.value += getPewsComponentIncrement(conceptService, obs, boundaries, ageInMonths);
			Double incrementCap = boundariesMap.get(componentId).getIncrement();	
			component.value = Math.min(incrementCap, component.value);	// The parent provides the max increment
			pewsComponents.put(componentId, component);
		}

		// Filling the view
		String modelPews = "";
		String modelLastUpdated = "";
		String modelAction = "";
		// Summing up the PEWS components.
		Double pewsScore = 0.0;
		for(Component component : pewsComponents.values())
			pewsScore += component.value;
		modelPews = ui.message("lfhcforms.pewsscore.title") + " = " + pewsScore;

		//		modelLastUpdated = (new PrettyTime(Context.getLocale())).format(earliestObsDate);
		modelLastUpdated = (new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss")).format(earliestObsDate);

		int actionIdx = getActionIndex(pewsScore);
		modelAction = ui.message("lfhcforms.pewsscore.action" + actionIdx);

		model.addAttribute("missingConcept", modelMissingConcept);
		model.addAttribute("errorMsg", modelErrMsg);
		model.addAttribute("pews", modelPews);
		model.addAttribute("lastUpdated", modelLastUpdated);
		model.addAttribute("action", modelAction);
		model.addAttribute("pewsComponents", pewsComponents.values());
	}

	private long getExpiryInMin(AdministrationService adminService) {
		final String propertyName = LFHCFormsConstants.PEWS_EXPIRY_PROPERTY;

		String expiryString = adminService.getGlobalProperty(propertyName);
		long expiryInMin = 0;
		if(expiryString != null) {
			try {
				expiryInMin = Integer.parseInt(expiryString);
			}
			catch(NumberFormatException e) {
				log.warn("The global property '" + propertyName + "''s value could not be parsed to an integer number of minutes. Falling back to the harcoded default value: " + expiryInMin, e);
			}
		}
		expiryInMin = Math.max(expiryInMin, getTimeWindowInMin(adminService));
		return expiryInMin;
	}

	private long getTimeWindowInMin(AdministrationService adminService) {
		final String propertyName = LFHCFormsConstants.PEWS_TIME_WINDOW_PROPERTY;

		String timeWindowString = adminService.getGlobalProperty(propertyName);
		long timeWindowInMin = LFHCFormsConstants.PEWS_FALLBACK_TIMEWINDOW;
		if(timeWindowString != null) {
			try {
				timeWindowInMin = Integer.parseInt(timeWindowString);
			}
			catch(NumberFormatException e) {
				log.error("The global property '" + propertyName + "''s value could not be parsed to an integer number of minutes. Falling back to the harcoded default value: " + timeWindowInMin, e);
			}
		}
		return timeWindowInMin;
	}

	private int getActionIndex(Double pewsScore) {
		int idx = 5;

		if(pewsScore >= 5)
			idx = 5;
		else if(pewsScore >= 4)
			idx = 4;
		else if(pewsScore >= 3)
			idx = 3;
		else if(pewsScore >= 2)
			idx = 2;
		else
			idx = 1;
		return idx;
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
			if(boundaries.getHighs().get(ageIdx) <= obs.getValueNumeric() || boundaries.getLows().get(ageIdx) > obs.getValueNumeric())
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

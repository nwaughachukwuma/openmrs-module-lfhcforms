package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.lfhcforms.fragment.controller.LivingConditionsScoreFragmentController.LCSDefinition.Question;
import org.openmrs.module.lfhcforms.fragment.controller.LivingConditionsScoreFragmentController.LCSDefinition.Score;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.OmodResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Retrieves the conceptIDs of the LCS definition JSON and saves as a new resource
 * 
 * @author Romain Buisson
 */
public class LivingConditionsScoreFragmentController {


	protected static final Log log = LogFactory.getLog(LivingConditionsScoreFragmentController.class);
	final ObjectMapper mapper = new ObjectMapper();

	/* Jackson friendly representation of the LCSDefinition object
	 * This is to allow full data binding between the JSON provided (lcsDefinition.json) and the Java object created from it
	 */

	public static class LCSDefinition {

		public LCSDefinition() {};

		private String name = "";
		private String description = "";
		private ArrayList<Question> questions = new ArrayList<Question>();

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public ArrayList<Question> getQuestions() {
			return questions;
		}
		public void setQuestions(ArrayList<Question> questions) {
			this.questions = questions;
		}


		public static class Question {

			public Question() {};

			private String conceptMapping = "";
			private String conceptId = "";
			private ArrayList<Score> scores = new ArrayList<Score>();

			public String getConceptMapping() {
				return conceptMapping;
			}
			public void setConceptMapping(String conceptMapping) {
				this.conceptMapping = conceptMapping;
			}
			public ArrayList<Score> getScores() {
				return scores;
			}
			public void setScores(ArrayList<Score> scores) {
				this.scores = scores;
			}
			public String getConceptId() {
				return conceptId;
			}
			public void setConceptId(String conceptId) {
				this.conceptId = conceptId;
			}

		}


		public static class Score {

			public Score() {};

			private String score = "";
			private String value = "";
			private String operator = "";
			private String low = "";
			private String high = "";
			private String conceptMapping = "";
			private String conceptId = "";

			public String getScore() {
				return score;
			}
			public void setScore(String score) {
				this.score = score;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public String getOperator() {
				return operator;
			}
			public void setOperator(String operator) {
				this.operator = operator;
			}
			public String getLow() {
				return low;
			}
			public void setLow(String low) {
				this.low = low;
			}
			public String getHigh() {
				return high;
			}
			public void setHigh(String high) {
				this.high = high;
			}
			public String getConceptMapping() {
				return conceptMapping;
			}
			public void setConceptMapping(String conceptMapping) {
				this.conceptMapping = conceptMapping;
			}
			public String getConceptId() {
				return conceptId;
			}
			public void setConceptId(String conceptId) {
				this.conceptId = conceptId;
			}

		}

	}

	/**
	 * @param resourceLoader
	 * @param resourcePath
	 * @return The JSON String defining the Living Conditions Score (lcsDefinition.json).
	 */
	protected String getLCSJson(final ResourceLoader resourceLoader, String resourcePath) {

		String json = "";

		try {
			json = resourceLoader.getResourceAsSting(resourcePath, "UTF-8");
		} catch (IOException e) {
			log.error("There was an error loading " + resourcePath, e);
		}
		return json;
	}

	/**
	 * Unmarshalls the lcsDefinition JSON into a lcsDefinition Java Object {@link lcsDefinition#}.
	 * @param json
	 */
	protected LCSDefinition getLCSDefinition(String json) {

		LCSDefinition lcsDefinition = new LCSDefinition();

		try {
			lcsDefinition = mapper.readValue(json, LCSDefinition.class);
		} catch (Exception e) {
			log.error("There was an error unmarshalling JSON data: \n" + json, e);
		}

		return lcsDefinition;
	}


	protected String getLCSDefinitionAsString(LCSDefinition lcsDefinition) {

		String json = "";

		try {
			json = mapper.writeValueAsString(lcsDefinition);
		} catch (Exception e) {
			log.error("There was an error converting the " + lcsDefinition + " into JSON string" , e);
		}

		return json;
	}


	public void controller(FragmentModel model,
			@SpringBean("conceptService") ConceptService conceptService)	{



		String json = getLCSJson(new OmodResouceLoaderImpl("lfhcforms"), "livingConditionsScore/lcsDefinition.json");
		LCSDefinition lcsDefinition = getLCSDefinition(json);

		LCSDefinition lcsDefinitionWithIds = retrieveIdsFromMappings(lcsDefinition, conceptService);
		String lcsDefJSON = getLCSDefinitionAsString(lcsDefinitionWithIds);

		model.put("lcsDefinition",lcsDefJSON);

	}

	private LCSDefinition retrieveIdsFromMappings(LCSDefinition lcsDefinitionWithMappings, ConceptService conceptService) {

		LCSDefinition lcsDefWithIds = new LCSDefinition();
		lcsDefWithIds = lcsDefinitionWithMappings;

		for (Question question : lcsDefWithIds.getQuestions()) {

			String[] splitMapping = question.getConceptMapping().split(":");
			Concept concept = conceptService.getConceptByMapping(splitMapping[1], splitMapping[0]);

			if (concept != null ) {
				
				question.setConceptId(concept.getConceptId().toString());

				for (Score score : question.getScores())  {

					String conceptMapping = score.getConceptMapping();

					if (score.getConceptMapping() != null && score.getConceptMapping() != "") {
						splitMapping = conceptMapping.split(":");
						concept = conceptService.getConceptByMapping(splitMapping[1], splitMapping[0]);

						score.setConceptId(concept.getConceptId().toString());
					}
				}
			}
		}

		return lcsDefWithIds;

	}
}

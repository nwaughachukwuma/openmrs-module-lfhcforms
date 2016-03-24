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
import org.openmrs.module.lfhcforms.utils.OmodResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Retrieves the conceptIDs of the LCS definition JSON and sends to the View
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

		/**
		 * Converts a LCSDefinition object into a JSON string
		 * 
		 * return json
		 */
		protected String getLCSDefinitionAsString() {

			final ObjectMapper mapper = new ObjectMapper();

			String json = "";

			try {
				json = mapper.writeValueAsString(this);
			} catch (Exception e) {
				log.error("There was an error converting the " + this + " into JSON string" , e);
			}

			return json;
		}

		public static class Question {

			public String getIsNumeric() {
				return isNumeric;
			}
			public void setIsNumeric(String isNumeric) {
				this.isNumeric = isNumeric;
			}
			public Question() {};

			private String conceptMapping = "";
			private String conceptId = "";
			private String isNumeric = "";
			
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
	 * 
	 * @param resourceLoader
	 * @param resourcePath
	 * @return The JSON String defining the Living Conditions Score (lcsDefinition.json).
	 */
	protected String getLCSJson(final ResourceLoader resourceLoader, String resourcePath) {

		String json = "";

		try {
			json = resourceLoader.getResourceAsString(resourcePath, "UTF-8");
		} catch (IOException e) {
			log.error("There was an error loading " + resourcePath, e);
		}
		return json;
	}

	/**
	 * Unmarshalls the lcsDefinition JSON into a LCSDefinition Java Object {@link lcsDefinition#}.
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



	/**
	 * Controller to retrieve ConceptIds based on ConceptMappings
	 * 
	 * @param model
	 * @param conceptService
	 */
	public void controller(FragmentModel model,
			@SpringBean("conceptService") ConceptService conceptService)	{

		String json = getLCSJson(new OmodResouceLoaderImpl("lfhcforms"), "livingConditionsScore/lcsDefinition.json");
		LCSDefinition lcsDefinition = getLCSDefinition(json);

		LCSDefinition lcsDefinitionWithIds = retrieveConceptIds(lcsDefinition, conceptService);
		String lcsDefJSON = lcsDefinitionWithIds.getLCSDefinitionAsString();

		model.put("lcsDefinition",lcsDefJSON);

	}

	/**
	 * Returns a new LCSDefinitionObject that has ConceptIds
	 * 
	 * @param lcsDefinitionWithMappings
	 * @param conceptService
	 * @return lcsDefinitonWithIds
	 */
	protected LCSDefinition retrieveConceptIds(LCSDefinition lcsDefinitionWithMappings, ConceptService conceptService) {

		LCSDefinition lcsDefWithIds = new LCSDefinition();
		lcsDefWithIds = lcsDefinitionWithMappings;

		for (Question question : lcsDefWithIds.getQuestions()) {

			if (question.getConceptMapping() != null && question.getConceptMapping() != "") {

				String[] splitMapping = question.getConceptMapping().split(":");
				Concept questionConcept = null;

				if (splitMapping.length == 1) {
					log.error("Malformed JSON configuration file: livingConditionsScore/lcsDefinition.json \n "
							+ "One of the questions seems to have no valid mapping" );
				} else {
					questionConcept = conceptService.getConceptByMapping(splitMapping[1], splitMapping[0]);
				}

				if (questionConcept != null ) {

					question.setConceptId(questionConcept.getConceptId().toString());

					for (Score score : question.getScores())  {

						String conceptMapping = score.getConceptMapping();

						if (score.getConceptMapping() != null && score.getConceptMapping() != "") {
							splitMapping = conceptMapping.split(":");
							Concept answerConcept = conceptService.getConceptByMapping(splitMapping[1], splitMapping[0]);

							if (answerConcept == null) {
								log.error("Unable to retrieve ConceptId for ConceptMapping " + score.getConceptMapping());
							} else {
								score.setConceptId(answerConcept.getConceptId().toString());
							}
						}
					}
				} else {
					log.error("Unable to retrieve ConceptId for ConceptMapping " + question.getConceptMapping());
				}
			}
		}

		return lcsDefWithIds;

	}
}

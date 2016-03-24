package org.openmrs.module.lfhcforms.fragment.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.lfhcforms.fragment.controller.LivingConditionsScoreFragmentController.LCSDefinition;
import org.openmrs.module.lfhcforms.fragment.controller.LivingConditionsScoreFragmentController.LCSDefinition.Question;
import org.openmrs.module.lfhcforms.fragment.controller.LivingConditionsScoreFragmentController.LCSDefinition.Score;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;
import org.skyscreamer.jsonassert.JSONAssert;

public class LivingConditionsScoreFragementControllerTest {

	private LivingConditionsScoreFragmentController controller = new LivingConditionsScoreFragmentController();
	private LCSDefinition lcsDefinition = new LCSDefinition();
	private Question question1 = new Question();
	private Question question2 = new Question();
	private Score score0Q1 = new Score();
	private Score score1Q1 = new Score();
	private Score score0Q2 = new Score();
	private Score score1Q2 = new Score();

	private ArrayList<Question> questions = new ArrayList<Question>();

	private ArrayList<Score> scoresQ1 = new ArrayList<Score>();
	private ArrayList<Score> scoresQ2 = new ArrayList<Score>();

	private ConceptService mockConceptService;
	private Concept mockQuestionConcept;
	private Concept mockAnswerConcept;
	private Score mockScore0Q1;
	private Question mockQuestion1;

	@Before
	public void setUp() {

		// We create a lcsDefinition object
		scoresQ1.addAll(Arrays.asList(score0Q1,score1Q1));
		scoresQ2.addAll(Arrays.asList(score0Q2,score1Q2));

		question1.setScores(scoresQ1);
		question2.setScores(scoresQ2);
		questions.addAll(Arrays.asList(question1,question2));

		lcsDefinition.setQuestions(questions);

		// A set of useful mocks
		mockConceptService = mock(ConceptService.class);
		mockQuestionConcept = mock(Concept.class);
		mockAnswerConcept = mock(Concept.class);
		mockScore0Q1 = mock(Score.class);
		mockQuestion1 = mock(Question.class);
	}

	@Test
	public void shouldAddConceptIdProperty() {

		when(mockConceptService.getConceptByMapping("1000", "LFHC")).thenReturn(mockQuestionConcept);
		when(mockQuestionConcept.getConceptId()).thenReturn(1234);

		when(mockConceptService.getConceptByMapping("1100", "LFHC")).thenReturn(mockAnswerConcept);
		when(mockAnswerConcept.getConceptId()).thenReturn(5678);

		question1.setConceptMapping("LFHC:1000");
		question1.setIsNumeric("true");

		score0Q1.setScore("0");
		score0Q1.setConceptMapping("LFHC:1100");

		LCSDefinition lcsDefWithIds = controller.retrieveConceptIds(lcsDefinition, mockConceptService);

		assertTrue(lcsDefWithIds.getQuestions().get(0).getConceptId().equals("1234"));
		assertTrue(lcsDefWithIds.getQuestions().get(0).getScores().get(0).getConceptId().equals("5678"));
	}

	@Test
	public void shouldConvertJsonToALCSDefinitionObject() {
		// Testing the "actual" resource
		String json = controller.getLCSJson(new DefaultResouceLoaderImpl(), "livingConditionsScore/lcsDefinition.json");
		LCSDefinition lcsDefFromJson = controller.getLCSDefinition(json);

		assertThat(lcsDefFromJson, instanceOf(LCSDefinition.class));
	}

	@Test
	public void shouldUnmarhsallJsonAndConvertBackToTheSameObject() {
		// Testing the "actual" resource
		String jsonIn = controller.getLCSJson(new DefaultResouceLoaderImpl(), "livingConditionsScore/lcsDefinition.json");
		LCSDefinition lcsDefFromJson = controller.getLCSDefinition(jsonIn);

		String jsonOut = lcsDefFromJson.getLCSDefinitionAsString(); 

		try {
			JSONAssert.assertEquals(jsonIn, jsonOut, false);
		} catch (JSONException e) {
			assertTrue(false);
		}
	}

	@Test
	public void shouldNotTryToAddConceptIdWhenConceptMappingIsNotFoundInQuestion() {

		when(mockConceptService.getConceptByMapping("1000", "LFHC")).thenReturn(mockQuestionConcept);
		when(mockQuestionConcept.getConceptId()).thenReturn(1234);

		// empty the current questions list and fill it with only our mock
		questions.clear();
		questions.add(mockQuestion1);

		// we ensure that even if the Question has no ConceptMapping, we still add a ConceptMapping for Score.
		// Not really need but closer to reality
		score0Q1.setScore("0");
		score0Q1.setConceptMapping("LFHC:1100");
		when(mockQuestion1.getScores()).thenReturn(scoresQ1);

		// Call the function one time with ConceptMapping=null
		controller.retrieveConceptIds(lcsDefinition, mockConceptService);

		// Call the function a second time with ConceptMapping=""
		when(mockQuestion1.getConceptMapping()).thenReturn("");
		controller.retrieveConceptIds(lcsDefinition, mockConceptService);

		// Verify that the setConceptId method is never called
		verify(mockQuestion1, never()).setConceptId(Mockito.anyString());

	}

	@Test
	public void shouldNotTryToAddConceptIdWhenConceptMappingIsNotFoundInScore() {

		when(mockConceptService.getConceptByMapping("1000", "LFHC")).thenReturn(mockQuestionConcept);
		when(mockQuestionConcept.getConceptId()).thenReturn(1234);
 
		question1.setConceptMapping("LFHC:1000");
		question1.setIsNumeric("true");
		
		// empty the current scores list and fill it with our mock instead
		scoresQ1.clear();
		scoresQ1.add(mockScore0Q1);

		controller.retrieveConceptIds(lcsDefinition, mockConceptService);

		// Verify that the setConceptId method is never called
		verify(mockScore0Q1, never()).setConceptId(Mockito.anyString());
	}

}

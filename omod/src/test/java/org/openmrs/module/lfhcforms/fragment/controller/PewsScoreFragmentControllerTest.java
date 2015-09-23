package org.openmrs.module.lfhcforms.fragment.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.prettytime.PrettyTime;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.lfhcforms.fragment.controller.PewsScoreFragmentController.Boundaries;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;

public class PewsScoreFragmentControllerTest {
	
	private PewsScoreFragmentController controller = new PewsScoreFragmentController();
	
	private Map<String, Boundaries> boundariesMap = null;
	
	@Before
    public void setUp() {
		// We collect the boundaries from the *actual* resource
		String json = controller.getBoundariesJson(new DefaultResouceLoaderImpl(), "pewsScore/boundaries.json");
		boundariesMap = controller.getBoundariesMapFromJson(json);
	}
	
	@Test
	public void getPewsComponentIncrement_checkNumerics() {
	
		Double pewsInc = 0.0;
		ConceptService conceptService = mock(ConceptService.class);
		Obs obs = mock(Obs.class);
		int patientAge = 0;
		
		Boundaries boundaries = boundariesMap.get("CIEL:5242");
		when(obs.getValueNumeric()).thenReturn(25.0);
		patientAge = 11;
		pewsInc = controller.getPewsComponentIncrement(conceptService, obs, boundaries, patientAge);
		assertEquals(pewsInc, new Double(0.0));
		
		when(obs.getValueNumeric()).thenReturn(55.0);
		patientAge = 11;
		pewsInc = controller.getPewsComponentIncrement(conceptService, obs, boundaries, patientAge);
		assertEquals(pewsInc, new Double(0.0));
		
		when(obs.getValueNumeric()).thenReturn(55.0);
		patientAge = 15;
		pewsInc = controller.getPewsComponentIncrement(conceptService, obs, boundaries, patientAge);
		assertEquals(pewsInc, boundaries.getIncrement());
	}
	
	@Test
	public void getPewsComponentIncrement_checkCoded() {
	
		final int maxAge = 204;
		Random random = new Random(); // to generate random patient ages
		
		Double pewsInc = 0.0;
		ConceptService conceptService = mock(ConceptService.class);
		Obs obs = mock(Obs.class);
		Concept answer = mock(Concept.class);
		when(answer.getId()).thenReturn(123456);
		when(obs.getValueCoded()).thenReturn(answer);
		
		String conceptMapping = "CIEL:136768";
		String[] splitMapping = conceptMapping.split(":");
		Boundaries boundaries = boundariesMap.get(conceptMapping);
		
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(mock(Concept.class));
		when(conceptService.getConceptByMapping(eq(splitMapping[1]), eq(splitMapping[0]))).thenReturn(answer);
		pewsInc = controller.getPewsComponentIncrement(conceptService, obs, boundaries, random.nextInt(maxAge));
		assertEquals(pewsInc, new Double(0.0));
		
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(answer);
		when(conceptService.getConceptByMapping(eq(splitMapping[1]), eq(splitMapping[0]))).thenReturn(answer);
		pewsInc = controller.getPewsComponentIncrement(conceptService, obs, boundaries, random.nextInt(maxAge));
		assertEquals(pewsInc, new Double(1.0));
	}
	
	@Test
	public void getPewsComponentIncrement_checkBoundaries() {
	
		HashMap<String, Integer> parentIdsCount = new HashMap<String, Integer>();
		HashSet<String> labels = new HashSet<String>();
		for(Boundaries boundaries : boundariesMap.values()) {
			
			if(!boundaries.isActive())
				continue;
			
			if(false == boundaries.getLabel().isEmpty()) {
				assertTrue(false == labels.contains(boundaries.getLabel()));
				labels.add(boundaries.getLabel());
			}
			
			if(boundaries.isNumeric()) {
				int maxAgeIdx = controller.getIndexBasedOnAge(Integer.MAX_VALUE);
				assertEquals(boundaries.getHighs().size(), maxAgeIdx + 1); 
				assertEquals(boundaries.getHighs().size(), boundaries.getLows().size());
				assertEquals(boundaries.getWhenAnswers().isEmpty(), true);
				for(int i = 0; i < boundaries.getHighs().size(); i++)
					assertTrue(boundaries.getLows().get(i) <= boundaries.getHighs().get(i));
			}
			else if(!boundaries.isConfig()) {
				assertEquals(boundaries.getWhenAnswers().isEmpty(), false);
				assertEquals(boundaries.getHighs().size(), 0); 
				assertEquals(boundaries.getHighs().size(), boundaries.getLows().size());
			}
			assertTrue(boundaries.getIncrement() > 0.0);
			
			if(boundaries.hasParent()) {
				String id = boundaries.getParentId();
				assertTrue(boundariesMap.containsKey(id));
				Boundaries parentBoundaries = boundariesMap.get(id);
				assertTrue(false == parentBoundaries.getLabel().isEmpty());
				assertTrue(boundaries.getLabel().isEmpty());
				
				if(!parentIdsCount.containsKey(id))
					parentIdsCount.put(id, 0);
				parentIdsCount.put(id, parentIdsCount.get(id) + 1);
			}
		}
		for(Integer count : parentIdsCount.values())
			assertTrue(count > 1);
	}
	
	@Test
	public void getPewsComponentIncrement_foo() throws ParseException {
	
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date thisDate = formatter.parse("2015-09-05");
		
		System.out.println( (new PrettyTime(new Locale("en-GB"))).format(thisDate) );
	}
}

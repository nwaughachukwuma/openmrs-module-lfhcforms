package org.openmrs.module.lfhcforms.fragment.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;

public class LatestWeightFragmentControllerTest {
	
	private LatestWeightFragmentController ctrl = new LatestWeightFragmentController();
	
	private ConceptService conceptService;
	private ObsService obsService;
	private Patient patient;
	private Log log;
	
	@Before
    public void setUp() {
		conceptService = mock(ConceptService.class);
		obsService = mock(ObsService.class);
		patient = mock(Patient.class);
		log = spy(LogFactory.getLog(LatestWeightFragmentController.class));
		ctrl.setLogger(log);
	}
	
	@Test
	public void getLatestWeight_CheckNullConcept() {
	
		// Setup
		when(conceptService.getConceptByMapping(eq(ctrl.getWeightConceptCode()), eq(ctrl.getWeightConceptSource()))).thenReturn(null);
		
		// Replay
		String weightString = ctrl.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, "");
		verify(log, times(1)).error(anyString());
	}
	
	@Test
	public void getLatestWeight_CheckWeight() {

		// Setup
		Double weight = 15.0;
		Concept conceptWeight = mock(Concept.class);
		final Obs obs = mock(Obs.class);
		when(obs.getValueNumeric()).thenReturn(weight);
		when(conceptService.getConceptByMapping(eq(ctrl.getWeightConceptCode()), eq(ctrl.getWeightConceptSource()))).thenReturn(conceptWeight);
		when(obsService.getObservationsByPersonAndConcept(patient, conceptWeight)).thenReturn(new ArrayList<Obs>() {{ add(obs); }});
		
		// Replay
		String weightString = ctrl.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, Double.toString(weight));
		verify(log, times(1)).error(anyString());
	}
	
	@Test
	public void getLatestWeight_CheckObsNotNumeric() {

		// Setup
		Concept conceptWeight = mock(Concept.class);
		final Obs obs = mock(Obs.class);
		when(obs.getValueNumeric()).thenReturn(null);
		when(conceptService.getConceptByMapping(eq(ctrl.getWeightConceptCode()), eq(ctrl.getWeightConceptSource()))).thenReturn(conceptWeight);
		when(obsService.getObservationsByPersonAndConcept(patient, conceptWeight)).thenReturn(new ArrayList<Obs>() {{ add(obs); }});
		
		// Replay
		String weightString = ctrl.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, "");
		verify(log, times(2)).error(anyString());
	}
	
	@Test
	public void getLatestWeight_CheckNoObs() {

		// Setup
		Concept conceptWeight = mock(Concept.class);
		when(conceptService.getConceptByMapping(eq(ctrl.getWeightConceptCode()), eq(ctrl.getWeightConceptSource()))).thenReturn(conceptWeight);
		when(obsService.getObservationsByPersonAndConcept(patient, conceptWeight)).thenReturn(new ArrayList<Obs>());
		
		// Replay
		String weightString = ctrl.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, "");
		verify(log, times(1)).error(anyString());
	}
	
	@Test
	public void getLatestWeight_CheckWeightWithUnits() {

		// Setup
		Double weight = 15.0;
		String units = "kg";
		Concept conceptWeight = mock(Concept.class);
		ConceptNumeric conceptWeightNumeric = mock(ConceptNumeric.class);
		when(conceptWeightNumeric.getUnits()).thenReturn(units);
		int id = 1234567;
		when(conceptWeight.getId()).thenReturn(id);
		final Obs obs = mock(Obs.class);
		when(obs.getValueNumeric()).thenReturn(weight);
		when(conceptService.getConceptNumeric(eq(id))).thenReturn(conceptWeightNumeric);
		when(conceptService.getConceptByMapping(eq(ctrl.getWeightConceptCode()), eq(ctrl.getWeightConceptSource()))).thenReturn(conceptWeight);
		when(obsService.getObservationsByPersonAndConcept(patient, conceptWeight)).thenReturn(new ArrayList<Obs>() {{ add(obs); }});
		
		// Replay
		String weightString = ctrl.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, Double.toString(weight) + units);
		verify(log, never()).error(anyString());
	}
}
package org.openmrs.module.lfhcforms.fragment.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;

public class LatestWeightFragmentControllerTest {
	
	private LatestWeightFragmentController controller = new LatestWeightFragmentController();
	
	private ConceptService conceptService;
	private ObsService obsService;
	private Patient patient;
	
	@Before
    public void setUp() {
		conceptService = mock(ConceptService.class);
		obsService = mock(ObsService.class);
		patient = mock(Patient.class);
	}
	
	@Test
	public void getLatestWeight_CheckNullConcept() {
	
		// Setup
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(null);
		
		// Replay
		String weightString = controller.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, "");		
	}
	
	@Test
	public void getLatestWeight_CheckWeight() {

		// Setup
		Double weight = 15.0;
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(null);		
		Concept conceptWeight = mock(Concept.class);
		final Obs obs = mock(Obs.class);
		when(obs.getValueNumeric()).thenReturn(weight);
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(conceptWeight);
		when(obsService.getObservationsByPersonAndConcept(patient, conceptWeight)).thenReturn(new ArrayList<Obs>() {{ add(obs); }});
		
		// Replay
		String weightString = controller.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, Double.toString(weight));
	}
	
	@Test
	public void getLatestWeight_CheckObsNotNumeric() {

		// Setup
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(null);		
		Concept conceptWeight = mock(Concept.class);
		final Obs obs = mock(Obs.class);
		when(obs.getValueNumeric()).thenReturn(null);
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(conceptWeight);
		when(obsService.getObservationsByPersonAndConcept(patient, conceptWeight)).thenReturn(new ArrayList<Obs>() {{ add(obs); }});
		
		// Replay
		String weightString = controller.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, "");
	}
	
	@Test
	public void getLatestWeight_CheckNoObs() {

		// Setup
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(null);		
		Concept conceptWeight = mock(Concept.class);
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(conceptWeight);
		when(obsService.getObservationsByPersonAndConcept(patient, conceptWeight)).thenReturn(new ArrayList<Obs>());
		
		// Replay
		String weightString = controller.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, "");
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
		when(conceptService.getConceptByMapping(anyString(), anyString())).thenReturn(conceptWeight);
		when(obsService.getObservationsByPersonAndConcept(patient, conceptWeight)).thenReturn(new ArrayList<Obs>() {{ add(obs); }});
		
		
		// Replay
		String weightString = controller.getLatestWeight(conceptService, obsService, patient);
		
		// Verif
		assertEquals(weightString, Double.toString(weight) + units);
	}	
}

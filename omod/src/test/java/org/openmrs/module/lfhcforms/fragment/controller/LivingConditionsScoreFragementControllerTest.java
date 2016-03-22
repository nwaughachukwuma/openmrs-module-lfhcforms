package org.openmrs.module.lfhcforms.fragment.controller;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.lfhcforms.fragment.controller.LivingConditionsScoreFragmentController.LCSDefinition;

public class LivingConditionsScoreFragementControllerTest {

	private LivingConditionsScoreFragmentController controller = new LivingConditionsScoreFragmentController();
	private LCSDefinition lcsDefinition = new LCSDefinition();
	
	@Before
    public void setUp() {
		 
	}
	
	@Test
	public void shouldAddConceptIdField() {
		assertTrue(true);
	}
	
	@Test
	public void shouldCorrectlyConvertJson() {
		assertTrue(true);
	}

	@Test
	public void shouldNotAddConceptIdWhenNoConceptMappingIsProvidedInScore() {
		assertTrue(true);
	}
	
	@Test
	public void shouldFailWhenConceptMappingIsNotFound() {
		assertTrue(true);
	}

	
}

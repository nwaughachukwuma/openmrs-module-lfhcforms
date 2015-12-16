package org.openmrs.module.lfhcforms.utils;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;

public class VisitTypeHelperTest {

	private VisitService mockVisitService;
	private EncounterService mockEncounterService;
	private List<VisitType> types = new ArrayList<VisitType>();
	private String property;
	private Patient mockPatient;
	private Encounter mockEncounter;
	private VisitType mockVisitType;
	private Visit spyVisit;
	private VisitTypeHelper visitTypeHelper;


	@Before
	public void setUp() {

		mockVisitService = mock(VisitService.class);
		mockEncounterService = mock(EncounterService.class);
		mockPatient = mock(Patient.class);
		mockEncounter = mock(Encounter.class);
		mockVisitType = mock(VisitType.class);
		spyVisit = spy(new Visit());
		visitTypeHelper = new VisitTypeHelper();

		property = "{ "
				+ "\"1\":\""+ "44-44" + "\","
				+ "\"2\":\""+ "33-33" + "\"," 
				+ "\"3\":\""+ "11-11" +"\""
				+ "}" ;

		{
			VisitType type = new VisitType("Outpatient","");
			type.setUuid("11-11");
			types.add(type);
		}
		{
			VisitType type = new VisitType("Inpatient","");
			type.setUuid("22-22");
			types.add(type);
		}
		{
			VisitType type = new VisitType("Outreach","");
			type.setUuid("33-33");
			types.add(type);
		}
		{
			VisitType type = new VisitType("Emergency","");
			type.setUuid("44-44");
			types.add(type);
		}

		when(mockVisitService.getVisitTypeByUuid("11-11")).thenReturn(types.get(0));
		when(mockVisitService.getVisitTypeByUuid("22-22")).thenReturn(types.get(1));
		when(mockVisitService.getVisitTypeByUuid("33-33")).thenReturn(types.get(2));
		when(mockVisitService.getVisitTypeByUuid("44-44")).thenReturn(types.get(3));
	}

	@Test
	public void shouldNotSetTransferWhenStartingVisit() {

		EncounterType type = new EncounterType();
		when(mockEncounterService.getEncounterTypeByUuid(any(String.class))).thenReturn(type);

		Person person = new Person();
		Location loginLocation = new Location();

		visitTypeHelper.setTransferEncounter(spyVisit, mockVisitService, mockEncounterService, mockEncounter, mockPatient, person, loginLocation, null, true);
		// verify that the addEncounter method is NOT called
		verify(spyVisit, times(0)).addEncounter(mockEncounter);
	}

	@Test
	public void shouldSetTransferWhenTransferVisit() {

		EncounterType type = new EncounterType();
		when(mockEncounterService.getEncounterTypeByUuid(any(String.class))).thenReturn(type);

		Person person = new Person();
		Location loginLocation = new Location();

		visitTypeHelper.setTransferEncounter(spyVisit, mockVisitService, mockEncounterService, mockEncounter, mockPatient, person, loginLocation, mockVisitType, true);

		// check that setEncounterType is called with type 'type'
		verify(mockEncounter, times(1)).setEncounterType(type);

		// check that addEncounter is called with 'mockEncounter'
		verify(spyVisit, times(1)).addEncounter(mockEncounter);
	}


	@Test
	public void shouldHandleNoPropertyFound() {
		List<VisitType> result = visitTypeHelper.getOrderedVisitTypes(types,null,mockVisitService);
		assertTrue(result!=null);
	}

	@Test
	public void shouldReturnTypesOrdered() {
		List<VisitType> result = visitTypeHelper.getOrderedVisitTypes(types,property,mockVisitService);
		assertTrue(result.get(0).getUuid().equals("44-44"));
		assertTrue(result.get(1).getUuid().equals("33-33"));
		assertTrue(result.get(2).getUuid().equals("11-11"));
	}

	@Test
	public void shouldFallbackWhenEmptyProperty() {
		// test empty property
		List<VisitType> result = visitTypeHelper.getOrderedVisitTypes(types,new String(),mockVisitService);
		assertTrue(result.equals(types));
	}

	@Test
	public void shouldFallbackWhenNoCommonValue() {
		// test property with no common values
		String prop = "{ "
				+ "\"1\":\""+ "66-66" + "\","
				+ "\"2\":\""+ "77-77" + "\"," 
				+ "\"3\":\""+ "88-88" +"\""
				+ "}" ;
		List<VisitType> result = visitTypeHelper.getOrderedVisitTypes(types,prop,mockVisitService);
		assertTrue(result.equals(types));
	}

	@Test
	public void shouldAppendUnOrderedTypesAtTheEnd() {
		// test when "types" contains Visit Types that are not in the "property value (ie, no order is provided
		// they should be added at the end of the List
		List<VisitType> result = visitTypeHelper.getOrderedVisitTypes(types,property,mockVisitService);
		assertTrue(result.get(3).equals(types.get(1)));
	}
}

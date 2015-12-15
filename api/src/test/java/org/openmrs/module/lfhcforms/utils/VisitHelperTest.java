package org.openmrs.module.lfhcforms.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;

public class VisitHelperTest {

	private Location mockLocation;
	private LocationAttributeType attrType;
	private VisitService mockVisitService;
	private EncounterService mockEncounterService;
	private List<VisitType> types = new ArrayList<VisitType>();
	private String property;
	private Patient mockPatient;
	private Encounter mockEncounter;
	private VisitType mockVisitType;
	private Visit spyVisit;
	private VisitHelper visitHelper;
	
	@Before
	public void setUp() {

		mockLocation = mock(Location.class);
		mockVisitService = mock(VisitService.class);
		mockEncounterService = mock(EncounterService.class);
		mockPatient = mock(Patient.class);
		mockEncounter = mock(Encounter.class);
		mockVisitType = mock(VisitType.class);
		spyVisit = spy(new Visit());

		attrType = new LocationAttributeType();
		visitHelper = new VisitHelper();

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
	public void shouldReturnMostRecentAttribute() {

		LocationAttribute attr1 =  new LocationAttribute();
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2015);
			cal.set(Calendar.MONTH, Calendar.JUNE);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date date = cal.getTime();	
			attr1.setDateCreated(date);
			attr1.setLocation(mockLocation);
		}

		LocationAttribute attr2 =  new LocationAttribute();
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2015);
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date date = cal.getTime();	
			attr2.setDateCreated(date);
			attr2.setLocation(mockLocation);
		}

		LocationAttribute attr3 =  new LocationAttribute();
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2015);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date date = cal.getTime();	
			attr3.setDateCreated(date);
			attr3.setLocation(mockLocation);
		}

		ArrayList<LocationAttribute> allAttr = new ArrayList<LocationAttribute>();
		allAttr.add(attr1);
		allAttr.add(attr2);
		allAttr.add(attr3);
		when(mockLocation.getActiveAttributes(attrType)).thenReturn(allAttr);
		assertEquals(attr2, visitHelper.getMostRecentAttribute(mockLocation, attrType));
	}

	@Test
	public void shouldReturnNullWhenNoAttributeIsFound() {
		assertNull(visitHelper.getMostRecentAttribute(mockLocation, attrType));
	}


}

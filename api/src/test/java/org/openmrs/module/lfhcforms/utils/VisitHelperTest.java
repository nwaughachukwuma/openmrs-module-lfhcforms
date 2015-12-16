package org.openmrs.module.lfhcforms.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;

public class VisitHelperTest {

	private Location mockLocation;
	private LocationAttributeType attrType;
	private Patient mockPatient;
	private VisitHelper visitHelper;

	@Before
	public void setUp() {

		mockLocation = mock(Location.class);
		mockPatient = mock(Patient.class);

		attrType = new LocationAttributeType();
		visitHelper = new VisitHelper();
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

	@Test
	public void shouldReturnAllActiveVisits() {

		List<Location> locations = new ArrayList<Location>();

		Location loc1 = new Location();
		Location loc2 = new Location();
		Location loc3 = new Location();

		locations.add(loc1);
		locations.add(loc2);
		locations.add(loc3);

		VisitDomainWrapper vdw1 = mock(VisitDomainWrapper.class);
		VisitDomainWrapper vdw2 = mock(VisitDomainWrapper.class);

		AdtService adtService = mock(AdtService.class);

		when(adtService.getAllLocationsThatSupportVisits()).thenReturn(locations);
		when(adtService.getActiveVisit(mockPatient, loc1)).thenReturn(vdw1);
		when(adtService.getActiveVisit(mockPatient, loc2)).thenReturn(vdw2);

		List<Visit> activeVisits = visitHelper.getActiveVisits(mockPatient,adtService);

		assertTrue(activeVisits.size() == 2);

	}

}

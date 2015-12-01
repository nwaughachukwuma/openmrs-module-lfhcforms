package org.openmrs.module.lfhcforms.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.LocationTag;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;
import org.openmrs.module.lfhcforms.utils.Utils;
import org.powermock.core.classloader.annotations.PrepareForTest;


public class UtilsTest {

	private Location location;
	private LocationAttributeType attrType;
	private Visit visit;
	private EncounterService es;
	private VisitService vs;
	private EmrApiProperties emrApiProperties;
	private VisitDomainWrapper visitWrapper;
	
	@Before
    public void setUp() {
		location = mock(Location.class);
		attrType = new LocationAttributeType();
		visit = mock(Visit.class);
		es = mock(EncounterService.class);
		vs = mock(VisitService.class);
		emrApiProperties = mock(EmrApiProperties.class);
		// TODO: NoClassDefFoundError with the following instanciation
		// visitWrapper = new VisitDomainWrapper(visit, emrApiProperties);
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
			attr1.setLocation(location);
		}

		LocationAttribute attr2 =  new LocationAttribute();
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2015);
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date date = cal.getTime();	
			attr2.setDateCreated(date);
			attr2.setLocation(location);
		}

		LocationAttribute attr3 =  new LocationAttribute();
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2015);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date date = cal.getTime();	
			attr3.setDateCreated(date);
			attr3.setLocation(location);

		}
		ArrayList<LocationAttribute> allAttr = new ArrayList<LocationAttribute>();
		allAttr.add(attr1);
		allAttr.add(attr2);
		allAttr.add(attr3);

		when(location.getActiveAttributes(attrType)).thenReturn(allAttr);

		assertEquals(attr2, Utils.getMostRecentAttribute(location, attrType));
	}
	
	@Test
	public void shouldReturnNullWhenNoAttributeIsFound() {
		assertNull(Utils.getMostRecentAttribute(location, attrType));
	}
	
	@Test
	public void shouldNotAdmitAlreadyAdmittedVisit() {
//		TODO: shouldNotAdmitAlreadyAdmittedVisit
	}
	
	@Test
	public void shouldAdmitNonAdmittedVisit() {
//		TODO: shouldAdmitNonAdmittedVisit
		
//		Utils.setAdmission(visit, vs, es, true, true);
//		assertTrue(visitWrapper.isAdmitted());
	}
	
	@Test
	public void shouldDischargeAdmittedVisit() {
//		TODO: shouldDischargeAdmittedVisit
	}

}

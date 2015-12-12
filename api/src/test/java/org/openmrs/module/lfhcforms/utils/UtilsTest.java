package org.openmrs.module.lfhcforms.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.module.lfhcforms.utils.Utils;

public class UtilsTest {

	private Location location;
	private LocationAttributeType attrType;
	private VisitService vs;
	private List<VisitType> types = new ArrayList<VisitType>();
	private String property;

	@Before
	public void setUp() {
		location = mock(Location.class);
		attrType = new LocationAttributeType();
		vs = mock(VisitService.class);
		
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
		
		when(vs.getVisitTypeByUuid("11-11")).thenReturn(types.get(0));
		when(vs.getVisitTypeByUuid("22-22")).thenReturn(types.get(1));
		when(vs.getVisitTypeByUuid("33-33")).thenReturn(types.get(2));
		when(vs.getVisitTypeByUuid("44-44")).thenReturn(types.get(3));
		

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
	public void shouldHandleNoPropertyFound() {
		List<VisitType> result = Utils.getOrderedVisitTypes(types,null,vs);
		assertTrue(result!=null);
	}

	@Test
	public void shouldReturnTypesOrdered() {
		List<VisitType> result = Utils.getOrderedVisitTypes(types,property,vs);
		assertTrue(result.get(0).getUuid().equals("44-44"));
		assertTrue(result.get(1).getUuid().equals("33-33"));
		assertTrue(result.get(2).getUuid().equals("11-11"));
	}

	@Test
	public void shouldFallbackWhenEmptyProperty() {
		// test empty property
		List<VisitType> result = Utils.getOrderedVisitTypes(types,new String(),vs);
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
		List<VisitType> result = Utils.getOrderedVisitTypes(types,prop,vs);
		assertTrue(result.equals(types));
	}
	
	@Test
	public void shouldAppendUnOrderedTypesAtTheEnd() {
		// test when "types" contains Visit Types that are not in the "property value (ie, no order is provided
		// they should be added at the end of the List
		List<VisitType> result = Utils.getOrderedVisitTypes(types,property,vs);
		assertTrue(result.get(3).equals(types.get(1)));
	}
}


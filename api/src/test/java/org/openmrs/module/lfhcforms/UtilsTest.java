package org.openmrs.module.lfhcforms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.module.lfhcforms.utils.Utils;

public class UtilsTest {

	private Location location;
	private LocationAttributeType attrType;
	
	@Before
    public void setUp() {
		location = mock(Location.class);
		attrType = new LocationAttributeType();
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
	

}

package org.openmrs.module.lfhcforms.utils;

import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;

/**
 * Provides a set of useful methods. 
 * 
 * @author Romain Buisson
 *
 */
public class Utils {


	/**
	 * 
	 * Returns the most recent location attribute for a given location and
	 * location attribute type
	 * 
	 * @param location
	 * @param attrType
	 * @return attributeMap
	 */
	public static LocationAttribute getMostRecentAttribute(Location location, LocationAttributeType attrType) {

		List<LocationAttribute> allAttr = location.getActiveAttributes(attrType);
		NavigableMap<Date, LocationAttribute> attrMap = new TreeMap<Date, LocationAttribute>();
		if (allAttr.size() != 0) {
			for (LocationAttribute attr : allAttr) {
				attrMap.put(attr.getDateCreated(), attr);
			}
			LocationAttribute result = attrMap.lastEntry().getValue();
			return result;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * Sets the admission status of a visit based on its location (if location is of 'admission location' type)
	 * 
	 * 
	 * @param location
	 * @param attrType
	 * @return 
	 * @return attributeMap
	 */
	public static void setAdmissionBasedOnLocation (Visit visit) {
		//TODO: In progress
		Encounter en = new Encounter();
		EncounterService es = Context.getEncounterService();
		en.setEncounterType(es.getEncounterTypeByUuid(LFHCFormsConstants.ADMISSION_ENCOUNTER_TYPE_UUID));
		
		es.saveEncounter(en);
		
		visit.addEncounter(en);
		Context.getVisitService().saveVisit(visit);
	}


}

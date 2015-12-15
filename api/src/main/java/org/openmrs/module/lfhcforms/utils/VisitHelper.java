package org.openmrs.module.lfhcforms.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;

/**
 * @author Romain Buisson
 *
 */

public class VisitHelper {
	
	private VisitTypeHelper visitTypeHelper = new VisitTypeHelper();

	/**
	 * 
	 * Checks if the patient has Active Visit in any location
	 * 
	 * @param patient
	 * @param adtService
	 * @return
	 */
	public List<Visit> getActiveVisits(Patient patient, AdtService adtService) {
	
		ArrayList<Visit> activeVisits = new ArrayList<Visit>();
		List<Location> visitLocations = adtService.getAllLocationsThatSupportVisits();
		for (Iterator<Location> it = visitLocations.iterator(); it.hasNext();) {
			Location loc = it.next();
			VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, loc);
			if (activeVisit != null) {
				activeVisits.add(activeVisit.getVisit());
			}
		}
		return activeVisits;
	}

	/**
	 * Returns the color and short name for a given visit
	 * 
	 * @param visit VisitDomainWrapper
	 * @return 
	 */
	public Map<String, Object> getVisitColorAndShortName(VisitDomainWrapper visit) {
	
		VisitType type = visit.getVisit().getVisitType();
		Map<String, Object> visitTypeColorAndShortName = visitTypeHelper.getVisitTypeColorAndShortName(type);
		return visitTypeColorAndShortName;
	}

	/**
	 * Returns the most recent location attribute for a given location and attribute type 
	 * 
	 * @param location
	 * @param attrType
	 * @return
	 */
	public LocationAttribute getMostRecentAttribute(Location location, LocationAttributeType attrType) {
	
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

}

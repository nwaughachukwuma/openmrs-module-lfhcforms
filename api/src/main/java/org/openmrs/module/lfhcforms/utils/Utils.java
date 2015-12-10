package org.openmrs.module.lfhcforms.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
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

	protected static final Log log = LogFactory.getLog(Utils.class);

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
	 * Sets admission status based on visit location
	 * 
	 * @param visit
	 */
	public static void setEncounterBasedOnVisitType(Visit visit, Location loginLocation) {
		setEncounterBasedOnVisitType(visit, loginLocation, null);
	}

	
	
	/**
	 * Create encounters based on visit type
	 * 
	 * @param visit
	 * @param loginLocation
	 * @param previousType
	 */
	public static void setEncounterBasedOnVisitType(Visit visit, Location loginLocation, VisitType previousType) {

		// any time a visit type is changed (visit type != previousType): create a transfer encounter.

		// all types are transfer type
		boolean isTransferType = true;

		if (visit.getVisitType() == previousType) {
			// visit type is not changed: do nothing
			return;
		}

		EncounterService es = Context.getEncounterService();
		VisitService vs = Context.getVisitService();
		Person person = Context.getUserContext().getAuthenticatedUser().getPerson();

		setEncounter(visit, vs, es, person, loginLocation, previousType, isTransferType);

	}

	/**
	 * 
	 * Creates a transfer encounter when visit type is changed
	 * 
	 * @param visit
	 * @param vs
	 * @param es
	 * @param person
	 * @param loginLocation
	 * @param previousType
	 * @param isTransferType
	 * 
	 */
	protected static void setEncounter(Visit visit, VisitService vs, EncounterService es, Person person, Location loginLocation,
			VisitType previousType, boolean isTransferType) {

		EncounterType transferEncounterType = es
				.getEncounterTypeByUuid(LFHCFormsConstants.TRANSFER_ENCOUNTER_TYPE_UUID);

		Patient patient = visit.getPatient();
		Encounter encounter = new Encounter();

		if ( previousType != null && isTransferType) {
			// add Transfer encounter
			encounter.setEncounterType(transferEncounterType);
			encounter.setPatient(patient);
			encounter.setEncounterDatetime(new Date());
			encounter.setProvider(person);
			encounter.setLocation(loginLocation);
			es.saveEncounter(encounter);
			visit.addEncounter(encounter);
			vs.saveVisit(visit);
		}
	}

	/**
	 * Returns the color and short name for a given visit
	 * 
	 * @param visit
	 * @return 
	 */
	public static Map<String, Object> getVisitColorAndShortName(VisitDomainWrapper visit) {

		VisitType type = visit.getVisit().getVisitType();
		Map<String, Object> visitTypeColorAndShortName = getVisitTypeColorAndShortName(type);
		return visitTypeColorAndShortName;
	}

	/**
	 * Returns the color and short name attributes for a given visit type
	 * 
	 */
	public static Map<String, Object> getVisitTypeColorAndShortName(VisitType type) {

		Map<String, Object> colorAndShortName = new HashMap<String, Object>();

		if (type.getUuid().equals(LFHCFormsConstants.OUTPATIENT_VISIT_TYPE_UUID)) {
			colorAndShortName.put("color", LFHCFormsConstants.OUTPATIENT_COLOR);
			colorAndShortName.put("shortName", LFHCFormsConstants.OUTPATIENT_SHORTNAME);
		}
		if (type.getUuid().equals(LFHCFormsConstants.INPATIENT_VISIT_TYPE_UUID)) {
			colorAndShortName.put("color", LFHCFormsConstants.INPATIENT_COLOR);
			colorAndShortName.put("shortName", LFHCFormsConstants.INPATIENT_SHORTNAME);
		}
		if (type.getUuid().equals(LFHCFormsConstants.EMERGENCY_VISIT_TYPE_UUID)) {
			colorAndShortName.put("color", LFHCFormsConstants.EMERGENCY_COLOR);
			colorAndShortName.put("shortName", LFHCFormsConstants.EMERGENCY_SHORTNAME);
		}
		if (type.getUuid().equals(LFHCFormsConstants.OPERATING_THEATER_VISIT_TYPE_UUID)) {
			colorAndShortName.put("color", LFHCFormsConstants.OPERATING_THEATER_COLOR);
			colorAndShortName.put("shortName", LFHCFormsConstants.OPERATING_THEATER_SHORTNAME);
		}
		if (type.getUuid().equals(LFHCFormsConstants.OUTREACH_VISIT_TYPE_UUID)) {
			colorAndShortName.put("color", LFHCFormsConstants.OUTREACH_COLOR);
			colorAndShortName.put("shortName", LFHCFormsConstants.OUTREACH_SHORTNAME);
		}

		// set default values
		if (colorAndShortName.get("color") == null ) {
			colorAndShortName.put("color", "grey");
		}
		if (colorAndShortName.get("shortName") == null) {
			colorAndShortName.put("shortName", "N/A");	
		}

		return colorAndShortName;
	}


	/**
	 * 
	 * Returns a map of the color and short name for a given list of visits
	 * 
	 * @param visits
	 * @return
	 */
	public static Map<Integer, Map<String, Object>> getVisitColorAndShortName(List<VisitDomainWrapper> visits) {

		Map<Integer, Map<String, Object>> visitsWithAttr = new LinkedHashMap<Integer, Map<String, Object>>();

		for (VisitDomainWrapper visit : visits) {
			Map<String,Object>visitColorAndShortName = getVisitColorAndShortName(visit);
			visitsWithAttr.put(visit.getVisitId(), visitColorAndShortName);
		}
		return visitsWithAttr;
	}
}

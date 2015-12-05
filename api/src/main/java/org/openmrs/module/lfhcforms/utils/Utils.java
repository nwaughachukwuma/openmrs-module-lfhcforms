package org.openmrs.module.lfhcforms.utils;

import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
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

	protected static final Log log = LogFactory.getLog(Utils.class);
	private static AdministrationService adtService;

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
	 * @param previousLocation
	 */
	public static void setAdmissionBasedOnLocation(Visit visit, Location previousLocation) {

		// retrieve admission tag
		LocationService ls = Context.getLocationService();
		List<LocationTag> tags = ls.getAllLocationTags(false);
		LocationTag admissionTag = null;
		LocationTag transferTag = null;
		for (LocationTag tag : tags) {
			String uuid = tag.getUuid();
			if (uuid.equals(LFHCFormsConstants.ADMISSION_LOCATION_TAG_UUID)) {
				admissionTag = tag;
			}
			if (uuid.equals(LFHCFormsConstants.TRANSFER_LOCATION_TAG_UUID)) {
				transferTag = tag;
			}
		}
		List<Location> admissionLocations = ls.getLocationsByTag(admissionTag);
		boolean isAdmissionLocation = admissionLocations.contains(visit.getLocation());

		List<Location> transferLocations = ls.getLocationsByTag(transferTag);
		boolean isTransferLocation = transferLocations.contains(visit.getLocation());

		// retrieve admission status
		EmrApiProperties emrApiProperties = Context.getRegisteredComponent("emrApiProperties", EmrApiProperties.class);
		VisitDomainWrapper visitWrapper = new VisitDomainWrapper(visit, emrApiProperties);
		boolean isAdmitted = visitWrapper.isAdmitted();

		EncounterService es = Context.getEncounterService();
		VisitService vs = Context.getVisitService();
		Person person = Context.getUserContext().getAuthenticatedUser().getPerson();

		setAdmission(visit, vs, es, person, previousLocation, isAdmitted, isAdmissionLocation, isTransferLocation);

	}

	/**
	 * 
	 * Create admission, discharge or transfer encounters for visits, based on
	 * visit location and current admission status
	 * 
	 * @param visit
	 * @param vs
	 * @param es
	 * @param person
	 * @param previousLocation
	 *            The location before the visit location has changed, if any
	 * @param isAdmitted
	 * @param isAdmissionLocation
	 * @param isTransferLocation
	 * 
	 * @should add admission encounter when visit location is of type 'admission
	 *         location' and visit is not admitted yet
	 * @should add discharge encounter when visit location is NOT of type
	 *         'admission location' but visit is admitted
	 * @should handle null previousLocation
	 * @should not add transfer location when visit is moved to the same location
	 * 
	 */
	protected static void setAdmission(Visit visit, VisitService vs, EncounterService es, Person person,
			Location previousLocation, boolean isAdmitted, boolean isAdmissionLocation, boolean isTransferLocation) {

		EncounterType admissionEncounterType = es
				.getEncounterTypeByUuid(LFHCFormsConstants.ADMISSION_ENCOUNTER_TYPE_UUID);
		EncounterType dischargeEncounterType = es
				.getEncounterTypeByUuid(LFHCFormsConstants.DISCHARGE_ENCOUNTER_TYPE_UUID);
		EncounterType transferEncounterType = es
				.getEncounterTypeByUuid(LFHCFormsConstants.TRANSFER_ENCOUNTER_TYPE_UUID);

		Patient patient = visit.getPatient();
		Encounter encounter = new Encounter();

		if (isAdmissionLocation && !isAdmitted) {
			// visit location is of type 'admission location' but NOT admitted
			// yet
			// add Admission encounter
			encounter.setEncounterType(admissionEncounterType);
			encounter.setPatient(patient);
			encounter.setEncounterDatetime(new Date());
			encounter.setProvider(person);
			encounter.setLocation(visit.getLocation());
			es.saveEncounter(encounter);
			visit.addEncounter(encounter);
		} else if (isAdmissionLocation && isAdmitted) {
			// visit location is of type 'admission location' and is already admitted
			// do nothing
		} else if (!isAdmissionLocation && !isAdmitted) {
			// visit location is NOT of type 'admission location' and is NOT admitted
			// do nothing
		} else if (!isAdmissionLocation && isAdmitted) {
			// visit location is NOT of type 'admission location' but is already admitted
			// add Discharge encounter
			encounter.setEncounterType(dischargeEncounterType);
			encounter.setPatient(patient);
			encounter.setEncounterDatetime(new Date());
			encounter.setProvider(person);
			// for Discharge, the location will be the location before change
			// (if any)
			if (previousLocation != null) {
				encounter.setLocation(previousLocation);
			} else {
				encounter.setLocation(visit.getLocation());
			}
			es.saveEncounter(encounter);
			visit.addEncounter(encounter);
		}
		
		if (isTransferLocation && (previousLocation != visit.getLocation()) 
				&& previousLocation != null) {
			// visit is of type 'transfer location' AND visit location is different from previous location.
			// add a Transfer encounter
			Encounter transferEncounter = new Encounter();
			transferEncounter.setEncounterType(transferEncounterType);
			transferEncounter.setPatient(patient);
			transferEncounter.setEncounterDatetime(new Date());
			transferEncounter.setProvider(person);
			transferEncounter.setLocation(visit.getLocation());
			es.saveEncounter(transferEncounter);
			visit.addEncounter(transferEncounter);
		}
		
		vs.saveVisit(visit);

	}
}

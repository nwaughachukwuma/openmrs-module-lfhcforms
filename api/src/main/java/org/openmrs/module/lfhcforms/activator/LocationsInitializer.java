package org.openmrs.module.lfhcforms.activator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;

/**
 * Sets up the Encounter Types required by the HFE forms.
 */
public class LocationsInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(LocationsInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting new Location Attributes Types for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);

		LocationService ls = Context.getLocationService();

		{
			String name = "Lao Friends Hospital for Children";
			String desc = "";
			String uuid = LFHCFormsConstants.LFHC_LOCATION_UUID;
			Location loc = ls.getLocationByUuid(uuid); 
			if (loc == null) {
				loc = new Location();
				loc.setName(name);
				loc.setDescription(desc);
				loc.setUuid(uuid);

				Set<LocationTag> tags = new HashSet<LocationTag>();
				tags.add(ls.getLocationTagByUuid(LFHCFormsConstants.VISIT_LOCATION_TAG_UUID));
				tags.add(ls.getLocationTagByUuid(LFHCFormsConstants.LOGIN_LOCATION_TAG_UUID));

				loc.setTags(tags);
				ls.saveLocation(loc);
				log.info("Creating new Location \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}	
		{
			String name = "Inpatient";
			String desc = "";
			String uuid = LFHCFormsConstants.INPATIENT_LOCATION_UUID;
			Location loc = ls.getLocationByUuid(uuid); 
			if (loc == null) {
				loc = new Location();
				loc.setName(name);
				loc.setDescription(desc);
				loc.setUuid(uuid);
				loc.setParentLocation(ls.getLocationByUuid(LFHCFormsConstants.LFHC_LOCATION_UUID));

				Set<LocationTag> inpatientTags = new HashSet<LocationTag>();
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.VISIT_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.ADMISSION_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.TRANSFER_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.LOGIN_LOCATION_TAG_UUID));

				loc.setTags(inpatientTags);
				ls.saveLocation(loc);
				log.info("Creating new Location \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}		
		{
			String name = "Outpatient";
			String desc = "";
			String uuid = LFHCFormsConstants.OUTPATIENT_LOCATION_UUID;
			Location loc = ls.getLocationByUuid(uuid); 
			if (loc == null) {
				loc = new Location();
				loc.setName(name);
				loc.setDescription(desc);
				loc.setUuid(uuid);
				loc.setParentLocation(ls.getLocationByUuid(LFHCFormsConstants.LFHC_LOCATION_UUID));

				Set<LocationTag> inpatientTags = new HashSet<LocationTag>();
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.VISIT_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.TRANSFER_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.LOGIN_LOCATION_TAG_UUID));

				loc.setTags(inpatientTags);
				ls.saveLocation(loc);
				log.info("Creating new Location \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}	
		{
			String name = "Outreach";
			String desc = "";
			String uuid = LFHCFormsConstants.OUTREACH_LOCATION_UUID;
			Location loc = ls.getLocationByUuid(uuid); 
			if (loc == null) {
				loc = new Location();
				loc.setName(name);
				loc.setDescription(desc);
				loc.setUuid(uuid);
				loc.setParentLocation(ls.getLocationByUuid(LFHCFormsConstants.LFHC_LOCATION_UUID));

				Set<LocationTag> inpatientTags = new HashSet<LocationTag>();
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.VISIT_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.TRANSFER_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.LOGIN_LOCATION_TAG_UUID));

				loc.setTags(inpatientTags);
				ls.saveLocation(loc);
				log.info("Creating new Location \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}
		{
			String name = "Emergency";
			String desc = "";
			String uuid = LFHCFormsConstants.EMERGENCY_LOCATION_UUID;
			Location loc = ls.getLocationByUuid(uuid); 
			if (loc == null) {
				loc = new Location();
				loc.setName(name);
				loc.setDescription(desc);
				loc.setUuid(uuid);
				loc.setParentLocation(ls.getLocationByUuid(LFHCFormsConstants.LFHC_LOCATION_UUID));

				Set<LocationTag> inpatientTags = new HashSet<LocationTag>();
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.VISIT_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.TRANSFER_LOCATION_TAG_UUID));
				inpatientTags.add(ls.getLocationTagByUuid(LFHCFormsConstants.LOGIN_LOCATION_TAG_UUID));

				loc.setTags(inpatientTags);
				ls.saveLocation(loc);
				log.info("Creating new Location \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}

	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}
}

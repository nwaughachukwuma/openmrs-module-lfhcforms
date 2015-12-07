package org.openmrs.module.lfhcforms.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;

/**
 * Sets up the Encounter Types required by the HFE forms.
 */
public class VisitTypesInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(VisitTypesInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting new Location Attributes Types for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);

		VisitService vs = Context.getVisitService();

		{
			String name = "Inpatient";
			String desc = "";
			String uuid = LFHCFormsConstants.INPATIENT_VISIT_TYPE_UUID;
			VisitType type = vs.getVisitTypeByUuid(uuid); 
			if (type == null) {
				type = new VisitType();
				type.setName(name);
				type.setDescription(desc);
				type.setUuid(uuid);
				vs.saveVisitType(type);
				log.info("Creating new Visit Type \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}	
		{
			String name = "Outpatient";
			String desc = "";
			String uuid = LFHCFormsConstants.OUTPATIENT_VISIT_TYPE_UUID;
			VisitType type = vs.getVisitTypeByUuid(uuid); 
			if (type == null) {
				type = new VisitType();
				type.setName(name);
				type.setDescription(desc);
				type.setUuid(uuid);
				vs.saveVisitType(type);
				log.info("Creating new Visit Type \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}			{
			String name = "Outreach";
			String desc = "";
			String uuid = LFHCFormsConstants.OUTREACH_VISIT_TYPE_UUID;
			VisitType type = vs.getVisitTypeByUuid(uuid); 
			if (type == null) {
				type = new VisitType();
				type.setName(name);
				type.setDescription(desc);
				type.setUuid(uuid);
				vs.saveVisitType(type);
				log.info("Creating new Visit Type \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}			{
			String name = "Emergency";
			String desc = "";
			String uuid = LFHCFormsConstants.EMERGENCY_VISIT_TYPE_UUID;
			VisitType type = vs.getVisitTypeByUuid(uuid); 
			if (type == null) {
				type = new VisitType();
				type.setName(name);
				type.setDescription(desc);
				type.setUuid(uuid);
				vs.saveVisitType(type);
				log.info("Creating new Visit Type \"" + name + "\"");
			}else {
				log.info("UUID of Location \"" + name + "\" already exists. Not creating new location");
			}
		}	
		{
			String name = "Operating Theater";
			String desc = "";
			String uuid = LFHCFormsConstants.OPERATING_THEATER_VISIT_TYPE_UUID;
			VisitType type = vs.getVisitTypeByUuid(uuid); 
			if (type == null) {
				type = new VisitType();
				type.setName(name);
				type.setDescription(desc);
				type.setUuid(uuid);
				vs.saveVisitType(type);
				log.info("Creating new Visit Type \"" + name + "\"");
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

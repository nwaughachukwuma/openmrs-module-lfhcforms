	package org.openmrs.module.lfhcforms.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;

/**
 * Sets up the Encounter Types required by the HFE forms.
 */
public class AttributeTypesInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(AttributeTypesInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting new Location Attributes Types for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);

//		LocationService ls = Context.getLocationService();
//
//		{
//			String name = "Color";
//			String desc = "Attribute type that records the color of the location";
//			String uuid = LFHCFormsConstants.COLOR_LOCATION_ATTRIBUTE_TYPE_UUID;
//			String dataType = FreeTextDatatype.class.getName();
//			LocationAttributeType locationAttrType = ls.getLocationAttributeTypeByUuid(uuid);
//			if(locationAttrType == null) {
//				locationAttrType = new LocationAttributeType();
//				locationAttrType.setName(name);
//				locationAttrType.setDescription(desc);
//				locationAttrType.setUuid(uuid);
//				locationAttrType.setDatatypeClassname(dataType);
//				ls.saveLocationAttributeType(locationAttrType);
//			}else {
//				log.info("UUID of Location Attribute Type \"" + name + "\" already exists. Not saving the Location Attribute Type");
//			}
//		}		
//		{
//			String name = "Short Name";
//			String desc = "Attribute type that records the short name of the location";
//			String uuid = LFHCFormsConstants.SHORT_NAME_LOCATION_ATTRIBUTE_TYPE_UUID;
//			String dataType = FreeTextDatatype.class.getName();
//			LocationAttributeType locationAttrType = ls.getLocationAttributeTypeByUuid(uuid);
//			if(locationAttrType == null) {
//				locationAttrType = new LocationAttributeType();
//				locationAttrType.setName(name);
//				locationAttrType.setDescription(desc);
//				locationAttrType.setUuid(uuid);
//				locationAttrType.setDatatypeClassname(dataType);
//				ls.saveLocationAttributeType(locationAttrType);
//			}else {
//				log.info("UUID of Location Attribute Type \"" + name + "\" already exists. Not saving the Location Attribute Type");
//			}
//		}
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}
}

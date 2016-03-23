package org.openmrs.module.lfhcforms.activator;

import java.io.IOException;
import java.util.HashSet;

/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;

/**
 * Installs and removes the address template for this module.
 * @see {@link https://raw.githubusercontent.com/PIH/openmrs-module-pihmalawi/master/api/src/main/java/org/openmrs/module/pihmalawi/activator/AddressTemplateInitializer.java}
 */
public class AddressTemplateInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(AddressTemplateInitializer.class);

	public static final String CODE_NAME = "lfhc";

	protected static final String XML_RESOURCE_PATH = "addressTemplate.xml";
	
	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Registering address template for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
		final LocationService locationService = Context.getLocationService();
		
		// Setting the address template
		final ResourceLoader loader = new DefaultResouceLoaderImpl();
		String xml = "";
		try {
			xml = loader.getResourceAsString(XML_RESOURCE_PATH, "UTF-8");
		}
		catch (IOException e) {
			log.error("Could not load resource file '" + XML_RESOURCE_PATH + ".", e);
		}
		locationService.saveAddressTemplate(xml);
		
		// Renaming the location(s)
		Location mainLocation = locationService.getLocationByUuid(LFHCFormsConstants.LFHC_LOCATION_UUID);
		if (mainLocation == null) {
			mainLocation = new Location();
			mainLocation.setUuid(LFHCFormsConstants.LFHC_LOCATION_UUID);
			mainLocation.setTags(new HashSet<LocationTag>(locationService.getAllLocationTags()));
		}
		mainLocation.setName("Lao Friends Hospital for Children");
		locationService.saveLocation(mainLocation);
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
//		log.info("Un-registering address template for LFHC Forms Module");
//		
//		AddressTemplate at = AddressSupport.getInstance().getLayoutTemplateByCodeName(CODE_NAME);
//		if (at != null) {
//			AddressSupport.getInstance().getLayoutTemplates().remove(at);
//		}
	}
}

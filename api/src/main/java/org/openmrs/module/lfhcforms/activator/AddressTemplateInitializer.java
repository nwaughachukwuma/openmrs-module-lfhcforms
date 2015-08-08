package org.openmrs.module.lfhcforms.activator;

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
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Installs and removes the address template for this module.
 * @see {@link https://raw.githubusercontent.com/PIH/openmrs-module-pihmalawi/master/api/src/main/java/org/openmrs/module/pihmalawi/activator/AddressTemplateInitializer.java}
 */
public class AddressTemplateInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(AddressTemplateInitializer.class);

	public static final String CODE_NAME = "lfhc";
	
	/*
	 * Each address template (but the default-generic one) is associated with a country.
	 * Presumably the look & feel and behavior is looked up upon the country.
	 * TODO: Verify the above assertion.
	 */
	public static final String ADDRESS_TEMPLATE_LAOS = "Laos";

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Registering address template for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
		AddressTemplate at = AddressSupport.getInstance().getLayoutTemplateByCodeName(CODE_NAME);

		if (at == null) {
			at = new AddressTemplate(CODE_NAME);
			AddressSupport.getInstance().getLayoutTemplates().add(at);
		}

		at.setDisplayName(LFHCFormsActivator.ACTIVATOR_MODULE_NAME + " Address Format");
		at.setCodeName(CODE_NAME);
		at.setCountry(ADDRESS_TEMPLATE_LAOS);

		Map<String, String> nameMappings = new HashMap<String, String>();
		nameMappings.put("countyDistrict", "Location.district");
		nameMappings.put("address1", "Location.address1");
		nameMappings.put("country", "Location.country");
		nameMappings.put("stateProvince", "Location.stateProvince");
		nameMappings.put("cityVillage", "Location.cityVillage");
		at.setNameMappings(nameMappings);

		Map<String, String> sizeMappings = new HashMap<String, String>();
		sizeMappings.put("countyDistrict", "40");
		sizeMappings.put("address1", "40");
		sizeMappings.put("country", "20");
		sizeMappings.put("stateProvince", "40");
		sizeMappings.put("cityVillage", "40");
		at.setSizeMappings(sizeMappings);

		Map<String, String> elementDefaults = new HashMap<String, String>();
		elementDefaults.put("country", ADDRESS_TEMPLATE_LAOS);
		at.setElementDefaults(elementDefaults);

		at.setLineByLineFormat(Arrays.asList("address1", "cityVillage", "countyDistrict", "stateProvince", "country"));
		
		
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
		log.info("Un-registering address template for LFHC Forms Module");
		
		AddressTemplate at = AddressSupport.getInstance().getLayoutTemplateByCodeName(CODE_NAME);
		if (at != null) {
			AddressSupport.getInstance().getLayoutTemplates().remove(at);
		}
	}
}

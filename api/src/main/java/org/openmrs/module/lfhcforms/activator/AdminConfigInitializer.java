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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;

/**
 * Installs and removes the address template for this module.
 * @see {@link https://raw.githubusercontent.com/PIH/openmrs-module-pihmalawi/master/api/src/main/java/org/openmrs/module/pihmalawi/activator/AddressTemplateInitializer.java}
 */
public class AdminConfigInitializer implements Initializer {

	
	protected static final Log log = LogFactory.getLog(AdminConfigInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting custom admin configuration for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
		// Disabling the default Patient Registration app (page).
		AppFrameworkService service = Context.getService(AppFrameworkService.class);
		service.disableApp("referenceapplication.registrationapp.registerPatient");
		service.disableApp("referenceapplication.vitals");
		service.disableApp("reportingui.reports");
		service.disableExtension("referenceapplication.realTime.vitals");
		service.disableExtension("referenceapplication.realTime.simpleVisitNote");
		
		// Disabling the activeVisitStatus fragment
		service.disableExtension("org.openmrs.module.coreapps.patientHeader.secondLineFragments.activeVisitStatus");
		
		AdministrationService adminService = Context.getAdministrationService();
		String pewsTime = adminService.getGlobalProperty(LFHCFormsConstants.PEWS_TIME_WINDOW_PROPERTY);
		if(pewsTime == null) {
			adminService.setGlobalProperty(LFHCFormsConstants.PEWS_TIME_WINDOW_PROPERTY, (new Integer(LFHCFormsConstants.PEWS_FALLBACK_TIMEWINDOW)).toString());
		}
		
		String pewsExpiry = adminService.getGlobalProperty(LFHCFormsConstants.PEWS_EXPIRY_PROPERTY);
		if(pewsExpiry == null) {
			// The default is an empty String that won't convert to any integer.
			// This triggers the default expiry time mechanism.
			adminService.setGlobalProperty(LFHCFormsConstants.PEWS_EXPIRY_PROPERTY, "");
		}
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
		log.info("Unsetting custom admin configuration for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);

		AppFrameworkService service = Context.getService(AppFrameworkService.class);
		service.enableApp("referenceapplication.registrationapp.registerPatient");
		service.enableApp("referenceapplication.vitals");
		service.enableExtension("referenceapplication.realTime.vitals");
		service.enableExtension("referenceapplication.realTime.simpleVisitNote");
		service.enableExtension("org.openmrs.module.coreapps.patientHeader.secondLineFragments.activeVisitStatus");
		
	}
}

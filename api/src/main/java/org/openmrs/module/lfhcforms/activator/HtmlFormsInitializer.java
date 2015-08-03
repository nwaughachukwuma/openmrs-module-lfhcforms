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
import org.openmrs.module.lfhcforms.LFHCFormsActivator;

/**
 * Sets up the HFE forms
 * @see {@link https://github.com/PIH/openmrs-module-pihmalawi/blob/master/api/src/main/java/org/openmrs/module/pihmalawi/activator/HtmlFormInitializer.java}
 */
public class HtmlFormsInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(HtmlFormsInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting HFE forms for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}
}

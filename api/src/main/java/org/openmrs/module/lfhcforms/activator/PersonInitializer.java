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
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;

/**
 * Creation of new person attribute types that are used with the registration app.
 * @author Dimitri Renault
 */
public class PersonInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(PersonInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting 'person' configuration for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
		{
			String uuid = LFHCFormsConstants.INSURANCE_DETAILS_UUID;
			PersonAttributeType type = Context.getPersonService().getPersonAttributeTypeByUuid(uuid);
			if(type == null) {
				type = new PersonAttributeType();
				type.setUuid(uuid);
			}
			type.setName("Insurance Details");
			type.setDescription("Insurance details.");
			type.setFormat("java.lang.String");
			Context.getPersonService().savePersonAttributeType(type);
		}
		{
			String uuid = LFHCFormsConstants.FATHER_NAME_UUID;
			PersonAttributeType type = Context.getPersonService().getPersonAttributeTypeByUuid(uuid);
			if(type == null) {
				type = new PersonAttributeType();
				type.setUuid(uuid);
			}
			type.setName("Father's Name");
			type.setDescription("First and/or last name of this person's father");
			type.setFormat("java.lang.String");
			Context.getPersonService().savePersonAttributeType(type);
		}
		{
			String uuid = LFHCFormsConstants.FATHER_OCCUPATION_UUID;
			PersonAttributeType type = Context.getPersonService().getPersonAttributeTypeByUuid(uuid);
			if(type == null) {
				type = new PersonAttributeType();
				type.setUuid(uuid);
			}
			type.setName("Father's Occupation");
			type.setDescription("Father's occupation or work employment");
			type.setFormat("java.lang.String");
			Context.getPersonService().savePersonAttributeType(type);
		}
		
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
		log.info("Unsetting 'person' configuration for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
	}
}

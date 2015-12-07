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
package org.openmrs.module.lfhcforms;


import java.util.ArrayList;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.lfhcforms.activator.AddressTemplateInitializer;
import org.openmrs.module.lfhcforms.activator.AdminConfigInitializer;
import org.openmrs.module.lfhcforms.activator.AttributeTypesInitializer;
import org.openmrs.module.lfhcforms.activator.EncounterTypesInitializer;
import org.openmrs.module.lfhcforms.activator.HtmlFormsInitializer;
import org.openmrs.module.lfhcforms.activator.Initializer;
import org.openmrs.module.lfhcforms.activator.LFHCConceptsInitializer;
import org.openmrs.module.lfhcforms.activator.PersonInitializer;
import org.openmrs.module.lfhcforms.activator.ReportsInitializer;
import org.openmrs.module.lfhcforms.activator.VisitTypesInitializer;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 * 
 * 
 * The whole initializer logic was borrowed from the PIH Malawi's implementation.
 * @see {@link https://github.com/PIH/openmrs-module-pihmalawi/blob/master/api/src/main/java/org/openmrs/module/pihmalawi/activator/PihMalawiModuleActivator.java}
 */
public class LFHCFormsActivator implements ModuleActivator {
	
	public final static String ACTIVATOR_MODULE_NAME = "LFHC Forms Module";
	
	protected Log log = LogFactory.getLog(getClass());
		
	/*
	 * The order is important here, be careful when swapping initializers around
	 */
	public List<Initializer> getInitializers() {
		List<Initializer> l = new ArrayList<Initializer>();
		l.add(new AdminConfigInitializer());
		l.add(new AddressTemplateInitializer());
		l.add(new EncounterTypesInitializer());
		l.add(new AttributeTypesInitializer());
		l.add(new LFHCConceptsInitializer());
		l.add(new HtmlFormsInitializer());
		l.add(new ReportsInitializer());
		l.add(new PersonInitializer());
		l.add(new VisitTypesInitializer());
		return l;
	}
	
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing LFHC Forms Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("LFHC Forms Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting LFHC Forms Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 * @see <a href="https://github.com/openmrs/openmrs-module-ebolaexample/blob/master/api/src/main/java/org/openmrs/module/ebolaexample/EbolaExampleActivator.java#L279">Ebola example</a>
	 */
	public void started() {

		for (Initializer initializer : getInitializers()) {
			initializer.started();
		}
				
		log.info("LFHC Forms Module started");
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping LFHC Forms Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		for (int i = getInitializers().size() - 1; i >= 0; i--) {
			getInitializers().get(i).stopped();
		}
		
		log.info("LFHC Forms Module stopped");
	}
		
}

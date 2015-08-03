package org.openmrs.module.lfhcforms.activator;

import java.util.Locale;

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
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;

/**
 * Saves additional and custom concepts in the Concepts Dictionary.
 * @see {@link https://github.com/PIH/openmrs-module-pihmalawi/blob/master/api/src/main/java/org/openmrs/module/pihmalawi/activator/MetadataInitializer.java}
 */
public class ConceptsInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(ConceptsInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting additional/custom concepts for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
		final ConceptService cs = Context.getConceptService();
		
		// Example for a simple concept
		{
			Integer id = 200000;
			String uuid = "03e9be37-1369-11e4-a125-54ee7513a7ff";
			String name = "Acute Psychotic disorder";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		
		// Example for a numeric concept
		{
			Integer id = 200004;
			String uuid = "43e9be37-1369-11e4-a125-54ee7513a7ff";
			String name = "Hourly Intravenous fluids";
			ConceptNumeric c = cs.getConceptNumeric(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new ConceptNumeric();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Question"));
				c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				c.setShortName(new ConceptName("iv intake", Locale.ENGLISH));
				c.addDescription(new ConceptDescription("amount of IV fluids given in that hour", Locale.ENGLISH));
				c.setUnits("ml");
				cs.saveConcept(c);
			}
		}
		
		// Example of a concept set
//		{
//			Integer id = 200008;
//			String uuid = "83e9be37-1369-11e4-a125-54ee7513a7ff";
//			String name = "xray List";
//			Concept c = cs.getConcept(id);
//			if (c == null) {
//				log.warn("Creating " + name);
//				c = new ConceptNumeric();
//				c.setConceptId(id);
//				c.setUuid(uuid);
//				c.setConceptClass(cs.getConceptClassByName("Question"));
//				c.setDatatype(cs.getConceptDatatypeByName("Coded"));
//				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
//				c.setShortName(new ConceptName("xray set", Locale.ENGLISH));
//				c.addDescription(new ConceptDescription("List of all xrays", Locale.ENGLISH));
//				{
//					c.setSet(true);
//					c.addSetMember(cs.getConcept(12));
//					c.addSetMember(cs.getConcept(101));
//					c.addSetMember(cs.getConcept(377));
//					c.addSetMember(cs.getConcept(380));
//					c.addSetMember(cs.getConcept(382));
//					c.addSetMember(cs.getConcept(384));
//					c.addSetMember(cs.getConcept(382));
//				}
//				cs.saveConcept(c);
//			}
//		}

		// Set of concepts for the "ethnic group" demographics question.
		{
			Integer id = 202001;
			String uuid = "202001AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac8300-3931-11e5-a151-feff819cdc9f";
			String name = "Ethnic group";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Question"));
				c.setDatatype(cs.getConceptDatatypeByName("Coded"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202002;
			String uuid = "202002AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac86de-3931-11e5-a151-feff819cdc9f";
			String name = "Lao (Tai and Rau)";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202003;
			String uuid = "202003AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac8a26-3931-11e5-a151-feff819cdc9f";
			String name = "Hmong-Mien";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202004;
			String uuid = "202004AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac8d3c-3931-11e5-a151-feff819cdc9f";
			String name = "Mon-Khmer";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202005;
			String uuid = "202005AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac8f62-3931-11e5-a151-feff819cdc9f";
			String name = "Palaungic";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202006;
			String uuid = "202006AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac9142-3931-11e5-a151-feff819cdc9f";
			String name = "Khmuic";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202007;
			String uuid = "202007AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac9430-3931-11e5-a151-feff819cdc9f";
			String name = "Tibeto-Burman";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202008;
			String uuid = "202008AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac962e-3931-11e5-a151-feff819cdc9f";
			String name = "Chinese";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202009;
			String uuid = "202009AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac9930-3931-11e5-a151-feff819cdc9f";
			String name = "Caucasian";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202010;
			String uuid = "202010AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac9bf6-3931-11e5-a151-feff819cdc9f";
			String name = "African";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202011;
			String uuid = "202011AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac9e44-3931-11e5-a151-feff819cdc9f";
			String name = "Hispanic";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202012;
			String uuid = "202012AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0baca128-3931-11e5-a151-feff819cdc9f";
			String name = "Other ethnic group";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}

		// Set of concepts for the "language spoken" demographics question.
		{
			Integer id = 202101;
			String uuid = "202101AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac8300-3931-11e5-a151-feff819cdc9f";
			String name = "Language spoken";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Question"));
				c.setDatatype(cs.getConceptDatatypeByName("Coded"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202102;
			String uuid = "202102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac86de-3931-11e5-a151-feff819cdc9f";
			String name = "Lao";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202103;
			String uuid = "202103AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac8a26-3931-11e5-a151-feff819cdc9f";
			String name = "Hmong";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202104;
			String uuid = "202104AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac8d3c-3931-11e5-a151-feff819cdc9f";
			String name = "Khmu";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202105;
			String uuid = "202105AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac8f62-3931-11e5-a151-feff819cdc9f";
			String name = "Chinese (lang)";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202106;
			String uuid = "202106AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac9142-3931-11e5-a151-feff819cdc9f";
			String name = "Thai";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202107;
			String uuid = "202107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac9430-3931-11e5-a151-feff819cdc9f";
			String name = "English";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 202108;
			String uuid = "202108AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			//String uuid = "0bac962e-3931-11e5-a151-feff819cdc9f";
			String name = "Other language";
			Concept c = cs.getConcept(id);
			if (c == null) {
				log.warn("Creating " + name);
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("Misc"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				cs.saveConcept(c);
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

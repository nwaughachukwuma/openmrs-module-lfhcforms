package org.openmrs.module.lfhcforms.activator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.DuplicateConceptNameException;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Saves additional and custom concepts in the Concepts Dictionary.
 * @see {@link https://github.com/PIH/openmrs-module-pihmalawi/blob/master/api/src/main/java/org/openmrs/module/pihmalawi/activator/MetadataInitializer.java}
 */
public class ConceptsInitializer implements Initializer {
	
	public final static String LFHC_CONCEPT_SOURCE = "LFHC";
	public final static String CIEL_CONCEPT_SOURCE = "CIEL";
	
	protected final static char CSV_DELIMITER = '\t';
	protected final static String CSV_INNER_DELIMITER = ",";
	
	/*
	 * Headers that MUST be found on the template concepts CSV file.
	 */
	protected final static String CSV_MAPPING_LFHC = "LFHC Mapping";
	protected final static String CSV_MAPPING_ICD10 = "ICD-10 Mapping";
	protected final static String CSV_NAME = "Name";
	protected final static String CSV_SHORTNAME = "Short Name";
	protected final static String CSV_SYNONYMS = "Synonyms (separate them by a comma)";
	protected final static String CSV_DESC = "Description";
	protected final static String CSV_ISSET = "Is set?";
	protected final static String CSV_DATATYPE = "Datatype";
	protected final static String CSV_CLASS = "Class";
	protected final static String CSV_UNIT = "Unit";
	protected final static String CSV_CONCEPT_LIST = "If Question or Set, concept list (mapped IDs separated by a comma)";
	
	protected static final Log log = LogFactory.getLog(ConceptsInitializer.class);
	
	// Array of the minimal set of headers to be found in the import CSV.
	protected final Set<String> referenceHeaders = new HashSet<String>();
	
	public ConceptsInitializer() {
		referenceHeaders.add(CSV_MAPPING_LFHC);
		referenceHeaders.add(CSV_MAPPING_ICD10);
		referenceHeaders.add(CSV_NAME);
		referenceHeaders.add(CSV_SHORTNAME);
		referenceHeaders.add(CSV_SYNONYMS);
		referenceHeaders.add(CSV_DESC);
		referenceHeaders.add(CSV_ISSET);
		referenceHeaders.add(CSV_DATATYPE);
		referenceHeaders.add(CSV_CLASS);
		referenceHeaders.add(CSV_UNIT);
		referenceHeaders.add(CSV_CONCEPT_LIST);
	}
	
	/**
	 * First step: addition of the Concept Sources.
	 * This is step needs to be completed when it comes to adding concepts.
	 * @param cs The ConceptService used throughout the {@link Initializer#}
	 */
	protected void addConceptSources(final ConceptService cs) {
		ConceptSource lfhcSource = cs.getConceptSourceByName(LFHC_CONCEPT_SOURCE);
		if(lfhcSource == null) {
			lfhcSource = new ConceptSource();
			lfhcSource.setName(LFHC_CONCEPT_SOURCE);
			lfhcSource.setDescription("Custom concepts defined at the LFHC.");
			cs.saveConceptSource(lfhcSource);
		}
	}

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting additional/custom concepts for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
		final ConceptService cs = Context.getConceptService();
		
		// Step 1: Setting up the concept sources.
		addConceptSources(cs);
		
		// Step 2: Adding the custom concepts.
		Map<String, Map<String, String>> concepts = getConceptMapFromCSVResource("lfhcConcepts.csv", new DefaultResouceLoaderImpl());
		addMappedConcepts(cs, concepts);
	}
	
	/**
	 * Parses the (api-located) CSV resource listing the custom concepts.
	 * @return A LFHC ID indexed map of map-represented concepts. Each map represented concept is a map representation
	 * of the list of the concept's properties (name, description, ... etc).
	 */
	protected Map<String, Map<String, String>> getConceptMapFromCSVResource(final String csvPath, final ResourceLoader loader) {

		InputStream inputStream = loader.getResourceAsStream(csvPath); 
		
		Map<String, Map<String, String>> concepts = new HashMap<String, Map<String, String>>();
		try {
			
			CSVReader reader = new CSVReader(new InputStreamReader(inputStream), CSV_DELIMITER);
	        
			String[] header = reader.readNext();
			
			// Checking the headers
			Set<String> readHeaders = new HashSet<String>(Arrays.asList(header));
			String faultyHeaders = "";
			String sep = ", ";
			for(String referenceHeader : referenceHeaders){
				if(!readHeaders.contains(referenceHeader)) {
					faultyHeaders += referenceHeader + sep;
				}
			}
			if(!faultyHeaders.isEmpty()) {
				faultyHeaders = faultyHeaders.substring(0, faultyHeaders.length() - sep.length());
				log.error("Could not process CSV file for custom concepts due to faulty header(s), please review the following header(s): " + faultyHeaders + ".");
				reader.close();
				return concepts;
			}
			
	        String[] line = null;
	        int csvRow = 0;
	        while((line = reader.readNext()) != null) {
	        	
	        	csvRow++;
	        	if(line.length != header.length) {
	        		log.warn("Line " + csvRow + " has " + line.length + " column(s), while the CSV header has " + header.length + " columns, line cannot be parsed, skippinh through to the next line.");
	        		continue;
	        	}
	        	
	        	// We obtain a map of the concep's properties.
	        	Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < header.length; i++) {
	                String string = header[i];
	                String val = line[i];
	                map.put(string, val);
	            }

                String lfhcId = map.get(CSV_MAPPING_LFHC);
                concepts.put(lfhcId, map);
	        }
	        
	        reader.close();
		}
		catch (IOException e) {
			log.error(e);
		}
		
		return concepts;
	}

	/**
	 * 
	 * @param cs The {@link ConceptService} instance.
	 * @param mappedConcepts The map representation of all custom concepts.
	 */
	protected void addMappedConcepts(final ConceptService cs, final Map<String, Map<String, String>> mappedConcepts) {

		if(mappedConcepts.size() == 0)
			return;
		
		ConceptSource lfhcSource = cs.getConceptSourceByName(LFHC_CONCEPT_SOURCE);
		
		Map<String, Concept> concepts = new HashMap<String, Concept>(); 
		Map<String, String> sets = new HashMap<String, String>();	// We hold the concept lists (sets or answers) until all concepts are created
		
		for(Map<String, String> mappedConcept : mappedConcepts.values()) {
			
			// Skipping already existing concepts.
			String lfhcMappedId = mappedConcept.get(CSV_MAPPING_LFHC);
			if(lfhcMappedId.isEmpty()) {
				log.warn("Concept found with no LFHC mapping. Skipping through to next concept in the CSV source.");
				continue;
			}
			String lfhcId = getCodeFromMappedId(lfhcMappedId);
			if(lfhcId.isEmpty()) {
				log.warn("This mapped ID is incorrectly formated: " + lfhcMappedId + ". Skipping through to next concept in the CSV source.");
				continue;
			}
			
			// If the concept already exist we don't process it any further.
			Concept c = cs.getConceptByMapping(lfhcId, LFHC_CONCEPT_SOURCE);
			if (c != null)  {
				log.warn("Concept " + lfhcMappedId + " already exists, skipping through to next custom concept in the CSV source.");
				continue;
			}

			// Retrieving the custom concept's properties and logging invalid entries.
			String icd10Id = mappedConcept.get(CSV_MAPPING_ICD10);
			String name = mappedConcept.get(CSV_NAME);
			if(name.isEmpty()) {
				log.warn("Concept " + lfhcMappedId + " has no '" + CSV_NAME + "' property provided. Skipping through to next concept.");
				continue;
			}
			String shortName = mappedConcept.get(CSV_SHORTNAME);
			String desc = mappedConcept.get(CSV_DESC);
			if(desc.isEmpty()) {
				desc = "NO DESCRIPTION PROVIDED. PLEASE FILL IN A DESCRIPTION FOLLOWING OPENMRS GUIDELINES FOR CREATING NEW CONCEPTS.";
				log.warn("Concept " + lfhcMappedId + " has no '" + CSV_DESC + "' property provided. It was saved with a warning placeholder description.");
			}
			ConceptDescription description = new ConceptDescription(desc, Locale.ENGLISH);
			String synonyms = mappedConcept.get(CSV_SYNONYMS);
			if(synonyms.isEmpty())
				synonyms = "";
			String conceptClassName = mappedConcept.get(CSV_CLASS);
			ConceptClass conceptClass = cs.getConceptClassByName(conceptClassName);
			if(conceptClass == null) {
				log.warn("Could not save concept " + lfhcMappedId + ": invalid Concept Class: " + conceptClassName + ". Skipping through to next concept in the CSV source.");
				continue;
			}
			String datatypeName = mappedConcept.get(CSV_DATATYPE);
			ConceptDatatype datatype = cs.getConceptDatatypeByName(datatypeName);
			if(datatype == null) {
				log.warn("Could not save concept " + lfhcMappedId + ": invalid Datatype: " + datatypeName + ". Skipping through to next concept in the CSV source.");
				continue;
			}
			String unit = mappedConcept.get(CSV_UNIT);
			String isSet = mappedConcept.get(CSV_ISSET);
			if(isSet.isEmpty())
				isSet = "No";
			String conceptList = mappedConcept.get(CSV_CONCEPT_LIST);			
			if(!conceptList.isEmpty())
				sets.put(lfhcMappedId, conceptList);

			// Creating the new custom concept
			if("Numeric".equalsIgnoreCase(datatypeName.trim())) {
				c = new ConceptNumeric();
				((ConceptNumeric) c).setUnits(unit);
			}
			else {
				c = new Concept();
			}
			c.setShortName(new ConceptName(shortName, Locale.ENGLISH));
			c.addDescription(description);
			c.setConceptClass(conceptClass);
			c.setDatatype(datatype);
			{	//The LFHC mapping
				ConceptReferenceTerm refTerm = new ConceptReferenceTerm(lfhcSource, lfhcId, "");
				ConceptMapType mapType = cs.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
				ConceptMap map = new ConceptMap(refTerm, mapType);
				c.addConceptMapping(map);
			}
			isSet = isSet.trim().toLowerCase();
			if("yes".equals(isSet) || "true".equals(isSet) || "1".equals(isSet))
				c.setSet(true);
			if(!synonyms.isEmpty()) {
				Collection<ConceptName> names = new HashSet<ConceptName>();
				String[] synList = synonyms.split(CSV_INNER_DELIMITER);
				for(String syn : synList) {
					names.add(new ConceptName(syn.trim(), Locale.ENGLISH));
				}
				c.setNames(names);
			}
			c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
			
			try {
				cs.saveConcept(c);
			}
			catch (DuplicateConceptNameException e) {
				log.warn("The name of concept " + lfhcMappedId + " is a duplicate, it could therefore not be saved. Skipping through to next concept.");
				continue;
			}
			
			concepts.put(lfhcMappedId, c);	// Caching of new concepts
		}
		
		// Adding sets or concept answers
		for(Entry<String, String> item : sets.entrySet()) {
			
			Concept c = concepts.get(item.getKey());	// The concept to append.
			if(c == null)
				continue;
			
			String[] setConcepts = item.getValue().split(CSV_INNER_DELIMITER);
			for(String conceptId : setConcepts) {
				
				conceptId = conceptId.trim();
				Concept member = concepts.get(conceptId);	// Trying to find the member in the cache first.
				if(member == null) {
					member = cs.getConceptByMapping(conceptId, CIEL_CONCEPT_SOURCE);	// Second: trying in CIEL.
				}
				if(member == null) {
					log.warn("Could not find concept " + conceptId + " to be used as an answer or set member for concept " + item.getKey() + ". Skipping it.");
					continue;
				}
				if(c.isSet()) {
					c.addSetMember(member);
				}
				else {
					c.addAnswer(new ConceptAnswer(member));
				}
				cs.saveConcept(c);
			}
		}
	}
	
	/**
	 * 
	 * @param mappedId
	 * @return The code out of a mapped id, an empty String otherwise. Eg. "1001" out of "CIEL:1001"
	 */
	protected String getCodeFromMappedId(String mappedId) {
		String split[] = mappedId.split(":");
		if(split.length != 2) {
			log.warn("This mapped ID is incorrectly formated: " + mappedId + ". Skipping through to next concept in the CSV source.");
			return "";
		}
		return split[1];
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}
}
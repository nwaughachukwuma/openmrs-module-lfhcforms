package org.openmrs.module.lfhcforms.activator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
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
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Parses a locally saved CSV containing the registration data for legacy patients to be imported.
 */
public class LFHCLegacyPatientsInitializer implements Initializer {
	
	protected static final Log log = LogFactory.getLog(LFHCLegacyPatientsInitializer.class);
	
	protected final static String GLOBAL_PROPERTY_BULK_IMPORT_PROCESS = "lfhcforms.bulkImportProcess";
	
	protected final static char		CSV_DELIMITER		= '\t';
	protected final static String	CSV_INNER_DELIMITER = ",";
	protected final static String	CSV_DATE_FORMAT = "MM/dd/yyyy";
	
	protected final static String	CSV_PATIENTS_PATH = "/.OpenMRS/BulkImportFiles/patients.csv";
	
//	protected final static long CPT_PROBLEM = "concept_source";
	
	/*
	 * Required headers that MUST be found on the concepts CSV file to be imported.
	 */
	protected final static String CSV_REG_DATE = "Registration Date";
	protected final static String CSV_LEGACY_ID = "Legacy ID";
	protected final static String CSV_IDENTIFIER = "OpenMRS ID";
	protected final static String CSV_FIRST_NAME = "First name";
	protected final static String CSV_LAST_NAME = "Last name";
	protected final static String CSV_PHONE = "Telephone";
	protected final static String CSV_DOB = "DOB";
	protected final static String CSV_GENDER = "Gender";
	protected final static String CSV_ADDR_PROVINCE = "Province";
	protected final static String CSV_ADDR_DISTRICT = "District";
	protected final static String CSV_ETHNICITY = "Ethnicity";
	
	// Array of the minimal set of headers to be found in the import CSV.
	protected final Set<String> requiredHeaders = new HashSet<String>();
	
	// Services needed throughout the Initializer
	private EmrApiProperties emrApiProperties;
	private LocationService locationService;
	private RegistrationCoreService registrationService;
	private EncounterService encounterService;
	private ConceptService conceptService;
	private ObsService obsService;
	private PatientService patientService;
	
	public LFHCLegacyPatientsInitializer() {
		requiredHeaders.add(CSV_REG_DATE);
		requiredHeaders.add(CSV_LEGACY_ID);
		requiredHeaders.add(CSV_IDENTIFIER);
		requiredHeaders.add(CSV_FIRST_NAME);
		requiredHeaders.add(CSV_LAST_NAME);
		requiredHeaders.add(CSV_PHONE);
		requiredHeaders.add(CSV_DOB);
		requiredHeaders.add(CSV_GENDER);
		requiredHeaders.add(CSV_ADDR_PROVINCE);
		requiredHeaders.add(CSV_ADDR_DISTRICT);
		requiredHeaders.add(CSV_ETHNICITY);
	}
	
	protected boolean doProcess() {
		boolean process = false;
		
		AdministrationService adminService = Context.getAdministrationService();
		GlobalProperty prop = adminService.getGlobalPropertyObject(GLOBAL_PROPERTY_BULK_IMPORT_PROCESS);
		if(prop != null) {
			if(prop.getPropertyValue().equals("true")) {
				process = true;
			}
			adminService.purgeGlobalProperty(prop);	// The global property is deleted right away after being read.
		}
		
		return process;
	}
	
	protected void initializeServices() {
		emrApiProperties = Context.getRegisteredComponent("emrApiProperties", EmrApiProperties.class);
		locationService = Context.getLocationService();
		registrationService = Context.getRegisteredComponent("registrationCoreService", RegistrationCoreService.class);
		encounterService = Context.getEncounterService();
		conceptService = Context.getConceptService();
		obsService = Context.getObsService();
		patientService = Context.getPatientService();
	}

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Importing patients in bulk for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
		if(doProcess() == false) {
			log.info("No bulk import processed for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
			return;
		}
		
		initializeServices();

		// Processing the patients CSV
		Map<String, Map<String, String>> mappedPatients = getPatientsMapFromCSVResource(CSV_PATIENTS_PATH);
		
		PersonAttributeType phoneAttrType = emrApiProperties.getTelephoneAttributeType();
		Location registrationDesk = locationService.getLocationByUuid("6351fcf4-e311-4a19-90f9-35667d99a8af");
		EncounterType registrationEncounterType = encounterService.getEncounterTypeByUuid("3e3424bd-6e9d-4c9c-b3a4-f3fee751fe7c");
		EncounterRole registrationEncounterRole = encounterService.getEncounterRoleByUuid("a0b03050-c99b-11e0-9572-0800200c9a66");
		Concept ethnicityQuestion = conceptService.getConceptByMapping("912", "LFHC");
		
		for(Map<String, String> mappedPatient : mappedPatients.values()) {
			
			Patient patient = LFHCLegacyPatientsInitializer.getPatientFromMappedPatient(mappedPatient, phoneAttrType);
			
			// Registration encounter
			String legacyId = mappedPatient.get(CSV_LEGACY_ID);			
			if(patient.isVoided() == false) {

				Encounter encounter = new Encounter();
		        encounter.setPatient(patient);
		        encounter.setEncounterType(registrationEncounterType);
		        encounter.setLocation(registrationDesk);
		        Date regDate = getDateFromString( mappedPatient.get(CSV_REG_DATE), CSV_DATE_FORMAT );
		        encounter.setEncounterDatetime(regDate);
//		        if (registrationEncounterRole != null) {
//		            encounter.addProvider(registrationEncounterRole, sessionContext.getCurrentProvider());
//		        }
		        
		        // Ethnicity
		        Concept ethnicityAnswer = getConceptFromConceptMapping(conceptService, mappedPatient.get(CSV_ETHNICITY));
	        	if(ethnicityAnswer != null) {
	        		Obs obs = new Obs();
	        		obs.setConcept(ethnicityQuestion);
	        		obs.setValueCoded(ethnicityAnswer);
	        		encounter.addObs(obs);
	        	}
	        	else {
	        		patient.setVoided(true);
	        		log.error("No concept could be mapped for the patient's ethnicity. Legacy ID: " + legacyId);
	        	}
		        
		        // If still good to go we save the patient and his/her registration encounter.
		        if(patient.isVoided() == false) {
		        	patient = registrationService.registerPatient(patient, null, null, registrationDesk);
					encounter = encounterService.saveEncounter(encounter);
				}
			}
		}
	}
	
	/**
	 * The Concept instance saved under a given concept mapping.
	 * @param conceptMapping The String represented concept mapping (eg. "LFHC:912")
	 * @return A retired Concept when the Concept could not be found.
	 */
	protected Concept getConceptFromConceptMapping(final ConceptService conceptService, String conceptMapping) {
		Concept concept = null;
		String[] split = conceptMapping.trim().split(":");
		if(split.length == 2) {
			concept = conceptService.getConceptByMapping(split[1], split[0].toUpperCase());
		}
		return concept;
	}
		
	/**
	 * Parses the (locally saved) CSV resource containing the legacy patients to import.
	 * @return A legacy ID indexed map of map-represented patients. Each map-represented patient is a map representation
	 * of the list of the patient's properties or attributes (name, phone, address's district... etc).
	 */
	protected Map<String, Map<String, String>> getPatientsMapFromCSVResource(String csvPath) {

		Map<String, Map<String, String>> mappedPatients = new HashMap<String, Map<String, String>>();
		
		// Randomly renaming the file before reading it.
		Path path = Paths.get(System.getProperty("user.home") +  csvPath);
		final String msgFileNotFound = "Legacy patients file for bulk import not found at: " + csvPath; 
		if(path == null) {
			log.error(msgFileNotFound);
			return mappedPatients;
		}		
		
		// Reading the file
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path.toString());
		} catch (FileNotFoundException e) {
			log.error(msgFileNotFound);
			return mappedPatients;
		}
	
		// Browsing the CSV line by line
		try {
			
			CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream), CSV_DELIMITER);
	        
			String[] header = csvReader.readNext();
			
			// Checking the headers
			Set<String> actualHeaders = new HashSet<String>(Arrays.asList(header));
			String faultyHeaders = "";
			String sep = ", ";
			for(String requiredHeader : requiredHeaders){
				if(!actualHeaders.contains(requiredHeader)) {
					faultyHeaders += requiredHeader + sep;
				}
			}
			if(!faultyHeaders.isEmpty()) {
				faultyHeaders = faultyHeaders.substring(0, faultyHeaders.length() - sep.length());
				log.error("Could not process CSV file for custom concepts due to faulty header(s), please review the following header(s): " + faultyHeaders + ".");
				csvReader.close();
				return mappedPatients;
			}
			
	        String[] line = null;
	        int csvRow = 0;
	        while((line = csvReader.readNext()) != null) {
	        	
	        	csvRow++;
	        	if(line.length != header.length) {
	        		log.warn("Line " + csvRow + " has " + line.length + " column(s), while the CSV header has " + header.length + " columns, line cannot be parsed, skippinh through to the next line.");
	        		continue;
	        	}
	        	
	        	// We obtain a map of the patient's properties.
	        	Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < header.length; i++) {
	                String string = header[i];
	                String val = line[i];
	                map.put(string, val);
	            }

                String legacyId = map.get(CSV_LEGACY_ID);
                String identifier = map.get(CSV_IDENTIFIER);
                if(!legacyId.isEmpty() && identifier.isEmpty())
                	mappedPatients.put(legacyId, map);
	        }
	        
	        csvReader.close();
		}
		catch (IOException e) {
			log.error(e);
		}
		
		return mappedPatients;
	}
	
	/**
	 * The Patient instance obtained from a map-representation of his/her patient's registration data.
	 * @param mappedPatient
	 * @param phoneAttrType
	 * @return A voided Patient should anything go wrong with the registration data. The 'voided' field is used to flag them.
	 */
	protected static Patient getPatientFromMappedPatient(Map<String, String> mappedPatient, PersonAttributeType phoneAttrType) {
		Patient patient = new Patient();
		final Date now = (new DateTime()).toDate();
		
		String legacyId = mappedPatient.get(CSV_LEGACY_ID);
		
		// Name
		String firstName = WordUtils.capitalize(mappedPatient.get(CSV_FIRST_NAME));
		String lastName = mappedPatient.get(CSV_LAST_NAME).toUpperCase();
		if(firstName.isEmpty()) {
			patient.setVoided(true);
			log.warn("Patient not imported because of missing first name. Legacy ID: " + legacyId);
		}
		if(lastName.isEmpty()) {
			patient.setVoided(true);
			log.warn("Patient not imported because of missing last name. Legacy ID: " + legacyId);
		}
		patient.addName(new PersonName(firstName,"", lastName));
		
		// Address
		String province = mappedPatient.get(CSV_ADDR_PROVINCE);
		String district = mappedPatient.get(CSV_ADDR_DISTRICT);
		PersonAddress address = new PersonAddress();
		address.setCountry("Laos");
		address.setCountyDistrict(district);
		address.setStateProvince(province);
		patient.addAddress(address);
		
		// Registration date
		Date regDate = getDateFromString(mappedPatient.get(CSV_REG_DATE), CSV_DATE_FORMAT);
		if(regDate == null) {
			patient.setVoided(true);
			log.warn("Patient not imported because of incorrectly formatted registration date. Legacy ID: " + legacyId);
		}
		if(regDate.after(now)) {
			patient.setVoided(true);
			log.warn("The registration date cannot be in the future. Legacy ID: " + legacyId);
		}
		
		// Birthdate
		Date birthDate = getDateFromString(mappedPatient.get(CSV_DOB), CSV_DATE_FORMAT);
		patient.setBirthdate(birthDate);
		if(birthDate == null) {
			patient.setVoided(true);
			log.warn("Patient not imported because of incorrectly formatted DOB. Legacy ID: " + legacyId);
		}
		
		// Gender
		String gender = mappedPatient.get(CSV_GENDER).toUpperCase();
		if((gender.equals("M") || gender.equals("F")) == false) {
			patient.setVoided(true);
			log.warn("Patient not imported because of unrecognized gender. Legacy ID: " + legacyId);
		}
		patient.setGender(mappedPatient.get(CSV_GENDER).toUpperCase());
		
		// Phone number
		patient.addAttribute(new PersonAttribute(phoneAttrType, mappedPatient.get(CSV_PHONE)));
		
		return patient;
	}
	
	protected static Date getDateFromString(String dateString, String dateFormat) {
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date date = null;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			// We leave it at null
		}
		return date;
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}
}
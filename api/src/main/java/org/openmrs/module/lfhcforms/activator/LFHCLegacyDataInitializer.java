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
import java.util.List;
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
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.LFHCFormsConstants;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Parses a locally saved CSV containing the registration data for legacy patients to be imported.
 */
public class LFHCLegacyDataInitializer implements Initializer {
	
	protected static final Log log = LogFactory.getLog(LFHCLegacyDataInitializer.class);
	protected final static String LOG_PREFIX = " - [BULK IMPORT] - ";
	
	protected final static String GLOBAL_PROPERTY_BULK_IMPORT_PROCESS = "lfhcforms.bulkImportProcess";
	
	protected final static char		CSV_DELIMITER		= '\t';
	protected final static String	CSV_INNER_DELIMITER = ",";
	protected final static String	CSV_DATE_FORMAT = "MM/dd/yyyy";
	
	protected final static String	CSV_PATIENTS_PATH = "/.OpenMRS/BulkImportFiles/patients.csv";
	protected final static String	CSV_VISITS_PATH = "/.OpenMRS/BulkImportFiles/visits.csv";
	
	protected static String OBS_CODED = "Coded";
	protected static String OBS_NUM = "Numeric";
	protected static String OBS_TEXT = "Text";
	protected static String OBS_BOOL = "Boolean";
	protected static String OBS_NA = "N/A";
	
	protected final static String OPD_LOCATION_UUID = "58c57d25-8d39-41ab-8422-108a0c277d98";
	
	/*
	 * Required headers that MUST be found on the CSV files to be imported.
	 */
	protected final static String CSV_REG_DATE = "Registration Date";
	protected final static String CSV_LEGACY_ID = "Legacy ID";
	protected final static String CSV_IDENTIFIER = "OpenMRS ID";
	protected final static String CSV_FIRST_NAME = "First name";
	protected final static String CSV_LAST_NAME = "Last name";
	protected final static String CSV_PHONE = "Telephone";
	protected final static String CSV_DOB = "DOB";
	protected final static String CSV_GENDER = "Gender";
	protected final static String CSV_FATHER_NAME = "Father's name";
	protected final static String CSV_MOTHER_NAME = "Mother's name";
	protected final static String CSV_ADDR_PROVINCE = "Province";
	protected final static String CSV_ADDR_DISTRICT = "District";
	protected final static String CSV_ADDR_VILLAGE = "Village";
	protected final static String CSV_ETHNICITY = "Ethnicity";
	
	protected final static String CSV_ENCOUNTER_DATE = "Encounter Date";
	protected final static String CSV_QUESTION = "Question";
	protected final static String CSV_ANSWER = "Answer";
	protected final static int CSV_QUESTION_COUNT = 78;
	
	// Array of the minimal set of headers to be found in the import CSV.
	protected final Set<String> patientsRequiredHeaders = new HashSet<String>();
	protected final Set<String> visitsRequiredHeaders = new HashSet<String>();
	
	// Services needed throughout the Initializer
	private EmrApiProperties emrApiProperties;
	private LocationService locationService;
	private RegistrationCoreService registrationService;
	private EncounterService encounterService;
	private ConceptService conceptService;
	private VisitService visitService;
	private PatientService patientService;
	private PersonService personService;
		
	public LFHCLegacyDataInitializer() {
		patientsRequiredHeaders.add(CSV_REG_DATE);
		patientsRequiredHeaders.add(CSV_LEGACY_ID);
		patientsRequiredHeaders.add(CSV_IDENTIFIER);
		patientsRequiredHeaders.add(CSV_FIRST_NAME);
		patientsRequiredHeaders.add(CSV_LAST_NAME);
		patientsRequiredHeaders.add(CSV_PHONE);
		patientsRequiredHeaders.add(CSV_DOB);
		patientsRequiredHeaders.add(CSV_GENDER);
		patientsRequiredHeaders.add(CSV_FATHER_NAME);
		patientsRequiredHeaders.add(CSV_MOTHER_NAME);
		patientsRequiredHeaders.add(CSV_ADDR_PROVINCE);
		patientsRequiredHeaders.add(CSV_ADDR_DISTRICT);
		patientsRequiredHeaders.add(CSV_ADDR_VILLAGE);
		patientsRequiredHeaders.add(CSV_ETHNICITY);
		
		visitsRequiredHeaders.add(CSV_ENCOUNTER_DATE);
		visitsRequiredHeaders.add(CSV_LEGACY_ID);
		for(int i = 1; i <= CSV_QUESTION_COUNT; i++) {
			visitsRequiredHeaders.add(CSV_QUESTION + i);
			visitsRequiredHeaders.add(CSV_ANSWER + i);
		}
	}
	
	/**
	 * Reads the relevant global property that flags whether the import process should occur.
	 * It also purges the property right away to eliminate the risk of accidental multiple imports.
	 */
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
		visitService = Context.getVisitService();
		patientService = Context.getPatientService();
		personService = Context.getPersonService();
	}
	
	protected Logger getLogger() {
		
		String logFileName = "importVisits_" + new SimpleDateFormat("yyyy-MM-dd_hhmm").format(new Date()) + ".log";
		
		Logger logger = Logger.getLogger(LFHCLegacyDataInitializer.class);
		SimpleLayout layout = new SimpleLayout();    
	    FileAppender appender = null;
		try {
			appender = new FileAppender(layout, logFileName, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		logger.addAppender(appender);
		logger.setLevel((Level) Level.DEBUG);

		return logger;
	}

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Importing patients in bulk for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
//		if(doProcess() == false) {
//			log.info("No bulk import processed for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
//			return;
//		}
		
		initializeServices();

		//
		// Processing the patients CSV
		//
//		Map<String, Map<String, String>> mappedPatients = getMapFromCSVResource(CSV_PATIENTS_PATH, CSV_LEGACY_ID, patientsRequiredHeaders);
//		
//		final Map<String, PersonAttributeType> personAttributes = new HashMap<String, PersonAttributeType>() {{ 
//			put(CSV_PHONE, emrApiProperties.getTelephoneAttributeType());
//			put(CSV_FATHER_NAME, personService.getPersonAttributeTypeByUuid(PersonInitializer.FATHER_NAME_ATTR_TYPE_UUID) );
//			put(CSV_MOTHER_NAME, personService.getPersonAttributeTypeByUuid("8d871d18-c2cc-11de-8d13-0010c6dffd0f") );
//		}};
//		final PatientIdentifierType omrsIdentifierType = patientService.getPatientIdentifierTypeByUuid("05a29f94-c0ed-11e2-94be-8c13b969e334");
//		final PatientIdentifierType legacyIdentifierType = patientService.getPatientIdentifierTypeByUuid("8d79403a-c2cc-11de-8d13-0010c6dffd0f");
//		Location registrationDesk = locationService.getLocationByUuid("6351fcf4-e311-4a19-90f9-35667d99a8af");
//		EncounterType registrationEncounterType = encounterService.getEncounterTypeByUuid("3e3424bd-6e9d-4c9c-b3a4-f3fee751fe7c");
//		EncounterRole registrationEncounterRole = encounterService.getEncounterRoleByUuid("a0b03050-c99b-11e0-9572-0800200c9a66");
//		Concept ethnicityQuestion = conceptService.getConceptByMapping("912", "LFHC");	// Ethnicity
//		
//		Map<String, Integer> identifierMapping = new HashMap<String, Integer>();
//		for(Map<String, String> mappedPatient : mappedPatients.values()) {
//			
//			String identifier = mappedPatient.get(CSV_IDENTIFIER);
//			String legacyId = mappedPatient.get(CSV_LEGACY_ID);
//			
//			// Skipping already legacy ID that were already saved
//			if(legacyId.isEmpty() == false) {
//				List<Patient> foundPatients = patientService.getPatients(null, legacyId, new ArrayList<PatientIdentifierType>() {{ add(legacyIdentifierType); }}, true);
//				if(foundPatients.isEmpty() == false) {
//					log.warn(LOG_PREFIX + "Patient(s) found matching the legacy ID, skipping import for that line. Legacy ID: " + legacyId);
//					continue;
//				}
//			}
//			else {
//				log.error(LOG_PREFIX + "Could not import a patient with no legacy ID.");
//				continue;
//			}
//			
//			// Adding the legacy ID to existing patients.
//			if(identifier.isEmpty() == false) {
//				List<Patient> foundPatients = patientService.getPatients(null, identifier, new ArrayList<PatientIdentifierType>() {{ add(omrsIdentifierType); }}, true);
//				if(foundPatients.size() == 1) {
//					Patient foundPatient = foundPatients.get(0);
//					boolean canAddLegacyId = true;
//					for(PatientIdentifier idn : foundPatient.getIdentifiers()) {
//						if(idn.getIdentifierType().equals(legacyIdentifierType)) {
//							canAddLegacyId = false;	// If there is already an identifier of that type, we don't add one more.
//							log.warn(LOG_PREFIX + "A legacy ID was already provided to an existing patient. Legacy ID: " + legacyId + ", exisiting legacy ID: " + idn.toString());
//						}
//					}
//					if(canAddLegacyId) {
//						foundPatient.addIdentifier( new PatientIdentifier(legacyId, legacyIdentifierType, registrationDesk) );
//						foundPatient = patientService.savePatient(foundPatient);
//						log.warn(LOG_PREFIX + "Adding the legacy ID to an already existing patient. Legacy ID: " + legacyId);
//					}
//					continue;
//				}
//			}
//			
//			// Building the new patients from its CSV data
//			Patient patient = LFHCLegacyDataInitializer.getPatientFromMappedProperties(mappedPatient, personAttributes);
//						
//			// Encounters
//			if(patient.isVoided() == false) {
//
//				// Registration encounter
//				Encounter registrationEncounter = new Encounter();
//		        registrationEncounter.setPatient(patient);
//		        registrationEncounter.setEncounterType(registrationEncounterType);
//		        registrationEncounter.setLocation(registrationDesk);
//		        registrationEncounter.setProvider(registrationEncounterRole, emrApiProperties.getUnknownProvider());
//		        Date regDate = getDateFromString( mappedPatient.get(CSV_REG_DATE), CSV_DATE_FORMAT );
//		        registrationEncounter.setEncounterDatetime(regDate);
//		        
//		        // Ethnicity
//		        String ethnicityConceptStr = mappedPatient.get(CSV_ETHNICITY);
//		        if(ethnicityConceptStr.isEmpty() == false) {
//			        Concept ethnicityAnswer = getConceptFromConceptMapping(conceptService, mappedPatient.get(CSV_ETHNICITY));
//		        	if(ethnicityAnswer != null) {
//		        		Obs obs = new Obs();
//		        		obs.setConcept(ethnicityQuestion);
//		        		obs.setValueCoded(ethnicityAnswer);
//		        		registrationEncounter.addObs(obs);
//		        	}
//		        	else {
//		        		patient.setVoided(true);
//		        		log.error(LOG_PREFIX + "No concept could be mapped for the patient's ethnicity. Legacy ID: " + legacyId);
//		        	}
//		        }
//		        
//		        // If still good to go we save the patient and his/her registration encounter.
//		        if(patient.isVoided() == false) {
//		        	patient = registrationService.registerPatient(patient, null, null, registrationDesk);
////		        	legacyId += "_" + UUID.randomUUID().toString().substring(0, 8);	// temp. avoiding duplicates
//		        	patient.addIdentifier( new PatientIdentifier(legacyId, legacyIdentifierType, registrationDesk) );
//		        	patient = patientService.savePatient(patient);
//		        	identifierMapping.put(legacyId, patient.getId());
//					registrationEncounter = encounterService.saveEncounter(registrationEncounter);
//				}
//			}
//		}
//		
//		if(true)
//			return;
		
		Logger logger = getLogger();	// Custom logger for import error messages
		
		//
		// Processing the visits CSV
		//
		Map<String, Map<String, String>> mappedVisits = getMapFromCSVResource(CSV_VISITS_PATH, "", visitsRequiredHeaders);
		EncounterType legacyEncounterType = encounterService.getEncounterTypeByUuid(EncounterTypesInitializer.LEGACY_ENCOUNTER_TYPE_UUID);
		VisitType outPatientVisitType = visitService.getVisitTypeByUuid(LFHCFormsConstants.OUTPATIENT_VISIT_TYPE_UUID);
		
		for(Map<String, String> mappedVisit : mappedVisits.values()) {
			String legacyId = mappedVisit.get(CSV_LEGACY_ID);
			String visitDateString = mappedVisit.get(CSV_ENCOUNTER_DATE);
			Date visitDate = getDateFromString(visitDateString, CSV_DATE_FORMAT);
//			Integer patientId = identifierMapping.get(legacyId);
			
			List<Patient> patients = patientService.getPatients(legacyId);
			if(patients.isEmpty()) {
				logger.error("No patient found for saving the following visit/encounter. Legacy ID: " + legacyId + ", visit date: " + visitDateString);
				continue;
			}
			if(patients.size() > 1) {
				logger.error(patients.size() + " found matching the legacy ID. Legacy ID: " + legacyId + ", visit date: " + visitDateString);
				continue;
			}
			
			Patient patient = patients.get(0);
			
			Set<Obs> obsSet = new HashSet<Obs>();
			for (int i = 1; i <= CSV_QUESTION_COUNT; i++) {
				String questionKey = CSV_QUESTION + i;
				String answerKey = CSV_ANSWER + i;
				
				String questionString = mappedVisit.get(questionKey);
				String answerString = mappedVisit.get(answerKey);
				final String rowInfo = "\nLegacy ID: " + legacyId + ", visit date: " + visitDateString + ", question: " + questionString + ", answer: " + answerString;
				
				// High-level checks.
				if (questionString.isEmpty() && answerString.isEmpty()) {
					continue;
				}
				if (questionString.isEmpty() || answerString.isEmpty()) {
					logger.error("The question or the answer is missing for the following visit/encounter." + rowInfo);
					obsSet.clear();
					break;
				}
				
				// Fetching the question-concept.
				Concept conceptQuestion = getConceptFromConceptMapping(conceptService, questionString);
				if (conceptQuestion == null) {
					logger.error("No concept-question could be mapped for saving the following visit/encounter." + rowInfo);
					obsSet.clear();
					break;
	        	}

				// Processing the answer.
				String obsType = conceptQuestion.getDatatype().getName();
				
				Concept conceptAnswer = getConceptFromConceptMapping(conceptService, answerString);
				boolean parsedToNumeric = false;
				double numericValue = 0.0;
				try {
					numericValue = Double.parseDouble(answerString);
					parsedToNumeric = true;
				}
				catch (NumberFormatException e) {
				}
				
				Obs obs = new Obs();
				obs.setConcept(conceptQuestion);
				
				if ( obsType.equals(OBS_CODED) ) {
					if (conceptAnswer == null) {
						logger.error("A non-coded answer was provided to a coded question." + rowInfo);
						obsSet.clear();
						break;
		        	}
					obs.setValueCoded(conceptAnswer);
				}
				else if ( obsType.equals(OBS_NUM) ) {
					if (parsedToNumeric == false) {
						logger.error("A non-numeric answer was provided to a numeric question." + rowInfo);
						obsSet.clear();
						break;
					}
					obs.setValueNumeric(numericValue);
				}
				else if ( obsType.equals(OBS_TEXT) ) {
					if (conceptAnswer != null) {
						logger.error("A coded answer was provided to a text question, this most likely most likely an error." + rowInfo);
						obsSet.clear();
						break;
		        	}
					if (parsedToNumeric == true) {
						logger.error("A numeric answer was provided to a text question, this most likely most likely an error." + rowInfo);
						obsSet.clear();
						break;
					}
					obs.setValueText(answerString);
				}
				else if ( obsType.equals(OBS_BOOL) ) {
					
					if (conceptAnswer != null) {
						answerString = conceptAnswer.getName(Locale.ENGLISH).getName();
					}
					
					boolean res = false;
					if (answerString.equalsIgnoreCase("yes") || answerString.equalsIgnoreCase("true") || answerString.equalsIgnoreCase("1") ) {
						res = true;
					}
					else if (answerString.equalsIgnoreCase("no") || answerString.equalsIgnoreCase("false") || answerString.equalsIgnoreCase("0") ) {
						res = false;
					}
					else {
						logger.error("No boolean answer could be obtained for a boolean question." + rowInfo);
						obsSet.clear();
						break;
					}
					
					obs.setValueBoolean(res);
				}
				else {
					logger.error("The question datatype is not handled: '" + obsType + "'." + rowInfo);
					obsSet.clear();
					break;
				}
        		obsSet.add(obs);
			}
			
			if (obsSet.isEmpty() == false) {
				final Encounter legacyEncounter = new Encounter();
				legacyEncounter.setEncounterType(legacyEncounterType);
				legacyEncounter.setEncounterDatetime(visitDate);
				legacyEncounter.setPatient(patient);
				legacyEncounter.setLocation( locationService.getLocationByUuid(OPD_LOCATION_UUID) );
				for (Obs obs : obsSet) {
					legacyEncounter.addObs(obs);
				}
				
				Visit visit = new Visit();
				visit.setVisitType(outPatientVisitType);
				visit.setPatient(legacyEncounter.getPatient());
				visit.setStartDatetime(legacyEncounter.getEncounterDatetime());
				visit.setStopDatetime((new DateTime(legacyEncounter.getEncounterDatetime())).plusHours(1).toDate());	// Visit duration is set here
				visit.setLocation(legacyEncounter.getLocation());
				visit.setEncounters(new HashSet<Encounter>() {{
					add(legacyEncounter);
				}});
				
				visitService.saveVisit(visit);
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
	 * Parses the (locally saved) CSV resource containing the legacy data.
	 * @return A indexed map of map-represented properties: CSV header -- Value for that header. 
	 */
	protected Map<String, Map<String, String>> getMapFromCSVResource(String csvPath, String indexHeader, Set<String> requiredHeaders) {

		Map<String, Map<String, String>> mappedProperties = new HashMap<String, Map<String, String>>();
		
		// Checking that the file is indeed there
		Path path = Paths.get(System.getProperty("user.home") +  csvPath);
		final String msgFileNotFound = "Import file for bulk import not found at: " + csvPath; 
		if(path == null) {
			log.error(LOG_PREFIX + msgFileNotFound);
			return mappedProperties;
		}		
		
		// Reading the file
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path.toString());
		} catch (FileNotFoundException e) {
			log.error(LOG_PREFIX + msgFileNotFound);
			return mappedProperties;
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
				log.error(LOG_PREFIX + "Could not process import CSV file due to faulty header(s), please review the following header(s): " + faultyHeaders + ".");
				csvReader.close();
				return mappedProperties;
			}
			
	        String[] line = null;
	        int csvRow = 0;
	        while((line = csvReader.readNext()) != null) {
	        	
	        	csvRow++;
	        	if(line.length != header.length) {
	        		log.warn(LOG_PREFIX + "Line " + csvRow + " has " + line.length + " column(s), while the CSV header has " + header.length + " columns, line cannot be parsed, skippinh through to the next line.");
	        		continue;
	        	}
	        	
	        	// We obtain a map of the patient's properties.
	        	Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < header.length; i++) {
	                String string = header[i];
	                String val = line[i];
	                map.put(string, val);
	            }
                
                String mapKey = (new Integer(csvRow)).toString();
                if(indexHeader.isEmpty() == false) {
                	mapKey = map.get(indexHeader);
                }
                if(!mapKey.isEmpty())
                	mappedProperties.put(mapKey, map);
	        }
	        
	        csvReader.close();
		}
		catch (IOException e) {
			log.error(e);
		}
		
		return mappedProperties;
	}
	
	/**
	 * The Patient instance obtained from a map-representation of his/her patient's registration data.
	 * @param mappedPatient
	 * @param phoneAttrType
	 * @return A voided Patient should anything go wrong with the registration data. The 'voided' field is used to flag them.
	 */
	protected static Patient getPatientFromMappedProperties(Map<String, String> mappedPatient/*, PatientIdentifierType legacyIdentifierType*/, Map<String, PersonAttributeType> personAttributes) {
		Patient patient = new Patient();
		final Date now = (new DateTime()).toDate();
	
		// Legacy ID
		String legacyId = mappedPatient.get(CSV_LEGACY_ID);
		
		// Checking OpenMRS identifier's presence
		String identifier = mappedPatient.get(CSV_IDENTIFIER);
		if(identifier.isEmpty() == false) {
			patient.setVoided(true);
		}
	
		// Name
		String firstName = WordUtils.capitalize(mappedPatient.get(CSV_FIRST_NAME));
		String lastName = mappedPatient.get(CSV_LAST_NAME).toUpperCase();
		if(firstName.isEmpty()) {
			patient.setVoided(true);
			log.warn(LOG_PREFIX + "Patient not imported because of missing first name. Legacy ID: " + legacyId);
		}
		if(lastName.isEmpty()) {
			patient.setVoided(true);
			log.warn(LOG_PREFIX + "Patient not imported because of missing last name. Legacy ID: " + legacyId);
		}
		patient.addName(new PersonName(firstName,"", lastName));
		
		// Address
		PersonAddress address = new PersonAddress();
		address.setCountry("Laos");
		address.setCityVillage( mappedPatient.get(CSV_ADDR_VILLAGE) );
		address.setCountyDistrict( mappedPatient.get(CSV_ADDR_DISTRICT) );
		address.setStateProvince( mappedPatient.get(CSV_ADDR_PROVINCE) );
		patient.addAddress(address);
		
		// Registration date
		Date regDate = getDateFromString(mappedPatient.get(CSV_REG_DATE), CSV_DATE_FORMAT);
		if(regDate == null) {
			patient.setVoided(true);
			log.warn(LOG_PREFIX + "Patient not imported because of incorrectly formatted registration date. Legacy ID: " + legacyId);
		}
		if(regDate.after(now)) {
			patient.setVoided(true);
			log.warn(LOG_PREFIX + "The registration date cannot be in the future. Legacy ID: " + legacyId);
		}
		
		// Birthdate
		Date birthDate = getDateFromString(mappedPatient.get(CSV_DOB), CSV_DATE_FORMAT);
		patient.setBirthdate(birthDate);
		if(birthDate == null) {
			patient.setVoided(true);
			log.warn(LOG_PREFIX + "Patient not imported because of incorrectly formatted DOB. Legacy ID: " + legacyId);
		}
		
		// Gender
		String gender = mappedPatient.get(CSV_GENDER).toUpperCase();
		if((gender.equals("M") || gender.equals("F")) == false) {
			patient.setVoided(true);
			log.warn(LOG_PREFIX + "Patient not imported because of unrecognized gender. Legacy ID: " + legacyId);
		}
		patient.setGender(mappedPatient.get(CSV_GENDER).toUpperCase());
		
		// Phone number, Father & Mother's names
		patient.addAttribute(new PersonAttribute(personAttributes.get(CSV_PHONE), mappedPatient.get(CSV_PHONE)));
		patient.addAttribute(new PersonAttribute(personAttributes.get(CSV_PHONE), mappedPatient.get(CSV_PHONE)));
		patient.addAttribute(new PersonAttribute(personAttributes.get(CSV_PHONE), mappedPatient.get(CSV_PHONE)));
		
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
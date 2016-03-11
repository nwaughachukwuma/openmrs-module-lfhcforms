package org.openmrs.module.lfhcforms.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;

/**
 * Sets up the Encounter Types required by the HFE forms.
 */
public class EncounterTypesInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(EncounterTypesInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting new Encounter Types for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);
		
		EncounterService es = Context.getEncounterService();

		{
			String name = "Registration Encounter";
			String desc = "Custom registration encounter type, recording ethnicity and language spoken.";
			String uuid = "3e3424bd-6e9d-4c9c-b3a4-f3fee751fe7c";
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Clinical Notes";
			String desc = "";
			String uuid = "49e7bc6c-bfbd-40dc-8164-45d82c55b5f9"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Diagnosis";
			String desc = "";
			String uuid = "3dbd13da-f210-4f20-a5b4-536a92e81474"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Xray/Ultrasound Order";
			String desc = "";
			String uuid = "301e0396-2069-4085-919e-5a7b423b54f8"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}		
		{
			String name = "Dispense Medication";
			String desc = "";
			String uuid = "ef469ea5-8b79-4a64-a1c8-ce3e09b4e703";
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Fluid Balance";
			String desc = "";
			String uuid = "9f5c90b2-03e0-4374-8534-6c32ea71f3ab"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Doctor History";
			String desc = "";
			String uuid = "a9702711-f39c-4a91-ad96-88c1213b12b7"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Immunity and Infection";
			String desc = "";
			String uuid = "e864f775-d1d0-4936-85df-557ad5c44ae1"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "IPD Nurse";
			String desc = "";
			String uuid = "c88f3763-77a4-4e57-a42f-eef996b03b98"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Lab Test Order";
			String desc = "";
			String uuid = "39deb891-8dc6-451b-9361-dbf564f27250";
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Lines and Tubes";
			String desc = "";
			String uuid = "934aad8b-250d-450b-9cb3-ca5f01841f61"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Medication Order";
			String desc = "";
			String uuid = "3c462397-7840-4890-ae78-d123d9fd138b";
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "OPD Nurse";
			String desc = "";
			String uuid = "c7700650-63a6-4893-976e-7a7a0cc43b04"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Doctor Physical Exam";
			String desc = "";
			String uuid = "0d9203a9-c1a1-4358-832d-2fc91373558e"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Treatment Plan/Order";
			String desc = "";
			String uuid = "feacdb57-4388-4b39-8840-2ff063bfab34"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Triage";
			String desc = "";
			String uuid = "5235f7da-194e-4278-afbd-a473bb430857";
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Wound Details";
			String desc = "";
			String uuid = "75a97fa9-4b04-4968-8f5b-041e5d0a3dae"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Neurovascular Observation";
			String desc = "";
			String uuid = "4a4e04a0-1424-4fb1-bebb-ba6bb16dd628"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Nurse Blood Transfusion";
			String desc = "";
			String uuid = "10c20ebf-1af2-4ddf-bdaa-219fe8ff930b"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Doctor Blood Transfusion";
			String desc = "";
			String uuid = "89886052-4a6b-4b74-a71b-4b87094b02cb"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Homecare Regular Visit";
			String desc = "";
			String uuid = "4897af83-80b0-4ac3-bdd5-7cdf3467ff63"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Homecare Pre-assessment";
			String desc = "";
			String uuid = "27c72549-481c-46be-ac32-5080669e2eef"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
		{
			String name = "Financial Assessment";
			String desc = "";
			String uuid = "4e8a589c-f025-44ed-8978-dded6478f3f0"; 
			EncounterType encounterType = es.getEncounterTypeByUuid(uuid);
			if(encounterType == null) {
				encounterType = new EncounterType(name, desc);
				encounterType.setUuid(uuid);
				es.saveEncounterType(encounterType);
			}
		}
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
		//TODO: Perhaps disable the encounter types?
	}
}

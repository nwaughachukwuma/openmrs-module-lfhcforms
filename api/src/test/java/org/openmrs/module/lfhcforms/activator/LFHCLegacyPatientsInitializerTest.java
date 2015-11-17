package org.openmrs.module.lfhcforms.activator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonAttributeType;

public class LFHCLegacyPatientsInitializerTest {

	protected LFHCLegacyPatientsInitializer ini = new LFHCLegacyPatientsInitializer();
	protected PersonAttributeType phonetAttrType = mock(PersonAttributeType.class);
	
	@Before
    public void setUp() {
    }

	@Test
	public void test_getPatientFromMappedPatient() throws Exception {
		
		Map<String, String> mappedPatient = new HashMap<String, String>();
		mappedPatient.put(LFHCLegacyPatientsInitializer.CSV_FIRST_NAME, "John");
		mappedPatient.put(LFHCLegacyPatientsInitializer.CSV_LAST_NAME, "Doe");
		mappedPatient.put(LFHCLegacyPatientsInitializer.CSV_ADDR_PROVINCE, "Luang Prabang");
		mappedPatient.put(LFHCLegacyPatientsInitializer.CSV_ADDR_DISTRICT, "Luang Prabang");
		mappedPatient.put(LFHCLegacyPatientsInitializer.CSV_GENDER, "M");
		mappedPatient.put(LFHCLegacyPatientsInitializer.CSV_DOB, "3/16/2012");
		mappedPatient.put(LFHCLegacyPatientsInitializer.CSV_REG_DATE, "10/22/2015");
		
		Patient patient = LFHCLegacyPatientsInitializer.getPatientFromMappedPatient(mappedPatient, phonetAttrType);

		LocalDate localDate = new LocalDate(patient.getBirthdate());
		assertEquals(patient.getBirthdate(), localDate.toDate());
	}
}

package org.openmrs.module.lfhcforms.activator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;

public class LFHCLegacyPatientsInitializerTest {

	protected LFHCLegacyDataInitializer ini = new LFHCLegacyDataInitializer();
	protected PersonAttributeType phonetAttrType = mock(PersonAttributeType.class);
	protected PersonAttributeType legacyIdAttrType = mock(PersonAttributeType.class);
	protected PatientIdentifierType legacyIdentifierType = mock(PatientIdentifierType.class);
	final Map<String, PersonAttributeType> personAttributes = new HashMap<String, PersonAttributeType>() {{ 
		put(LFHCLegacyDataInitializer.CSV_PHONE, mock(PersonAttributeType.class));
		put(LFHCLegacyDataInitializer.CSV_FATHER_NAME, mock(PersonAttributeType.class));
		put(LFHCLegacyDataInitializer.CSV_MOTHER_NAME, mock(PersonAttributeType.class));
	}};
	
	@Before
    public void setUp() {
    }

	@Test
	public void test_getPatientFromMappedPatient() throws Exception {
		
		Map<String, String> mappedPatient = new HashMap<String, String>();
		mappedPatient.put(LFHCLegacyDataInitializer.CSV_IDENTIFIER, "1504356");
		mappedPatient.put(LFHCLegacyDataInitializer.CSV_FIRST_NAME, "John");
		mappedPatient.put(LFHCLegacyDataInitializer.CSV_LAST_NAME, "Doe");
		mappedPatient.put(LFHCLegacyDataInitializer.CSV_ADDR_PROVINCE, "Luang Prabang");
		mappedPatient.put(LFHCLegacyDataInitializer.CSV_ADDR_DISTRICT, "Luang Prabang");
		mappedPatient.put(LFHCLegacyDataInitializer.CSV_GENDER, "M");
		mappedPatient.put(LFHCLegacyDataInitializer.CSV_DOB, "3/16/2012");
		mappedPatient.put(LFHCLegacyDataInitializer.CSV_REG_DATE, "10/22/2015");
		
		Patient patient = LFHCLegacyDataInitializer.getPatientFromMappedProperties(mappedPatient, personAttributes);

		LocalDate localDate = new LocalDate(patient.getBirthdate());
		assertEquals(patient.getBirthdate(), localDate.toDate());
	}
}

package org.openmrs.module.lfhcforms.activator;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMapType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;

public class ConceptsInitializerTest {

	protected ConceptService cs = mock(ConceptService.class);
	
	@Before
    public void setUp() {
		when(cs.getConceptDatatypeByName(anyString())).thenReturn(new ConceptDatatype(0));
		when(cs.getConceptClassByName(anyString())).thenReturn(new ConceptClass(0));
		when(cs.getConceptMapTypeByUuid(anyString())).thenReturn(new ConceptMapType(0));
    }

	@Test
	public void getResourcesToInclude_shouldMaintainInsertionOrder() throws Exception {
		
		ConceptsInitializer ini = new ConceptsInitializer();
		
		ini.addConceptSources(cs);
		Map<String, Map<String, String>> mappedConcepts = ini.getConceptMapFromCSVResource("lfhcConcepts.csv", new DefaultResouceLoaderImpl());
		ini.addMappedConcepts(cs, mappedConcepts);
	}
}

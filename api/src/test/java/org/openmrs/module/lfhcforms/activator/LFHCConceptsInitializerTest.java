package org.openmrs.module.lfhcforms.activator;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMapType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

//@SuppressStaticInitializationFor({Context.class})
//@RunWith( PowerMockRunner.class )
//@PrepareForTest( Context.class )
public class LFHCConceptsInitializerTest {

	protected ConceptService cs = mock(ConceptService.class);
	
	@Before
    public void setUp() {
		PowerMockito.mockStatic(Context.class);
		
//		AdministrationService adminService = mock(AdministrationService.class);
//		when(Context.getAdministrationService()).thenReturn(adminService);
//		
//		when(cs.getConceptDatatypeByName(anyString())).thenReturn(new ConceptDatatype(0));
//		when(cs.getConceptClassByName(anyString())).thenReturn(new ConceptClass(0));
//		when(cs.getConceptMapTypeByUuid(anyString())).thenReturn(new ConceptMapType(0));
    }

	@Test
	public void getResourcesToInclude_shouldMaintainInsertionOrder() throws Exception {
		
//		LFHCConceptsInitializer ini = new LFHCConceptsInitializer();
//		
//		ini.addConceptSources(cs);
//		Map<String, Map<String, String>> mappedConcepts = ini.getConceptMapFromCSVResource("lfhcConcepts.csv", new DefaultResouceLoaderImpl());
//		ini.addMappedConcepts(cs, mappedConcepts);
		
	}
}

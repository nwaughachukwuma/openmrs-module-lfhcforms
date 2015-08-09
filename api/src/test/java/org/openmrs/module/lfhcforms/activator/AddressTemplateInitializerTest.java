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

public class AddressTemplateInitializerTest {

	protected ConceptService cs = mock(ConceptService.class);
	
	@Before
    public void setUp() {
    }

	@Test
	public void getResourcesToInclude_shouldMaintainInsertionOrder() throws Exception {
		
//		AddressTemplateInitializer ini = new AddressTemplateInitializer();
//		ini.started();
	}
}

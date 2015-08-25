package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;

public class PewsScoreFragmentControllerTest {
	
	@Before
    public void setUp() {
    }

	@Test
	public void fooBar() throws IOException {
		
		String json = "";

		ResourceLoader loader = new DefaultResouceLoaderImpl();
		json = loader.getResourceAsSting("boundaries.json", "UTF-8");
		
		
		ObjectMapper mapper = new ObjectMapper();
		
		
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, PewsScoreFragmentController.Boundaries.class);
		HashMap<String, PewsScoreFragmentController.Boundaries> map = mapper.readValue(json, mapType);
		map.clear();
		
		
//		PewsScoreFragmentController.Boundaries b = new PewsScoreFragmentController.Boundaries();
//		try {
//			json = mapper.writeValueAsString(b);
//		} catch (JsonGenerationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}

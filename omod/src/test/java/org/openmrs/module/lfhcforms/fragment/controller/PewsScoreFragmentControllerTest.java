package org.openmrs.module.lfhcforms.fragment.controller;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.lfhcforms.fragment.controller.PewsScoreFragmentController.Boundaries;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;

public class PewsScoreFragmentControllerTest {
	
	@Before
    public void setUp() {
    }

	@Test
	public void fooBar() throws IOException {
		
		String json = "";
		
		PewsScoreFragmentController controller = new PewsScoreFragmentController();
		json = controller.getBoundariesJson(new DefaultResouceLoaderImpl(), "boundaries.json");
		
		Map<String, Boundaries> map = controller.getBoundariesMapFromJson(json);
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

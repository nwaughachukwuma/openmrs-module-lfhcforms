package org.openmrs.module.lfhcforms.fragment.controller.visit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.module.appui.AppUiConstants;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.lfhcforms.utils.VisitTypeHelper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;

public class VisitTypeChangeFragmentControllerTest {

	private VisitTypeChangeFragmentController controller;
	private AdtService mockAdtService;
	private VisitService mockVisitService;
	private VisitTypeHelper mockVisitTypeHelper;
	private Patient patient;
	private VisitType selectedType;
	private Visit visit;
	private UiUtils mockUiUtils;
	private UiSessionContext mockEmrContext;
	private	HttpSession session;
	private HttpServletRequest request;
	
	@Before
	public void setUp() throws Exception {

		controller = new VisitTypeChangeFragmentController();
		
		patient = new Patient();
		visit = new Visit();
		selectedType = new VisitType();

		mockUiUtils = mock(UiUtils.class);
		mockEmrContext = mock(UiSessionContext.class);
		mockAdtService = mock(AdtService.class);
		mockVisitService = mock(VisitService.class);
		mockVisitTypeHelper = mock(VisitTypeHelper.class);

		session = mock(HttpSession.class);
		request = mock(HttpServletRequest.class);

		when(request.getSession()).thenReturn(session);
		
	}

	@Test
	public void shouldSetNewVisitType() {
			
		String successMessage = "Success message";
		String formattedSelectedType = "SelectedType";
		when(mockUiUtils.format(selectedType)).thenReturn(formattedSelectedType);
		when(mockUiUtils.message("lfhcforms.app.visit.visittype.change.success", formattedSelectedType)).thenReturn(successMessage);

		FragmentActionResult result = controller.change(mockAdtService, mockVisitService, mockVisitTypeHelper, 
				patient, selectedType, visit, mockUiUtils, mockEmrContext, request);
	
		verify(mockVisitService).saveVisit(visit);
		assertTrue(visit.getVisitType().equals(selectedType));
		
		verify(session).setAttribute(AppUiConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, successMessage);
		verify(session).setAttribute(AppUiConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
		assertThat(result, is(instanceOf(SuccessResult.class)));
	}
}

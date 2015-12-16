package org.openmrs.module.lfhcforms.fragment.controller.visit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.module.appui.AppUiConstants;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.lfhcforms.utils.VisitHelper;
import org.openmrs.module.lfhcforms.utils.VisitTypeHelper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;

public class VisitStartFragmentControllerTest {

	private VisitStartFragmentController controller;
	private UiUtils mockUiUtils;
	private UiSessionContext mockEmrContext;
	private AdtService mockAdtService;
	private VisitService mockVisitService;
	private VisitHelper mockVisitHelper;
	private VisitTypeHelper mockVisitTypeHelper;
	private VisitType selectedType;
	private	HttpSession session;
	private HttpServletRequest request;
	private Patient patient;
	private Location visitLocation;

	@Before
	public void setUp() {
		controller = new VisitStartFragmentController();

		patient = new Patient();
		visitLocation = new Location();
		selectedType = new VisitType();

		mockUiUtils = mock(UiUtils.class);
		mockEmrContext = mock(UiSessionContext.class);
		mockAdtService = mock(AdtService.class);
		mockVisitService = mock(VisitService.class);
		mockVisitHelper = mock(VisitHelper.class);
		mockVisitTypeHelper = mock(VisitTypeHelper.class);

		session = mock(HttpSession.class);
		request = mock(HttpServletRequest.class);

		when(request.getSession()).thenReturn(session);
	}

	@Test
	public void shouldCreateNewVisit() throws Exception {

		String successMessage = "Success message";
		String formattedPatient = "Patient name";
		when(mockUiUtils.format(patient)).thenReturn(formattedPatient);
		when(mockUiUtils.message("coreapps.visit.createQuickVisit.successMessage", formattedPatient)).thenReturn(successMessage);

		Visit visit = new Visit();
		when(mockAdtService.ensureVisit(any(Patient.class), any(Date.class), any(Location.class))).thenReturn(visit);

		FragmentActionResult result = controller.create(patient, selectedType, mockUiUtils, 
				mockEmrContext, request, mockAdtService, mockVisitService, mockVisitHelper, mockVisitTypeHelper);

		verify(mockAdtService).ensureVisit(eq(patient), any(Date.class), any(Location.class));
		verify(mockVisitService).saveVisit(visit);

		verify(session).setAttribute(AppUiConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, successMessage);
		verify(session).setAttribute(AppUiConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
		assertThat(result, is(CoreMatchers.any(FragmentActionResult.class)));
	}

	@Test
	public void shouldSetVisitType() {

		Visit visit = new Visit();
		when(mockAdtService.ensureVisit(any(Patient.class), any(Date.class), any(Location.class))).thenReturn(visit);

		controller.create(patient, selectedType, mockUiUtils, 
				mockEmrContext, request, mockAdtService, mockVisitService, mockVisitHelper, mockVisitTypeHelper);

		// ensure that the visit type is set to the visit
		assertTrue(visit.getVisitType().equals(selectedType));
	}

	@Test
	public void shouldFailIfAnyActiveVisits() {

		Visit visit1 = new Visit();
		Visit visit2 = new Visit();
		List<Visit> activeVisits = new ArrayList<Visit>();
		activeVisits.add(visit1);
		activeVisits.add(visit2);

		when(mockVisitHelper.getActiveVisits(patient, mockAdtService)).thenReturn(activeVisits);

		FragmentActionResult result = controller.create(patient, selectedType, mockUiUtils, 
				mockEmrContext, request, mockAdtService, mockVisitService, mockVisitHelper, mockVisitTypeHelper);

		assertThat(result, is(instanceOf(FailureResult.class)));
		verify(mockAdtService, never()).ensureVisit(eq(patient), any(Date.class), eq(visitLocation));
	}
}

package org.openmrs.module.lfhcforms;

public class LFHCFormsConstants {

	// Main Location UUID
	public static final String LFHC_LOCATION_UUID = "aff27d58-a15c-49a6-9beb-d30dcfc0c66e";
	
	// Visit With Visit Type App
	public static final String OUTREACH_COLOR = "BurlyWood";
	public static final String OUTPATIENT_COLOR = "#3399ff";
	public static final String INPATIENT_COLOR = "Coral";
	public static final String EMERGENCY_COLOR = "Red";
	public static final String OPERATING_THEATER_COLOR = "MediumPurple";
	
	public static final String OUTREACH_SHORTNAME = "OUTREACH";
	public static final String OUTPATIENT_SHORTNAME = "OPD";
	public static final String INPATIENT_SHORTNAME = "IPD";
	public static final String EMERGENCY_SHORTNAME = "ER";
	public static final String OPERATING_THEATER_SHORTNAME = "OR";
	
	public static final String SHORT_NAME_LOCATION_ATTRIBUTE_TYPE_UUID = "f31de69c-ca4f-4613-aec6-2a3f28cdc973";
	
	// Registration App
	public static final String FATHER_NAME_UUID = "51c5e4f4-7e13-11e5-8bcf-feff819cdc9f";	
	public static final String FATHER_OCCUPATION_UUID = "51c5e88c-7e13-11e5-8bcf-feff819cdc9f";	
	public static final String MOTHER_NAME_UUID = "8d871d18-c2cc-11de-8d13-0010c6dffd0f";	
	public static final String INSURANCE_DETAILS_UUID = "5c6ee7c2-7e1b-11e5-8bcf-feff819cdc9f";

	// PEWS widget
	public static final String PEWS_TIME_WINDOW_PROPERTY = "lfhcforms.pewsTimeWindowInMin";
	public static final int PEWS_FALLBACK_TIMEWINDOW = 0;
	public static final String PEWS_EXPIRY_PROPERTY = "lfhcforms.pewsExpiryInMin";
	
	// Concept Initializer
	public final static String LFHC_CONCEPT_SOURCE = "LFHC";
	public final static String CIEL_CONCEPT_SOURCE = "CIEL";
	
	// Admission encounter type
	public final static String ADMISSION_ENCOUNTER_TYPE_UUID = "e22e39fd-7db2-45e7-80f1-60fa0d5a4378";
	public final static String DISCHARGE_ENCOUNTER_TYPE_UUID = "181820aa-88c9-479b-9077-af92f5364329";
	public final static String TRANSFER_ENCOUNTER_TYPE_UUID = "7b68d557-85ef-4fc8-b767-4fa4f5eb5c23";
	
	// Location tag
	public final static String ADMISSION_LOCATION_TAG_UUID = "1c783dca-fd54-4ea8-a0fc-2875374e9cb6";
	public final static String TRANSFER_LOCATION_TAG_UUID = "8d4626ca-7abd-42ad-be48-56767bbcf272";
	public final static String LOGIN_LOCATION_TAG_UUID = "b8bbf83e-645f-451f-8efe-a0db56f09676";
	public final static String VISIT_LOCATION_TAG_UUID = "37dd4458-dc9e-4ae6-a1f1-789c1162d37b";
	
	// Visit types
	public final static String OUTPATIENT_VISIT_TYPE_UUID = "7b0f5697-27e3-40c4-8bae-f4049abfb4ed";
	public final static String INPATIENT_VISIT_TYPE_UUID = "033e8ed9-caed-4d91-8777-d9df64128d4c";
	public final static String OUTREACH_VISIT_TYPE_UUID = "ae47d76a-9799-4b30-be99-e19130b1a54e";
	public final static String EMERGENCY_VISIT_TYPE_UUID = "8545d579-520d-4543-8cc9-1b4922338484";
	public final static String OPERATING_THEATER_VISIT_TYPE_UUID = "b5566741-40e4-427d-8c20-b44a14c55a6d";

	// a JSON like object property to set visit types order
	public final static String VISIT_TYPES_ORDER_PROPERTY = "lfhcforms.visit.visittype.order";
	
}

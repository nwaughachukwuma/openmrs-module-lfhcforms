var displayToast = true

function changeEncounterDate(visitEndDate) {

	console.log(visitEndDate);

	if (displayToast) {
		$().toastmessage( 'showToast', { type: 'alert',
			position: 'top-right',
			text:  alertMessage } );
		displayToast = false;
	};

	// Set the encounterDate to be 'visit end date' instead of 'visit start date'
	if (htmlForm) {
		htmlForm.setEncounterDate(visitEndDate);
		$().toastmessage( 'showToast', { type: 'alert',
			position: 'top-right',
			text:  successMessage } );
	}

	// Set the encounter time to be 'visit end time' instead of 00:00
	setEncounterTime(visitEndDate);
}

function setEncounterTime (visitEndDate) {

	var hoursField = $('#encounterDate').find('.hfe-hours');
	var minutesField = $('#encounterDate').find('.hfe-minutes');
	var secondsField = $('#encounterDate').find('.hfe-seconds');

	if (typeof $(hoursField).val() === "undefined") {
		console.error("$('#encounterDate').find('.hfe-hours') field not found. Time may not be set correctly");
	}
	$(hoursField).val(visitEndDate.getHours());
	$(minutesField).val(visitEndDate.getMinutes());
	$(secondsField).val(0);
}

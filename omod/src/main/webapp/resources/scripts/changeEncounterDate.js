var displayToast = true

function changeEncounterDate(visitEndDate) {

	//console.log(visitEndDate);

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

	if (typeof $(".hfe-hours").val() === "undefined") {
		console.error("$('.hfe-hours') field not found. Time may not be set correctly");
	}
	$(".hfe-hours").val(visitEndDate.getHours());
	$(".hfe-minutes").val(visitEndDate.getMinutes());
	$(".hfe-seconds").val(0);
}

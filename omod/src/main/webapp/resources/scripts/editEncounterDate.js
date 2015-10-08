var editEncounterDateDialog = null;
function showEditEncounterDateDialog() {
	if (!editEncounterDateDialog) {
		editEncounterDateDialog = emr.setupConfirmationDialog({
			selector: '#encounter-date-dialog',
			actions: {
				confirm: function() {
					console.log("confirmed");
					$("#encounter-date-dialog").hide();
										$().toastmessage( 'showToast', { type: 'success',
                                              position: 'top-right',
                                              text:  successMessage } );
					return true;
				}
			}
		});
	}
	editEncounterDateDialog.show();
}

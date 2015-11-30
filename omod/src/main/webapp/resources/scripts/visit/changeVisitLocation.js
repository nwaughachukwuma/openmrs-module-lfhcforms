

var visit = visit || {};

visit.changeVisitLocationDialog = null;

visit.createChangeVisitLocationDialog = function() {
	visit.changeVisitLocationDialog = emr.setupConfirmationDialog({
		selector: '#change-visit-location-dialog',
		actions: {
			confirm: function() {
				emr.getFragmentActionWithCallback('lfhcforms', 'visit/visitLocationChange', 'change',
					{ patientId: visit.patientId, visitId:visit.visitId,
						selectedLocation: jq('#new-location-drop-down').find(":selected").val() },
					function(data) {
						jq('#change-visit-location-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('disabled')
						// debugger;
						visit.reloadPageWithoutVisitId();
					},function(err){
						// debugger;

						visit.reloadPageWithoutVisitId();
					});
			},
			cancel: function() {
				visit.changeVisitLocationDialog.close();
			}
		}
	});

	visit.changeVisitLocationDialog.close();
}

visit.showChangeVisitLocationDialog = function(visitId) {
	visit.visitId = visitId;
	//console.log(patientId);
	if (visit.changeVisitLocationDialog == null) {
		visit.createChangeVisitLocationDialog(visitId);
	}
	visit.changeVisitLocationDialog.show();
};


var visit = visit || {};

visit.reloadPageWithoutVisitId = function() {
    window.location.search = window.location.search.replace(/visitId=[^\&]*/g, "");
}


visit.startVisitWithLocationDialog = null;

visit.createStartVisitWithLocationDialog = function(patientId) {
	console.log("clicked");
	visit.startVisitWithLocationDialog = emr.setupConfirmationDialog({
		selector: '#start-visit-with-location-dialog',
		actions: {
			confirm: function() {
				emr.getFragmentActionWithCallback('lfhcforms', 'startVisitWithLocation', 'create',
					{ patientId: visit.patientId,
						selectedLocation: jq('#visit-location-drop-down').find(":selected").val() },
					function(data) {
						jq('#start-visit-with-location-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('disabled')
						console.log("success");
						//debugger;
						visit.reloadPageWithoutVisitId();
					},function(err){
						console.log("fail");
						//debugger;
						visit.reloadPageWithoutVisitId();
					});
			},
			cancel: function() {
				visit.startVisitWithLocationDialog.close();
			}
		}
	});

	visit.startVisitWithLocationDialog.close();
}

visit.showStartVisitWithLocationDialog = function(patientId) {
	visit.patientId = patientId;
	console.log(patientId);
	if (visit.startVisitWithLocationDialog == null) {
		visit.createStartVisitWithLocationDialog();
	}
	visit.startVisitWithLocationDialog.show();
};


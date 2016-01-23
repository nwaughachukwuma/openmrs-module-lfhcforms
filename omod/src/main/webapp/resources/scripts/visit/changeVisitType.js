

var visit = visit || {};

visit.changeVisitTypeDialog = null;

visit.createChangeVisitTypeDialog = function() {
	visit.changeVisitTypeDialog = emr.setupConfirmationDialog({
		selector: '#change-visit-visittype-dialog',
		actions: {
			confirm: function() {
				emr.getFragmentActionWithCallback('lfhcforms', 'visit/visitTypeChange', 'change',
					{ patientId: visit.patientId, visitId:visit.visitId,
						selectedType: jq('#new-visittype-drop-down').find(":selected").val() },
					function(data) {
						jq('#change-visit-visittype-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('disabled')
						// debugger;
						visit.reloadPageWithoutVisitId();
					},function(err){
						// debugger;

						visit.reloadPageWithoutVisitId();
					});
			},
			cancel: function() {
				visit.changeVisitTypeDialog.close();
			}
		}
	});

	visit.changeVisitTypeDialog.close();
}

visit.showChangeVisitTypeDialog = function(visitId) {
	visit.visitId = visitId;
	//console.log(patientId);
	if (visit.changeVisitTypeDialog == null) {
		visit.createChangeVisitTypeDialog(visitId);
	}
	visit.changeVisitTypeDialog.show();
};


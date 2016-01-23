var visit = visit || {};

visit.startVisitWithVisitTypeDialog = null;

visit.createStartVisitWithVisitTypeDialog = function() {
	visit.startVisitWithVisitTypeDialog = emr.setupConfirmationDialog({
		selector: '#start-visit-with-visittype-dialog',
		actions: {
			confirm: function() {	
				emr.getFragmentActionWithCallback('lfhcforms', 'visit/visitStart', 'create',
					{ patientId: visit.patientId,
						selectedType: jq('#visit-visittype-drop-down').find(":selected").val() },
						function(data) {
							jq('#start-visit-with-visittype-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('disabled');
						visit.reloadPageWithoutVisitId();
					},function(err){
						visit.reloadPageWithoutVisitId();
					});
			},
			cancel: function() {
				visit.startVisitWithVisitTypeDialog.close();
			}
		}
	});

	visit.startVisitWithVisitTypeDialog.close();
}

visit.showStartVisitWithVisitTypeDialog = function(patientId) {
	visit.patientId = patientId;
	//console.log(patientId);
	if (visit.startVisitWithVisitTypeDialog == null) {
		visit.createStartVisitWithVisitTypeDialog();
	};
	visit.startVisitWithVisitTypeDialog.show();
};


$(document).ready(
	// override the existing 'Start Visit' button (in the Patient Dashboard - Encounter list) to trigger the new 'StartVisitWithVisitType' dialog instead of existing action.
	// this function is not called by an extension point (different from showStartVisitWithVisitTypeDialog() ).
	// Therefore, we need to retrieve the Patient ID from the URL parameters.
	function replaceStartVisitButton() {
	if (document.getElementById("noVisitShowVisitCreationDialog") != null) {
		$("#noVisitShowVisitCreationDialog").attr("href","");
		var classString = document.getElementById('noVisitShowVisitCreationDialog').className;
		var label = document.getElementById('noVisitShowVisitCreationDialog').text;
		$("#noVisitShowVisitCreationDialog").replaceWith('<span id="noVisitShowVisitCreationDialog" class="' + classString + '">' + label + '</span>' );
		$("#noVisitShowVisitCreationDialog").click(function () {
			var patientId = getParameterByName("patientId");
			visit.showStartVisitWithVisitTypeDialog(patientId);
		});
	}
})

// retrieve patientId from URL.
function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
	results = regex.exec(location.search);
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
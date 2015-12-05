var visit = visit || {};

visit.startVisitWithLocationDialog = null;

visit.createStartVisitWithLocationDialog = function() {
	visit.startVisitWithLocationDialog = emr.setupConfirmationDialog({
		selector: '#start-visit-with-location-dialog',
		actions: {
			confirm: function() {	
				emr.getFragmentActionWithCallback('lfhcforms', 'visit/visitWithLocationStart', 'create',
					{ patientId: visit.patientId,
						selectedLocation: jq('#visit-location-drop-down').find(":selected").val() },
						function(data) {
							jq('#start-visit-with-location-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('disabled');
						visit.reloadPageWithoutVisitId();
					},function(err){
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
	//console.log(patientId);
	if (visit.startVisitWithLocationDialog == null) {
		visit.createStartVisitWithLocationDialog();
	};
	visit.startVisitWithLocationDialog.show();
};


$(document).ready(
	// override the existing 'Start Visit' button to trigger the new 'StartVisitWithLocation' dialog instead of extisting action.
	// this function is not called by an extension point (different from showStartVisitWithLocationDialog() ).
	// Therefore, we need to retrieve the Patient ID from the URL parameters.
	function replaceStartVisitButton() {
	if (document.getElementById("noVisitShowVisitCreationDialog") != null) {
		$("#noVisitShowVisitCreationDialog").attr("href","");
		var classString = document.getElementById('noVisitShowVisitCreationDialog').className;
		var label = document.getElementById('noVisitShowVisitCreationDialog').text;
		$("#noVisitShowVisitCreationDialog").replaceWith('<span id="noVisitShowVisitCreationDialog" class="' + classString + '">' + label + '</span>' );
		$("#noVisitShowVisitCreationDialog").click(function () {
			var patientId = getParameterByName("patientId");
			visit.showStartVisitWithLocationDialog(patientId);
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
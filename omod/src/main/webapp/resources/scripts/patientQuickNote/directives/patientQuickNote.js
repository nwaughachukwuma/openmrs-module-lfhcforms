angular.module('lfhcforms.fragment.patientQuickNote')

.directive('lfhcformsQuickNote', ['ObsService', 'Obs', function(obsService, Obs){
	return {
		scope: {
			config: '=?',
			obsQuery: '='
		},
		restrict: 'E',
		templateUrl: '/' + OPENMRS_CONTEXT_PATH + '/ms/uiframework/resource/lfhcforms/partials/quickNote.html',
		controller: function($scope) {

			$scope.note = {
				text: ""
			}
			$scope.showRemoveButton = false;
			var draftObs = {};
			var obsArray = [];

			var resetFields = function () {
				$scope.note = {
					text: ""
				}
				$scope.showRemoveButton = true;
				draftObs = {};
				obsArray = [];
			}

			var loadNonVoidedObs = function() {
				obsService.getObs({
					v: 'full',
					patient: $scope.obsQuery.patient,
					concept: $scope.obsQuery.concept
				}).then(function(results) {
					resetFields();
					obsArray = results
					if (obsArray.length !=0) {
						$scope.note.text = obsArray[0].value;
					} else {
						$scope.showRemoveButton = false
					}
					initDraftObs();
				})
			}

			$scope.toggleRemoveButton = function() {
				if(obsArray.length != 0) {
					$scope.showRemoveButton = !$scope.showRemoveButton;
				}
			}

			var initDraftObs = function() {
				draftObs.person = $scope.obsQuery.patient;
				draftObs.concept = $scope.obsQuery.concept;
				draftObs.obsDatetime = moment();
				if (obsArray.length != 0) {
					draftObs.uuid = obsArray[0].uuid;
				}
			}

			$scope.updateNote = function() {
				var obsArray = [draftObs]
				draftObs.value = $scope.note.text;

				if (draftObs.value == "") {
					$scope.deleteNote();
				} else {
					Obs.save(draftObs).$promise.then(function () {
						loadNonVoidedObs();
					});				
				}
			} 			

			$scope.deleteNote = function() {
				Obs.delete(draftObs).$promise.then(function () {
					loadNonVoidedObs();
					resetFields();
				});

			}

			loadNonVoidedObs(); 
			initDraftObs();
		}
	};
}])
.run(function(editableOptions) {
	editableOptions.theme = 'bs2';
})
;
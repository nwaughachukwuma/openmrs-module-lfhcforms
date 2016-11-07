angular.module('lfhcforms.fragment.patientHeaderNote')

.directive('lfhcformsClickToEditObs', ['ObsService', 'Obs', function(obsService, Obs){
	return {
		scope: {
			module: '=',
			config: "="
		},
		restrict: 'E',
		// 'templateUrl:' field can not be set using a field from the $scope (eg: $scope.module.getPath()) because the field is not yet available in the $scope,
		// therefore we use 'template:' with 'ng-include' to display the 'templateUrl' as soon as it is available
		template: "<div ng-include='templateUrl'></div>",
		controller: function($scope) {

			$scope.templateUrl = '/' + $scope.module.getPartialsPath(OPENMRS_CONTEXT_PATH) + '/clickToEditObs.html'

        	// Loading i18n messages
			var msgCodes = [
			$scope.module.getProvider() + ".clickToEditObs.empty",
			$scope.module.getProvider() + ".clickToEditObs.saveError",
			$scope.module.getProvider() + ".clickToEditObs.loadError",
			$scope.module.getProvider() + ".clickToEditObs.deleteError",
			$scope.module.getProvider() + ".clickToEditObs.saveSuccess",
			$scope.module.getProvider() + ".clickToEditObs.deleteSuccess"
			]

			emr.loadMessages(msgCodes.toString(), function(msgs) {
				$scope.msgs = msgs;
			});

			// Initialize vars
			$scope.firstLoading = true;
			$scope.note = {
				text: ""
			}
			var draftObs = {};
			var obsArray = [];

			var loading = function (isLoading) {
				$scope.showSpinner = isLoading;
			}

			var loadNonVoidedObs = function() {
				loading(true);
				obsService.getObs({
					v: 'full',
					patient: $scope.config.patient,
					concept: $scope.config.concept
				}).then(function(results) {
					$scope.firstLoading = false;
					loading(false);
					obsArray = results
					if (obsArray.length !=0) {
						// Taking the first value if there is more than one obs (although this shouldn't happen)
						$scope.note.text = obsArray[0].value;
						$scope.showRemoveButton = true;
					} else {
						$scope.showRemoveButton = false;
					}
					initDraftObs();
				}, function(error) {
					loading(false);
					emr.errorMessage($scope.module.getProvider() + ".clickToEditObs.loadError");
				})
			}

			// Configure the new obs to be saved
			var initDraftObs = function() {
				draftObs = {};
				draftObs.person = $scope.config.patient;
				draftObs.concept = $scope.config.concept;
				draftObs.obsDatetime = moment();
				draftObs.value = $scope.note.text;
				if (obsArray.length != 0) {
					draftObs.uuid = obsArray[0].uuid;
				}
			}

			$scope.toggleRemoveButton = function() {
				if(obsArray.length != 0) {
					$scope.showRemoveButton = !$scope.showRemoveButton;
				}
			}

			$scope.updateObs = function() {
				loading(true);
				// Do not update the obs if the previous text value (draftObs.value) is the exact same as the newly entered text
				if (draftObs.value == $scope.note.text) {
					$scope.showRemoveButton = true;
					loadNonVoidedObs();
					return;
				}
				var obsArray = [draftObs]
				draftObs.value = $scope.note.text;
				// Empty text value is considered as deleting the note
				if (draftObs.value == "") {
					$scope.deleteObs();
				} else {
					$scope.showRemoveButton = true;
					Obs.save(draftObs).$promise.then(function (results) {
						loadNonVoidedObs();
						emr.successMessage($scope.module.getProvider() + ".clickToEditObs.saveSuccess");
					}, function(error) {
						loading(false);
						emr.errorMessage($scope.module.getProvider() + ".clickToEditObs.saveError");
					});				
				}
			} 			

			$scope.deleteObs = function() {
				loading(true);
				$scope.note.text = "";
				$scope.showRemoveButton = false;
				Obs.delete(draftObs).$promise.then(function (results) {
					loadNonVoidedObs();
					emr.successMessage($scope.module.getProvider() + ".clickToEditObs.deleteSuccess");
				}, function(error) {
					emr.errorMessage($scope.module.getProvider() + ".clickToEditObs.deleteError");
				});
			}

			loadNonVoidedObs();
			initDraftObs();
		}
	};
}])
.run(function(editableOptions) {
	// configure the 'xeditable' theme
	editableOptions.theme = 'bs2';
})
;
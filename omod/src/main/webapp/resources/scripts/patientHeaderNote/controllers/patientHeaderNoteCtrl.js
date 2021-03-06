angular.module('lfhcforms.fragment.patientHeaderNote').controller('patientHeaderNoteCtrl', ['$scope', '$window', '$log',
	function($scope, $window, $log) {

		if ($window.lfhcforms.patientHeaderNote.config.concept == null)  {
			$log.error("The concept passed in the config is 'null'. Check the server logs for more details")
		}

		$scope.config = {
			patient: $window.lfhcforms.patientHeaderNote.config.patient.uuid,
			concept: $window.lfhcforms.patientHeaderNote.config.concept.uuid
		};

		$scope.module = {
			'getProvider': function() {
				return "lfhcforms";
			},
			'getPath': function(openmrsContextPath) {
				return openmrsContextPath + '/' + this.getProvider();
			},
			'getPartialsPath': function(openmrsContextPath) {
				return openmrsContextPath + '/ms/uiframework/resource/' + this.getProvider() + '/partials';
			}
		}

	}])
;
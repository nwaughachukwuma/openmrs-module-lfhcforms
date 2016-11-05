angular.module('lfhcforms.fragment.patientQuickNote').controller('patientQuickNoteCtrl', ['$scope', '$window',
	function($scope, $window) {
		$scope.obsQuery = {
			patient: $window.config.patient.uuid,
			concept: $window.config.concept.uuid,
		};
	}])
;
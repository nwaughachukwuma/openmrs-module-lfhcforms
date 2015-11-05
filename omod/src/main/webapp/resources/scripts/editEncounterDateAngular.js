var myApp = angular.module('yourpageapp', ['ngDialog']);

myApp. // make sure to declare a dependency on ngDialog in your app
 
controller('YourPageCtrl', ['$scope', 'ngDialog', '$templateCache',
function($scope, ngDialog, $templateCache) {
    $scope.showEditEncounterDateDialog = function() {
        ngDialog.openConfirm({
            showClose: false,
            closeByEscape: true,
            closeByDocument: true,
            data: angular.toJson({
                helperData: "One way to push data into the dialog"
                         }),
             // in this example we defined this inline with <script type="ng-template"> but you can also give a url
            
        }).then(function(reason) {
            console.log("They chose reason: " + reason);
        }, function() {
            console.log("They clicked cancel");
        });
    };
}]);


app.controller('indexController', function($scope, $http, $window, facebookServices, Notification) {

$scope.enableNoConnected = true
    var kind = $.cookie("kindofuser")
    if (!angular.isUndefined(kind)) {
        $scope.enableNoConnected = false

        if (kind == "a") {
            $scope.enableAdmin = true
        }
        else if (kind == "su") {
            $scope.enableSimple = true
        }
        else {
            $scope.enableSeller = true
        }
    }

})
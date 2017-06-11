app.controller('headerController', function($scope, $http, $compile, $window, facebookServices, Notification) {

    $http.get('/assets/html/navbar.html').success(function(response) {
        $("#navbar").append($compile(response)($scope));
    })

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

    $scope.logout = function() {
        $.removeCookie("token", { path: '/' });
        $.removeCookie("kindofuser", { path: '/' });
        Notification.success('You will be soon redirected...');
        setTimeout(function(){
             $window.location.href = '/';
        }, 2000);
    }

})
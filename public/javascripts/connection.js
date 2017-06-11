app.controller('connection', function($scope, $http, $window, facebookServices, Notification) {

    facebookServices.initialize()

    $scope.linkFacebook = function() {
        facebookServices.connectFacebook().then(function(dataToken) {
            if (facebookServices.isReady()) {
                facebookServices.getInformation().then(function(dataFacebook) {
                    var rqt = {
                        method : 'POST',
                        url : '/signinFacebook',
                        data : $.param({
                            email: dataFacebook.email,
                            tokenFacebook: dataToken.access_token
                        }),
                        headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                    };
                    $http(rqt)
                        .success(function(data){
                            $.cookie("kindofuser", "su");
                            $window.location.href = '/';
                        })
                        .error(function(data){
                            facebookServices.clearCache()
                            $window.location.href = '/registration#?firstName=' + dataFacebook.name.split(" ")[0] +
                                '&lastName=' + dataFacebook.name.split(" ")[1] + '&email=' + dataFacebook.email + '&token=' + dataToken.access_token;
                        })
                });
            }
        })
    }

    $scope.submitForm = function() {
        var rqt = {
            method : 'POST',
            url : '/signin',
            data : $.param({
                email: $scope.user.email,
                password: $scope.user.password
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        $http(rqt)
            .success(function(data){
                if (data.kindOfUser == "Simple User") {
                    $.cookie("kindofuser", "su");
                }
                else if (data.kindOfUser == "Admin") {
                    $.cookie("kindofuser", "a");
                }
                else {
                    $.cookie("kindofuser", "sc");
                }
                $window.location.href = '/';
            })
            .error(function(data){
                Notification.error("Email or password incorrect")
            })
    };

})


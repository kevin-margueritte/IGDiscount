app.controller('modifyAccount', function($scope, $http, $window, $location, facebookServices, Notification) {

    $scope.enableSimpleUserForm = false
    $scope.enableSellerCompanyForm = false
    $scope.enableAdminForm = false
    $scope.kindOkUser

    facebookServices.initialize()

    angular.element(document).ready(function() {
        $scope.id = $location.search().id
        $scope.kindOfUser = $location.search().kindOfUser
        if (angular.isUndefined($scope.id) && angular.isUndefined($scope.kinfOfUser)) {
            var rqt = {
                method : 'GET',
                url : '/kindOfUser',
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };
            $http(rqt)
                .success(function(data){
                    $scope.id = data.id
                    $scope.kindOfUser = data.kindOfUser
                    if (data.kindOfUser == "Simple User") {
                        $scope.enableSimpleUserForm = true
                        getInfoSimpleUser()
                    }
                    else if (data.kindOfUser == "Seller Company") {
                        $scope.enableSellerCompanyForm = true
                        getInfoSellerCompany()
                    }
                    else {
                        $scope.enableAdminForm = true
                        getInfoAdmin()
                    }
                })
                .error(function(data){
                    $window.location.href = '/';
                })
        }
        else {
            if ($scope.kindOfUser == "su") {
                $scope.enableSimpleUserForm = true
                getInfoSimpleUser()
            }
            else if ($scope.kindOfUser == "sc") {
                $scope.enableSellerCompanyForm = true
                getInfoSellerCompany()
            }
            else {
                $scope.enableAdminForm = true
                getInfoAdmin()
            }
        }
    })

    $scope.submitFormPassword = function() {
        if ($scope.kindOfUser == "Simple User") {
            var url = '/simpleUser/'+$scope.id+'/password'
        }
        else if ($scope.kindOfUser == "Admin") {
            var url = '/admin/'+$scope.id+'/password'
        }
        else {
            var url = '/sellerCompany/'+$scope.id+'/password'
        }
        var rqt = {
            method : 'PUT',
            data : $.param({
                     password : $scope.user.password,
                 }),
            url : url,
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        $http(rqt)
            .success(function(data){
                Notification.success("Password updated!")
            })
    }

    var getInfoSimpleUser = function() {
        var rqt = {
            method : 'GET',
            url : '/simpleUser/'+$scope.id,
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        $http(rqt)
            .success(function(data){
                $scope.simpleUser = {}
                $scope.simpleUser.firstName = data.firstName
                $scope.simpleUser.email = data.email
                $scope.simpleUser.lastName = data.lastName
                $scope.simpleUser.adress = data.street
                $scope.simpleUser.city = data.city
                $scope.simpleUser.streetNumber = parseInt(data.streetNumber)
                $scope.simpleUser.postalCode = parseInt(data.postalCode)
                if (data.logFacebook) {
                    $scope.disableFacebook = true
                }
                else {
                    $scope.disableFacebook = false
                }
            })
            .error(function(data){
                $window.location.href = '/';
            })
    }

    var getInfoSellerCompany = function() {
                var rqt = {
                    method : 'GET',
                    url : '/sellerCompany/'+$scope.id,
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                };
                $http(rqt)
                    .success(function(data){
                        $scope.sellerCompany = {}
                        $scope.sellerCompany.email = data.email
                        $scope.sellerCompany.companyName = data.companyName
                        $scope.sellerCompany.siret = parseInt(data.siret)
                        $scope.sellerCompany.adress = data.street
                        $scope.sellerCompany.city = data.city
                        $scope.sellerCompany.streetNumber = parseInt(data.streetNumber)
                        $scope.sellerCompany.postalCode = parseInt(data.postalCode)
                    })
                    .error(function(data){
                        $window.location.href = '/';
                    })
            }

    var getInfoAdmin = function() {
                var rqt = {
                    method : 'GET',
                    url : '/admin/'+$scope.id,
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                };
                $http(rqt)
                    .success(function(data){
                        $scope.admin = {}
                        $scope.admin.email = data.email
                        $scope.admin.firstName = data.firstName
                        $scope.admin.lastName = data.lastName
                    })
                    .error(function(data){
                        $window.location.href = '/';
                    })
            }

    $scope.submitAdminForm = function() {
        var rqt = {
            method : 'PUT',
            url : '/admin/'+$scope.id,
            data : $.param({
                email: $scope.admin.email,
                firstName : $scope.admin.firstName,
                lastName : $scope.admin.lastName,
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        $http(rqt)
            .success(function(data){
                Notification.success("Your data has been updated")
            })
    };

    $scope.submitSimpleUserForm = function() {
            var rqt = {
                method : 'PUT',
                url : '/simpleUser/'+$scope.id,
                data : $.param({
                    email: $scope.simpleUser.email,
                    firstName : $scope.simpleUser.firstName,
                    lastName : $scope.simpleUser.lastName,
                    street : $scope.simpleUser.adress,
                    streetNumber : $scope.simpleUser.streetNumber,
                    postalCode : $scope.simpleUser.postalCode,
                    city : $scope.simpleUser.city
                }),
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };
            $http(rqt)
                .success(function(data){
                    Notification.success("Your data has been updated")
                })
        };

    $scope.submitSellerCompanyForm = function() {
                var rqt = {
                    method : 'PUT',
                    url : '/sellerCompany/'+$scope.id,
                    data : $.param({
                        email: $scope.sellerCompany.email,
                        siret : $scope.sellerCompany.siret,
                        companyName : $scope.sellerCompany.companyName,
                        street : $scope.sellerCompany.adress,
                        streetNumber : $scope.sellerCompany.streetNumber,
                        postalCode : $scope.sellerCompany.postalCode,
                        city : $scope.sellerCompany.city
                    }),
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                };
                $http(rqt)
                    .success(function(data){
                        Notification.success("Your data has been updated")
                    })
            };
})
app.controller('adminAccount', function($scope, $filter, $http, $window, Notification, usSpinnerService) {

    $scope.userRows = []

    angular.element(document).ready(function() {
        var rqtAdmin = {
            method : 'GET',
            url : '/admin',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        var rqtSimpleUser = {
            method : 'GET',
            url : '/simpleUser',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        var rqtSellerCompany = {
            method : 'GET',
            url : '/sellerCompany',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        $scope.jsonUsers = []
        usSpinnerService.spin('spinner-1');
        $scope.startcounter++;

        $http(rqtAdmin).success(function(dataAdmin){
            for (var i = 0; i < dataAdmin.length; i++) {
                $scope.userRows.push({
                    id: dataAdmin[i].id,
                    name : dataAdmin[i].firstName + " " + dataAdmin[i].lastName,
                    email : dataAdmin[i].email,
                    kindOfUser : "Admin"
                });
            }
            $http(rqtSimpleUser).success(function(dataSimpleUser){
                for (var i = 0; i < dataSimpleUser.length; i++) {
                    $scope.userRows.push({
                        id: dataSimpleUser[i].id,
                        name : dataSimpleUser[i].firstName + " " + dataSimpleUser[i].lastName,
                        email : dataSimpleUser[i].email,
                        kindOfUser : "Simple User"
                    });
                }
                $http(rqtSellerCompany).success(function(dataSellerCompany){
                    for (var i = 0; i < dataSellerCompany.length; i++) {
                        $scope.userRows.push({
                            id: dataSellerCompany[i].id,
                            name : dataSellerCompany[i].companyName,
                            email : dataSellerCompany[i].email,
                            kindOfUser : "Seller Company"
                        });
                    }
                    usSpinnerService.stop('spinner-1');
                    $scope.groupProperty = "kindOfUser"
                })
            })
        }).error(function(data){
              $window.location.href = '/';
          })
    });

    $scope.displayedCollection = [].concat($scope.userRows);

    //#?id=1&kindOfUser=su

    $scope.update = function(row) {
        if (row.kindOfUser == "Simple User") {
            $window.location.href = '/account#?id='+row.id+'&kindOfUser=su';
        }
        else if (row.kindOfUser == "Seller Company") {
            $window.location.href = '/account#?id='+row.id+'&kindOfUser=sc';
        }
        else {
            $window.location.href = '/account#?id='+row.id+'&kindOfUser=admin';
        }
    }

    $scope.removeRow = function removeRow(row) {
        var rqt
        if (row.kindOfUser == "Simple User") {
            rqt = {
                method : 'DELETE',
                url : '/simpleUser/'+row.id,
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };
        }
        else if (row.kindOfUser == "Seller Company") {
            rqt = {
                method : 'DELETE',
                url : '/sellerCompany/'+row.id,
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };
        }
        else {
            rqt = {
                method : 'DELETE',
                url : '/admin/'+row.id,
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };
        }
        $http(rqt).success(function(data){
            Notification.success('User deleted');
            var index = $scope.displayedCollection.indexOf(row);
            if (index !== -1) {
                $scope.displayedCollection.splice(index, 1);
            }
        });

    }
})
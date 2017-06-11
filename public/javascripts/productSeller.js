app.controller('productSeller', function($scope, $filter, $http, $window, usSpinnerService) {

    $scope.productRows = []
    $scope.product = {}

    angular.element(document).ready(function() {

        var rqt = {
            method : 'GET',
            url : '/kindOfUser',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        $http(rqt).success(function(data){
            usSpinnerService.spin('spinner-1');
            $scope.startcounter++;
            if (data.kindOfUser != "Seller Company") {
                $window.location.href = '/';
            }
            else {
                var rqt = {
                    method : 'GET',
                    url : '/sellerCompany/'+ data.id +'/product/available',
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                };

                $http(rqt).success(function(data){
                    var xhr = new XMLHttpRequest();
                    for (var i = 0; i < data.length; i++) {
                        var arrayBuffer = data[i].image.content;
                        var bytes = new Uint8Array(arrayBuffer);
                        var blob = new Blob( [ bytes ], { type: data[i].image.mime } );
                        var urlCreator = window.URL || window.webkitURL;
                        var imageUrl = urlCreator.createObjectURL( blob );


                        $scope.productRows.push({
                            id : data[i].id,
                            image: imageUrl,
                            name : data[i].name,
                            price : (Math.round(data[i].price*100)/100) + "€",
                            quantity : data[i].quantity,
                            description : data[i].description
                        });
                    }
                    usSpinnerService.stop('spinner-1');
                 })
            }
        })
    })

    $scope.update = function(row) {
        $window.location.href = '/product/update/#?id='+row.id
    }

    $scope.removeRow = function removeRow(row) {
        var rqt = {
            method : 'DELETE',
            url : '/product/'+row.id,
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        }
        $http(rqt).success(function(data){
            var index = $scope.displayedCollection.indexOf(row);
            if (index !== -1) {
                $scope.displayedCollection.splice(index, 1);
            }
            Notification.success('Product ' + row.name + ' deleted');
        });
    }

    $scope.displayedCollection = [].concat($scope.productRows);


})
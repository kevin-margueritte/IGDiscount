app.controller('listProduct', function($scope, $filter, $http, $window, usSpinnerService, Notification) {

    $scope.orderRows = []
    $scope.order = {}

    angular.element(document).ready(function() {

        var rqtKindOfUser = {
            method : 'GET',
            url : '/kindOfUser',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        usSpinnerService.spin('spinner-1');
        $scope.startcounter++;

        $http(rqtKindOfUser).success(function(data){
            if (data.kindOfUser == "Simple User") {
                var rqtProduct = {
                    method : 'GET',
                    url : '/simpleUser/'+ data.id +'/order',
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                }

                $scope.idSimpleUser = data.id

                $http(rqtProduct).success(function(data){
                    var xhr = new XMLHttpRequest();
                    for (var i = 0; i < data.length; i++) {
                        var arrayBuffer = data[i].product.image.content;
                        var bytes = new Uint8Array(arrayBuffer);
                        var blob = new Blob( [ bytes ], { type: data[i].product.image.mime } );
                        var urlCreator = window.URL || window.webkitURL;
                        var imageUrl = urlCreator.createObjectURL( blob );

                        var a = new Date(data[i].stateDate);
                        var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
                        var year = a.getFullYear();
                        var month = months[a.getMonth()];
                        var date = a.getDate();
                        var hour = a.getHours();
                        var min = a.getMinutes();

                        $scope.orderRows.push({
                            id : data[i].id,
                            seller : data[i].product.seller.companyName,
                            day : date + " " + month + " " + year,
                            month : month + " " + year,
                            year : year,
                            image: imageUrl,
                            date: date + " " + month + " " + year + ", " + hour + ":" +min,
                            name : data[i].product.name,
                            price : (Math.round(data[i].product.price*100)/100),
                            quantity : data[i].quantity,
                            state : data[i].state
                        });
                    }
                    usSpinnerService.stop('spinner-1');
                    $scope.groupProperty = 'day'
                })

            }
         })
    })

     $scope.displayedCollection = [].concat($scope.orderRows);


})
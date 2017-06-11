app.controller('listProduct', function($scope, $filter, $http, $window, usSpinnerService, Notification) {

    angular.element(document).ready(function() {
        $scope.all = false
        $scope.load()
    })

    $scope.load = function() {
        var rqtKindOfUser = {
            method : 'GET',
            url : '/kindOfUser',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        usSpinnerService.spin('spinner-1');
        $scope.startcounter++;

        $http(rqtKindOfUser).success(function(data){
            $scope.orderRows = []
            $scope.order = {}
            if (data.kindOfUser == "Admin") {
                var rqtProduct = {
                    method : 'GET',
                    url : '/order/confirmed',
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                }

                $scope.idSimpleUser = data.id

                $http(rqtProduct).success(function(data){
                    console.log(data)
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
                            customerId : data[i].simpleUser.id,
                            customer : data[i].simpleUser.firstName + " " + data[i].simpleUser.lastName,
                            image: imageUrl,
                            seller : data[i].product.seller.companyName,
                            day : date + " " + month + " " + year,
                            date: date + " " + month + " " + year + ", " + hour + ":" +min,
                            name : data[i].product.name,
                            price : (Math.round((data[i].product.price*data[i].quantity)*100)/100),
                            disabled : false
                        });
                    }
                    $scope.groupProperty = "day"
                    usSpinnerService.stop('spinner-1');

                })

            }
         })
     }

    $scope.confirm = function(row) {

        var rqt = {
            method : 'PUT',
            url : '/order/' + row.id + '/paid',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        row.disabled = true
        Notification.info({message: 'Waiting for payment : '+ row.price +' €', delay: 4000});
        setTimeout(function(){
            $http(rqt).success(function(data){
                Notification.success('Account "' + row.seller + '" has been credited with ' + row.price + ' €');
            })
        }, 4000);
    }

    $scope.confirmAll = function(rows) {
        for (var i = 0; i < rows.length; i++) {
            $scope.confirm(rows[i])
        }
    }

    $scope.displayedCollection = [].concat($scope.orderRows);


})
app.controller('listProduct', function($scope, $filter, $http, $window, usSpinnerService, Notification) {

    $scope.productRows = []
    $scope.product = {}

    angular.element(document).ready(function() {

        var rqtProduct = {
            method : 'GET',
            url : '/product/available',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        var rqtKindOfUser = {
            method : 'GET',
            url : '/kindOfUser',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        usSpinnerService.spin('spinner-1');
        $scope.startcounter++;

        $http(rqtProduct).success(function(data){
            var xhr = new XMLHttpRequest();
            for (var i = 0; i < data.length; i++) {
                var arrayBuffer = data[i].image.content;
                var bytes = new Uint8Array(arrayBuffer);
                var blob = new Blob( [ bytes ], { type: data[i].image.mime } );
                var urlCreator = window.URL || window.webkitURL;
                var imageUrl = urlCreator.createObjectURL( blob );


                $scope.productRows.unshift({
                    id : data[i].id,
                    image: imageUrl,
                    imageName : data[i].image.name,
                    seller: data[i].seller.companyName,
                    name : data[i].name,
                    price : (Math.round(data[i].price*100)/100) + "€",
                    quantity : data[i].quantity,
                    description : data[i].description,
                    external : "false"
                });
            }
            usSpinnerService.stop('spinner-1');
            $scope.filterProperty = "false"
         })

         var rqtSellebook = {
             method : 'GET',
             url : 'https://sellbook-polytech.eu-gb.mybluemix.net/products',
             headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
         };
         Notification.success('Enable external sellers to show theirs products');

         $http(rqtSellebook).success(function(data){
             for (var i = 0; i < data.length; i++) {
                 $scope.productRows.unshift({
                     id : data[i].idProduct,
                     image: "images/no-image.png",
                     imageName : "no image",
                     seller: "Sellbook",
                     name : data[i].nameProduct,
                     price : (Math.round(data[i].priceSeller*100)/100) + "€",
                     quantity : data[i].quantityStock,
                     description : data[i].descriptionProduct,
                     external : "true"
                 });
             }
          }).error(function (error, status){
                Notification.error('Sellbook market is unavailable');
             });

          var rqtAwiProject = {
               method : 'GET',
               url : 'https://awimarket.herokuapp.com/api/products',
               headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
           };

           $http(rqtAwiProject).success(function(data){
               for (var i = 0; i < data.length; i++) {
                   $scope.productRows.unshift({
                       id : data[i].product.id,
                       image: data[i].product.image,
                       imageName : "image",
                       seller: "AWIProject",
                       name : data[i].product.Name,
                       price : (Math.round(data[i].product.price*100)/100) + "€",
                       quantity : data[i].product.quantity,
                       description : data[i].product.desc,
                       external : "true"
                   });
               }
            }).error(function (error, status){
                Notification.error('AWI market is unavailable');
             });

         $http(rqtKindOfUser).success(function(data){
            if (data.kindOfUser == "Simple User") {
                $scope.productBasket = []
                $scope.cartMode = true
                $scope.idSimpleUser = data.id
                var rqtBasket = {
                    method : 'GET',
                    url : '/simpleUser/' + data.id + '/product',
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                };

                $http(rqtBasket).success(function(data){
                    for (var i = 0; i < data.length; i++) {
                        $scope.productBasket.push(data[i].product.id)
                    }
                })
            }
         })

    })

    $scope.submitForm = function(idProduct) {
        var rqtAddCart = {
            method : 'POST',
            url : '/simpleUser/'+ $scope.idSimpleUser +'/product/' + idProduct,
            data : $.param({
                quantity: $scope.quantityCart
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        var rqtUpdateCart = {
            method : 'PUT',
            url : '/simpleUser/'+ $scope.idSimpleUser +'/product/' + idProduct +'/quantity',
            data : $.param({
                quantity: $scope.quantityCart
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        if ($scope.productBasket.indexOf(idProduct) >= 0) {
            $http(rqtUpdateCart).success(function(data){
                Notification.success('Product updated into the cart');
            })
        }
        else {
            $http(rqtAddCart).success(function(data){
                Notification.success('Product added into the cart');
                $scope.productBasket.push({
                    id : idProduct
                })
            })
        }
    }

    $scope.show = function(row) {
        $scope.quantityCart = 1
        $scope.product.id = row.id
        $scope.product.name = row.name
        $scope.product.image = row.image
        $scope.product.price = row.price
        $scope.product.quantity = row.quantity
        $scope.product.seller = row.seller
        $scope.product.description = row.description
        $scope.product.external = row.external
        $('#modal-product').modal();
        $('#modal-product').modal('show');
    }

    $scope.externalSeller = function() {
        console.log("toto")
        $window.location.href = 'https://sellbook-polytech.eu-gb.mybluemix.net';
    }

     $scope.displayedCollection = [].concat($scope.productRows);


})
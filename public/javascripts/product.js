app.controller('product', function($scope, $http, $window, $location, usSpinnerService, Notification) {

    var dropzoneCreate;
    var dropzoneUpdate;
    $scope.showError = false;
    $scope.showWaiting = false;
    $scope.disable= true;
    $scope.product = {};
    $scope.creatingMode = true

    $scope.startSpin = function() {
        usSpinnerService.spin('spinner-1');
        $scope.startcounter++;
    };

    angular.element(document).ready(function () {
        $scope.id = $location.search().id
        dropzoneCreate = new Dropzone("#dropzoneProduct",{
          paramName: 'image',
          url:'/product',
          method: 'POST',
          maxFilesize: 0.1,
          maxFiles: 1,
          autoProcessQueue: false,
          acceptedFiles: '.jpg, .png, .jpeg',
          removedfile: function(file) {
            setDisable(true)
            var _ref;
            return (_ref = file.previewElement) != null ? _ref.parentNode.removeChild(file.previewElement) : void 0;
          },
          maxfilesexceeded: function(file) {
              this.removeAllFiles();
              this.addFile(file);
          },
          accept: function (file, done) {
            setDisable(false)
            done()
          },
          sending: function(file, xhr, formData) {
            formData.append("description", $scope.product.description)
            formData.append("name", $scope.product.name)
            formData.append("price", Math.round($scope.product.price*100)/100)
            formData.append("quantity", parseInt($scope.product.quantity))
            $scope.showWaiting = true;

          },
          success: function(file, response){
            Notification.success('Product created! <br> You will be soon redirected...');
            setTimeout(function(){
                 $window.location.href = '/product/manage/sellercompany';
            }, 2000);
            return response;
          },
          addRemoveLinks: true,
          dictDefaultMessage: 'Please put a JPG/JPEG or PNG picture (less than 100 Ko)'
        });

        if (!angular.isUndefined($scope.id)) {
            $scope.creatingMode = false

            dropzoneUpdate = new Dropzone("#dropzoneUpdate",{
              paramName: 'image',
              url:'/product/'+$scope.id+'/image',
              method: 'PUT',
              maxFilesize: 0.1,
              maxFiles: 1,
              autoProcessQueue: true,
              acceptedFiles: '.jpg, .png, .jpeg',
              removedfile: function(file) {
                setDisable(true)
                var _ref;
                return (_ref = file.previewElement) != null ? _ref.parentNode.removeChild(file.previewElement) : void 0;
              },
              maxfilesexceeded: function(file) {
                this.removeAllFiles();
                this.addFile(file);
              },
              accept: function (file, done) {
                setDisable(false)
                done()
              },
              sending: function(file, xhr, formData) {},
              addRemoveLinks: true,
              dictDefaultMessage: 'Please put a JPG/JPEG or PNG picture (less than 100 Ko)'
            });

            var rqt = {
                method : 'GET',
                url : '/product/' + $scope.id,
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };

            $http(rqt).success(function(data){
                $scope.product.description = data.description
                $scope.product.name = data.name
                $scope.product.price = Math.round(data.price*100)/100
                $scope.product.quantity = data.quantity
                var arrayBuffer = data.image.content;
                var bytes = new Uint8Array(arrayBuffer);
                var blob = new Blob( [ bytes ], { type: data.image.mime } );
                var urlCreator = window.URL || window.webkitURL;
                var imageUrl = urlCreator.createObjectURL( blob );

                var mockFile = { name: data.name, accepted: true };
                dropzoneUpdate.emit("addedfile", mockFile);
                dropzoneUpdate.createThumbnailFromUrl(mockFile, imageUrl);
                dropzoneUpdate.emit("success", mockFile);
                dropzoneUpdate.emit("complete", mockFile);
                dropzoneUpdate.files.push(mockFile);
                $scope.disable = false
            })

        }
    })

    setDisable = function(b) {
        $scope.disable = b
        $scope.$apply()
    }

    $scope.submitForm = function() {
        $scope.startSpin();
        if ($scope.creatingMode) {
            dropzoneCreate.processQueue()
        }
        else {
            var rqtUpdate = {
                method : 'PUT',
                data : $.param({
                         description : $scope.product.description,
                         name : $scope.product.name,
                         price : Math.round($scope.product.price*100)/100,
                         quantity : parseInt($scope.product.quantity)
                     }),
                url : '/product/' + $scope.id,
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };

            $http(rqtUpdate).success(function(data){
                Notification.success('Product updated! <br> You will be soon redirected...');
                setTimeout(function(){
                     $window.location.href = '/';
                }, 2000);
            })
        }
    }
})
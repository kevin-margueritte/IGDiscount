angular.module('app.facebookServices', []).factory('facebookServices', function($q) {

    var authorizationResult = false;

    return {
        initialize: function() {
            OAuth.initialize('kwpfPLWbfwLrVCtpuJNpJPpxKf0', {cache:true});
            authorizationResult = OAuth.create('facebook');
        },
        isReady: function() {
            return (authorizationResult);
        },
        connectFacebook: function() {
            var deferred = $q.defer();
            OAuth.popup('facebook', {cache:true}, function(error, result) {
                if (!error) {
                    authorizationResult = result;
                    deferred.resolve(result);
                }
            });
            return deferred.promise;
        },
        clearCache: function() {
            OAuth.clearCache('facebook');
            authorizationResult = false;
        },
        getInformation: function () {
            var deferred = $q.defer();
            var promise = authorizationResult.get('/me?fields=id,name,email').done(function(data) {
                deferred.resolve(data)
            });
            return deferred.promise;
        }
    }
});
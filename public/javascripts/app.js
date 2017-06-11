var app = angular.module('app', ['app.facebookServices', 'ui-notification', 'smart-table', 'angularSpinner', 'angular.filter'])
.config( [
    '$compileProvider',
    function( $compileProvider )
    {
        $compileProvider.imgSrcSanitizationWhitelist(/^\s*(https?|tel|ftp|file|blob|content|ms-appx|x-wmapp0):|data:image\//);
    }
]);
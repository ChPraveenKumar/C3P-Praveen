var C3PApp = angular.module("C3PApp", ['smart-table', 'ui.router','ui.bootstrap', 'ngSanitize','moment-picker']);
/*Constants regarding user login defined here
$rootScope.$on("$routeChangeStart", function (event, next, current) {
    if (sessionStorage.restorestate == "true") {
        $rootScope.$broadcast('restorestate'); //let everything know we need to restore state
        sessionStorage.restorestate = false;
    }
});

//let everthing know that we need to save state now.
window.onbeforeunload = function (event) {
    $rootScope.$broadcast('savestate');
};*/


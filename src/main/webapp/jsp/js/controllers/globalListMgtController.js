C3PApp.controller('globalListMgtController',['$scope','$rootScope','$http', '$state', 'activeTabs','$stateParams',function($scope, $rootScope, $http, $state, activeTabs,$stateParams) {
	activeTabs.activeTabsFunc('Admin');
	$rootScope.menubar = activeTabs.menubar;
	
	$scope.deviceDetails = false;
	$scope.customerDetails = false;
	$scope.onAttributeChange = function(){
		
		if($scope.attributeName == 'Vendor' || $scope.attributeName == 'Device Type' || $scope.attributeName == 'Model' || $scope.attributeName == 'OS' || $scope.attributeName == 'OS Version'){
			$scope.customerDetails = false;
			$scope.deviceDetails = true;
		}else{
			$scope.deviceDetails = false;
			$scope.customerDetails = true;
		}
	
		} 
}])
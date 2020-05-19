C3PApp.controller('deviceMgtController',['$scope','$rootScope','$http', '$state','activeTabs',function($scope, $rootScope, $http, $state, activeTabs) {
	
	/// Higlighted Active tab goes here--------
	activeTabs.activeTabsFunc('Admin');
	$rootScope.menubar = activeTabs.menubar;
	/// Higlighted Active tab goes here--------
	

	$scope.deviceMgtUsername="";
	$scope.deviceMgtPassword="";
	$scope.submitDeviceMgtData = function(evt) {
		console.log("In method")
		console.log(JSON
				.stringify($scope.configuartion))
		var Data = {
						username : $scope.deviceMgtUsername,
						password : $scope.deviceMgtPassword
					};
			$http(
				{

					url : localStorage.getItem('path') + "/addDeviceManagementUser/updateRouterCredential",

					//url : "http://localhost:8023/addDeviceManagementUser/user",
					method : "POST",
					data : JSON.stringify(Data)

				}).then(function(response) {
			console.log("In response")
			console.log(response)
			bootbox.confirm({
				title : 'Request Status',
				message : response.data.entity.Message,
				buttons : {
						
					confirm : {
						label : 'Ok',
						className : 'btn-default'
					},

				},
				callback : function(result) {
					if (result)
					 $state.go('DeviceManagement');
					 
				}
			});
			console.log(response)

		})
		
	}
	$scope.getUserDetails = function(){
	$http(
			{

				url : localStorage.getItem('path') + "/addDeviceManagementUser/getRouterCredentials",
				method : "GET"
			
			}).then(function(response) {
		        $scope.deviceMgtUsername =response.data.entity.username
		        $scope.deviceMgtPassword =response.data.entity.password
				console.log(response);

	})
	}
	$scope.getUserDetails();
	
} ])
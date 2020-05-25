C3PApp.controller('dashboardController',['$scope','$rootScope','$http', '$state', 'activeTabs',function($scope, $rootScope, $http, $state, activeTabs) {
	
	activeTabs.activeTabsFunc('Dashboard');
	$rootScope.menubar = activeTabs.menubar;
	
	
    $scope.SearchItems = [ "Request ID", "Region",
            "Vendor", "Model", "Status" ];
    $scope.searchField = $scope.SearchItems[0];
    $scope.searchInput = ""
    $scope.sortKey = "";
    $scope.reverse = false;
    $scope.requestsList = [];
    $scope.rowCollection = '';
    
    
    $scope.getUpdatedGridData = function(){  
    	$scope.requestsList = [];
    	$scope.getData();
    };
    
    $scope.getDashboardConfigFlag = function(flag){
    	$rootScope.disableModifyScheduleButton = flag;
    	localStorage.setItem("flagForButton", $rootScope.disableModifyScheduleButton);
    }

    $scope.getData = function() {

        $http({
    		url : localStorage.getItem('path') + "/GetAllRequestDashboardViewService/GetAllDashboardViewJSON ",

            //url : "http://localhost:8023/GetAllRequestService/GetAllRequests",
            method : "GET"

        }).then(function(response) {
          
            console.log(response)

            $scope.requestsList = JSON.parse(response.data.entity.output);
            $scope.rowCollection = JSON.parse(response.data.entity.output);
            $scope.successRequests = JSON.parse(response.data.entity.SuccessfulRequests);
            $scope.failureRequests = JSON.parse(response.data.entity.FailureRequests);
            $scope.TotalRequests = JSON.parse(response.data.entity.TotalRequests);
            $scope.MaxElapsedTime=response.data.entity.MaxElapsedTime;
            $scope.MinElapsedTime=response.data.entity.MinElapsedTime;
            $scope.AvgElapsedTime=response.data.entity.AvgElapsedTime;
            console.log($scope.maxElapsedTime);
            console.log($scope.minElapsedTime);
            console.log($scope.avgElapsedTime);
            if ($scope.requestsList.length > 0){
            	$scope.clearSearch();
        	}
        })

    }

    $scope.getData();
    
    $scope.clearSearch = function() {
    	$scope.searchInput="";
    	$scope.errorMessege = false;
    }   
    $scope.searchRequest = function() {
       
        var Data = {
            key : $scope.searchField,
            value : $scope.searchInput,
            page: 'dashboard'
        };
        $http({
        		url : localStorage.getItem('path') + "/SearchRequestService/search",
                //url : "http://localhost:8023/SearchRequestService/search",
                method : "POST",
                data : JSON.stringify(Data),
                headers : {
                    'Content-Type' : 'application/json'
                }

            }).then(function(response) {
            	//$scope.requestsList ="";
            	$scope.errorMessege = false;
            	$scope.requestsList = JSON.parse(response.data.entity.output);
            	if ($scope.requestsList == ""){
            		$scope.errorMessege = true;
            	}
        })
    }
  
} ])
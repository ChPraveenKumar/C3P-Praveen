C3PApp.controller('viewCMController',['$scope','$rootScope','$http', '$state', 'activeTabs',function($scope, $rootScope, $http, $state, activeTabs) {
	
	/// Higlighted Active tab goes here--------
		activeTabs.activeTabsFunc('Configuration');
		$rootScope.menubar = activeTabs.menubar;
	/// Higlighted Active tab goes here--------
	
    $scope.SearchItems = [ "Request ID", "Model", "Status" ];
    $scope.searchField = $scope.SearchItems[0];
    $scope.searchInput = ""
    $scope.sortKey = "";
    $scope.reverse = false;
    $scope.requestsList = "";
    $scope.rowCollection = '';
    
    
    $scope.getUpdatedGridData = function(){  
    	$scope.requestsList = [];
    	$scope.getData();
    };
    
    $scope.getViewConfigFlag = function(flag){
    	$rootScope.disableModifyScheduleButton = flag;
    	localStorage.setItem("flagForButton", $rootScope.disableModifyScheduleButton);
    }
    
    $scope.getData = function() {

        $http({
        	url : localStorage.getItem('path') + "/GetAllRequestTreeJSONService/GetAllRequestTreeJSON",
            //url : "http://localhost:8023/GetAllRequestService/GetAllRequests",
            method : "GET"

        }).then(function(response) {
          
            $scope.requestsList = JSON.parse(response.data.entity.output);
            $scope.rowCollection = JSON.parse(response.data.entity.output);
            $scope.successRequests = JSON.parse(response.data.entity.SuccessfulRequests);
            $scope.failureRequests = JSON.parse(response.data.entity.FailureRequests);
            $scope.TotalRequests = JSON.parse(response.data.entity.TotalRequests);
              console.log($scope.requestsList);
        })

    }

    $scope.getData();
    $scope.searchRequest = function() {
    	if($scope.searchInput == ''){
    		$scope.errorMessege = false;
    		 $scope.getData();
    	}
    	else{
        var Data = {
            key : $scope.searchField,
            value : $scope.searchInput,
            page : 'viewpage'
        };
        $http({
        		url : localStorage.getItem('path') + "/SearchRequestService/search",
               // url : "http://localhost:8023/SearchRequestService/search",
                method : "POST",
                data : JSON.stringify(Data),
                headers : {
                    'Content-Type' : 'application/json'
                }

            }).then(function(response) {
            //$scope.requestsList="";
             if (response.data.entity.output == ""){
             		$scope.errorMessege = true;
             		 $scope.requestsList = response.data.entity.output;
             	}else{
             		$scope.errorMessege = false;
             		 $scope.requestsList = JSON.parse(response.data.entity.output);
             	}
            
               
        })
    	}
    }
    
     /*$scope.sort = function(sortkey,event) {
		 var id = event.target.id;		 
	        $.each($scope.requestsList, function(idx, val){
	        	if (idx < $scope.requestsList.length - 1){
	        		if ($scope.requestsList[idx][sortkey] !== $scope.requestsList[idx+1][sortkey]){
						$('#'+ id).toggleClass('sort-ascent').toggleClass('sort-descent');		 
						$scope.reverse = !$scope.reverse;
						$scope.sortKey = sortkey;
						return false;
					}
	        	}
	        })
	        //console.log($scope.requestsList)
	    }// -->Custom directive (sortTable) made for sort in customDirective.js file*/
} ])
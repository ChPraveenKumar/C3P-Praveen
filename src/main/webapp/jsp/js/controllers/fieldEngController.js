C3PApp.controller('fieldEngController',['$scope','$rootScope','$http','$state', 'activeTabs','$stateParams',function($scope, $rootScope, $http, $state, activeTabs,$stateParams) {
	
	$scope.SearchItems = [ "Select","Request ID", "Status",
	                        "Approver"];
	$scope.searchField = $scope.SearchItems[0];
    $scope.searchInput = ""
    $scope.sortKey = "";
    $scope.reverse = false;
    $scope.requestsList = "";
    $scope.rowCollection = '';
	$scope.isEditable = $stateParams.isEditable;
	
	activeTabs.activeTabsFunc('Admin');
	$rootScope.menubar = activeTabs.menubar;
	$scope.searchRequest = function() {
	    if ($scope.searchField == "Select"){
	    	$scope.pendingTemplateList();
	    }else{   
        var Data = {
            key : $scope.searchField,
            value : $scope.searchInput
            
        };
        $http({
        		url : localStorage.getItem('path') + "/createTemplate /search",
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
    }
	
	$scope.clearSearch = function() {
    	$scope.searchInput="";
    	$scope.errorMessege = false;
    } 
	
	
	$scope.pendingFENotiList = function(){
	 $http({
            url : localStorage.getItem('path') + "/GetNotifications/get",
        method : "GET"
      
     }).then(function(response){
    	 console.log(response);
    	 if (response.data.entity){
				if(response.data.entity.FERequestDetailedList != ""){
	    		  $scope.errorFlag = false;
	    		   $scope.requestsList = JSON.parse(response.data.entity.FERequestDetailedList);
	    		 }
				 if(response.data.entity.SERequestDetailedList != ""){
	    		  $scope.errorFlag = false;
	    		   $scope.requestsList = JSON.parse(response.data.entity.SERequestDetailedList);
	    		 }
	    		 else{
					$scope.errorFlag = false;
				}
			}else{
     		alertPopUp('Error ', 'Unable to get data');
     		return false;
				
			}
     });
     }
	$scope.pendingFENotiList();
	
	$scope.openProceedPopUP = function(){
		    	Data = {
		    			"requestId" :  $stateParams.requestId,
		    			"version" : $stateParams.version,
						"status":true
					},
				 $http({
			        url : localStorage.getItem('path') + "/configuration/responsefromfe",
			        method : "POST",
			        data : JSON.stringify(Data),
			      
			     }).then(function(response){
			     	//$scope.loading = false;
			    	 $state.go('fieldEng');
			     });
			}
	
	
	$scope.openHoldPopUP = function(){
		
					Data = {
							"requestId" :  $stateParams.requestId,
			    			"version" : $stateParams.version,
							"status":false
						},
				 $http({
			        url : localStorage.getItem('path') + "/configuration/responsefromfe",
			        method : "POST",
			        data : JSON.stringify(Data),
			      
			     }).then(function(response){
			    	 $state.go('fieldEng');
			     });
		        
		    	
		 
	}
	
	 /*Method to get view template configuration*/
    $scope.getReqConfData = function(event) {
    	$scope.reqIdOnDetail = $stateParams.requestId + "-v" + $stateParams.version;
        var Data = {
        		requestId : $stateParams.requestId,
        		version : $stateParams.version,
        		readFlag : $stateParams.read
        }
   		$http(
   				{
   					url : localStorage.getItem('path') + "/configuration/getBasicConfiguration",
   					method : "POST",
   					data : JSON.stringify(Data),
   					headers : {
   						'Content-Type' : 'application/json'
   					}
   				})
   				.then(
   						function(response) {
   							console.log(response)
   							if (response.data.entity.output){
   								$scope.getConfigurationData = JSON.parse(response.data.entity.output);
   								$scope.comment = response.data.entity.comment;
   								if(response.data.entity.comment == 'undefined' || response.data.entity.comment == ""){
   								$scope.comment = "";
   								}
   								
   								$rootScope.refreshNotificationCount();
   							}else{
   				        		console.log('Unable to get the data');
   							}
   					})
    	
    }
    $scope.getReqConfData();
    
} ])


 
	 
	
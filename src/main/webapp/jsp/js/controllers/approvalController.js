C3PApp.controller('approvalController',['$scope','$rootScope','$http', '$state', 'activeTabs','$stateParams',function($scope, $rootScope, $http, $state, activeTabs,$stateParams) {
	
	$scope.SearchItems = [ "Select","Template ID", "Status",
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
	$scope.openApprovePopUP = function(){
		
		bootbox.prompt({
		    title: "Add Note Here",
		    inputType: 'textarea',
		    callback: function (result) {
		    	Data = {
		    			"templateid" : $stateParams.templateId + "-v" + $stateParams.version,
						"status":"Approved",
						"comment":$rootScope.userInfo.username + " , " + new Date().toLocaleDateString() +" , " + new Date().toLocaleTimeString() + " " + result

					},
				 $http({
			        url : localStorage.getItem('path') + "/createTemplate/updateTemplateStatus",
			        method : "POST",
			        data : JSON.stringify(Data),
			      
			     }).then(function(response){
			    	 $state.go('Approval');
			     });
		        
		    	}
		});
		 $('.bootbox-input-textarea').attr('maxlength','100');
	}
	
	 $scope.refreshNotificationCount = function(){
           $http({
            url : localStorage.getItem('path') + "/GetNotifications/get",
            method : "GET"
          
         }).then(function(response){
                     localStorage.setItem("notificationCount",response.data.entity.NotificationCount);
                        $rootScope.notificationCount = localStorage.getItem("notificationCount");
                    
         });
         }
	$scope.refreshNotificationCount();
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
	
	$scope.openRejectPopUP = function(){
		bootbox.prompt({
		    title: "Add Note Here",
		    inputType: 'textarea',
		    callback: function (result) {
				if(result == ""){
					$('.bootbox-input-textarea').css("borderColor","red");
					end;
				}else{
					Data = {
					"templateid" : $stateParams.templateId + "-v" + $stateParams.version,
					"status":"Rejected",
					"comment":$rootScope.userInfo.username + " : " + new Date().toLocaleDateString() +"  " + new Date().toLocaleTimeString() + " " + result

					},
				 $http({
			        url : localStorage.getItem('path') + "/createTemplate/updateTemplateStatus",
			        method : "POST",
			        data : JSON.stringify(Data),
			      
			     }).then(function(response){
			    	 $state.go('Approval');
			     });
		        
		    	}
				}

		    
		});
		 $('.bootbox-input-textarea').attr('maxlength','100');
	}
	
	$scope.backOnApproval = function(){
		$state.go('Approval');
	}
	
	$scope.pendingTemplateList = function(){
	 $http({
            url : localStorage.getItem('path') + "/GetNotifications/get",
        method : "GET"
      
     }).then(function(response){
    	 console.log(response);
    	 if (response.data.entity.TemplateDetailedList){
				if(JSON.parse(response.data.entity.TemplateDetailedList) == ""){
					$scope.errorFlag = true; 
				}else{
					$scope.errorFlag = false;
					$scope.requestsList=JSON.parse(response.data.entity.TemplateDetailedList);
					
				}
			}else{
     		alertPopUp('Error ', 'Unable to get data');
     		return false;
				
			}
     });
     }
	$scope.pendingTemplateList();
	
	 /*Method to get view template configuration*/
    $scope.getTempConfData = function(event) {
    	$scope.templateIdOnDetail = $stateParams.templateId + "-v" + $stateParams.version;
        var Data = {
        		templateid : $stateParams.templateId + "-v" + $stateParams.version,
        		readFlag : $stateParams.read
        }
   		$http(
   				{
   					url : localStorage.getItem('path') + "/GetTemplateConfigurationData/getTemplateViewForTemplateVersion",
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
   								$scope.getConfigurationAdminData = JSON.parse(response.data.entity.output);
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
    $scope.getTempConfData();
    
} ])


 
	 
	
C3PApp.controller('requestDetailsController',['$scope','$interval','$rootScope','$http','$state', '$stateParams','dataFactory','activeTabs' ,function($scope,$interval, $rootScope, $http,$state,  $stateParams, dataFactory,activeTabs) {
  activeTabs.activeTabsFunc($stateParams.pageName);
	$rootScope.menubar = activeTabs.menubar;
	$scope.searchRequestDetails = function() {
    	
        var Data = {
            key : 'Request ID',
            value : $stateParams.dashboardReq_Details,
            version : $stateParams.version,
            readFlag : $stateParams.read
        };
        $http({
        		url : localStorage.getItem('path') + "/SearchRequestServiceWithVersion/search",
                //url : "http://localhost:8023/SearchRequestService/search",
                method : "POST",
                data : JSON.stringify(Data),
                headers : {
                    'Content-Type' : 'application/json'
                }

            }).then(function(response) {
            	
            	
//            	console.log(response.data)
//            	console.log(response.data.entity.output)
				if (response.data.entity.output){
					$scope.RequestDetails = JSON.parse(response.data.entity.output);
				}
                if (response.data.entity.ReportStatus){
                	$scope.ReportDetails = JSON.parse(response.data.entity.ReportStatus);
                }
            	
            	
        })
    }
    
	$rootScope.disableModifyScheduleButton  = JSON.parse(localStorage.getItem("flagForButton"));
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
	$scope.getReqScheduler = function(id, evntFromReqDetail){
		openAddPopUp(id);
		$rootScope.getScheduledGrid($stateParams.dashboardReq_Details, $stateParams.version, $stateParams.templateId, evntFromReqDetail);
	}
	
   
    $scope.searchRequestDetails();
    $interval($scope.searchRequestDetails, 45000);
    
    $scope.getUpdatedGridData = function(){  
    	$scope.ReportDetails = [];
    	$scope.searchRequestDetails();
    };
    
    $scope.closeReportPopUp = function(id) {
		$(id).css("display", "none");
	}
    
    $scope.scrollingFunc = function(){
		$('.versionPopUpContainer').attr("data-scrolling", "false");
		$('.versionPopUpContainer').on('scroll', function () {
		     if($(this).attr("data-scrolling") == "false"){
		        $('.versionPopUpContainer').not(this).attr("data-scrolling", "true");
		        $('.versionPopUpContainer').not(this).scrollTop($(this).scrollTop());
		    }
		$(this).attr("data-scrolling", "false");
		});
	}
    
    $scope.getbackupConfig = function(backupConfig){
    	var uploadUrl = localStorage.getItem('path') + "/GetReportData/getRouterConfigData",
		transformRequest = "", 
		headers = {
			'Content-Type' : 'application/json'
		},
		data = {
	    	"flagForData": backupConfig,
	    	requestID : $stateParams.dashboardReq_Details,
			version :  $stateParams.version
			},
		 Data = JSON.stringify(data);
		 dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
	        .then(function(response){
	        	if (response.data.entity){
	        		$scope.getPrevdata = response.data.entity.previousRouterVersion;	        		
	        		$("#prevRouterVersion").css("display", "block");
	        		dragElement(document.getElementById(("prevRouterVersionContainer")));	
					
	        	}else{
	        		alertPopUp('Error', 'Data not Found');
	        		return false;
	        	}
	     }, function (error) {
	    	   alertPopUp('Error', 'Unable to save data');
	    	   return false
	    });
    }
    $scope.getConfigDiff = function(findDiff){
    	var uploadUrl = localStorage.getItem('path') + "/GetReportData/getRouterConfigData",
		transformRequest = "", 
		headers = {
			'Content-Type' : 'application/json'
		},
		data = {
	    	"flagForData": findDiff,
	    	requestID : $stateParams.dashboardReq_Details,
			version :  $stateParams.version
			},
		 Data = JSON.stringify(data);
		 dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
	        .then(function(response){
	        	if (response.data.entity){
	        		var prevVersion = response.data.entity.previousRouterVersion;	        			        		
	        		var currVersion = response.data.entity.currentRouterVersion;
	        		setTimeout(function(){ 
	        			 $scope.$apply(function () {
	        				 
	        				 $scope.getDiffViews(prevVersion, currVersion);
	        				 $scope.scrollingFunc();
	        			 });
		        		
	        		});
	        		
	        		$("#DiffConfigVersion").css("display", "block");
	        		dragElement(document.getElementById(("DiffConfigVersionContainer")));	
					
	        	}else{
	        		alertPopUp('Error', 'Data not Found');
	        		return false;
	        	}
	     }, function (error) {
	    	   alertPopUp('Error', 'Unable to save data');
	    	   return false
	    });
    }
    
    $scope.getDiffViews = function(baseText, newText){
    	$scope.disableText = false;
    	var output = "", args = {}
    	args   = {
    		        source: baseText,
    		        diff  : newText,
    		        lang  : "text"
    		    }
    		
    	if (args.source == "" && args.diff !== ""){    		
    		setTimeout(function(){ 
	    		$(".versionPopUpContainer .replace:first").css({"visibility": "hidden"});
	        	$(".versionPopUpContainer .replace:last em").css({"background-color": "white"});
    		 }, 30);    		 
    		args.source = "No Backup Generated"
    		output = global.prettydiff.prettydiff(args);
    		$("#prettydiff .diff , #prettydiff p").remove();
    		$("#prettydiff").append(output);
    	}else if(args.source !== "" && args.diff == ""){
    		setTimeout(function(){ 
	    		$(".versionPopUpContainer .replace:first em").css({"background-color": "white"});
	        	$(".versionPopUpContainer .replace:last").css({"visibility": "hidden"});
    		 }, 30);
    		args.diff = "No Backup Generated"
    		output = global.prettydiff.prettydiff(args);
    		$("#prettydiff .diff , #prettydiff p").remove();
    		$("#prettydiff").append(output);
    	}else if (args.source == "" && args.diff == ""){
    		$scope.disableText = true;
    	}else{
    		output = global.prettydiff.prettydiff(args);
    		$("#prettydiff .diff , #prettydiff p").remove();
    		$("#prettydiff").append(output);
    	}    		    
    	
    }

   
     $scope.getConfigratioData = function(testType, report_title) {
		var Data = {
			requestID : $stateParams.dashboardReq_Details,
			testType : testType,
			version :  $stateParams.version
		};
		$scope.report_title = report_title ;
		//console.log(JSON.stringify(Data))
		$http(
				{
					url : localStorage.getItem('path') + "/GetReportData/getReportDataforTest",
					//url : "http://localhost:8023/GetReportData/getReportDataforTest",
					method : "POST",
					data : JSON.stringify(Data),
					headers : {
						'Content-Type' : 'application/json'
					}

				})
				.then(
						function(response) {
						$scope.disableText = false;
							//console.log(response)
							if (testType == "deliverConfig"){
								$scope.deliverConfig = true;
								if (response.data.entity.backupStatus == "Completed"){
									$scope.backupStatus = 'Success';
									$(".bckupStatus").addClass("bkup-success-icon").removeClass("bkup-fail-icon");
								}else {
									$scope.backupStatus = 'Failed';
									$(".bckupStatus").addClass("bkup-fail-icon").removeClass("bkup-success-icon");
								}
								
								if (response.data.entity.currentRouterVersion === "" && response.data.entity.previousRouterVersion === "" ){
									$scope.disableText = true;
								}
								
								if (response.data.entity.status == "Success"){
									$scope.deliveryStatus = response.data.entity.status;
									$(".deliveryStatus").addClass("bkup-success-icon").removeClass("bkup-fail-icon");
									if (response.data.entity.errorType == "Warning"){
										$scope.errorType = response.data.entity.errorType;
										$scope.errorWarningMsg = response.data.entity.errorDesc;
										$scope.errorRouterMessage = response.data.entity.errorRouterMessage;
										
									}else {
										$scope.errorType = "false";
									}
								}else {
									$scope.deliveryStatus = response.data.entity.status;
									$(".deliveryStatus").addClass("bkup-fail-icon").removeClass("bkup-success-icon");
									if (response.data.entity.errorType == "Failure"){
										$scope.errorType = response.data.entity.errorType;
										$scope.errorWarningMsg = response.data.entity.errorDesc;
										$scope.errorRouterMessage = response.data.entity.errorRouterMessage;
										
									}else {
										$scope.errorType = "false";
									}
								}
								
								//$("#backupandDelivery").css("display", "block");
								openAddPopUp('backupandDelivery');
								dragElement(document.getElementById(("backupandDeliveryContainer")));	
							}else {
								$scope.deliverConfig = false;
								$scope.getReportdata = response.data.entity.output;
								$scope.getdata =$scope.getReportdata;
								$scope.getType = response.data.entity.testType;
								$scope.format = response.data.entity.format;
								$scope.formatColor = response.data.entity.formatColor;

								if($scope.format == 'true'){
									var cusReport = angular.element(document.querySelector( '#reportContainer' ) );
									cusReport.html($scope.getdata);
								}
								if($scope.formatColor == 'true'){
									var cusReport = angular.element(document.querySelector( '#reportContainerColorCode' ) );
									cusReport.html($scope.getdata);
								}
								//$("#reportPopUp").css("display", "block");
								openAddPopUp('reportPopUp');
								dragElement(document.getElementById(("reportPopUpContainer")));	
							}
							
							
						})
						
	}
     /*method to get Health Check data*/
     $scope.getPreHealthCheckSubTestData = function(testType) {
 		var Data = {
 			requestID : $stateParams.dashboardReq_Details,
 			testType : testType,
 			version :  $stateParams.version
 		};
 		//console.log(JSON.stringify(Data))
 		$http(
 				{
 					url : localStorage.getItem('path') + "/GetCertificationTestData/getPrevalidationTestData",
 					//url : "http://localhost:8023/GetReportData/getReportDataforTest",
 					method : "POST",
 					data : JSON.stringify(Data),
 					headers : {
 						'Content-Type' : 'application/json'
 					}

 				})
 				.then(
 						function(response) {
 							//console.log(response)
 							$scope.getSubTestData = response.data.entity.output;
 							
 						})
 						
 	}
     $interval($scope.getPreHealthCheckSubTestData, 45000);
     /*method to get Pre Validation data*/
     $scope.getPreValidateSubTestData = function(testType) {
 		var Data = {
 			requestID : $stateParams.dashboardReq_Details,
 			testType : testType,
 			version :  $stateParams.version
 		};
 		//console.log(JSON.stringify(Data))
 		$http(
 				{
 					url : localStorage.getItem('path') + "/GetCertificationTestData/getPrevalidationTestData",
 					//url : "http://localhost:8023/GetReportData/getReportDataforTest",
 					method : "POST",
 					data : JSON.stringify(Data),
 					headers : {
 						'Content-Type' : 'application/json'
 					}

 				})
 				.then(
 						function(response) {
 							//console.log(response)
 							$scope.getSubTestData = response.data.entity.output;
 							
 						})
 						
 	}
     $interval($scope.getPreValidateSubTestData, 45000);
     /*method to get Network Test data*/
    /* $scope.getNetworkSubTestData = function(testType) {
    	 var Data = {
    	 			requestID : $stateParams.dashboardReq_Details,
    	 			testType : testType,
    	 			version :  $stateParams.version
    	 		};
    	 	transformRequest = "", 
			headers = {
				'Content-Type' : 'application/json'
			},
    	 
    	 dataFactory.postDataFact(localStorage.getItem('path') + "/GetCertificationTestData/getPrevalidationTestData", Data, transformRequest, headers)
         .then(function(response){
        	 console.log(response)
        	 $scope.getSubTestData = response.data.entity.output;
         });
 	}
		*/
		
		
		 $scope.restartSEFlow = function() {
	 		var Data = {
				requestId : $stateParams.dashboardReq_Details,
	 			version :  $stateParams.version
				};
	 		$http(
	 				{
	 					url : localStorage.getItem('path') + "/configuration/restartFEFlow",
	 					method : "POST",
	 					data : JSON.stringify(Data),
	 					headers : {
	 						'Content-Type' : 'application/json'
	 					}
	
	 				})
 			.then(
					function(response) {
						console.log(response);
						$state.go('Dashboard');
					})
 			}
   
		
     $scope.getNetworkSubTestData = function(testType) {
 		var Data = {
 			requestID : $stateParams.dashboardReq_Details,
 			testType : testType,
 			version :  $stateParams.version
 		};
 		//console.log(JSON.stringify(Data))
 		$http(
 				{
 					url : localStorage.getItem('path') + "/GetCertificationTestData/getPrevalidationTestData",
 					//url : "http://localhost:8023/GetReportData/getReportDataforTest",
 					method : "POST",
 					data : JSON.stringify(Data),
 					headers : {
 						'Content-Type' : 'application/json'
 					}

 				})
 				.then(
 						function(response) {
 							//console.log(response)
 							$scope.getSubTestData = response.data.entity.output;
 							
 						})
 						
 	}
     $interval($scope.getNetworkSubTestData, 45000);
}])

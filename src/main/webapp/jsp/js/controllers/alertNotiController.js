C3PApp.controller('alertNotiController',['$scope','$rootScope','$http', '$state',function($scope, $rootScope, $http, $state) {
		
	$scope.getallAlertNotiData = function() {

		$http(
				{
					url : localStorage.getItem('path') + "/GetAllAlertData/getAll",
					//url : "http://localhost:8023/GetAllAlertData/getAll",
					method : "GET"

				})
				.then(
						function(response) {
							console.log(response)
							$scope.requestsList = JSON
									.parse(response.data.entity.output);
							$scope.rowCollection = JSON
									.parse(response.data.entity.output);
						
							console.log(JSON.parse(response.data.entity.SuccessfulRequests));

						})

	}
	$scope.getallAlertNotiData();
	$scope.sort = function(sortkey,event) {
		 var id = event.target.id;
		 $('#'+ id).toggleClass('sort-ascent').toggleClass('sort-descent');
		    $scope.reverse = !$scope.reverse;
	        $scope.sortKey = sortkey;
	        
	        console.log($scope.requestsList)
	    }
	$scope.searchAlertNotiRecord = function() {
		console.log("In search function")
		
		var Data = {
			alert_code : $scope.alertNotiCode,
			alert_description : $scope.alertNotiDesc,
			
		};
		$http(
				{
					url : localStorage.getItem('path') +  "/SearchAllAlertNotification/search",
					//url : "http://localhost:8023/SearchAllAlertNotification/search",
					method : "POST",
					data : JSON.stringify(Data),
					headers : {
						'Content-Type' : 'application/json',
						'Accept': 'application/json' 

					}

				})
				.then(
						function(response) {
							$scope.errorMessege = false;
							console.log(response.data.entity.output)
							$scope.requestsList = JSON
							.parse(response.data.entity.output);
							$scope.rowCollection = JSON
							.parse(response.data.entity.output);
							
							if ($scope.requestsList == ""){
			            		$scope.errorMessege = true;
			            	}

						})
	}
	$scope.resetAlertNotiData = function() {
		$('.ng-invalid').css('border','');
		$scope.addAlertNotiType = "";
		$scope.addAlertNotiCategory ="";
		$scope.addAlertNotiDesc = "";
		$scope.addAlertNotiCode ="";
	}
	$scope.flushAlertNotiData = function() {
		openAddPopUp('addPopUp');
		dragElement(document.getElementById(("addPopUpContainer")));	
		$scope.resetAlertNotiData();
		$('.ng-invalid').css('border','');
	}
	 $scope.generateNewCode = function(){
		 $http(
					{
						url : localStorage.getItem('path') + "/GetLastAlertID/getLastAlertId",
						//url : "http://localhost:8023/GetLastAlertID/getLastAlertId",
						method : "GET"

					})
					.then(
							function(response) {
								console.log(response)
								$scope.alerCode = JSON
										.parse(response.data.entity.LastID);
								
								var selectedCategory = $scope.addAlertNotiType;
							if (selectedCategory == 'Alert'){
								$scope.newCode = parseInt($scope.alerCode) +001;
								$scope.addAlertNotiCode = 'A' +$scope.newCode;
							 }else{
								$scope.newCode = parseInt($scope.alerCode) +001;
								$scope.addAlertNotiCode = 'N' +$scope.newCode;
							 }	

							})
			
		 
		 
	 }
	 $scope.addNewAlertNotiData = function(event) {
		 console.log("In add func");
		  if(!$scope.newAlertNotiForm.$valid) {
									 	$('.ng-invalid').css('border','1px solid rgb(222, 52, 52)');
										$('#newAlertNoti').css('border','none');
										alertPopUp('Alert Management','Please fill all the required fields.')
										event.preventDefault();	
									}else{
	        var Data = {
			 		alert_type: $scope.addAlertNotiType,
	        		alert_category: $scope.addAlertNotiCategory,
	        		alert_description:$scope.addAlertNotiDesc,
	        		alert_code:$scope.addAlertNotiCode
	        		};
	        $http({
	        		url : localStorage.getItem('path') + "/AddNewAlertNotificationService/add",
	        		//url : "http://localhost:8023/AddNewAlertNotificationService/add",
	                method : "POST",
	                data : JSON.stringify(Data),
	                headers : {
	                    'Content-Type' : 'application/json'
	                }

	            }).then(function(response) {
	            
	            	console.log(response)
	            	console.log("In response")
					//$scope.loading = false;
				
					bootbox.confirm({
						title : 'Alert Management',
						message : response.data.entity.status,
						buttons : {
								
							confirm : {
								label : 'Ok',
								className : 'btn-default'
							},

						},
						callback : function(result) {
							if (result)
							$state.go('Alert');
							$scope.getallAlertNotiData();
							closeAddPopUp('addPopUp');
							
						}
					});
				        })
	        //event.preventDefault();
	    }
	    }
	 $scope.editAlertNotiData = function(alert_code,alert_category,alert_description,alert_type){
		 openAddPopUp('editPopUp');
		 dragElement(document.getElementById(("editPopUpContainer")));	
		 $scope.editAlertNotiDesc =  alert_description;
		 $scope.editAlertNotiCode =  alert_code;
		 $scope.editAlertNotiCategory =  alert_category;
		 $scope.editAlertNotiType =  alert_category;
	 }
	 $scope.updateAlertNotiData = function() {
		 console.log("In update func")
	        var Data = {
	        		desc:$scope.addAlertNotiDesc,
	        		};
	        $http({
	        		url : localStorage.getItem('path') + "/UpdateAlertDBService/update",
	                //url : "http://localhost:8023/UpdateIpamDBService/update",
	                method : "POST",
	                data : JSON.stringify(Data),
	                headers : {
	                    'Content-Type' : 'application/json'
	                }

	            }).then(function(response) {
	            
	            	console.log(response)
	                $scope.requestsList = JSON.parse(response.data.entity.output);
	            	console.log("In response")
					//$scope.loading = false;
					bootbox.confirm({
						title : 'IP Management',
						message : response.status,
						buttons : {
								
							confirm : {
								label : 'Ok',
								className : 'btn-default'
							},

						},
						callback : function(result) {
							if (result)
							 $state.go('Alert');
							 $scope.getallAlertNotiData();
							 $scope.closeAddPopUp();
							 
						}
					});
				

	        })
	    }
	
	
} ])
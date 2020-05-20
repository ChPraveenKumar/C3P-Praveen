C3PApp.controller('findipcontroller',['$scope','$rootScope','$http', '$state', 'activeTabs',function($scope, $rootScope, $http, $state, activeTabs) {
							
							
							/// Higlighted Active tab goes here--------
							activeTabs.activeTabsFunc('Admin');
							$rootScope.menubar = activeTabs.menubar;
							/// Higlighted Active tab goes here--------
							
							
							$scope.SearchItems = "";
							$scope.searchField = $scope.SearchItems[0];
							$scope.ipamSite = "";
							$scope.ipamCustomerName = "";
							$scope.ipammask="";
							$scope.ipamservice="";
							$scope.ipamip="";
							$scope.sortKey = "";
							$scope.reverse = false;
							$scope.requestsList = [];
							$scope.rowCollection = '';
							$scope.updateSite = "";
							$scope.updateIp = "";
							$scope.updateMask = "";
							$scope.updateService = ""; 
							$scope.updateCustomer = "";
							$scope.updateRegion = "";

							$scope.getallipamData = function() {

								$http(
										{
											url : localStorage.getItem('path') + "/GetAllIpamData/getAll",
											//url : "http://localhost:8023/GetAllIpamData/getAll",
											method : "GET"

										})
										.then(
												function(response) {
													console.log(response)
													$scope.requestsList = JSON
															.parse(response.data.entity.output);
													$scope.rowCollection = JSON
															.parse(response.data.entity.output);
													$scope.successRequests = JSON
															.parse(response.data.entity.SuccessfulRequests);
													$scope.failureRequests = JSON
															.parse(response.data.entity.FailureRequests);
													$scope.TotalRequests = JSON
															.parse(response.data.entity.TotalRequests);
													console.log(JSON.parse(response.data.entity.SuccessfulRequests));

												})

							}
							$scope.getallipamData();
							
							 $scope.editIPMgmtData = function( customer,site,region,service,ip,mask){
								 openAddPopUp('editIPPopUp');
								 dragElement(document.getElementById(("editIPPopUpContainer")));	
								 $scope.editIPmgmtCustomer =  customer;
								 $scope.editIPmgmtSite =  site;
								 $scope.editIPmgmtRegion =  region;
								 $scope.editIPmgmtService =  service;
								 $scope.editIPmgmtIPadd =  ip;
								 $scope.editIPmgmtMask =  mask;
							 }
	
							$scope.getIp = function() {
								console.log("In search function")
								var Data = {
									site : $scope.ipamSite,
									customer : $scope.ipamCustomerName,
									mask: $scope.ipammask
								
								};
								$http(
										{
											url : localStorage.getItem('path') + "/GetIPfromEIPAM/getip",
											//url : "http://localhost:8023/GetIPfromEIPAM/getip",
											method : "POST",
											data : JSON.stringify(Data),
											headers : {
												'Content-Type' : 'application/json'
											}

										})
										.then(
												function(response) {
													console.log(response.data.entity.output)
													$scope.ipamip = JSON
															.parse(response.data.entity.output);

												})
							}
							$scope.searchIPAMRecord = function() {
								console.log("In search function")
								var Data = {
									site : $scope.ipamSite,
									customer : $scope.ipamCustomerName,
									service: $scope.ipamservice,
									ip: $scope.ipamip
								};
								$http(
										{
											url : localStorage.getItem('path') + "/SearchAllIpamData/search",
											//url : "http://localhost:8023/SearchAllIpamData/search",
											method : "POST",
											data : JSON.stringify(Data),
											headers : {
												'Content-Type' : 'application/json'
											}

										})
										.then(
												function(response) {
													console.log(response.data.entity.output)
													$scope.requestsList = JSON
															.parse(response.data.entity.output);
													//$scope.ipamip = JSON
														//	.parse(response.data.entity.output);

												})
							}
							/*$scope.sort = function(sortkey) {
								$scope.reverse = !$scope.reverse;
								$scope.sortKey = sortkey;
								console.log($scope.requestsList)
							}*/
							$scope.flushIpMgmtData = function(sortkey) {
								openAddPopUp('addPopUp');
								dragElement(document.getElementById(("addPopUpContainer")));	
								$scope.resetIpMgmtData();
								$('.ng-invalid').css('border','');
							}
							$scope.resetIpMgmtData = function(sortkey) {
								$('.ng-invalid').css('border','');
								$scope.updateSite ="";
								$scope.updateRegion="";
								$scope.updateIp="";
								$scope.updateMask="";
								$scope.updateService="";
								$scope.updateCustomer="";
							}
													
							 $scope.addIpamData = function(event) {
								 console.log("In add func");
								 var validationFlag="";
								  $('input').each(function(index, item) {
									  if ($(item).hasClass("ng-invalid") && $(item).hasClass("ng-valid-required")) {
									    	validationFlag = true; 
									    }
									});

								  if(validationFlag){
									 
									  alertPopUp('IP Management','Please enter valid input in field.')
								  }
								  else{
								  if(!$scope.newIpMgmtForm.$valid) {
									 	$('.ng-invalid').css('border','1px solid rgb(222, 52, 52)');
										$('#newIpMgmt').css('border','none');
										alertPopUp('IP Management','Please fill all the required fields.')
										event.preventDefault();	
									}else{
							        var Data = {
									 site: $scope.updateSite,
						        		region: $scope.updateRegion,
						        		ip:$scope.updateIp,
						        		mask:$scope.updateMask,
						        		service:$scope.updateService,
						        		customer:$scope.updateCustomer
							        		};
							        $http({
							        		url : localStorage.getItem('path') + "/UpdateIpamDBService/add",
							        		//url : "http://localhost:8023/AddNewAlertNotificationService/add",
							                method : "POST",
							                data : JSON.stringify(Data),
							                headers : {
							                    'Content-Type' : 'application/json'
							                }

							            }).then(function(response) {
							            	console.log(response)
							            	console.log("In response")
											bootbox.confirm({
												title : 'IP Management',
												message : response.data.entity.output,
												buttons : {
														
													confirm : {
														label : 'Ok',
														className : 'btn-default'
													},

												},
												callback : function(result) {
													if (result)
													$state.go('Admin');
													$scope.getallipamData();
													closeAddPopUp('addPopUp');
												}
											});
							            	
										        })
							        
							    }
								  } 
							 }
							 $scope.updateIpamData = function(event) {

								 console.log("In update func");
								 var validationFlag="";
								  $('input').each(function(index, item) {
									  if ($(item).hasClass("ng-invalid") && $(item).hasClass("ng-valid-required")) {
									    	validationFlag = true; 
									    }
									});

								  if(validationFlag){
									 
									  alertPopUp('IP Management','Please enter valid input in field.')
								  }
								  else{
								  if(!$scope.editIpMgmtForm.$valid) {
									 	$('.ng-invalid').css('border','1px solid rgb(222, 52, 52)');
										$('#editIpMgmt').css('border','none');
										alertPopUp('IP Management','Please fill all the required fields.')
										event.preventDefault();	
									}else{
							        var Data = {
									 	ip:$scope.editIPmgmtIPadd,
									 	mask:$scope.editIPmgmtMask,
									 	customer:$scope.editIPmgmtCustomer,
									 	site:$scope.editIPmgmtSite
						        			};
							        $http({
							        		url : localStorage.getItem('path') + "/UpdateIpamDBService/update",
							        		//url : "http://localhost:8023/AddNewAlertNotificationService/add",
							                method : "POST",
							                data : JSON.stringify(Data),
							                headers : {
							                    'Content-Type' : 'application/json'
							                }

							            }).then(function(response) {
							            	console.log(response)
							            	console.log("In response")
											bootbox.confirm({
												title : 'IP Management',
												message : response.data.entity.output,
												buttons : {
														
													confirm : {
														label : 'Ok',
														className : 'btn-default'
													},

												},
												callback : function(result) {
													if (result)
													$state.go('Admin');
													$scope.getallipamData();
													closeAddPopUp('editIPPopUp');
												}
											});
							            	
										        })
							        
							    }
								  } 
							 
								 
							 }
						}
					 ])
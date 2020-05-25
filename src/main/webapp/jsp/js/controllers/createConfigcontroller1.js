C3PApp
		.controller(
				'createConfigController',
				[
						'$scope',
						'$rootScope',
						'$http',
						'$state',
						'fileUpload',
						'dataFactory',
						'activeTabs',
						'$interval',
						function($scope, $rootScope, $http, $state,fileUpload, dataFactory, activeTabs, $interval) {
							
							/// Higlighted Active tab goes here--------
							activeTabs.activeTabsFunc('Configuration');
							$rootScope.menubar = activeTabs.menubar;
							/// Higlighted Active tab goes here--------
							
							$scope.myform = {};
							$scope.resetFlag = false;
							$scope.autoHostName = true;
							$scope.createConfSubmitted = false;
							$scope.chkStatus = [];
							$scope.configuartion = {
									    'c3p_interface': {
									      'ip': '',
									    'mask': ''
									    
									    
									    },
									    'internetLcVrf': {
									     'networkIp_subnetMask':'',
									     'networkIp':''
									    },
									    'certificationOptionListFlags' :{}
									    }
							$scope.eventDisable = false;
							$scope.reSchedule = false;
							$scope.cancelSchedule = false;
							$scope.saveSchedule = false;
							var wanInterfaceObj, 
							lanInterface,
							loopbackInt ,
							vpnObj,
							routingObj ,
							snmp,
							banner,
							enablePassword = undefined;
							
							$scope.minDateString = moment();
							
							$scope.minDateStringFunc = function(){
								$scope.minDateString = moment();
							}
							
							$interval($scope.minDateStringFunc, 1000);
							
							$scope.openSchedulePopup = function(id){
								openAddPopUp(id);
							}
							$scope.schedulerOption = 2;
							$scope.checkSchedulerIndex = function(index){
								
								$scope.schedulerOption = index;
								
								if ($scope.schedulerOption == 1){
									$scope.eventDisable = true;
								}else {
									$scope.eventDisable = false;
								}							
								
							}
							
							$scope.scheduleAction = function(action, requestId, version){
								if(action == "reschedule"){
									$scope.reSchedule = true;
									$scope.saveSchedule = false;
									$scope.eventDisable = false;
									$scope.requestId = requestId;
									$scope.version = version;
									$scope.cancelSchedule = false;
								}else if (action == "run"){
									$scope.saveSchedule = false;
									$scope.runScheduleRequest(requestId, version);
								}else if(action == "ok"){
									$scope.saveSchedule = false;
									if ($scope.cancelSchedule === true){
										var reqId = $scope.reqId,
										vers = $scope.vers
										$scope.abortRequest(reqId, vers);
									}else{										
										$state.go('Dashboard');
									}									
								}else if(action == "cancel"){
									$scope.saveSchedule = false;
									$scope.cancelSchedule = true;
									$scope.eventDisable = true;
									$scope.reqId = requestId;
									$scope.vers = version;
									$scope.cancelSchedulerReq(requestId, version)
								}else if(action == "close"){
									$scope.saveSchedule = false;
									if ($scope.cancelSchedule === true){
										var reqId = $scope.reqId,
										vers = $scope.vers
										$scope.abortRequest(reqId, vers);
									}else if($scope.saveSchedule === false && $scope.scedulerDatePicker !== undefined){
										$scope.closeSchedulePopUp('schedulePopUp')			
									}else{										
										$state.go('Dashboard');
									}
								}
							}							
							
							
							$scope.closeSchedulePopUp = function(id){
								if ($scope.saveSchedule === false && $scope.scedulerDatePicker !== undefined){
									bootbox.confirm({
									    title: "Schedule Request",
									    message: "Do you want to save the changes?",
									    buttons: {
									        cancel: {
									            label: '<i class="fa fa-times"></i> No'
									        },
									        confirm: {
									            label: '<i class="fa fa-check"></i> Yes'
									        }
									    },
									    callback: function (result) {
									       if (result == true){
									    	   
									       }else{
									    	   $scope.scedulerDatePicker = "";
									    	   closeAddPopUp(id);
									       }
									    }
									});
								}else{
									
									closeAddPopUp(id);
								}
							}
							
							$scope.abortRequest = function(requestId, version){
								bootbox.confirm({
								    title: "Abort Request",
								    message: "Do you want to abort this request?",
								    buttons: {
								        cancel: {
								            label: '<i class="fa fa-times"></i> No'
								        },
								        confirm: {
								            label: '<i class="fa fa-check"></i> Yes'
								        }
								    },
								    callback: function (result) {
								       if (result == true){
								    	   
											var uploadUrl = localStorage.getItem('path') + "/RequestScheduleService/abortRequest ",
											transformRequest = "", 
											headers = {
												'Content-Type' : 'application/json'
											},
											data = {
													"requestId": requestId,
													"version": version
												},
											Data = JSON.stringify(data);
												
											dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
								               .then(function(response){
								            	   //console.log(response.data.output);
								            	   $state.go('Dashboard');
								               }, function (error) {
								            	   alertPopUp('Error', 'Unable to load data');
								            });
								       }else{
								    	   //console.log("request canceled");
								       }
								    }
								});
							}
							
							$scope.cancelSchedulerReq = function(requestId, version){
								
								var uploadUrl = localStorage.getItem('path') + "/RequestScheduleService/cancelRequest",
								transformRequest = "", 
								headers = {
									'Content-Type' : 'application/json'
								},
								data = {
										"requestId": requestId,
										"version": version
									},
								Data = JSON.stringify(data);
									
								dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
					               .then(function(response){
					            	   //console.log(response);
					            	   $rootScope.getScheduledGrid(requestId, version);
					               }, function (error) {
					            	   alertPopUp('Error', 'Unable to load data');
					            });
							}
							
							$scope.getScheduledData = function(evt){
								$scope.saveSchedule = true;
								if ($scope.scedulerDatePicker == "" || $scope.scedulerDatePicker == undefined){
									return false;
								}else{
									var configuartion = JSON.parse(localStorage.getItem('configuartion'));
									configuartion.scheduledTime = $scope.scedulerDatePicker;
									var urlPath = "";
									
									if ($scope.reSchedule == true){
											urlPath = "/RequestScheduleService/recheduleRequest";
											var	data = {
													"requestId": $scope.requestId,
													"version": $scope.version,
													"scheduledTime": $scope.scedulerDatePicker
												}
											Data = JSON.stringify(data);
					            	   }else if($rootScope.modifyScheduler === true){
					            		   var editRequestDetails = $rootScope.editRequestDetails;
					            		   var certifictionFlags = $rootScope.certificationListFlags;
					            		   editRequestDetails.scheduledTime = $scope.scedulerDatePicker;
					            		   urlPath = "/ModifyConfiguration/modify";
					            		    var data = {
					            		   	editeData : editRequestDetails,
					            		   	certificationOptionListFlags : certifictionFlags
					            		   }
					            		   Data = JSON.stringify(data);
					            	   }else{
					            		   urlPath = "/ConfigMngmntService/createConfigurationDcm";
					            		   Data = JSON.stringify(configuartion);
					            	   }
									
									
									
									var uploadUrl = localStorage.getItem('path') + urlPath,
									transformRequest = "", 
									headers = {
										'Content-Type' : 'application/json'
									}																	
										
									dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
						               .then(function(response){
						            	   //console.log(response);
						            	   
						            	   if ($scope.reSchedule == true){
						            		   $rootScope.getScheduledGrid($scope.requestId, $scope.version);
						            	   }else{
						            		   $rootScope.getScheduledGrid(response.data.requestId, response.data.version);
						            	   }						            	   
						            	   
						            	   $scope.scedulerDatePicker = "";
						            	   $scope.eventDisable = evt;
						               }, function (error) {
						            	   alertPopUp('Error', 'Unable to load data');
						            });
								}
								
							}
							
							$scope.runScheduleRequest = function(requestId, version){
								
								bootbox.confirm({
								    title: "Run Scheduler Request",
								    message: "Do you want to execute this request?",
								    buttons: {
								        cancel: {
								            label: '<i class="fa fa-times"></i> No'
								        },
								        confirm: {
								            label: '<i class="fa fa-check"></i> Yes'
								        }
								    },
								    callback: function (result) {
								       if (result == true){
								    	   var configuartion = JSON.parse(localStorage.getItem('configuartion'));
								    	   
								    	   if ($rootScope.modifyScheduler === true){
								    	   		configuartion.certificationOptionListFlags = $rootScope.certificationListFlags;
								    	   }
								    	   								    	   
											var uploadUrl = localStorage.getItem('path') + "/RequestScheduleService/runScheduleRequest",
											transformRequest = "", 
											headers = {
												'Content-Type' : 'application/json'
											},
											data = {
													"requestId": requestId,
													"version": version,
													"data": configuartion
												},
											Data = JSON.stringify(data);
												
											dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
								               .then(function(response){
								            	   //console.log(response.data.output);
								            	   $state.go('dashboardReqDetails', { dashboardReq_Details: requestId,version: version});
								               }, function (error) {
								            	   alertPopUp('Error', 'Unable to load data');
								            });
								       }else{
								    	   //console.log("request canceled");
								       }
								    }
								});
								
								
							}
							 
							$rootScope.getScheduledGrid = function(requestId, version, evntFromReqDetail){
								
																
								
								var uploadUrl = localStorage.getItem('path') + "/RequestScheduleService/getScheduledHistory",
								transformRequest = "", 
								headers = {
									'Content-Type' : 'application/json'
								},
								data = {
									"requestId": requestId,
									"version": version
								}
								
								Data = JSON.stringify(data);
									
								dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
					               .then(function(response){
					            	   //console.log(response);
					            	   $scope.requestsList = JSON.parse(response.data.entity.output);
					            	   
					            	   if (evntFromReqDetail === true && $scope.requestsList.length > 0){
											$scope.eventDisable = true;
										}
					            	   
					            	   angular.forEach($scope.requestsList, function(itm){ 
					            		   itm.showActionReschdule = false;
					            		   itm.showActionRun = false;
					            		   itm.showActionCancel = false;
									    });
					            	   if ($scope.requestsList.length >= 1){
					            		   if($scope.cancelSchedule === true){
					            			   $scope.requestsList[0].showActionReschdule = true;
					            			   $scope.requestsList[0].showActionRun = false;
					            			   $scope.requestsList[0].showActionCancel = false;					            			   
					            		   }else if($rootScope.modifyReSchedule === true){
					            			   $scope.requestsList[0].showActionReschdule = false;
					            			   $scope.requestsList[0].showActionRun = false;
					            			   $scope.requestsList[0].showActionCancel = false;
					            			   $rootScope.modifyReSchedule = false;
					            		   }else{					            		   	   
					            			   $scope.requestsList[0].showActionReschdule = true;
					            			   $scope.requestsList[0].showActionRun = true;
					            			   $scope.requestsList[0].showActionCancel = true;
					            		   }					            		   					            		   
					            	   }else{
					            	   			$rootScope.modifyReSchedule = false;
					            	   }
					            	   
					               }, function (error) {
					            	   alertPopUp('Error', 'Unable to load data');
					            });
							}
							
							$scope.configuartion.importFile = '1';
							$scope.isCheckboxSelected = function(index) {
						        if ($scope.configuartion.importFile == '1'){
						        	$scope.configuartion.internetLcVrf.customerNo = "";
						        	$scope.resetFlag = true;
						        }else{
						        	$scope.resetFlag = false;
						        }
						        return index === $scope.configuartion.importFile;
						    };
							
							$scope.orderId = "Order No";										
							$scope.closeImportPopUp = function(id){
								closeAddPopUp(id);
								if (id == 'addImportPopUp'){
									$scope.configuartion.internetLcVrf.customerNo = "";
									$scope.configuartion.importFile = '1';
								}
							}
							$scope.enterKeyPress = function(e, method){
								if(e.which == 13) {
									method(event);
								}								
							}
							$scope.getxmlData = function(event){
								event.stopPropagation();
								event.stopImmediatePropagation();
								event.preventDefault();
					if ($scope.configuartion.internetLcVrf.customerNo == "" || $scope.configuartion.internetLcVrf.customerNo == undefined){
						$("#addImportPopUp input[type='text']").css("border","1px solid red")
						return false;
					}
					var orderId = {									
							"orderID": $scope.configuartion.internetLcVrf.customerNo
					},
					uploadUrl = localStorage.getItem('path') + "/GetAllXMLData/get",
					transformRequest = "", 
					headers = {
						'Content-Type' : 'application/json'
					},
					Data = JSON.stringify(orderId);
						
					dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
		               .then(function(response){
		            	   
		            	   if (response.data.entity.status == true){					            	   
						       var configuartionData = JSON.parse(response.data.entity.output);
						       var InformationStatus = configuartionData.interfaceRoutingInfo.InformationStatus;
						       
						       $scope.configuartion.lanDescription = configuartionData.interfaceRoutingInfo.lanInterface.lanInterfaceDescription
						       $scope.configuartion.lanIPaddress = configuartionData.interfaceRoutingInfo.lanInterface.lanInterfaceIP
						       $scope.configuartion.lanSubnetMask = configuartionData.interfaceRoutingInfo.lanInterface.lanInterfaceSubnetMask
						       $scope.configuartion.lanTnterface = configuartionData.interfaceRoutingInfo.lanInterface.lanInterfaceType
							   $scope.configuartion.customer = configuartionData.customerInfo.customer;
						       $scope.configuartion.siteid = configuartionData.customerInfo.siteid;
						       $scope.configuartion.region = configuartionData.customerInfo.region;
						       $scope.configuartion.service = configuartionData.customerInfo.service;
						       $scope.configuartion.vendor = configuartionData.deviceInfo.vendor;
						       $scope.configuartion.deviceType = configuartionData.deviceInfo.deviceType;
						       $scope.configuartion.model = configuartionData.deviceInfo.model.toString();
						       $scope.configuartion.os = configuartionData.deviceInfo.os;
						       $scope.configuartion.osVersion = configuartionData.deviceInfo.osVersion.toString() ;
						       $scope.configuartion.managementIp = configuartionData.deviceInfo.managementIp;
						       $scope.configuartion.hostname = configuartionData.deviceInfo.hostname;
						       $scope.configuartion.c3p_interface.name = configuartionData.interfaceRoutingInfo.wanInterface.wanInterfaceName;
						       $scope.configuartion.c3p_interface.ip = configuartionData.interfaceRoutingInfo.wanInterface.wanInterfaceIP;
						       $scope.configuartion.c3p_interface.bandwidth = configuartionData.interfaceRoutingInfo.wanInterface.bandwidth.toString();
						       $scope.configuartion.c3p_interface.mask = configuartionData.interfaceRoutingInfo.wanInterface.wanInterfaceSubnetMask;
						       $scope.configuartion.c3p_interface.description = configuartionData.interfaceRoutingInfo.wanInterface.description;
						       $scope.configuartion.c3p_interface.encapsulation = configuartionData.interfaceRoutingInfo.wanInterface.encapsulation;
						       $scope.configuartion.loopBackType = configuartionData.interfaceRoutingInfo.loopbackInterface.loopInterfaceName;
						       $scope.configuartion.loopbackIPaddress = configuartionData.interfaceRoutingInfo.loopbackInterface.loopbackIPaddress;
						       $scope.configuartion.loopbackSubnetMask = configuartionData.interfaceRoutingInfo.loopbackInterface.loopbackSubnetMask;
						       $scope.configuartion.enablePassword = configuartionData.interfaceRoutingInfo.enablePassword;
						       $scope.configuartion.banner = configuartionData.interfaceRoutingInfo.banner;
						       $scope.configuartion.vrfName = configuartionData.interfaceRoutingInfo.vpn.vrfName;
						       $scope.configuartion.snmpString = configuartionData.interfaceRoutingInfo.snmp.snmpString;
						       $scope.configuartion.snmpHostAddress = configuartionData.interfaceRoutingInfo.snmp.snmpHostAddress;
						       $scope.configuartion.internetLcVrf.routingProtocol = configuartionData.interfaceRoutingInfo.routing.routingProtocol;
						       $scope.configuartion.internetLcVrf.AS = configuartionData.interfaceRoutingInfo.routing.asnumber;
						       $scope.configuartion.internetLcVrf.networkIp = configuartionData.interfaceRoutingInfo.routing.networkIP;
						       $scope.configuartion.internetLcVrf.networkIp_subnetMask = configuartionData.interfaceRoutingInfo.routing.networkMask;
						       $scope.configuartion.internetLcVrf.neighbor1 = configuartionData.interfaceRoutingInfo.routing.neighbour1Ip;
						       $scope.configuartion.internetLcVrf.neighbor2 = configuartionData.interfaceRoutingInfo.routing.neighbour2Ip;
						       $scope.configuartion.internetLcVrf.neighbor1_remoteAS = configuartionData.interfaceRoutingInfo.routing.neighbour1As;
						       $scope.configuartion.internetLcVrf.neighbor2_remoteAS = configuartionData.interfaceRoutingInfo.routing.neighbour2As;
						       setTimeout(function(){ $("#createCongigForm .ng-not-empty").attr("disabled","disabled").css({"opacity":"0.7", "cursor":"default"}); }, 10);
							   
							   wanInterfaceObj = InformationStatus.wanInterfaceObj,
							   lanInterface = InformationStatus.lanInterface,
							   loopbackInt = InformationStatus.loopbackInt,
							   vpnObj = InformationStatus.vpnObj,
							   routingObj = InformationStatus.routingObj,
							   snmp = InformationStatus.snmp,
							   banner = InformationStatus.banner,
							   enablePassword = InformationStatus.enablePassword;
							   $scope.optionList = [{value: "WAN Interface", selected:wanInterfaceObj},{value: "LAN Interface", selected: lanInterface},
								                     {value:"Loopback Interface", selected:loopbackInt}, {value:"VRF", selected:vpnObj}, {value:"Routing Protocol", selected:routingObj}, {value:"SNMP", selected:snmp}, {value:"Banner", selected:banner}, {value:"Enable Password",selected:enablePassword} ];
							   angular.forEach($scope.optionList, function(itm){ 
								   $scope.checkOptionSelect(itm.value, itm.selected);								   
							    });
							   if (wanInterfaceObj == true && loopbackInt == true && vpnObj == true && routingObj == true && snmp == true && banner == true && enablePassword == true){
								   $scope.isAllSelected = true;
							   }else{
								   $scope.isAllSelected = false;
							   }
							   closeAddPopUp('addImportPopUp');						   
							   
		            	   }else{
		            	   	alertPopUp('Error', response.data.entity.errormessage);
		            	   	$scope.resetImportedData();
		            	   }
		               }, function (error) {
		            	   alertPopUp('Error', 'Unable to load data');
		            	   $scope.resetImportedData();
		            });
				};
		
				$scope.openImportPopUp = function(){
					openAddPopUp('addImportPopUp');
					dragElement(document.getElementById(("addImportPopUpContainer")));	
				}
				
							$scope.resetConfiguration = function () {
								$scope.lanInterface = undefined;
								$scope.chkStatus = [];
								$scope.LoopbackInterface = undefined;
								$scope.VPN = undefined;
								$scope.rountingProtocol = undefined;
								$scope.SNMP = undefined;
								$scope.banner = undefined;
								$scope.enablePassword = undefined;
								$scope.isAllSelected = undefined;
								angular.forEach($scope.optionList, function(itm){ 
									if(itm.value == "Basic Configuration"){
		                            itm.selected = true;
		                            }else{
		                            itm.selected = undefined;
		                            }    

							    });
							}
							
							/*Reset Test & Turn Up Strategy*/
							$scope.resetCertificationTests = function () {
								angular.forEach($scope.certificationOptionList, function(itm){ 
							    	 itm.selected = false;
							    });
							}	
														
							/*Method to get list of configuration features */
							
							$scope.getConfigFeature = function(){
								openAddPopUp('interfaceRountingOptions');
								dragElement(document.getElementById(("interfaceRountingOptionsContainer")));	
								if ($scope.$parent.myFile){
									//$scope.optionList = $scope.$parent.optionList;
									$scope.$parent.myFile = "";
								}
							}
							
							$scope.getInterfaceRountingOptions = function() {
							      var Data = {
							        		region : $scope.configuartion.region,
											vendor : $scope.configuartion.vendor,
											model : $scope.configuartion.model,
											os : $scope.configuartion.os,
											osVersion : $scope.configuartion.osVersion
								        };
								        $http({
								        		url : localStorage.getItem('path') + "/GetAllRequestDashboardViewService/getListForConfigurationFeature",
								                method : "POST",
								                data : JSON.stringify(Data),
								                headers : {
								                    'Content-Type' : 'application/json'
								                }

								            }).then(function(response) {
								             if (response.data.entity.Result == "Success"){
								            	 $scope.messageFlag = false;
								            	 $scope.selectAllFlag = true;
								            	 $scope.optionList = JSON.parse(response.data.entity.featureList);
								            	 angular.forEach($scope.optionList, function(itm){ 
													   $scope.checkOptionSelect(itm.value, itm.selected);								   
												    });
								            	    
								             	}else{
								             		$scope.messageFlag = true;
								             		$scope.selectAllFlag = false;
								             		$scope.messageFlag = response.data.entity.Message;
								             	}
								            
								               
								        })

							}
							
							/*List of Test & Turn Up Strategy */
							$scope.certificationOptionList = [{value:"Interfaces status",selected: true, disabled: false},
							                                  {value:"Platform & IOS",selected: true,disabled: false}, {value:"WAN Interface",selected: false,disabled: true},{value : "BGP neighbor",selected:false,disabled: true}, {value:"Throughput",selected:false,disabled: true}, {value:"FrameLoss",selected:true,disabled: false}, {value:"Latency",selected:true,disabled: false} ];
							
								
						
							/*Method to get list of Test & Turn Up Strategy */
							$scope.getCPEcertificationOptions = function() {
								openAddPopUp('CPEcertificationOptions');
								dragElement(document.getElementById(("certificationOptionsContainer")));	
								//console.log($scope.certificationOptionList);
							}
							
							$scope.toggleAll = function() {
							     var toggleStatus = $scope.isAllSelected;
							     angular.forEach($scope.optionList, function(itm){ 
							    	 if(itm.value == "Basic Configuration"){
                                     itm.selected = true;
                                     }else{
                                     itm.selected = toggleStatus;
                                    }    
							   $scope.checkOptionSelect(itm.value, itm.selected);
							    });
							   
							  }
							$scope.checkOptionSelect= function(option, selected){
								
								if (option.toLowerCase() == 'WAN Interface'.toLowerCase()) {
									if (selected == true){
										$scope.WANInterface = true;
										angular.forEach($scope.certificationOptionList, function(itm){
											 if(itm.value == 'WAN Interface' || itm.value == 'Throughput' ){
												 itm.selected = true;
												 itm.disabled = false;
											 }
										});
									}else{
										$scope.WANInterface = false;										
										 angular.forEach($scope.certificationOptionList, function(itm){
											 if(itm.value == 'WAN Interface' || itm.value == 'Throughput' ){
												 itm.disabled = true;
												 itm.selected = false;
											 }
										});
										 $scope.configuartion.c3p_interface.name = "";
										 $scope.configuartion.c3p_interface.ip = "";
										 $scope.configuartion.c3p_interface.mask = "";
										 $scope.configuartion.c3p_interface.encapsulation = "";
									}
									
								}
								else if (option.toLowerCase() == 'LAN Interface'.toLowerCase()) {
									
									if (selected == true){
										$scope.lanInterface = true;
									}else{
										$scope.lanInterface = false;
										$scope.configuartion.lanTnterface = "";
										$scope.configuartion.lanIPaddress = "";
										$scope.configuartion.lanSubnetMask = "";
										$scope.configuartion.lanDescription = "";
									}
								}
								else if (option.toLowerCase() == 'Loopback Interface'.toLowerCase()) {
									
									if (selected == true){
										$scope.LoopbackInterface = true;
									}else{
										$scope.LoopbackInterface = false;
										$scope.configuartion.loopBackType = "";
										$scope.configuartion.loopbackIPaddress = "";
										$scope.configuartion.loopbackSubnetMask = "";
									}
								}
								else if (option.toLowerCase() == 'VRF'.toLowerCase()) {
									if (selected == true){
										$scope.VPN = true;
									}else{
										$scope.VPN = false;
										$scope.configuartion.vrfName = "";
									}
								}
								else if (option.toLowerCase() == 'Routing Protocol'.toLowerCase()) {
									if (selected == true){
										
										$scope.rountingProtocol = true;
										angular.forEach($scope.certificationOptionList, function(itm){
											if(itm.value == 'BGP neighbor'){
												 itm.selected = true;
												 itm.disabled = false;
											}
										});
									}else{
										$scope.rountingProtocol = false;
										angular.forEach($scope.certificationOptionList, function(itm){
											if(itm.value == 'BGP neighbor'){
												 itm.selected = false;
												 itm.disabled = true;
											 }
										});
										$scope.configuartion.internetLcVrf.routingProtocol = "";
										$scope.configuartion.internetLcVrf.AS = "";
										$scope.configuartion.internetLcVrf.networkIp = "";
										$scope.configuartion.internetLcVrf.networkIp_subnetMask = "";
										$scope.configuartion.internetLcVrf.neighbor1 = "";
										$scope.configuartion.internetLcVrf.neighbor1_remoteAS = "";
										$scope.configuartion.internetLcVrf.neighbor2 ="";
										$scope.configuartion.internetLcVrf.neighbor2_remoteAS = "";
									}
								}
								else if (option.toLowerCase() == 'SNMP'.toLowerCase()) {
									if (selected == true){
										$scope.SNMP = true;
									}else{
										$scope.SNMP = false;
										$scope.configuartion.snmpString = "";
										$scope.configuartion.snmpHostAddress = "";
									}
								}
								else if (option.toLowerCase() == 'Banner'.toLowerCase()) {
									if (selected == true){
										$scope.banner = true;
									}else{
										$scope.banner = false;
										$scope.configuartion.banner = "";
									}
								}
								else if (option.toLowerCase() == 'Enable Password'.toLowerCase()) {
									if (selected == true){
										$scope.enablePassword = true;
									}else{
										$scope.enablePassword = false;
										$scope.configuartion.enablePassword = "";
									}
								}
							}
							/*method to show Configuration Features*/
							$scope.showHideConfiguration = function (option,chkStatus) {
								$scope.isAllSelected = $scope.optionList.every(function(itm){return itm.selected;})
								$scope.checkOptionSelect(option.value, chkStatus);
									
							}
							
							$scope.resetImportedData = function(id){
								var $el = $(id);
							      $el.wrap('<form>').closest('form').get(0).reset();
							      $el.unwrap();
							}
							$scope.resetForm = function(id) {
								$scope.$parent.myFile = "";
								$scope.autoHostName = true;
								$('.ng-invalid-required').css('border','');
								$('input').css('background-color','');
								$('select').css('background-color','');
								$('textarea').css('background-color','');
							      $(".ng-not-empty").removeAttr("disabled").css({"opacity": "1"});
								$scope.configuartion = {
									    'c3p_interface': {
									      'ip': '',
									    'mask': ''
									    
									    },
									    'internetLcVrf':{
									    	'networkIp' : '',
									    }
									    }
								$scope.customnameforhostname = "";
								$scope.deviceTypeforhostname = "";
								$scope.siteidforhostname = "";
								$scope.regionforhostname = "";
								$scope.vendorforhostname = "";
								$scope.modelforhostname = "";
								$scope.computedhostname = "";
								$scope.configuartion.managementIp="";
								$scope.ipamip = "";
								$scope.configuartion.model="";
								$scope.configuartion.importFile = '1';
								$scope.configuartion.networkType = 'Legacy';
								$scope.resetConfiguration();
								$scope.resetImportedData(id);
								}
							
							$scope.getGenerateConfigratioData = function(testType, report_title, event) {
								$rootScope.editReqDetailsFlag = false;
								$rootScope.modifyScheduler = false
								event.stopPropagation();
								event.stopImmediatePropagation();
								$scope.report_title = report_title;
								
								var requiredValidationFlag =false;	  
								 $('input, select').each(function(index, item) {
									if ($(item).hasClass("ng-invalid-required")) {
										requiredValidationFlag = true; 
									}
								});
								 
								

								 if(requiredValidationFlag){
									 $('.ng-invalid-required').css('border','1px solid rgb(222, 52, 52)');
										$('#createCongigForm').css('border','none');
										alertPopUp('Create Configuration','Please fill all the mandatory fields');
										event.preventDefault();
								  }
								  else{
									  var validationFlag="";
									  $('input').each(function(index, item) {
										  if ($(item).hasClass("ng-invalid") && $(item).hasClass("ng-valid-required")) {
										    	validationFlag = true; 
										    }
										});
								
									if(validationFlag){
										  alertPopUp('IP Management','Please enter valid input in field.')
										  event.preventDefault();
									
								 }
								 else{
									
										angular.forEach($scope.certificationOptionList, function(itm){ 
											 if(itm.selected){
												 itm.selected = 1;
											 }else{
												 itm.selected = 0;
											}
											 $scope.configuartion.certificationOptionListFlags[itm.value] = itm.selected;
											
											 
										});
								$http(
										{

											url : localStorage.getItem('path') + "/GetConfigurationTemplate/createConfigurationTemplate",

											//url : "http://localhost:8023/ConfigMngmntService/createConfigurationDcm",
											method : "POST",
											data : JSON
													.stringify($scope.configuartion),
											headers : {
												'Content-Type' : 'application/json'
											}

										}).then(function(response) {
									//console.log("In response")
									$scope.loading = false;
									localStorage.setItem("configuartion", JSON.stringify($scope.configuartion));
									$scope.getReportdata = response.data.output;
									$scope.getdata =$scope.getReportdata;
									var cusReport = angular.element(document.querySelector( '#reportContainer' ) );
									cusReport.html($scope.getdata);
									//console.log(response);

								})
								openAddPopUp('generateConfigPopUp');
								dragElement(document.getElementById(("generateConfigPopUpContainer")));	
								}
							 }
							
							}
							
							$scope.saveConfiguration = function(event) {
								if($rootScope.editReqDetailsFlag){
									$rootScope.saveConfigurationForEdit(event);
								}
								else{
								$scope.createConfSubmitted = true;
								var configuartion = JSON.parse(localStorage.getItem('configuartion'));
								$http(
										{

											url : localStorage.getItem('path') + "/ConfigMngmntService/createConfigurationDcm",

											//url : "http://localhost:8023/ConfigMngmntService/createConfigurationDcm",
											method : "POST",
											data : JSON
													.stringify(configuartion),

											headers : {
												'Content-Type' : 'application/json'
											}

										}).then(function(response) {
									//console.log("In response")
									$scope.loading = false;
									event.stopImmediatePropagation();
									bootbox.confirm({
										title : 'Request Status',
										message : 'Configuration Submitted',
										closeButton: false,
										buttons : {
												
											confirm : {
												label : 'Ok',
												className : 'btn-default'
											},

										},
										callback : function() {
											$state.go('dashboardReqDetails', { dashboardReq_Details: response.data.requestId,version: response.data.version});
										}
									});
									//console.log(response)

								})
								}
							}
							
						$scope.getEIPAMIP = function() {
								//console.log("in get IPAM IP method")
								var Data = {
									site : $scope.configuartion.siteid,
									customer : $scope.configuartion.customer,
									service : $scope.configuartion.service,
									region : $scope.configuartion.region
								};
								//console.log(JSON.stringify(Data))
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
													//console.log(response)

													$scope.wanIPvalue = response.data.entity.output.ip;
													$scope.configuartion.c3p_interface.ip=$scope.wanIPvalue;
													$scope.wanIPMask = response.data.entity.output.mask;
													$scope.configuartion.c3p_interface.mask=$scope.wanIPMask;
													if(!$scope.wanIPvalue){
														$scope.wanIPvalueErrorMessege = response.data.entity.output;
														alertPopUp('IP Management',$scope.wanIPvalueErrorMessege);
														
													}else{
														$scope.onWanIpChange();
													}
													
												})
												
								$scope.onWanIpChange = function() {
									//$scope.configuartion.internetLcVrf= {};
									if ($scope.wanIPvalue == ""){
										$scope.neighbor1 = "";
										$scope.configuartion.internetLcVrf.neighbor1 =$scope.neighbor1;
									}else
										{
										var ipAdress =$scope.wanIPvalue;
										var parts = ipAdress.split('.');
										var lastDigit = parts.pop();
										var ipLastdigit = parseInt(lastDigit) +1;
										var new_text = parts.join('.');
										var ipadressUpdated = new_text + "." + ipLastdigit
										$scope.neighbor1 = ipadressUpdated;
										$scope.configuartion.internetLcVrf.neighbor1 = $scope.neighbor1;
									}
								}
								
							}

							$scope.onCustomerNameChange = function() {
								if($scope.autoHostName){
									if ($scope.configuartion.customer.length >= 0) {
										$scope.customnameforhostname = $scope.configuartion.customer.split(' ').join('').substring(0, 4);
	
										//console.log("Substring name" + $scope.customnameforhostname)
									}
									if ($scope.varHostname) {
										$scope.varHostname="";
										$scope.configuartion.hostname="";
										$scope.generateHostname();
									}
								}else{
									//console.log("In file import");
								}
							}
							$scope.onSiteIDChange = function() {
								if($scope.autoHostName){
									if ($scope.configuartion.siteid.length >= 0) {
										$scope.siteidforhostname = $scope.configuartion.siteid.split(' ').join('').substring(0, 2);
	
										//console.log("Substring site" + $scope.siteidforhostname)
									}
									if ($scope.varHostname) {
										$scope.varHostname="";
										$scope.configuartion.hostname="";
										$scope.generateHostname();
									}
								}else{
									//console.log("In file import");
								}
							}
							$scope.onRegionChange = function() {
								if($scope.autoHostName){
									if ($scope.configuartion.region.length >= 2) {
										$scope.regionforhostname = $scope.configuartion.region
												.substring(0, 2);
									}
									//console.log("Substring region" + $scope.regionforhostname)
									if ($scope.varHostname) {
										$scope.varHostname="";
										$scope.configuartion.hostname="";
										$scope.generateHostname();
									}
								}else{
									//console.log("In file import");
								}
							}
							$scope.OsChange = function() {
								
								$scope.configuartion.osVersion ="";
								}
							$scope.onDeviceChange = function() {
								if($scope.autoHostName){
									$scope.configuartion.model ="";
									$scope.configuartion.os="";
									$scope.configuartion.osVersion ="";
									$scope.varHostname="";
									$scope.configuartion.hostname = $scope.varHostname;
									$scope.deviceTypeforhostname = $scope.configuartion.deviceType
											.substring(0, 1);
									if ($scope.varHostname) {
										$scope.configuartion.hostname="";
										$scope.generateHostname();
										$scope.generateHostname();
									}
								}else{
									//console.log("In file import");
								}
							}
							$scope.onVendorChange = function() {
								if($scope.autoHostName){
										$scope.varHostname="";
										$scope.configuartion.hostname = $scope.varHostname;
										$scope.configuartion.os="";
										$scope.configuartion.deviceType ="";
										$scope.configuartion.model ="";
										$scope.configuartion.osVersion ="";
										if ($scope.configuartion.vendor.length >= 2) {
											$scope.vendorforhostname = $scope.configuartion.vendor
													.substring(0, 2);
										}
										//console.log("Substring device" + $scope.vendorforhostname)
										if ($scope.varHostname) {
											$scope.configuartion.hostname="";
											$scope.generateHostname();
										}
									}else{
										//console.log("In file import");
									}	
							}
                            
							$scope.onModelChange = function() {
							if($scope.autoHostName){
								$scope.configuartion.c3p_interface.name ="";
								$scope.configuartion.osVersion ="";
								$scope.configuartion.os="";
								$scope.varHostname="";
								$scope.configuartion.lanTnterface = "";
								$scope.configuartion.c3p_interface.encapsulation = "";
								$scope.configuartion.loopBackType = "";
								$scope.configuartion.c3p_interface.speed = "";
								$scope.configuartion.c3p_interface.bandwidth = "";
								$scope.configuartion.hostname = $scope.varHostname;
								$scope.modelforhostname = $scope.configuartion.model
								$scope.generateHostname();
								
							}else{
								//console.log("In file import");
							}

							}
							
							$scope.resetVrfName = function() {

								$scope.configuartion.vrfName = "";
								$("#vrfName").addClass('disabled');
							}
							
							$scope.resetVrfName = function() {

								$scope.configuartion.vrfName = "";
								$("#vrfName").addClass('disabled');
							}
//							
							
							$scope.onLoopbackChange = function() {
								if($scope.configuartion.loopBackType != ''){
									$scope.configuartion.loopbackSubnetMask = "255.255.255.255";
								}
							}
							$scope.generateHostname = function() {
								if($scope.configuartion.model != '' && $scope.configuartion.model != undefined ){
									if ($scope.regionforhostname) {
										$scope.computedhostname = $scope.regionforhostname;
									}
									if ($scope.customnameforhostname) {
										$scope.computedhostname = $scope.computedhostname
												.concat($scope.customnameforhostname)
									}
									if ($scope.modelforhostname){
										$scope.computedhostname = $scope.computedhostname
										.concat($scope.modelforhostname)
									}
										
									if ($scope.siteidforhostname) {
										$scope.computedhostname = $scope.computedhostname
												.concat($scope.siteidforhostname)
									}
									$scope.computedhostname = $scope.computedhostname
											.concat("01")
	
									if ($scope.deviceTypeforhostname) {
										$scope.computedhostname = $scope.computedhostname
												.concat($scope.deviceTypeforhostname)
									}
									$scope.varHostname = $scope.computedhostname;
									$scope.configuartion.hostname=$scope.varHostname;
								}
							}

						} ])
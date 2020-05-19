C3PApp.controller('templateMgtController',['$scope','$rootScope','$http', '$state', 
                  'dataFactory', 'activeTabs',function($scope, $rootScope, $http, $state, dataFactory, activeTabs) {
                  
                  
	/// Higlighted Active tab goes here--------
	activeTabs.activeTabsFunc('Admin');
	$rootScope.menubar = activeTabs.menubar;
	/// Higlighted Active tab goes here--------
	
	
	$scope.SearchItems = [ "Template ID", "Device Type", "Vendor","Model","OS","OS Version" ];
	$scope.searchField = $scope.SearchItems[0];
    $scope.searchInput = ""
    $scope.sortKey = "";
    $scope.reverse = false;
    $scope.requestsList = "";
    $scope.rowCollection = '';
    $scope.getNewConfig = false;
    $scope.deviceDetails = false;
    $scope.configFeature = false;
    $scope.finalView = false;
    $scope.templateTags = "";
    $scope.filePath = 'jsp/js/templates/templateAdminManagement.html';
	 var newitm={
	   			name :'',
	   			id : '',
	   			checked :''
	   		},
	   	    validateInputArray = [],
	   	    checkMandateFlag = false,
	   	    apiPathArray = ['getVendor','getRegion'], idSelectAllFlag = false;
	$scope.regionDropDown = [];
	$scope.vedorDropDown = [];
	$scope.deviceDropDown = [];
	$scope.modelDropDown = [];
	$scope.osDropDown = [];
	$scope.osVersionDropDown = [];
	$scope.deviceTypeDropDown = [];
	$scope.model = "";
	$scope.devicetype = "";
	$scope.vendor = "";
	$scope.os = "";
	$scope.osversion = "";
	 $scope.configuartion = {
			 'vendor':'',
			 'deviceType':'',
			 'model':'',
			 'os':'',
			 'osVersion':'',
			 'region':''
	 };
	 
	 $scope.closeReportPopUp = function(id) {
		$(id).css("display", "none");
	}
	 
	 $scope.getDupeTempIdEvent = function(id){
		 $scope.configuartion.vendor = "";
		 $scope.configuartion.deviceType = "";
		 $scope.configuartion.model = "";
		 $scope.configuartion.os = "";
		 $scope.configuartion.reagion = "";
		 $scope.configuartion.osVersion = "";
		 $scope.osVersionDropDown = [];
		 $scope.deviceDropDown = [];
		 $scope.modelDropDown = [];
		 $scope.osDropDown = [];
		 
		 $scope.closeReportPopUp(id);
	 }
	 
	 /*Method for Template record search */
	    $scope.templateSearchRequest = function() {
	    	if(this.searchInput == ''){
	    		$scope.errorMessege = false;
	    		$scope.getTemplateList();
	    	}
	    	else{
	        var Data = {
        		key : this.searchField,
				value : this.searchInput
	            //page : 'viewpage'
	        };
	        $http({
	        		url : localStorage.getItem('path') + "/SearchTemplateList/search",
	                method : "POST",
	                data : JSON.stringify(Data),
	                headers : {
	                    'Content-Type' : 'application/json'
	                }

	            }).then(function(response) {
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
	    
	    /*Method to get parentFatureList*/
	    $scope.parentFatureData = function() {
	    	
	        $http({
	        		url : localStorage.getItem('path') + "/GetTemplateConfigurationData /getParentFeatureList ",
	                method : "GET",
	                headers : {
	                    'Content-Type' : 'application/json'
	                }

	            }).then(function(response) {
	            	$scope.parentFatureList =  JSON.parse(response.data.entity.output);
	                 
	        })
	    	}
	   

	/* $scope.getVendorDeviceType = function(methodType, deviceDetailsPath){
		 
	 angular.forEach(deviceDetailsPath, function(paths){
		 
		 if (methodType == 'GET'){
				 var uploadUrl = localStorage.getItem('path') + "/GetConfigurationData/"+paths,
					transformRequest = "", 
					headers = {
						'Content-Type' : 'application/json'
					};
				 
				 dataFactory.getDataFact(uploadUrl, Data, transformRequest, headers)
			        .then(function(response){		            	
			        	if (response.data.entity.output){		            		
			        		if (paths == 'getVendor'){		            			
			        			$scope.vedorDropDown = JSON.parse(response.data.entity.output)
			        		}else if (paths == 'getRegion'){
			        			$scope.regionDropDown = JSON.parse(response.data.entity.output)
			        		}            		
			        	}else{
			        		alertPopUp('Error', 'Data not found');
			        	}
			     }, function (error) {
			    	   alertPopUp('Error', 'Unable to load data');
			    }); 
					 
		 } else if (methodType == 'POST'){
		 	 
			 var uploadUrl = localStorage.getItem('path') + "/GetConfigurationData/"+paths,
				transformRequest = "", 
				headers = {
					'Content-Type' : 'application/json'
				};
			 
				 
			 if (paths == 'getDeviceType' && $scope.configuartion.vendor){
				 var vendor = {									
							"vendor": $scope.configuartion.vendor
					},
				 Data = JSON.stringify(vendor);
				 
				 $scope.configuartion.deviceType = "";
				 $scope.configuartion.model = "";
				 $scope.configuartion.os = "";
				 $scope.configuartion.osVersion = "";
				 $scope.osVersionDropDown = [];
				 $scope.deviceDropDown = [];
				 $scope.modelDropDown = [];
				 $scope.osDropDown = [];
				 
			 }else if (paths == 'model' && $scope.configuartion.deviceType && $scope.configuartion.vendor){
				 
				 var model = {									
							"vendor": $scope.configuartion.vendor,
							"deviceType": $scope.configuartion.deviceType.toLowerCase()
					},
				 Data = JSON.stringify(model);
				 $scope.configuartion.model = "";
				 $scope.configuartion.os = "";
				 $scope.configuartion.osVersion = "";
				 $scope.osVersionDropDown = [];
				 $scope.modelDropDown = [];
				 $scope.osDropDown = [];
			 }else if (paths == 'os' && $scope.configuartion.deviceType && $scope.configuartion.vendor){
					 
					 var os = {					
							 "deviceType": $scope.configuartion.deviceType.toLowerCase(),
							 "make": $scope.configuartion.vendor
								
						},
					 Data = JSON.stringify(os);
					 
					 $scope.configuartion.osVersion = "";
					 $scope.osVersionDropDown = [];
					 
			 }else if (paths == 'osVersion' && $scope.configuartion.os){
				 
				 var osVersion = {									
							"os": $scope.configuartion.os,
							"model": $scope.configuartion.model
					},
				 Data = JSON.stringify(osVersion);		
				 
			 }else if($scope.configuartion.vendor == undefined){
				 $scope.configuartion.deviceType = "";
				 $scope.configuartion.model = "";
				 $scope.configuartion.os = "";
				 $scope.configuartion.osVersion = "";
				 $scope.osVersionDropDown = [];
				 $scope.deviceDropDown = [];
				 $scope.modelDropDown = [];
				 $scope.osDropDown = [];
				 return false;
			 }else if ($scope.configuartion.deviceType == undefined){
				 $scope.configuartion.model = "";
				 $scope.configuartion.os = "";
				 $scope.configuartion.osVersion = "";
				 $scope.osVersionDropDown = [];
				 $scope.modelDropDown = [];
				 $scope.osDropDown = [];
				 return false;
			 }else if ($scope.configuartion.os == undefined){
				 $scope.configuartion.osVersion = "";
				 $scope.osVersionDropDown = [];
				 return false;
			 }
			 
			 dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
	            .then(function(response){
	            	if (JSON.parse(response.data.entity.output).length > 0){
	            		if (paths == 'getDeviceType'){
	            			
	            			$scope.deviceDropDown = JSON.parse(response.data.entity.output);
	            		}else if (paths == 'model'){
	            			
	   					 	$scope.modelDropDown = JSON.parse(response.data.entity.output);
	   				 }else if (paths == 'os'){
	            			
	   					 	$scope.osDropDown = JSON.parse(response.data.entity.output);
	   				 }else if (paths == 'osVersion'){
	            			
	   					 	$scope.osVersionDropDown = JSON.parse(response.data.entity.output);
	   				 }
	            		$scope.configuartionFlag = true;
	            	}else if (this.configuartion == undefined){
	            		$scope.configuartionFlag = false;
	            	}else {
	            		alertPopUp('Error', 'Data not found');
	            	}
	         }, function (error) {
	        	   alertPopUp('Error', 'Unable to load data');
	        }); 
		 
		 
	 }
	});   
}*/
	 
	//$scope.getVendorDeviceType('GET', apiPathArray);
    
	$scope.getRegionList= function() {
		$http({
			url : 'http://localhost:8080/C3P/regions',
			method: 'GET'
		}).then(function(response) {
			console.log("Regions Response is ::"+response);
			if(response.data.entity && response.data.entity.length > 0) {
				for(var i=0; i< response.data.entity.length; i++) {
					$scope.regionDropDown.push(response.data.entity[i]);
				}
			} else {
				alertPopUp('Error','Unable to get data');
				return false;
			}
		});
	}
	
	$scope.getVendorList = function() {
		$http({
			url : 'http://localhost:8080/C3P/vendor',
			method: 'GET'
		}).then(function(response) {
			console.log("Vendor Response is ::"+response);
			if(response.data.entity && response.data.entity.length > 0) {
				for(var i=0; i< response.data.entity.length; i++) {
					$scope.vedorDropDown.push(response.data.entity[i]);
				}
			} else {
				alertPopUp('Error','Unable to get data');
				return false;
			}
		});
	}
	
	$scope.getDeviceTypeOnVendor = function(selectedVendor) {
		sVendor = selectedVendor;
		if(selectedVendor != undefined && selectedVendor != "undefined" && selectedVendor != "") {
			$scope.deviceTypeDropDown = [];
			$scope.osDropDown = [];
			$scope.modelDropDown = [];
			$scope.osVersionDropDown = [];
			$http({
				url : 'http://localhost:8080/C3P/devicetype?vendor=' + selectedVendor,
				method: 'GET'
			}).then(function(response) {
				console.log("Vendor-DT Response is ::"+response);
				if(response.data.entity && response.data.entity.length > 0) {
					for(var i=0; i< response.data.entity.length; i++) {
						$scope.deviceTypeDropDown.push(response.data.entity[i]);
					}
				} else {
					alertPopUp('Error','No Model is associated for selected Device type and Vendor, please visit Global List Management for the association');
					return false;
				}
			});
			
			$http({
				url : 'http://localhost:8080/C3P/os?vendor=' + selectedVendor,
				method: 'GET'
			}).then(function(response) {
				console.log("Vendor-DT Response is ::"+response);
				if(response.data.entity && response.data.entity.length > 0) {
					for(var i=0; i< response.data.entity.length; i++) {
						$scope.osDropDown.push(response.data.entity[i]);
					}
				} else {
					alertPopUp('Error','No OS is associated for selected Vendor, please visit Global List Management for the association');
					return false;
				}
			});
		} else {
			$scope.deviceTypeDropDown = [];
			$scope.configuartion.deviceType = "";
			//$scope.devicetype = "slct";
			$scope.osDropDown = [];
			$scope.configuartion.os = "";
			//$scope.os = "slct";
			$scope.osVersionDropDown = [];
			$scope.configuartion.osversion = "";
			$scope.modelDropDown = [];
			$scope.configuartion.model = "";
		}
		
	}
	//"/model?devicetype=" + selectedDeviceType + "&vendor=" + selectedVendor;
	$scope.getModelOSonDeviceType = function(selectedDevicetype, selectedVendor) {
		
		$http({
			url : 'http://localhost:8080/C3P/model?devicetype=' + selectedDevicetype + "&vendor=" + selectedVendor,
			method: 'GET'
		}).then(function(response) {
			console.log("Vendor-DT Response is ::"+response);
			if(response.data.entity && response.data.entity.length > 0) {
				for(var i=0; i< response.data.entity.length; i++) {
					$scope.modelDropDown.push(response.data.entity[i]);
				}
			} else {
				alertPopUp('Error','No Model is associated for selected Device type and Vendor, please visit Global List Management for the association');
				return false;
			}
		});
	}
	
	
	$scope.getOSVersionOnOS = function(selectedOS) {
	$scope.osVersionDropDown = [];
		if(selectedOS != undefined && selectedOS != "undefined" && selectedOS != "") {
			$http({
				url : 'http://localhost:8080/C3P/osversionforos?os=' + selectedOS,
				method: 'GET'
			}).then(function(response) {
				console.log("Vendor-DT Response is ::"+response);
				if(response.data.entity && response.data.entity.length > 0) {
					for(var i=0; i< response.data.entity.length; i++) {
						$scope.osVersionDropDown.push(response.data.entity[i]);
					}
				} else {
					alertPopUp('Error','No OS Version is assoicated for selected OS, please visit Global List Management for the association');
					return false;
				}
			});
		} else {
			$scope.osVersionDropDown = [];
		}
		
		
	}
	
    $scope.getNewConfigTemplate = function(templateName,ediTemplate,editTemplateId,templateVersion){
    	/*navigate to edit configuration feature page of template */
    	if (ediTemplate === true){
    	$scope.ediTemplate = true;
  	    $scope.templateId = editTemplateId;
  	    $scope.templateVersion = templateVersion;
  	    $scope.configTemplateName= "";
  	  	$scope.deviceDetails = false;
		$scope.configFeature = true;
	    $scope.finalView = false;
	    $scope.configBarWidth = "50%";
	    $scope.templateTags = "Configuration Feature";		        	    
	    $scope.configTemplateName = 'jsp/js/templates/configFeatureTemp.html';
	    $scope.loading = true;
     	var Data = {
        		"templateid" : $scope.templateId,
				"templateVersion" : $scope.templateVersion
	        };
	        $http({
		    		url : localStorage.getItem('path') + "/GetTemplateConfigurationData/getDataOnEdit",
		            method : "POST",
		            data : JSON.stringify(Data),
		            headers : {
		                'Content-Type' : 'application/json'
		            }
		
		        }).then(function(response) {
		        		console.log("In response");
		        		$scope.loading = false;
		        		$scope.nodes = JSON.parse(response.data.entity.left);
							angular.forEach($scope.nodes, function(parentItm){
								if(parentItm.childList.length > 0){
									 angular.forEach(parentItm.childList, function(itm){
										  if(itm.checked == true){
											  allSelected = true;
										  }else{
											  allSelected = false;
										  }
									  })
									if(allSelected != false){
   									  parentItm.checked = true;
   								  }
								}
							if(parentItm.name == 'Basic Config1'){
								parentItm.name = 'Basic Configuration';
							}
							$rootScope.isSelectAll($scope.nodes);
						})
						$scope.getConfigurationData = JSON.parse(response.data.entity.right);
						$scope.templateVersion = response.data.entity.version;
		    })
     	  $scope.getConfigurationFeaturesForEdit();	
		  $scope.filePath = 'jsp/js/templates/'+templateName;
      	} else {
      		$scope.filePath = 'jsp/js/templates/'+templateName;
        	
        	if (templateName == "templateConfigManagement.html"){
        		$scope.deviceDetails = true;
        		$scope.templateTags = "Device Details (Mandatory)";
        		
        	}else if (templateName == "templateAdminManagement.html"){
        		$scope.loading = true;
        		sessionStorage.setItem("newfeatureArray", new Array());
        		var dataFinalViewArray = [],
        	    dataFinalViewStr = "";
        	    angular.forEach($scope.getConfigurationData, function(items){
        	    	if (items.checked.toString() == "true"){
        	    		angular.forEach(items.list, function(confItems){
        	    			dataFinalViewArray.push(confItems.command_value);
        	    		});
        	    	}
        	    });
        	    if(this.finalViewComment != undefined){
        	    	var templateComment = $rootScope.userInfo.username + " : " + new Date().toLocaleDateString() +" , " + new Date().toLocaleTimeString() + " " +this.finalViewComment; 
        	    }
        	    dataFinalViewStr = dataFinalViewArray.join("");
        		if($scope.ediTemplate == true){
        			data = {
        			 "templateid": $scope.templateId,
    				 "templateVersion" : $scope.templateVersion,
    				 "templateData": dataFinalViewStr,
 	       	    	 "templateComment": templateComment,
        			}
    			}else{
    			data = {
	       	    	"templateid": $scope.templateId,
	       	    	"templateData": dataFinalViewStr,
	       	    	"templateComment": templateComment,
	       	    	"vendor": $scope.configuartion.vendor,
				    "deviceType": $scope.configuartion.deviceType.toLowerCase(),						 
				    "model": $scope.configuartion.model,
				    "deviceOs": $scope.configuartion.os,
				    "osVersion": $scope.configuartion.osVersion,
				    "region": $scope.configuartion.reagion						
    				}
    			}
        	    var uploadUrl = localStorage.getItem('path') + "/createTemplate/saveTemplate",
    			transformRequest = "", 
    			headers = {
    				'Content-Type' : 'application/json'
    			},
    			 Data = JSON.stringify(data);
    			 dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
    		        .then(function(response){
    		        	 $scope.requestsList=JSON.parse(response.data.entity.templateList);
    		        	if (response.data.entity.output){
    		        		$scope.loading = false;
     		        		location.reload();
     		        		
    		        	}else{
    		        		alertPopUp('Error', 'Data not saved');
    		        		return false;
    		        	}
    		     }, function (error) {
    		    	   alertPopUp('Error', 'Unable to save data');
    		    	   return false
    		    });
    			
        	}
        	$scope.configuartion = {};
        	$scope.osVersionDropDown = [];
    		 $scope.deviceDropDown = [];
    		 $scope.modelDropDown = [];
    		 $scope.osDropDown = [];
        	$scope.getConfigTemplates('deviceDetailsTemp.html');
      	}
    	
    }
    
    $scope.configTemplateName = 'jsp/js/templates/deviceDetailsTemp.html'
    $scope.configBarWidth = "0%"
  /* Validation for device Details page */
    $scope.checkMandateInputs = function(mandateCheck){
    	if (mandateCheck === true){
			if ($("input, select").hasClass("ng-empty") == true && $("input, select").attr("required") == "required"){
				validateInputArray = $("input.ng-empty, select.ng-empty");
				angular.forEach(validateInputArray, function(item){
					if (validateInputArray.length > 0){
						
						$(item).css({"border": "1px solid rgb(222, 52, 52)"});
						checkMandateFlag = true;
					}else{
						checkMandateFlag = false;
					}					
				})
				validateInputArray = []
			} else{
				checkMandateFlag = false;
			}
    	}
    }
    	
    $scope.getConfigTemplates = function(templateName, mandateCheck){
    	var requiredValidationFlag =false;	  
		 $('input, select').each(function(index, item) {
			if ($(item).hasClass("ng-invalid-required")) {
				requiredValidationFlag = true; 
			}
		});
		 if(requiredValidationFlag){
			 $('.ng-invalid-required').css('border','1px solid rgb(222, 52, 52)');
			 $('#ConfigMgmtForm').css('border','none');
				alertPopUp('Device Details','Please fill all the mandatory fields');
				event.preventDefault();
		  }else{
    	
    	if (mandateCheck === true){
    		$scope.loading = true;
    		$scope.checkMandateInputs(mandateCheck);
    		if (checkMandateFlag == true){
    			return false;
    		}else {
    		sessionStorage.setItem("newfeatureArray", new Array());
			var uploadUrl = localStorage.getItem('path') + "/createTemplate/add",
			transformRequest = "", 
			headers = {
				'Content-Type' : 'application/json'
			},
			data = {
					 "vendor": $scope.configuartion.vendor,
					 "deviceType": $scope.configuartion.deviceType.toLowerCase(),						 
					 "model": $scope.configuartion.model,
					 "deviceOs": $scope.configuartion.os,
					 "osVersion": $scope.configuartion.osVersion,
					 "region": $scope.configuartion.reagion
						
				},
			Data = JSON.stringify(data);
			$scope.alertErrorMessage = "";
			 dataFactory.postDataFact(uploadUrl, Data, transformRequest, headers)
		        .then(function(response){
		        	if (response.data.entity.error === "" ){		            		
		        		$scope.templateId = response.data.entity.templateID;
		        		$scope.configurationData();
		            	$scope.getConfigurationFeatures();
		            	$scope.loading = false;  
	        	    if (templateName == "configFeatureTemp.html"){
	              		$scope.deviceDetails = false;
	              		$scope.configFeature = true;
	              	    $scope.finalView = false;
	              	    $scope.configBarWidth = "50%"
	              	    $scope.templateTags = "Configuration Feature";
	              	    $scope.configTemplateName = 'jsp/js/templates/'+templateName;
	              	}
            	  
		        	}else{
		        		$scope.loading = false;  
		        		$scope.alertErrorMessage = 'Template ID: "'+response.data.entity.templateID+'" is already present in the system, do you want to modify?';
		        		$("#duplicateTemIdPopup").css("display","block")
		        		return false;
		        	}
			     }, function (error) {
			    	   alertPopUp('Error', 'Unable to save data');
			    	   return false
			    });
				 
    		}    		
    	} 
    	
    	if (templateName == "deviceDetailsTemp.html"){
    		$scope.deviceDetails = true;
    		$scope.configFeature = false;
    	    $scope.finalView = false;
    	    $scope.configBarWidth = "0%";
    	    $scope.templateTags = "Device Details (Mandatory)";
    	    $scope.configTemplateName = 'jsp/js/templates/'+templateName;
	    	}
    	else if (templateName == "finalViewTemp.html"){
    		
    		$scope.saveDataOnNext = function (templateName){
    			var newfeatureArrayList = sessionStorage.getItem("newfeatureArray");
    			if(newfeatureArrayList !="" && newfeatureArrayList != null ){
    				newfeatureArrayList = JSON.parse(sessionStorage.getItem("newfeatureArray"));
    				
    			}
        		var Data = {
    					templateID: $scope.templateId,
    					version : $scope.templateVersion,
    					newfeatureArray : newfeatureArrayList
        			};
        				$http(
        			{
        				url : localStorage.getItem('path') + "/GetTemplateConfigurationData/nextOnTemplateManagement",
        				method : "POST",
        				data : JSON.stringify(Data),
        				headers : {
        					'Content-Type' : 'application/json'
        				}
        				})
        				.then(
    						function(response) {
    							if (response.data.entity.output){
    								console.log(response)
    								$scope.getConfigurationData = JSON
    								.parse(response.data.entity.output);
    	  							
    							}else{
    								console.log("Unable to get Configuration data");
    								alertPopUp('Error ', 'Unable to get Configuration feature');
    			        		return false;
    							}
        							
        					})
        		$scope.deviceDetails = false;
        		$scope.configFeature = false;
        	    $scope.finalView = true;
        	    $scope.configBarWidth = "100%"
        	    $scope.templateTags = "Final View";
        	    $scope.configTemplateName = 'jsp/js/templates/'+templateName;
        		
    		}
    		if($scope.ediTemplate == true ){
    			$scope.editItmFlag;
    			angular.forEach($scope.editnodes, function(editItm){
					angular.forEach($scope.nodes, function(itm){
						if(editItm.name == itm.name && itm.childList.length == 0){
							if(editItm.checked != itm.checked){
								$scope.editItmFlag = true;
							}
						}
						angular.forEach(itm.childList, function(editItm){
							angular.forEach(editItm.childList, function(itm){
								if(editItm.name == itm.name){
									if(editItm.checked != itm.checked){
										$scope.editItmFlag = true;
									}
								}
							})
						})
						})
					})
			if($scope.editItmFlag){
    				bootbox.confirm({
        		        title : 'Message',
        		        message: "Do you want to save changes?",
        		        closeButton: false,
        		            buttons: {          
        		                confirm: {
        		                    label: 'No',
        		                    className: 'btn-default'
        		                },
        		                 cancel: {
        		                    label: 'Yes',
        		                    className: 'btn-primary'
        		                }
        		            },
        		            callback: function (result) {
        		                if(result)
        		                	{
        		                	$scope.confirmEdit();
            		               }else{
            		             		$scope.$apply(function(){
            		             			$scope.saveDataOnNext(templateName);
                		            	});
            		             	}
        		            }
        		        });
    			}else{
    				bootbox.confirm({
        		        title : 'Message',
        		        message: "There are no changes in the template.Do you still want to save the template?",
        		        closeButton: false,
        		            buttons: {          
        		                confirm: {
        		                    label: 'No',
        		                    className: 'btn-default'
        		                },
        		                 cancel: {
        		                    label: 'Yes',
        		                    className: 'btn-primary'
        		                }
        		            },
        		            callback: function (result) {
    		                if(result)
    		                	{
    		                	    $scope.filePath = 'jsp/js/templates/'+templateName;
        		                    location.reload();
    		                	}else{
    		                		$scope.$apply(function(){
    		                		$scope.saveDataOnNext(templateName);
    		                	});
    		                	}
        	                }
        		        });
    						
    	    	}
    		}
    		else{
    			$scope.saveDataOnNext(templateName);
    		}
    	}
    	
    	if (idSelectAllFlag === true){
    		$scope.isChooseAll = true;
    	}else{
    		$scope.isChooseAll = false;
    	}
    }
    }
    
    $scope.getTempOnBack = function(templateName){
       
		if (templateName == "deviceDetailsTemp.html"){
		if($scope.ediTemplate === true){
			bootbox.confirm({
		        title : 'Message',
		        message: "Do you want to discard the changes?",
		        closeButton: false,
		            buttons: {          
		               
		                confirm: {
		                    label: 'Yes',
		                    className: 'btn-default'
		                }
		            },
		            callback: function (result) {
		                if(result)
		                	{
		                	$scope.confirmEdit();
		                	location.reload();
		                	
		                	}
		            }
		        });
			}if($scope.ediTemplate === undefined){
        			bootbox.confirm({
    		        title : 'Message',
    		        message: "Do you want to save changes?",
    		        closeButton: false,
    		            buttons: {          
    		                confirm: {
    		                    label: 'No',
    		                    className: 'btn-default'
    		                },
    		                 cancel: {
    		                    label: 'Yes',
    		                    className: 'btn-primary'
    		                }
    		            },
    		            callback: function (result) {
    		                if(result)
    		                	{
    		                	$scope.confirmEdit();
    		                	$scope.configTemplateName = 'jsp/js/templates/'+templateName;	
    		                	}
    		               }
    		        });
        			
    			}
		    	}else if (templateName == "configFeatureTemp.html"){
		    		if($scope.ediTemplate == true){
		    			var Data = {
		    	        		"templateid" : $scope.templateId,
		    					"templateVersion" : $scope.templateVersion
		    		        };
		    		        $http({
		    			    		url : localStorage.getItem('path') + "/GetTemplateConfigurationData/getDataOnEdit",
		    			            method : "POST",
		    			            data : JSON.stringify(Data),
		    			            headers : {
		    			                'Content-Type' : 'application/json'
		    			            }
		    			
		    			        }).then(function(response) {
		    			        		console.log("In response");
		    			        		$scope.loading = false;
		    			        		$scope.nodes = JSON.parse(response.data.entity.left);
		    								angular.forEach($scope.nodes, function(parentItm){
		    									if(parentItm.childList.length > 0){
		    										 angular.forEach(parentItm.childList, function(itm){
		    											  if(itm.checked == true){
		    												  allSelected = true;
		    											  }else{
		    												  allSelected = false;
		    											  }
		    										  })
		    										if(allSelected != false){
		    	   									  parentItm.checked = true;
		    	   								  }
		    									}
		    								if(parentItm.name == 'Basic Config1'){
		    									parentItm.name = 'Basic Configuration';
		    								}
		    								$rootScope.isSelectAll($scope.nodes);
		    							})
		    							$scope.getConfigurationData = JSON.parse(response.data.entity.right);
		    		})
		    		}
		    	    sessionStorage.setItem("backflag", true);
		    	    sessionStorage.setItem("backCount", '0');
		    	    sessionStorage.setItem("newfeatureArray", []);
		    		$scope.deviceDetails = false;
		    		$scope.configFeature = true;
		    	    $scope.finalView = false;
		    	    $scope.configBarWidth = "50%"
		    	    $scope.templateTags = "Configuration Feature";
		    	    $scope.configTemplateName = 'jsp/js/templates/'+templateName;
		    	}else if (templateName == "finalViewTemp.html"){
		    		$scope.deviceDetails = false;
		    		$scope.configFeature = false;
		    	    $scope.finalView = true;
		    	    $scope.configBarWidth = "100%"
		    	    $scope.templateTags = "Final View";
		    	    $scope.configTemplateName = 'jsp/js/templates/'+templateName;
		    	}
				
		    	if (idSelectAllFlag === true){
		    		$scope.isChooseAll = true;
		    	}else{
		    		$scope.isChooseAll = false;
		    	}
    }
    
    
   /*Method to get configuration Feature list*/
    $scope.getConfigurationFeatures = function() {
  	  		var Data = {
			  		"templateid" :$scope.templateId,
			  		"templateVersion" :$scope.templateVersion
				};
  				$http(
				{
					url : localStorage.getItem('path') + "/GetTemplateConfigurationData/getDataForLeftPanel",
					method : "POST",
					data : JSON.stringify(Data),
					headers : {
						'Content-Type' : 'application/json'
					}
					})
  				.then(
  						function(response) {
  							if (response.data.entity.output){
  								console.log(response)
  	  							$scope.nodes = JSON.parse(response.data.entity.output);
  								angular.forEach($scope.nodes, function(parentItm){
  									if(parentItm.childList.length > 0){
  										 angular.forEach(parentItm.childList, function(itm){
  	  										  if(itm.checked == true){
  	  											  allSelected = true;
  	  										  }else{
  	  											  allSelected = false;
  	  										  }
  	  									  })
  	  									if(allSelected != false){
    	   									  parentItm.checked = true;
    	   								  }
  									}
									if(parentItm.name == 'Basic Config1'){
										parentItm.name = 'Basic Configuration';
									}
									$rootScope.isSelectAll($scope.nodes);
								})
  							}else{
  								console.log('Unable to get Configuration feature');
  							}
  						
  					})
  	 	}
    
    
    $scope.confirmEdit = function(){
		//templateName = temp;
   		
   		var Data = {
		  		"templateid" :$scope.templateId,
		  		"templateVersion" :$scope.templateVersion
			};
				$http(
			{
				url : localStorage.getItem('path') + "/GetTemplateConfigurationData/backOnModify",
				method : "POST",
				data : JSON.stringify(Data),
				headers : {
					'Content-Type' : 'application/json'
				}
				})
				.then(
						function(response) {
							if (response.data.entity.output){
								console.log(response)
	  						}else{
								console.log('Unable to get data');
							}
						
					})
				
			}
    
    /*Method to save configuration Feature list for edit chcek*/
    $scope.getConfigurationFeaturesForEdit = function() {
  	  		var Data = {
			  		"templateid" :$scope.templateId,
			  		"templateVersion" :$scope.templateVersion
				};
  				$http(
				{
					url : localStorage.getItem('path') + "/GetTemplateConfigurationData/getDataForLeftPanel",
					method : "POST",
					data : JSON.stringify(Data),
					headers : {
						'Content-Type' : 'application/json'
					}
					})
  				.then(
  						function(response) {
  							if (response.data.entity.output){
  								console.log(response)
  	  							$scope.editnodes = JSON.parse(response.data.entity.output);
  							}else{
  								console.log('Unable to get Configuration feature');
  							}
  						
  					})
  	 	}
    
    /*Method to get view template configuration*/
    $scope.getTempConfData = function(event) {
        openAddPopUp('templateConfPopUp');
    	dragElement(document.getElementById(("templateConfPopUpContainer")));	
        var Data = {
	    	templateid :event.target.innerText
		};
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
   								$scope.getConfigurationAdminData = JSON
   								.parse(response.data.entity.output);
   							}else{
   				        		console.log('Unable to get the data');
   							}
   					})
    	
    }
    
    /*Method to get template list*/
    $scope.getTemplateList = function() {
  	  			$http(
				{
					url : localStorage.getItem('path') + "/GetTemplateConfigurationData/getTemplateList",
					method : "GET",
					headers : {
						'Content-Type' : 'application/json'
					}
					})
  				.then(
  						function(response) {
  							console.log(response)
  							if (response.data.entity.output){
  								if(JSON.parse(response.data.entity.output) == ""){
  									$scope.errorFlag = true; 
  								}else{
  									$scope.errorFlag = false;
  									$scope.requestsList=JSON.parse(response.data.entity.output);
  								}
  								$rootScope.refreshNotificationCount();
  							}else{
				        		alertPopUp('Error ', 'Unable to get data');
				        		return false;
								
							}
  							 
  					}),function(error) {
						console.log(response)
						alertPopUp('Error', 'Unable to get data');
						return false
  	  			}
  	 	}
    setTimeout($scope.getTemplateList, 10);
    setTimeout($scope.getRegionList, 10);
	setTimeout($scope.getVendorList, 10);
   /*Method to get configuration data*/
  $scope.configurationData = function() {
	  var Data = {
			  "templateid" :$scope.templateId,
			  "templateVersion" : $scope.templateVersion
			};
		$http(
				{
					url : localStorage.getItem('path') + "/GetTemplateConfigurationData/getDataForRightPanel",
					method : "POST",
					data : JSON.stringify(Data),
					headers : {
						'Content-Type' : 'application/json'
					}
				})
				.then(
						function(response) {
							console.log(response)
							if (response.data.entity.sequence){
								$scope.getConfigurationData = JSON.parse(response.data.entity.sequence);
								console.log(response.data.entity.sequence);
							}else{
								console.log('Unable to get Configuration data');
				        	}
							
					})
	  }
   
    
    $rootScope.changeConfigurationData = function(node) {
    /*if child is checked feature above the child element automatically will get checked*/	
    	if(node.hasParent>0){
    		var confFlag=false;
    		var childConf= new Array();	
        	var childCheckFlag = false;
        	var childUnCheckFlag = false;
        	var childConf = [];
        	angular.forEach($scope.nodes, function(itm){
				if (node.parent == itm.name){
					angular.forEach(itm.childList, function(confItm){
						if(confItm.name == node.name && node.checked == true ){
							childCheckFlag = true;
						}
						if(confItm.name == node.name && node.checked == false ){
							childUnCheckFlag = true;
						}
						if(node.parent == node.name && node.checked == false ){
							childUnCheckFlag = true;
						}
						if(confFlag != true){
							var childConfObj ={
				        			name : '',
				        			id : ''
				        	}
							childConfObj.name = confItm.name; 
							childConfObj.id = confItm.id; 
							childConfObj.checked = true;
							if(childUnCheckFlag){
								childConfObj.checked = false;
							}
							childConf.push(childConfObj);
							if(childCheckFlag != true){
								confItm.checked = true;
							}
							if(childUnCheckFlag == true){
								confItm.checked = false;
							}
							
						}else{
							var childConfObj ={
				        			name : '',
				        			id : ''
				        	}
							childConfObj.name = confItm.name; 
							childConfObj.id = confItm.id; 
							childConfObj.checked = false;
							childConf.push(childConfObj);
							confItm.checked = false;
						}
						if(confItm.name == node.name){
							confFlag = true;
						}
					
					})
				}
    		})
		 var Data = {
				  templateid :$scope.templateId,
				  templateVersion :$scope.templateVersion,
				  checked : childConf,
				};
			$http(
					{
						url : localStorage.getItem('path') + "/GetTemplateConfigurationData/singleSelect ",
						method : "POST",
						data : JSON.stringify(Data),
						headers : {
							'Content-Type' : 'application/json'
						}
					})
			.then(function(response) {
						console.log(response);
					   	if(node.checked){
					   		$scope.getupdatedConfigurationData = JSON.parse(response.data.entity.output);
		    		   		angular.forEach($scope.getupdatedConfigurationData, function(itm){
				    			angular.forEach($scope.getConfigurationData, function(confItm){
				    				if (itm.id == confItm.id){
				    					confItm.checked = true;
				    					confItm.list = itm.list;
				    					$("#"+ confItm.id).css("display", "block");
				    					$("#"+ confItm.id).css("animation", "change 10s step-end both");
				    					var elmnt = document.getElementById(confItm.id);
				    					setTimeout(function(){ elmnt.scrollIntoViewIfNeeded(); }, 100);
				    					
				    				}
				    			});
				    		});
					    	}else{
					    		angular.forEach($scope.getConfigurationData, function(confItm){
				    				angular.forEach(childConf, function(itm){
				    				if (itm.id == confItm.id && itm.checked == false){
				    					$("#"+ confItm.id).css("display", "none");
				    					confItm.checked = false;
				    					confItm.list = [];
				    					confItm.list.push('');
				    					}
				    				});
				    			});
				    	}
				 })
			
    	}else{
    	/*when parent feature is selected not child*/
    	  var nodeId ;
	      var confText;
	      var featureConf;
	      var parentConf = [];
	      var parentConfObj = {
	    	  name : '',
	    	  id : '',
	    	  checked : ''
	      }
	      parentConfObj.id = node.id;
	      parentConfObj.name = node.name ;
	      parentConfObj.checked = node.checked;
	      parentConf.push(parentConfObj)
		  var Data = {
				  templateid :$scope.templateId,
				  templateVersion :$scope.templateVersion,
				  checked : parentConf
				 };
			$http(
					{
						url : localStorage.getItem('path') + "/GetTemplateConfigurationData/singleSelect",
						method : "POST",
						data : JSON.stringify(Data),
						headers : {
							'Content-Type' : 'application/json'
						}
					})
			.then(function(response) {
						console.log(response);
					   	if(node.checked){
					   		$scope.getupdatedConfigurationData = JSON.parse(response.data.entity.output);
		    		   		angular.forEach($scope.getupdatedConfigurationData, function(itm){
				    			angular.forEach($scope.getConfigurationData, function(confItm){
				    				if (itm.id == confItm.id){
				    					$("#"+ confItm.id).css("display", "block");
				    					confItm.checked = true;
				    					confItm.list = itm.list;
				    					$("#"+confItm.id).css("animation", "change 10s step-end both");
				    					var elmnt = document.getElementById(confItm.id);
				    				    setTimeout(function(){ elmnt.scrollIntoViewIfNeeded(); }, 100);
				    				}
				    			});
				    		});
				    	}else{
				    		var confTextEmpty ="";
				    		if(node.childList.length > 0){
				    			angular.forEach(node.childList, function(itm){
				    				angular.forEach($scope.getConfigurationData, function(confItm){
					    				if (itm.id == confItm.id){
					    					$("#"+ confItm.id).css("display", "none");
					    					confItm.checked = false;
					    					confItm.list = [];
					    				    confItm.list.push('');
						    			}
					    			});
				    			});
				    		}else{
				    			angular.forEach($scope.getConfigurationData, function(confItm){
				    				if (node.id == confItm.id){
				    					$("#"+ confItm.id).css("display", "none");
				    					confItm.checked = false;
				    					confItm.list = [];
				    					confItm.list.push('');
					    			}
				    			});
				    		}
				    	}
				 })
    	}
	  }
	 
  $scope.resetNewConfFeature = function(){
	      this.newConfFeatureName ="";
	      this.parentConfFeature =undefined;
	      this.newConfFeatureText ="";
	      this.newConfNoCommand ="";
	} 
	
  /*Method to get new configuration feature pop up */ 
  $scope.getCustomConfiguration = function() {
	    $scope.parentFatureData();
	    openAddPopUp('customConfiguration');
		dragElement(document.getElementById(("customConfigurationContainer")));	
		  this.newConfFeatureName ="";
	      this.parentConfFeature =undefined;
	      this.newConfFeatureText ="";
	      this.newConfNoCommand ="";
	}
 
  
  $rootScope.isSelectAll = function(node) {
	  var isSelectAll;
	  var selectAll = document.getElementById('selectAll');
	  if(node.checked == false){
		  selectAll.checked = false;
	  }else{
		  angular.forEach($scope.nodes, function(parentItm){ 
			  if(parentItm.checked == false && parentItm.childList.length == 0){
				  isSelectAll = false;
			  }
			  angular.forEach(parentItm.childList, function(itm){ 
				  if(itm.checked == false ){
					  isSelectAll = false;
				  }
			   	});
		  })
		  if (isSelectAll != false){
			  selectAll.checked = true;
		  }
	  }
	  
  }
  /*Method to select all configuration features*/
  $scope.chooseAll = function(flag) {
	    var toggleStatus ;
	    if(flag == 'reset'){
			   toggleStatus = false;
			   this.isChooseAll = false;	  
		    }else{
		    	idSelectAllFlag = this.isChooseAll;
		    	toggleStatus = this.isChooseAll;
		    }
	    angular.forEach($scope.nodes, function(itm){ 
		    	if(itm.disabled){
		    		itm.checked = true;
		    	}else{
		    		 itm.checked = toggleStatus;
				   	 if(itm.childList){
				   		angular.forEach(itm.childList, function(itm){ 
				   	   	 itm.checked = toggleStatus;
				   		});
				   	 }
		    	}
			   	
		   });
		    var Data = {
				  templateid :$scope.templateId,
				  templateVersion : $scope.templateVersion,
				  checked : toggleStatus
				};
			$http(
				{
					url : localStorage.getItem('path') + "/GetTemplateConfigurationData/getDataOnSelectDelectAll",
					method : "POST",
					data : JSON.stringify(Data),
					headers : {
						'Content-Type' : 'application/json'
					}
				})
				.then(
					function(response) {
						console.log(response)
						$scope.getSelectAllConfigurationData = JSON.parse(response.data.entity.sequence);
						$scope.getConfigurationData = JSON.parse(response.data.entity.sequence);
					   		
					})
					
  				}
  
  $rootScope.delNewFeature = function(node) {
	  var removeIndex;
	  if(node.newParentFeatureId == undefined){
		  removeIndex = $scope.nodes.indexOf(node);
		  		  if(removeIndex!=-1){
		  			$scope.nodes.splice(removeIndex, 1);
		  		  }
	  }else{
		  angular.forEach($scope.nodes, function(itm){ 
		    	if(itm.id == node.newParentFeatureId){
		    		removeIndex = itm.childList.indexOf(node);
		    		if(removeIndex != -1){
		    			itm.childList.splice(removeIndex, 1);
		    		}
		    	}
		   });
		 }
	  var newfeatureArray = sessionStorage.getItem("newfeatureArray");
		if(newfeatureArray !="" ){
			newfeatureArrayList = JSON.parse(newfeatureArray);
			var delFeaIndex = newfeatureArrayList.indexOf(node);
			for(var i=0 ; i<newfeatureArrayList.length; i++)
			{
			    if(newfeatureArrayList[i].value== node.id){
			    	newfeatureArrayList.splice(i,1);
			    }
			    
			}
			
		}
		sessionStorage.setItem("newfeatureArray",  JSON.stringify(newfeatureArrayList));
	  var id = "drop_drag_" + node.id;
      var el = document.getElementById(id);
  	  el.parentNode.removeChild(el);
  	  sessionStorage.setItem("delNewFeatureId",true);
  }
  
  /*Method to add new configuration feature*/
  $scope.addNewConfFeature = function(event) {
	    var newConfFeatureName = this.newConfFeatureName;
	    var dupConfFeatureName = false;
	    angular.forEach($scope.nodes, function(itm){ 
	    	if(itm.name == newConfFeatureName){
	    		 dupConfFeatureName = true;
	    	}
		 });
	    
	    if(!this.newConfFeatureName){
	    	alertPopUp('Error', 'Please Enter Configuration name');
	    }else if(!this.parentConfFeature){
	    	alertPopUp('Error', 'Please Enter Parent Feature name');
	    }else if(!this.newConfFeatureText){
	    	alertPopUp('Error', 'Please Enter Command');
	    }else if(!this.newConfNoCommand){
	    	alertPopUp('Error', 'Please Enter No Command');
	    }
	    else if(dupConfFeatureName){
	    	alertPopUp('Error', 'Fearture name is already present.Please add another feature name.');
		}
	    else{
	    	closeAddPopUp('customConfiguration')
		    $scope.editItmFlag = true;
		    var featureName = this.newConfFeatureName;
		    var featureText = this.newConfFeatureText;
		    var NoCommandText = this.newConfNoCommand;
		    var parentConfFeature = this.parentConfFeature;
		    var newitm = {}; 
	    	newitm.id= featureName;
	    	newitm.newFeature= true;
		   	newitm.name= featureName;
		   	newitm.checked= true;
		   	newitm.confText= featureText;
			newitm.noCommandText= NoCommandText;
		   	newitm.draggable= true;
			newitm.disabled= true;
		   	newitm["childList"] = [];
		    /*add new parent configuration*/
		    if(parentConfFeature == 'Add New Feature'){
		    	localStorage.setItem("newParentFeature", true);
		       	$scope.nodes.push(newitm);
		    }else{
		    	localStorage.setItem("newParentFeature", false);
		    /*add new child configuration*/
		    angular.forEach($scope.nodes, function(itm){ 
		    	if(itm.name == parentConfFeature){
		    		localStorage.setItem("parentFeaturId", itm.id);
		    		newitm.newParentFeatureId = itm.id;
		    		var itemList = itm.childList;
			   		itemList.push(newitm);
			   }
		   });
		  }
		   /* function showHide(ulId) {
		        var hideThis = document.getElementById(ulId);
		        var showHide = angular.element(hideThis).attr('class');
		        angular.element(hideThis).attr('class', (showHide === 'show' ? 'hide' : 'show'));
		      }
		    showHide($scope.parentConfigurationFeatureId);*/
		 }
  }
  /*parent uncheck on click of children*/
 /* $rootScope.parentUnCheckChange = function(node) {
	  var allSelected;
	  if(node.checked == true){
		  angular.forEach($scope.nodes, function(parentItm){
			  if (node.parent == parentItm.name){
				    angular.forEach(parentItm.childList, function(itm){
					  if(itm.checked == true){
						  allSelected = true;
					  }else{
						  allSelected = false;
					  }
				  })
				  if(allSelected != false){
					  parentItm.checked = true;
				  }
			  }
			  
		   })
	  }else{
	  angular.forEach($scope.nodes, function(itm){
		  if (node.parent == itm.name){
			  itm.checked = false;
    	  }
	   })
	  }
  	}	*/
  /**********/
$rootScope.parentUnCheckChange = function(node) {
	  var allSelected;
	  var notAllSelected;
	  if(node.checked == true){
		  angular.forEach($scope.nodes, function(parentItm){
			  if (node.parent == parentItm.name){
				    angular.forEach(parentItm.childList, function(itm){
					  if(itm.checked == true){
						  allSelected = true;
					  }else{
						  allSelected = false;
					  }
				  })
				  if(allSelected != false){
					  parentItm.checked = true;
				  }
			  }
			  
		   })
		angular.forEach($scope.nodes, function(itm){
		  if (node.parent == itm.name){
			  itm.checked = true;
    	  }
		})
	  }else{
		 angular.forEach($scope.nodes, function(parentItm){
			  if (node.parent == parentItm.name){
			     angular.forEach(parentItm.childList, function(itm){
			    	 if(itm.checked != false){
			    		 notAllSelected = true;
			    	 }
			     })
			     if(notAllSelected == true){
					  parentItm.checked = true;
				  }else{
					  parentItm.checked = false;
				  }
			  }
			  
		   })
		 }
  	  }

$scope.clearSearch = function() {
	this.searchInput="";
	$scope.errorMessege = false;
} 
}]);


/*drag and drop */

function parentaAllowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
	var dropElId = "";
	ev.draggable = false;
	var dragElement = document.getElementById(ev.target.id);
	ev.dataTransfer.setData("noCommandText", event.target.dataset.value);
	
	/*var confTextArray = new Array();
	confTextArray = event.target.dataset.name.split(/\n/);*/
	ev.dataTransfer.setData("confText", event.target.dataset.name);
	ev.dataTransfer.setData("featureId",ev.target.id);
	dropElId = "drop_" +  ev.target.id;
	ev.dataTransfer.setData("id",dropElId);
	ev.dataTransfer.effectAllowed = 'move';
	/*if(ev.clientY <= distance)//top
    {
        $(document).scrollTop(ev.clientY - step)
    }
    if(ev.clientY >= ($(window).height() - distance))//bottom
    {
        $(document).scrollTop(ev.clientY + step)
    }*/
	
}
function dragDropedEl(id) {
	var el = document.getElementById(id);
	el.parentNode.removeChild(el);
}

var dragData;
var dragId; 
var noCommandText;
var childId;
var child_id;
var newfeatureArray = new Array();

function drop(ev) {
	    var multiDrag = false;
	    //newfeatureArray = sessionStorage.getItem("newfeatureArray");
	    var featurelist = sessionStorage.getItem("newfeatureArray");
	    if(featurelist !="" && featurelist != null ){
	    	  newfeatureArray = JSON.parse(featurelist);
		}
	    var count = sessionStorage.getItem("backCount");
	    if(sessionStorage.getItem("backflag") == 'true' &&  count == 0 )
		{
		dragId = undefined;
		newfeatureArray = [];
		sessionStorage.setItem("backflag", false);
	    sessionStorage.setItem("backCount", '1');
		
		}
	if(dragId != ev.dataTransfer.getData("id") && dragId != undefined && sessionStorage.getItem("delNewFeatureId") == 'false' ){
		var elem = document.getElementById(dragId);
	    elem.removeAttribute("draggable");
	    elem.style.cursor='default';
		console.log(ev.dataTransfer.getData("id"));
	}
	var data = document.createElement('pre');
    data.draggable = "true";
    if (ev.dataTransfer.getData("confText") != ""){
    	data.innerHTML = ev.dataTransfer.getData("confText");
    	dragData = ev.dataTransfer.getData("confText");
    	dragId = ev.dataTransfer.getData("id");
    	noCommandText = ev.dataTransfer.getData("noCommandText"); 
    	var featureId = ev.dataTransfer.getData("featureId");
    	var el = document.getElementById(featureId);
    	el.attributes.draggable.value = false;
    }else{
    	multiDrag = true;
    	data.innerHTML = dragData;
    	var el = document.getElementById(dragId);
    	el.parentNode.removeChild(el);
    }
    data.id = dragId;
    data.setAttribute('class','newFeaElemnetStyle');
    data.style.cursor="-webkit-grab";
    if(localStorage.getItem("newParentFeature") == 'true'){
    	 ev.currentTarget.appendChild(data);
    }else{
    	 ev.target.appendChild(data);
    	
    }
    var elmnt = document.getElementById(dragId);
    elmnt.scrollIntoViewIfNeeded();
    var parentId = document.getElementById(ev.target.getAttribute("parentid"));
   /* var confTextArray = new Array();
	confTextArray = dragData.split(/\n/);*/
    var parent_id = ev.target.getAttribute("parentid");
	var newParentFeature = localStorage.getItem("newParentFeature");
	var childId = ev.target.getAttribute("childid");
	var lastCmdId = parentId.getAttribute("lastcmdid");
	var parentFeatureId = localStorage.getItem("parentFeaturId");
	var newfeature = {}; 
	newfeature.value = dragId.replace("drop_drag_", "");
	newfeature.id = dragId.replace("drop_drag_", "");
    newfeature.dragId = dragId;
    newfeature.confText = dragData;
    newfeature.noCommandText = noCommandText;
    newfeature.parent_id = parent_id;
    newfeature.newParentFeature= newParentFeature;
    newfeature.childId = childId; 
    newfeature.lastCmdId = lastCmdId;
    newfeature.parentFeatureId = parentFeatureId;
    if (!multiDrag){
    	newfeatureArray.push(newfeature);
    }else{
    	angular.forEach(newfeatureArray, function(itm){ 
        	if(itm.dragId == dragId){
        		itm.lastCmdId = lastCmdId;
        		itm.parent_id = parent_id;
        		itm.childId = childId 
    	   }
        });
    }
    sessionStorage.setItem("newfeatureArray", JSON.stringify(newfeatureArray));
	sessionStorage.setItem("delNewFeatureId",false);
	ev.preventDefault();
    ev.stopPropagation();
}

if (window.performance) {
	  console.info("window.performance works fine on this browser");
	} 
	var Data = {
	  		"refresh" : "true",
		};
	  if (performance.navigation.type == 1) {
	    console.info( "This page is reloaded" );
	    	 $.ajax({
		            type: "POST",
		            data: JSON.stringify(Data),
		            url:  localStorage.getItem('path') + "/GetTemplateConfigurationData/backOnModify",
		           
		    });
	  } else {
	    console.info( "This page is not reloaded");
	  }

  
C3PApp.controller('modifyrequestDetailsController',['$scope','$rootScope','$http' ,'$state', '$stateParams' ,function($scope, $rootScope, $http,$state,  $stateParams) {
   
	$scope.editFieldFlags={
			      'wanFlags': '',
			    'routingProtocolFlag': ''
	}
	$scope.certificationOptionListFlags = {}
	
	$rootScope.modifyScheduler = false;
	$rootScope.modifyReSchedule = false;
	$scope.templateId = $stateParams.templateId;
	$scope.getModifyScheduler = function(id){
		$rootScope.modifyScheduler = true;
		$rootScope.modifyReSchedule = true;
		openAddPopUp(id);
		$rootScope.getScheduledGrid($stateParams.dashboardReq_Details, $stateParams.version, $stateParams.templateId, false);
	}
	
	
	$scope.getGenerateConfig = function(data, report_title, event){
		$rootScope.editRequestDetails = $scope.editRequestDetails[0];
		
		openAddPopUp('generateConfigPopUp');
		dragElement(document.getElementById(("generateConfigPopUpContainer")));	
		
	}
 
	//$scope.editFieldFlags={};
	$scope.searchRequDetailsForEdit = function() {
    	
        var Data = {
            key : 'Request ID',
            value : $stateParams.dashboardReq_Details,
            version : $stateParams.version
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
            	console.log(response.data.entity.output)
            	console.log(response.data.entity.flags)
                $scope.editRequestDetails = JSON.parse(response.data.entity.output);            	
            	$scope.editFieldFlags = response.data.entity.flags;
            	$scope.certificationOptionList = JSON
				.parse(response.data.entity.certificationOptionList);
                $scope.ReportDetails = JSON.parse(response.data.entity.ReportStatus);
            	            
            	var currentVersion =$scope.editRequestDetails[0].request_version;
            	if(currentVersion.toString().includes(".")){
					var parts = currentVersion.toString().split('.');
					var lastDigit = parts.pop();
					var versionLastdigit = parseInt(lastDigit) +1;
					var new_text = parts.join('.');
					var updatedVersion = new_text + "." + versionLastdigit;
					$scope.version= updatedVersion;
            	}else{
            		var updatedVersion= currentVersion.toString().concat(".1");
            		$scope.version = updatedVersion;
            	}
            	$scope.checkOptionSelect();
        })
    }
    $scope.searchRequDetailsForEdit();
    
    /* List of Test & Turn Up Strategy */
	/*$scope.certificationOptionList = [{value:"Interfaces status",selected: true},
	                                  {value:"Platform & IOS",selected: true}, {value:"WAN Interface",selected: false},{value : "BGP neighbor",selected:false}, {value:"Throughput",selected:false}, {value:"FrameLoss",selected:true}, {value:"Latency",selected:true} ];
	*/
		

	/* Method to get list of Test & Turn Up Strategy */
    $scope.getCPEcertificationOptions = function() {
		openAddPopUp('CPEcertificationOptions');
			
		console.log($scope.certificationOptionList);
	}
    /* Method to set flags for Test & Turn Up Strategy */
	$scope.checkOptionSelect= function(){
	
		if ($scope.editFieldFlags.wanFlags != true) {
			
				angular.forEach($scope.certificationOptionList, function(itm){
					 if(itm.value == 'WAN Interface' || itm.value == 'Throughput' ){
						// itm.selected = true;
						 itm.disabled = true;
					 }
				});
		}if ($scope.editFieldFlags.routingProtocolFlag != true) {
				
				angular.forEach($scope.certificationOptionList, function(itm){
					if(itm.value == 'BGP neighbor'){
						// itm.selected = true;
						 itm.disabled = true;
					}
				});
			}
		}
	
	
	/*Reset Test & Turn Up Strategy*/
	$scope.resetCertificationTests = function () {
		angular.forEach($scope.certificationOptionList, function(itm){ 
	    	 itm.selected = false;
	    });
	}
	
    $scope.closeReportPopUp = function() {
		$("#reportPopUp").css("display", "none");
	}
    
    /*Method to get generate config data*/
    $scope.getGenerateConfigratioDataForEdit = function(report_title, event) {
    	$rootScope.editRequestDetails = $scope.editRequestDetails[0];
    	$rootScope.editReqDetailsFlag = true;
		//event.stopPropagation();
		//event.stopImmediatePropagation();
		$scope.report_title = report_title;
		
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
		 var requiredValidationFlag ="";	  
		 $('input, select').each(function(index, item) {
			if ($(item).hasClass("ng-invalid-required")) {
				requiredValidationFlag = true; 
			}
		});
		 if(requiredValidationFlag){
		 $('.ng-invalid-required').css('border','1px solid rgb(222, 52, 52)');
			$('#editConfigForm').css('border','none');
			alertPopUp('Edit','Please fill all the mandatory fields');
			event.preventDefault();
		 }
		else{
		$scope.loading = true;
	
		//console.log(JSON.stringify($scope.configuartion));
		angular.forEach($scope.certificationOptionList, function(itm){ 
			 if(itm.selected){
				 itm.selected = 1;
			 }else{
				 itm.selected = 0;
			}
			 $scope.certificationOptionListFlags[itm.value] = itm.selected;
			
			 
		});
		$rootScope.certificationListFlags = $scope.certificationOptionListFlags;
		var Data = $scope.editRequestDetails[0]
		$http(
				{

					url : localStorage.getItem('path') + "/GetConfigurationTemplateForModify/createConfigurationTemplateModify",

					//url : "http://localhost:8023/ConfigMngmntService/createConfigurationDcm",
					method : "POST",
					data : JSON.stringify(Data),
					headers : {
						'Content-Type' : 'application/json'
					}

				}).then(function(response) {
				
			//console.log("In response")
			$scope.loading = false;
			localStorage.setItem("configuartion", JSON.stringify($scope.editRequestDetails[0]));
			$scope.getReportdata = response.data.output;
			if (response.data.changes === "false"){
				bootbox.confirm({
						title : 'Config Message',
						message : "There are no modifications,Would you like to proceed?",
						buttons: {
					        cancel: {
					            label: '<i class="fa fa-times"></i> No'
					        },
					        confirm: {
					            label: '<i class="fa fa-check"></i> Yes'
					        }
					    },
						callback : function(result) {
							if (result){								
								
							}else{
								closeAddPopUp('generateConfigPopUp');	
							}						
							
						}
					});
				        
			}
			$scope.getdata =$scope.getReportdata;
			//console.log(response);

		})
		openAddPopUp('generateConfigPopUp');
		dragElement(document.getElementById(("generateConfigPopUpContainer")));	
		}
	 }
	
	}
    
    $rootScope.saveConfigurationForEdit = function(event) {		
    	var Data = {
				editeData : $scope.editRequestDetails[0],
				certificationOptionListFlags : $scope.certificationOptionListFlags
				};
		$http(
				{
	
					url : localStorage.getItem('path') + "/ModifyConfiguration/modify",
	
					//url : "http://localhost:8023/ConfigMngmntService/createConfigurationDcm",
					method : "POST",
					data : JSON.stringify(Data),
					headers : {
						'Content-Type' : 'application/json'
					}

			}).then(function(response) {
				
			console.log("In response")
			$scope.loading = false;
			event.stopImmediatePropagation();
			bootbox.dialog({
				title : 'Request Status',
				message : 'Updated Sucessfully',
				closeButton: false,
				buttons : {
						
					confirm : {
						label : 'Ok',
						className : 'btn-default',
						callback: function() {
							$state.go('dashboardReqDetails', { dashboardReq_Details: response.data.requestId,version: response.data.version});
					  }
					},
	
				}
		});
		console.log(response)

	})
	}
	
    
$scope.getEIPAMIP = function() {
		console.log("in get IPAM IP method")
		var Data = {
			site : $scope.configuartion.siteid,
			customer : $scope.configuartion.customer,
			service : $scope.configuartion.service,
			region : $scope.configuartion.region
		};
		console.log(JSON.stringify(Data))
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
							console.log(response)
							$scope.wanIPvalue = response.data.entity.output.ip;
							$scope.configuartion.c3p_interface.ip=$scope.wanIPvalue;
							$scope.wanIPMask = response.data.entity.output.mask;
							$scope.configuartion.c3p_interface.mask=$scope.wanIPMask;
							$scope.onWanIpChange();
						})
						
		$scope.onWanIpChange = function() {
			$scope.configuartion.internetLcVrf= {};
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

		if ($scope.configuartion.customer.length >= 4) {
			$scope.customnameforhostname = $scope.configuartion.customer
					.substring(0, 4);

			console.log("Substring name"
					+ $scope.customnameforhostname)
		}
		if ($scope.varHostname) {
			$scope.varHostname=null;
			$scope.configuartion.hostname=null;
			$scope.generateHostname();
		}
	}
	$scope.onSiteIDChange = function() {

		if ($scope.configuartion.siteid.length >= 2) {
			$scope.siteidforhostname = $scope.configuartion.siteid
					.substring(0, 2);

			console.log("Substring site"
					+ $scope.siteidforhostname)
		}
		if ($scope.varHostname) {
			$scope.varHostname="";
			$scope.configuartion.hostname="";
			$scope.generateHostname();
		}

	}
	$scope.onRegionChange = function() {
		if ($scope.configuartion.region.length >= 2) {
			$scope.regionforhostname = $scope.configuartion.region
					.substring(0, 2);
		}
		console.log("Substring region"
				+ $scope.regionforhostname)
		if ($scope.varHostname) {
			$scope.varHostname="";
			$scope.configuartion.hostname="";
			$scope.generateHostname();
		}
	}
	$scope.OsChange = function() {
		
		$scope.configuartion.osVersion ="";
		}
	$scope.onDeviceChange = function() {
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
	}
	$scope.onVendorChange = function() {
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
		console.log("Substring device"
				+ $scope.vendorforhostname)
		if ($scope.varHostname) {
			$scope.configuartion.hostname="";
			$scope.generateHostname();
			$scope.generateHostname();
		}
	}

	$scope.onModelChange = function() {
	$scope.configuartion.c3p_interface.name ="";
		$scope.configuartion.osVersion ="";
		$scope.configuartion.os="";
		$scope.varHostname="";
		$scope.configuartion.hostname = $scope.varHostname;
		$scope.modelforhostname = $scope.configuartion.model
		
		$scope.generateHostname();

	}
	
	$scope.resetVrfName = function() {

		$scope.configuartion.vrfName = "";
		$("#vrfName").addClass('disabled');
	}
	$scope.makeVrfNameMandatory = function() {

		$scope.configuartion.vrfName = "";
		$("#vrfName").removeClass('disabled');
	}
	
	$scope.generateHostname = function() {
		if ($scope.regionforhostname) {
			$scope.computedhostname = $scope.regionforhostname;
		}
		if ($scope.customnameforhostname) {
			$scope.computedhostname = $scope.computedhostname
					.concat($scope.customnameforhostname)
		}

		$scope.computedhostname = $scope.computedhostname
				.concat($scope.modelforhostname)

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
    
}])
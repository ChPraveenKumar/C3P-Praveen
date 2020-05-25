C3PApp
  .directive('loading', function () {
      return {
        restrict: 'E',
        replace:true,
        template: '<div class="popup" style="padding-top: 19%;"><img class="loading" width="100" height="100" /><div style="padding-left: 48%">Loading...</div></div>',
        link: function (scope, element, attr) {
              scope.$watch('loading', function (val) {
                  if (val)
                      $(element).show();
                  else
                      $(element).hide();
              });
        }
      }
  })
  
  C3PApp.directive('mandateInputs', function () {
      return {
        link: function (scope, element, attr) {
        	var validateInputArray = [];
        	$(element).click(function (){
        		if (attr.mandateInputs === "true"){
        			if ($("input, select").hasClass("ng-empty") == true && $("input, select").attr("required") == "required"){
        				//validateInputArray.push()
        				console.log(scope);
        				return false;
        			}
            	}
        	});        	
        }
      }
  });

 C3PApp.directive('reportpopOver', function($http,$stateParams) {
    	  return {
    	    restrict: 'C',
    	    link: function(scope, element, attr) {
    	        var i = 0;

    	        $(function() {
    	        	$('.reportpop-over').tooltip();
    	        	element.bind('mouseover',function(e) {
    	        		var testType = "Pretest";
    	        		if(testType == "Pretest"){
    	        			var data = "Reachability,Vendor";
    	        		}
    	        		 attr.$set('originalTitle', data);  
	 	                 element.tooltip('show');
    	        	/*	var Data = {
    	        				requestID : $stateParams.dashboardReq_Details,
    	        				testType: "preValidate",
    	        				version :  $stateParams.version
    	        		        };
    	        		$http({
    	        		    method : "POST",
    	        		    data : JSON.stringify(Data),
    	        		    url : localStorage.getItem('path') + "/GetReportData/getReportDataforTest"
    	        		  }).then(function mySuccess(response) {
    	        		      scope.myWelcome = response.data;
    	        		      attr.$set('originalTitle', "Some text ");  
    	 	                  element.tooltip('show'); 
    	        		    }, function myError(response) {
    	        		      scope.myWelcome = response.statusText;
    	        		  });*/
    	    	         
    	    	        });
    	        });
     	       
    	    }
    	}
    	});

C3PApp.directive('fileModel', ['$parse','fileUpload','$rootScope','$http','dataFactory', function ($parse,fileUpload,$rootScope,$http,dataFactory) {
    return {
       restrict: 'A',
       link: function(scope, element, attrs) {
          var model = $parse(attrs.fileModel);
          var modelSetter = model.assign;
          
          element.bind('change', function(){
             scope.$apply(function(){
                modelSetter(scope, element[0].files[0]);
                //scope.uploadFile();
                function uploadFile(){
                	scope.autoHostName = false;
				    var file = scope.myFile;
				    $rootScope.myFile= file;
				    var fd = new FormData();
				       fd.append('file', file);
		               console.log('file is ' );
		               console.dir(file);
		               
		               var uploadUrl = localStorage.getItem('path') + "/GetAllExcelData/get";
		               //$rootScope.getFileData(file, uploadUrl)
		               
		               var headers = {'Content-Type': undefined},
		               transformRequest = angular.identity
		               dataFactory.postDataFact(uploadUrl, fd, transformRequest, headers)
		               .then(function(response){
		            	   //closeAddPopUp('addImportPopUp');
		            	  
					       var configuartionData = JSON
													.parse(response.data.entity.output);
						  
					       if (configuartionData.result == "success"){
					    	   
					    	   var InformationStatus = configuartionData.interfaceRoutingInfo.InformationStatus;
					    	   scope.configuartion.lanDescription = configuartionData.interfaceRoutingInfo.lanInterface.lanInterfaceDescription
						       scope.configuartion.lanIPaddress = configuartionData.interfaceRoutingInfo.lanInterface.lanInterfaceIP
						       scope.configuartion.lanSubnetMask = configuartionData.interfaceRoutingInfo.lanInterface.lanInterfaceSubnetMask
						       scope.configuartion.lanTnterface = configuartionData.interfaceRoutingInfo.lanInterface.lanInterfaceType
					    	   scope.configuartion.customer = configuartionData.customerInfo.customer;
						       scope.configuartion.siteid = configuartionData.customerInfo.siteid;
						       scope.configuartion.vrfName = configuartionData.interfaceRoutingInfo.vpn.vrfName;
								//$scope.configuartion.c3p_interface.name = configuartionData[4].variableValue;
						       scope.configuartion.internetLcVrf.networkIp = configuartionData.interfaceRoutingInfo.routing.networkIP;
						       scope.configuartion.internetLcVrf.networkIp_subnetMask = configuartionData.interfaceRoutingInfo.routing.networkMask;						
						       scope.configuartion.region="CANADA";
						       scope.configuartion.hostname=configuartionData.deviceInfo.hostname;
						       
						        //Code change by Ruchita
						       scope.configuartion.vendor=configuartionData.deviceInfo.vendor;
						       scope.configuartion.deviceType=configuartionData.deviceInfo.deviceType;
						       scope.configuartion.model=configuartionData.deviceInfo.model;
						       scope.configuartion.hostname=configuartionData.deviceInfo.hostname;
						       scope.configuartion.os=configuartionData.deviceInfo.os;
						       scope.configuartion.c3p_interface.bandwidth =configuartionData.interfaceRoutingInfo.wanInterface.bandwidth;
						       scope.configuartion.c3p_interface.name=configuartionData.interfaceRoutingInfo.wanInterface.wanInt;
						       setTimeout(function(){ 
						    	   $("#createCongigForm .ng-not-empty").attr("disabled","disabled").css({"opacity":"0.7", "cursor":"default"}); 
						    	   
						       }, 10);
						       var wanInterfaceObj = InformationStatus.wanInterfaceObj,
						       lanInterface = InformationStatus.lanInterface,
						       loopbackInt = InformationStatus.loopbackInt,
						       vpnObj = InformationStatus.vpnObj,
						       routingObj = InformationStatus.routingObj,
						       snmp = InformationStatus.snmp,
						       banner = InformationStatus.banner,
						       enablePassword = InformationStatus.enablePassword;
						       
						       scope.$parent.optionList = [{value: "WAN Interface", selected:wanInterfaceObj},{value: "LAN Interface", selected: lanInterface},
								                     {value:"Loopback Interface", selected:loopbackInt}, {value:"VPN", selected:vpnObj}, {value:"Routing Protocol-BGP", selected:routingObj}, {value:"SNMP", selected:snmp}, {value:"Banner", selected:banner}, {value:"Enable Password",selected:enablePassword} ];
							   angular.forEach(scope.$parent.optionList, function(itm){ 
							    	scope.checkOptionSelect(itm.value, itm.selected);
							    	setTimeout(function(){ 
								    	   $("#createCongigForm .ng-not-empty").attr("disabled","disabled").css({"opacity":"0.7", "cursor":"default"}); 
								    	   
								       }, 10);
							    });
							   
							   if (scope.configuartion.c3p_interface.name == 'Serial1/0'){
								   scope.configuartion.c3p_interface.encapsulation = "PPP";
							   }
					       }else{
					    	   var errorMessage = "";
					    	   
					    	   $.each(configuartionData.error.errorField, function(key, val){
					    	   	 $.each(val, function(key1, val1){
					    	   	 	errorMessage = errorMessage.concat(key1 +": "+val1+ ", ");
					    	   	 })					    		   
					    	   });
					    	   alertPopUp('Error', 'Invalid '+ errorMessage+"please try again");
					    	   scope.resetImportedData();
					       }
					       
		               }, function (error) {
		            	   alertPopUp('Error', 'Unable to load data');
		            	   scope.resetImportedData();
			            });
		            }
                uploadFile();                
             });
          });
       }
    };
 }]);

C3PApp.factory('dataFactory', ['$http', function ($http) {
	var dataFactory = {};
	dataFactory.postDataFact = function (urlBase, data, transformRequest, headers) {
        return $http.post(urlBase, data, {
            transformRequest: transformRequest,
            headers: headers
         })
    };
    dataFactory.getDataFact = function (urlBase) {
        return $http.get(urlBase)
    };
    return dataFactory;
}]);

C3PApp.factory('activeTabs', ['$http', function ($http) {
	var activeTabs = {};
		 
	activeTabs.menubar = {
		'Home' : false,
        'Dashboard' : false,
        'Configuration' : false,
        'Report' : false,
        'Admin' : false
   	}
	
	activeTabs.activeTabsFunc = function (key){
		Object.keys(activeTabs.menubar).forEach(function(k) {
            if(k==key)
                activeTabs.menubar[k] = true;
            else
                activeTabs.menubar[k] = false;
        })
	};
	
    return activeTabs;
}]);

C3PApp.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function(file, uploadUrl){
       var fd = new FormData();
       fd.append('file', file);
    
       $http.post(uploadUrl, fd, {
          transformRequest: angular.identity,
          headers: {'Content-Type': undefined}
       })
    
       .success(function(response){
       $rootScope.configuartion = response;
       console.log(response);
       })
    
       .error(function(){
       });
    }
 }]);

C3PApp.directive('sortTable', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
        	scope.sortKey = '';
        	scope.reverse = false;
        	var dataArray = [];
        	$('#'+ attrs.id).css("cursor","pointer");
            element.on('click', function() {
            	if (attrs.sortTable === "true"){
            		dataArray = scope.requestsList;
                	$.each(dataArray, function(idx, val){
        	        	if (idx < dataArray.length - 1){
        	        		if (dataArray[idx][attrs.id] !== dataArray[idx+1][attrs.id]){        	        			
        						$('#'+ attrs.id).toggleClass('sort-ascent').toggleClass('sort-descent');        						
        						scope.reverse = !scope.reverse;
        						scope.sortKey = attrs.id;
        						scope.$apply();
        						return false;
        					}
        	        	}
        	        }) 
            	}            	               
            });
        }
    }
});


/*Directive for template feature selection*/
C3PApp.directive('treeView', ['$rootScope','$compile',function($rootScope,$compile) {
	  return {
	    restrict : 'E',
	    scope : {
	      localNodes : '=model',
	      localClick : '&click',
	      changeConfigurationData: '&'
	    },
	    link : function (scope, tElement, tAttrs, transclude) {
	      
	      var maxLevels = (angular.isUndefined(tAttrs.maxlevels)) ? 10 : tAttrs.maxlevels; 
	      var hasCheckBox = (angular.isUndefined(tAttrs.checkbox)) ? false : true;
	      scope.showItems = [];
	      
	      scope.showHide = function(ulId) {
	        
	        var hideThisParent = document.getElementById("parent_" + ulId);
	        var showHideParent = angular.element(hideThisParent).attr('class');
	        angular.element(hideThisParent).attr('class', (showHideParent === 'expand_plus' ? 'expand_minus' : 'expand_plus'));
	        
	        var hideThis = document.getElementById("showHide_" + ulId);
	        var showHide = angular.element(hideThis).attr('class');
	        angular.element(hideThis).attr('class', (showHide === 'show' ? 'hide' : 'show'));
	       }
	      
	     scope.showIcon = function(node) {
	        if (node.childList.length > 0 && node.name != "Basic Configuration" ) return true;
	      }
	     scope.showDeleteIcon = function(node) {
		         return node.newFeature;
		      }
	     scope.deleteNewFeature = function(node) {
	    	 $rootScope.delNewFeature(node);
	  		
	     }
	     scope.checkIfChildren = function(node) {
	        if (node.childList.length > 0) return true;
	      }

	      /// SELECT ALL CHILDRENS
	      function parentCheckChange(item) {
	        for (var i in item.childList) {
	          item.childList[i].checked = item.checked;
	          if (item.childList[i].childList.length) {
	            parentCheckChange(item.childList[i]);
	          }
	        }
	      }
	     scope.checkChange = function(node) {
	    
	    	$rootScope.changeConfigurationData(node);
	        if (node.childList.length > 0) {
	          parentCheckChange(node);
	         
	        }
	        if(node.name != node.parent){
	        	$rootScope.parentUnCheckChange(node);
	        }
	        $rootScope.isSelectAll(node);
	       
	      }
	      
	      function renderTreeView(collection, level, max) {
	        var text = '';
	        text += '<li ng-repeat="n in ' + collection + '" >';
	        text+= '<span ><i class="feature_icon"></i></span>'
	        if (hasCheckBox) {
	            text += '<input class="tree-checkbox" ng-show="!n.newFeature" ng-disabled=n.disabled style=" bottom: -3px;position: relative" type=checkbox ng-model=n.checked ng-change=checkChange(n)>';
	          }
	          
	        text += '<span ng-show=showIcon(n) class="expand_plus" id="parent_{{n.id}}" ng-click=showHide(n.id)><i class=""></i></span>';
	        text += '<span ng-show=!showIcon(n)></span>';
	        text += '<span ng-show=showDeleteIcon(n) class="delete_icon" ng-click=deleteNewFeature(n)></span>';
	        text += '<label id="drag_{{n.id}}" style="color: #555;font-size: 13px;" parentFeaId="{{n.parentFeaturId}}" data-name="{{n.confText}}" data-value="{{n.noCommandText}}" draggable="{{n.draggable}}" ondragstart="drag(event)">{{n.name}}</label>';
	       
	        
	        if (level < max) {
	          text += '<ul id="showHide_{{n.id}}" class="hide" ng-if=checkIfChildren(n)>'+renderTreeView('n.childList', level + 1, max)+'</ul></li>';
	        } else {
	          text += '</li>';
	        }
	        
	        return text;
	      }// end renderTreeView();
	      
	      try {
	        var text = '<ul class="tree-view-wrapper">';
	        text += renderTreeView('localNodes', 1, maxLevels);
	        text += '</ul>';
	        tElement.html(text);
	        $compile(tElement.contents())(scope);
	      }
	      catch(err) {
	        tElement.html('<b>ERROR!!!</b> - ' + err);
	        $compile(tElement.contents())(scope);
	      }
	    }
	  };
	}]);

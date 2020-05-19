C3PApp.controller('headerController',['$scope','$interval','$rootScope','$http', '$state', 'activeTabs',function($scope,$interval, $rootScope, $http, $state, activeTabs) {
	
	$scope.feNotiReqListFlag = false;
    $scope.seNotiReqeListFlag  = false;
	$scope.pendingTemplateList = function(){
		 $http({
	        url : localStorage.getItem('path') + "/GetNotifications/get",
	        method : "GET"
	      
	     }).then(function(response){
	    	 if(response.data.entity.TemplateList != ""){
	    		 $scope.notiTemplateList = JSON.parse(response.data.entity.TemplateList);
	    	 }
			 if(response.data.entity.FERequestDetailedList != ""){
	    		 $scope.feNotiReqList = JSON.parse(response.data.entity.FERequestDetailedList);
	    		 $scope.feNotiReqListFlag = true;
	    	 }
			if(response.data.entity.SERequestDetailedList != ""){
			       $scope.seNotiReqeListFlag  = true;
	    		   $scope.seNotiReqeList = JSON.parse(response.data.entity.SERequestDetailedList);
	    		 }
	    	
	     });
	     }
	


 $scope.login = function(){
    	console.log("Path:"+localStorage.getItem('path'));
    	if ($scope.user.username == "" && $scope.user.password ==""){
    		alertPopUp('Login Page', 'Please enter Username and Password');
    		
    	}
    	else{
         $http({
                       url : localStorage.getItem('path') + "/LoginService/login",
             method : "POST",
             data : JSON.stringify($scope.user),
        
         }).then(function(response){
        	 console.log(response);
        	 	if(JSON.parse(response.data.entity.Result)){
        	 			localStorage.setItem("notificationCount",response.data.entity.NotificationCount);
        	 			$rootScope.notificationCount = localStorage.getItem("notificationCount");
        	 		/* Admin Tab will only be shown to Admin User*/
        	 		var PrivilegeLevel = response.data.entity.PrivilegeLeve;
        	 		if (PrivilegeLevel === 2){
        	 			$scope.showPrivilegeLeveAdmin = true;
        	 			localStorage.setItem("PrivilegeLevelFlag",$scope.showPrivilegeLeveAdmin );
        	 		}else{
        	 			$scope.showPrivilegeLeveAdmin = false;
        	 			localStorage.setItem("PrivilegeLevelFlag",$scope.showPrivilegeLeveAdmin );
        	 		}
        	 		
        	 		/* Storing username in localStorage so it will be available after refresh in localStorage */
        	 		if (typeof(Storage) !== "undefined") {
        	 			
        	 			localStorage.setItem("user",$scope.user.username );
        	 		   console.log("user" + localStorage.getItem('user'));
        	 		} else {
        	 			
        	 		  console.log ( "browser does not support Web Storage...");
        	 		  
        	 		}
        	 		$rootScope.userInfo = {
        	 				'username' : '',
        	 				'password' : ''
        	 		}
        	 		
        	 		if (!!$scope.user.keepmelogin) {
        	 			localStorage.setItem("username", $scope.user.username);
        	 			localStorage.setItem("password", $scope.user.password);
        	 			localStorage.setItem("keepmelogin", 'remember');
    	 		    } else {
    	 		    	localStorage.setItem("username", '');
        	 			localStorage.setItem("password", '');
        	 			localStorage.setItem("keepmelogin", 'forget');
    	 		    }
        	 		
        	 		/* Storing username in rootScope*/
        	 		localStorage.setItem("user",$scope.user.username );
        	 		$rootScope.userInfo.username = $scope.user.username;    
        	 		$rootScope.userInfo.password = $scope.user.password;
        	 		
                    $state.go('Home');
        	 	}
        	 	else
        	 		{
        	 		 alertPopUp('Login Page', response.data.entity.Message);
        	 		// bootbox.alert(response.data.entity.Message);
        	 		 console.log(response.data.entity);
        	 		 $scope.user="";
        	 		}
                
         })
    	}
         
    }
     $scope.logOut = function(){       
        bootbox.confirm({
        title : 'Confirm Logout',
        message: "Are you sure you want to Logout?",
            buttons: {          
               
                confirm: {
                    label: 'Yes',
                    className: 'btn-default'
                },
                 cancel: {
                    label: 'No',
                    className: 'btn-primary'
                }
            },
            callback: function (result) {
            if(result)
            	{
                	$http({
                        url : localStorage.getItem('path') + "/LogoutService/logout",
                         method : "GET",
                         data : JSON.stringify($scope.user),
                    
                     }).then(function(response){
                     	console.log("User Logged out");
                     });
                	localStorage.setItem("user","");
	            	$rootScope.userInfo=null;
	            	$state.go('login');
	            	$scope.user="";
            	} 
            }
        });
    }
     
     $(function () {
 	    $(".dropdown-submenu a.test").hover(function () {
 	    	$(this).next('ul').toggle();
 	    	
 	    	
 	    });
 	});
     
    $scope.makeMeActive = function(key){
        activeTabs.activeTabsFunc(key);
		$rootScope.menubar = activeTabs.menubar;
	
    }
    
} ])
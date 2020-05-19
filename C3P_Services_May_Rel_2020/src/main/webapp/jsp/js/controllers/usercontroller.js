C3PApp.controller('userController',['$scope','$rootScope','$http', '$state','activeTabs',function($scope, $rootScope, $http, $state,activeTabs) {  
	
	
    $rootScope.menubar = {
        'Home' : false,
        'Dashboard' : false,
        'Configuration' : false,
        'Report' : false,
        'Admin' : false
        
    }
        
    $scope.user ={
        username : '',
        password :'',
        keepmelogin: false
    }
   
    $scope.makeMeActive = function(key){
        activeTabs.activeTabsFunc(key);
		$rootScope.menubar = activeTabs.menubar;
	
    }
    
    $scope.feNotiReqListFlag = false;
    $scope.seNotiReqeListFlag  = false;
    $scope.pendingTemplateList = function(){
		 $http({
	            url : localStorage.getItem('path') + "/GetNotifications/get",
	        	method : "GET"
	      
	     }).then(function(response){
	    		 $scope.notiTemplateList = JSON.parse(response.data.entity.TemplateList);
	    		 if(response.data.entity.FERequestDetailedList != ""){
	    		  $scope.feNotiReqListFlag = true;
	    		  $scope.feNotiReqList = JSON.parse(response.data.entity.FERequestDetailedList);
	    		 }
	    		 if(response.data.entity.SERequestDetailedList != ""){
	    		  $scope.seNotiReqeListFlag  = true;
	    		   $scope.seNotiReqeList = JSON.parse(response.data.entity.SERequestDetailedList);
	    		 }
	    		 console.log(response);
	    	
	     });
	     }
    
    $scope.showPrivilegeLeveAdmin = JSON.parse(localStorage.getItem("PrivilegeLevelFlag"));
    
    $scope.user.username = localStorage.getItem("username");
	  $scope.user.password = localStorage.getItem("password");
	  if (localStorage.getItem("keepmelogin") == "remember"){
		  $scope.user.keepmelogin  = true;
	  }else if (localStorage.getItem("keepmelogin") == "forget"){
		  $scope.user.keepmelogin  = false;
	  }
   
	/* $scope.login = function(){
    	
    	if ($scope.user.username == "" && $scope.user.password ==""){
    		alertPopUp('Login Page', 'Please enter Username and Password');
    	}
    	else{
         $http({
             //url : "http://localhost:8023/LoginService/login",

            url : localStorage.getItem('path') + "/LoginService/login",
             method : "POST",
             data : JSON.stringify($scope.user),
        
         }).then(function(response){
        	 console.log(response);
        	 	if(JSON.parse(response.data.entity.Result)){
        	 		if (response.data.entity.NotificationCount){
        	 			localStorage.setItem("notificationCount",response.data.entity.NotificationCount);
        	 			localStorage.setItem("notiTemplateList",JSON.parse(response.data.entity.TemplateList));
        	 			$rootScope.notificationCount = localStorage.getItem("notificationCount");
        	 			$scope.notiTemplateList = JSON.parse(response.data.entity.TemplateList);
        	 		}
        	 		 Admin Tab will only be shown to Admin User
        	 		var PrivilegeLevel = response.data.entity.PrivilegeLeve;
        	 		if (PrivilegeLevel === 2){
        	 			$scope.showPrivilegeLeveAdmin = true;
        	 			localStorage.setItem("PrivilegeLevelFlag",$scope.showPrivilegeLeveAdmin );
        	 		}else{
        	 			$scope.showPrivilegeLeveAdmin = false;
        	 			localStorage.setItem("PrivilegeLevelFlag",$scope.showPrivilegeLeveAdmin );
        	 		}
        	 		
        	 		 Storing username in localStorage so it will be available after refresh in localStorage 
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
        	 		
        	 		 Storing username in rootScope
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
         
    }*/
    
    function userCtrl($scope, userService) {
        $scope.user = userService;
    }
    	
  $rootScope.refreshNotificationCount = function(){
           $http({
            url : localStorage.getItem('path') + "/GetNotifications/get",
            method : "GET"
          
         }).then(function(response){
                     localStorage.setItem("notificationCount",response.data.entity.NotificationCount);
                        $rootScope.notificationCount = localStorage.getItem("notificationCount");
                    
         });
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
            	}
            }
        });
    }
    
    $(function () {
 	    $(".dropdown-submenu a.test").hover(function () {
 	    	$(this).next('ul').toggle();
 	    	
 	    	
 	    });
 	});
    
} ])


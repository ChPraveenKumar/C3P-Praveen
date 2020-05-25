//application angular ui-routing - navigate to different views
C3PApp.config(

    function($stateProvider, $urlRouterProvider) {
       
        $urlRouterProvider.otherwise('/');
          
        $stateProvider
           .state('login', {
	            url: '/',
	            templateUrl: 'jsp/js/templates/login.html',
	            controller : 'headerController'                
           })
           .state('Home', {
                url: '/Home',                   
                templateUrl: 'jsp/js/templates/home.html',
                controller : 'userController'                         
            })
            .state('Dashboard', {
                url: '/Dashboard',                   
                templateUrl: 'jsp/js/templates/dashboard.html',
                controller : 'dashboardController'
            })
             .state('Approval', {
                url: '/Approval',                   
                templateUrl: 'jsp/js/templates/approval.html',
                controller : 'approvalController'
            })
            .state('approvalDetails', {
                url: '/approvalDetails?templateId?version?isEditable?read',
                templateUrl: 'jsp/js/templates/approvalDetails.html',
                controller : 'approvalController'
               
            })
            .state('fieldEng', {
                url: '/fieldEng',                   
                templateUrl: 'jsp/js/templates/fieldEng.html',
                controller : 'fieldEngController'
            })
            .state('fieldEngDetails', {
                url: '/fieldEngDetails?requestId?version?read',                   
                templateUrl: 'jsp/js/templates/fieldEngDetails.html',
                controller : 'fieldEngController'
            })
            .state('RA', {
                url: '/RA',                   
                templateUrl: 'jsp/js/templates/responsiveAnalysis.html',
                controller : 'alertNotiController'
            })
            .state('dashboardReqDetails', {
                url: '/dashboardReqDetails?dashboardReq_Details?version?read?pageName',
                templateUrl: 'jsp/js/templates/dashboardReqDetails.html',
                controller : 'requestDetailsController'
               
            }) 
            .state('editDashboardDetails', {
                url: '/editDashboardDetails?dashboardReq_Details?version',
                templateUrl: 'jsp/js/templates/editDashboardDetails.html',
                controller : 'modifyrequestDetailsController'
               
            })
            .state('reqDetails', {
                url: '/reqDetails?dashboardReq_Details?version?pageName',
                templateUrl: 'jsp/js/templates/reqDetails.html',
                controller : 'requestDetailsController'
               
            })
            .state('CMCreate', {
                url: '/create',
                templateUrl: 'jsp/js/templates/createCM.html',
                controller: 'createConfigController'
            })
            .state('createSDN', {
                url: '/createSDN',
                templateUrl: 'jsp/js/templates/createSDN.html',
                controller: 'createConfigController'
            })
            .state('iosUpdate', {
                url: '/iosUpdate',
                templateUrl: 'jsp/js/templates/iosUpdateSearchScreen.html',
                controller: 'iosUpdateController'
            })
            .state('ViewConfiguration', {
                url: '/ViewConfiguration',
                templateUrl: 'jsp/js/templates/viewConfiguration.html',
                controller : 'viewCMController'
            })
            
            .state('Reports', {
                url: '/Reports',                   
                templateUrl: 'jsp/js/templates/report.html',
                controller : 'reportController'
            })
            .state('DeviceManagement', {
                url: '/DeviceManagement',                   
                templateUrl: 'jsp/js/templates/deviceManagement.html',
                controller : 'deviceMgtController'
            })
            .state('GlobalListManagement', {
                url: '/GlobalListManagement',                   
                templateUrl: 'jsp/js/templates/globalListManagement.html',
                controller : 'globalListMgtController'
            })
            .state('Alert', {
                url: '/Alert',                   
                templateUrl: 'jsp/js/templates/alert.html',
                controller : 'alertNotiController'
            })
            .state('Admin', {
                url: '/Admin',                   
                templateUrl: 'jsp/js/templates/findip.html',
               controller : 'findipcontroller'
            })
            .state('templateManagement', {
                url: '/templateManagement',                   
                templateUrl: 'jsp/js/templates/templateManagement.html',
                controller : 'templateMgtController'
            });
    }
).run(function ($http,$rootScope,$state,$location) {
	var fullPath = window.location.origin + '/C3P';
	localStorage.setItem("path", fullPath);
	/* done authentication on each page or on state change */
	$rootScope.$on('$stateChangeStart', function(angularEvent, newUrl) {
		$rootScope.notificationCount = localStorage.getItem("notificationCount");
		var user = localStorage.getItem('user');
		var PrivilegeLevelFlag = JSON.parse(localStorage.getItem('PrivilegeLevelFlag'));
		/* setting user in rootscope after refresh */
		$rootScope.userInfo = {
 				'username' : '',
 				'showPrivilegeLeveAdmin': false
 		}
		$rootScope.userInfo.username = user;
		$rootScope.userInfo.showPrivilegeLeveAdmin = PrivilegeLevelFlag;
	     if (user == "" || user == null) {
             // User is not authenticated
             $location.path('/');
         }
        
     });
   $http.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
   
}); 





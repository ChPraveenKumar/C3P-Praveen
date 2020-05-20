C3PApp.component('pageHeader',{
    templateUrl : 'jsp/js/templates/header.html',
    controller : 'headerController'
})

C3PApp.component('commonReqDetails',{
	url: '/commonReqDetails?dashboardReq_Details?version',
    templateUrl : 'jsp/js/templates/commonReqDetails.html',
    controller : 'requestDetailsController'
})

C3PApp.component('schedule',{
/*	url: '/schedule?dashboardReq_Details?version',*/
    templateUrl : 'jsp/js/templates/schedule.html',
    controller : 'createConfigController'
})
C3PApp.component('iosReqDetails',{
	url: '/iosReqDetails?dashboardReq_Details?version',
    templateUrl : 'jsp/js/templates/commonReqDetails.html',
    controller : 'requestDetailsController'
})
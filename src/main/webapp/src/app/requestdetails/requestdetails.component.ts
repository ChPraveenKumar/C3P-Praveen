import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Global } from'../global';
import { CommonService } from '../common.service';
import { ActivatedRoute } from '@angular/router';
import {DOCUMENT} from '@angular/platform-browser';
import * as $ from 'jquery';
//import * as globaldiff from '';
declare var global:any; 
 
@Component({
  selector: 'app-requestdetails',
  templateUrl: './requestdetails.component.html',
  styleUrls: ['./requestdetails.component.css'],
  providers: [ CommonService]
})
export class RequestdetailsComponent implements OnInit {
  RequestDetails: any[];
  searchUrl;
  postData;
  respData;
  menubar;
  respDataOutput;
  ReportDetails;
  private sub: any;
  display_request_id;
  templateId: string="";
  version;
  activeTab;
  activeTabs;
  getSubTestData;
  ReportDetailsFlag;
  report_title;
  deliverConfig;
  backupStatus;
  deliveryStatus;
  errorType;
  errorWarningMsg;
  errorRouterMessage;
  getReportdata;
  getdata;
  getType;
  format;
  formatColor;
  openAddPopUp;
  closeAddPopUp;
  notificationCount;
  notificationData;
  setUrl;
  getData;
  dragElement;
  alertPopUp;
  getPrevdata;
  disableText;
  private args= {source:"", diff:"", lang:""};
  @ViewChild('callSchedule') schedule;
  constructor(private global: Global, private commonService: CommonService, private route: ActivatedRoute) {
    this.menubar = global.menubar
    this.openAddPopUp = global.openAddPopUp
    this.closeAddPopUp = global.closeAddPopUp
    this.notificationCount = global.notificationCount
    this.alertPopUp = global.alertPopUp
    this.dragElement = global.dragElement
    
   }

  ngOnInit() {        
    this.getRouteParam();
    this.searchRequest();          
    this.getNotificationCountData();
  }
  
  getReqScheduler(id, evntFromReqDetail){
    this.openAddPopUp(id);
    this.dragElement(document.getElementById(id));
    this.schedule.schedulerOption = '2';
		this.schedule.getScheduledGrid(this.display_request_id, this.version, this.templateId, evntFromReqDetail);
  }

  getConfigDiff = function(findDiff){

    this.searchUrl = "/GetReportData/getRouterConfigData"
    this.postData = {"flagForData":findDiff, requestID: this.display_request_id, version: this.version}
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(respData => {this.respData = respData       
      if (respData.entity){
        var prevVersion = respData.entity.previousRouterVersion;	        			        		
        var currVersion = respData.entity.currentRouterVersion;
        //setTimeout(function(){            
            this.getDiffViews(prevVersion, currVersion);
            this.scrollingFunc();
          
        //});
        
        $("#DiffConfigVersion").css("display", "block");
        this.dragElement(document.getElementById(("DiffConfigVersionContainer")));	
    
      }
    })
  }

  scrollingFunc (){
		$('.versionPopUpContainer').attr("data-scrolling", "false");
		$('.versionPopUpContainer').on('scroll', function () {
		     if($(this).attr("data-scrolling") == "false"){
		        $('.versionPopUpContainer').not(this).attr("data-scrolling", "true");
		        $('.versionPopUpContainer').not(this).scrollTop($(this).scrollTop());
		    }
		$(this).attr("data-scrolling", "false");
		});
	}

  getDiffViews (baseText, newText){
    this.disableText = false;
    let output = ""
    this.args= {
              source: baseText,
              diff  : newText,
              lang  : "text"
          }
      
    if (this.args.source == "" && this.args.diff !== ""){
      setTimeout(()=>
        $(".versionPopUpContainer .replace:first").css({"visibility": "hidden"}) &&
        $(".versionPopUpContainer .replace:last em").css({"background-color": "white"})
      , 10);
      this.args.source = "No Backup Generated"
      output = global.global.prettydiff.prettydiff(this.args);
      $("#prettydiff .diff , #prettydiff p").remove();
      $("#prettydiff").append(output);
    }else if(this.args.source !== "" && this.args.diff == ""){
      setTimeout(()=>      
        $(".versionPopUpContainer .replace:first em").css({"background-color": "white"}) &&
        $(".versionPopUpContainer .replace:last").css({"visibility": "hidden"})
      , 10);
      this.args.diff = "No Backup Generated"
      output = global.global.prettydiff.prettydiff(this.args);
      $("#prettydiff .diff , #prettydiff p").remove();
      $("#prettydiff").append(output);
    }else if (this.args.source == "" && this.args.diff == ""){
      this.disableText = true;
    }else{
      output = global.global.prettydiff.prettydiff(this.args);
      $("#prettydiff .diff , #prettydiff p").remove();
      $("#prettydiff").append(output);
    }    		    
    
  }
   

  getbackupConfig(backupConfig){

    this.searchUrl = "/GetReportData/getRouterConfigData"
    this.postData = {"flagForData":backupConfig, requestID: this.display_request_id, version: this.version}
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(respData => {this.respData = respData       
      if (respData.entity){
        this.getPrevdata = respData.entity.previousRouterVersion;	        		
        $("#prevRouterVersion").css("display", "block");
        this.dragElement(document.getElementById(("prevRouterVersionContainer")));	
    
      }else{
        this.alertPopUp('Error', 'Data not Found');
        return false;
      }
    })
  }

  getNotificationCountData(): void {
    this.setUrl = "/GetNotifications/get";
    this.getData = ""
    this.commonService.getData(this.setUrl, this.getData) 
      .subscribe(notificationData => {this.notificationData = notificationData
        localStorage.setItem("notificationCount",notificationData.entity.NotificationCount);
        this.notificationCount = localStorage.getItem("notificationCount");
      });
  }

  searchRequest(): void {
    this.searchUrl = "/SearchRequestServiceWithVersion/search"
    this.postData = {key:'Request ID', value:this.display_request_id, version : this.version}
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(respData => {this.respData = respData
      this.RequestDetails = JSON.parse(respData.entity.output)      
      if (respData.entity.ReportStatus){        
        this.ReportDetails = JSON.parse(respData.entity.ReportStatus);
        this.ReportDetailsFlag = true
      }
      if (this.RequestDetails){
        setTimeout(()=>this.makeMeActive(this.activeTabs), 10);
      }
    })
    
  }
  getUpdatedGridData(){
    this.ReportDetails = [];
    this.ReportDetailsFlag = false
    this.searchRequest();
  };
  
  getValidateSubTestData(testType): void {
    this.searchUrl = "/GetCertificationTestData/getPrevalidationTestData"
    this.postData = {requestID:this.display_request_id, testType : testType, version : this.version}
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(respData => {this.respData = respData
      this.getSubTestData = respData.entity.output;
    })
  }

  getConfigratioData(testType, report_title): void {
    this.searchUrl = "/GetReportData/getReportDataforTest"
    this.report_title = report_title;
    this.postData = {requestID:this.display_request_id, testType : testType, version : this.version}
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(respData => {this.respData = respData
      if (testType == "deliverConfig"){
        this.deliverConfig = true
        if (respData.entity.backupStatus == "Completed"){
          this.backupStatus = 'Success';
          //$(".bckupStatus").addClass("bkup-success-icon").removeClass("bkup-fail-icon");
        }else {
          this.backupStatus = 'Failed';
         // $(".bckupStatus").addClass("bkup-fail-icon").removeClass("bkup-success-icon");
        }

        if (respData.entity.status == "Success"){
          this.deliveryStatus = respData.entity.status;
          //$(".deliveryStatus").addClass("bkup-success-icon").removeClass("bkup-fail-icon");
          if (respData.entity.errorType == "Warning"){
            this.errorType = respData.entity.errorType;
            this.errorWarningMsg = respData.entity.errorDesc;
            this.errorRouterMessage = respData.entity.errorRouterMessage;
            
          }else {
            this.errorType = "false";
          }
        }else {
          this.deliveryStatus = respData.entity.status;
          //$(".deliveryStatus").addClass("bkup-fail-icon").removeClass("bkup-success-icon");
          if (respData.entity.errorType == "Failure"){
            this.errorType = respData.entity.errorType;
            this.errorWarningMsg = respData.entity.errorDesc;
            this.errorRouterMessage = respData.entity.errorRouterMessage;
            
          }else {
            this.errorType = "false";
          }
        }
        this.openAddPopUp('backupandDelivery');
				this.dragElement(document.getElementById(("backupandDeliveryContainer")));
      }else {
        this.deliverConfig = false;
        this.getReportdata = respData.entity.output;
        this.getdata = this.getReportdata;
        this.getType = respData.entity.testType;
        this.format = respData.entity.format;
        this.formatColor = respData.entity.formatColor;

        if(this.format == 'true'){
         // var cusReport = $( '#reportContainer' ) ;
          //cusReport.html(this.getdata);
        }
        if(this.formatColor == 'true'){
          //var cusReport =$( '#reportContainerColorCode' );
          //cusReport.html(this.getdata);
        }
        $("#reportPopUp").css("display", "block");
        this.openAddPopUp('reportPopUp');
        this.dragElement(document.getElementById(("reportPopUpContainer")));	
      }
    })
  }
 

  getRouteParam (){
    this.sub = this.route.queryParams.subscribe(params => {
      this.display_request_id = params['dashboardReq_Details']; // (+) converts string 'id' to a number
      this.version = params['version'];
      this.activeTabs = params['page'];
   });
  }
  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  makeMeActive = function(key){
    this.activetab =this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }

}

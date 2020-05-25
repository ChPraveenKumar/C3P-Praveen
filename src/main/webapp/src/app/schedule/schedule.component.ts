import { Component, OnInit } from '@angular/core';
import { Global } from'../global';
import { CommonService } from '../common.service';
import { Router } from '@angular/router';
import {Observable} from 'rxjs/Rx';
import 'eonasdan-bootstrap-datetimepicker';
import * as moment from 'moment';
declare var bootbox:any; 

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  menubar;
  openAddPopUp;
  closeAddPopUp;
  schedulerOption:string ;
  schedulerValue:boolean = true;
  searchUrl;
  postData;
  respData;
  requestsList;
  eventDisable:boolean = false;
	reSchedule:boolean = false;
	cancelSchedule:boolean = false;
  saveSchedule:boolean = false;
  requestId;
  version;
  reqId;
  vers;
  scedulerDatePicker;  
  date: moment.Moment;
  startOptions: any;
  modifyReSchedule: any;
  createConfSubmitted;
  constructor(private global: Global, private commonService: CommonService, private router: Router) {
    this.menubar = global.menubar
    this.openAddPopUp = global.openAddPopUp
    this.closeAddPopUp = global.closeAddPopUp
    this.date = moment();
    this.startOptions = {format: 'DD/MM/YYYY HH:mm:ss', 
     /* debug:true*/
    };
    
   }

   myDate(){
    this.startOptions.minDate = moment()
   }
   
  ngOnInit() {
    this.myDate()
  }

  closeSchedulePopUp (id){
    if (this.saveSchedule === false && this.scedulerDatePicker !== undefined){
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
               this.scedulerDatePicker = "";
               this.closeAddPopUp(id);
             }
          }
      });
    }else{
      
      this.closeAddPopUp(id);
    }
  }

  abortRequest (requestId, version){
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
            this.searchUrl = "/RequestScheduleService/abortRequest"
            this.postData = {'requestId': requestId, 'version': version}
            this.commonService.postData(this.searchUrl, this.postData)
              .subscribe(respData => {this.respData = respData
                //$state.go('Dashboard');
                this.router.navigateByUrl("/dashboard");
              })
          }
        }
    })
  }
  cancelSchedulerReq(requestId, version){
    this.searchUrl = "/RequestScheduleService/cancelRequest"
    this.postData = {'requestId': requestId, 'version': version}
    this.commonService.postData(this.searchUrl, this.postData)
      .subscribe(respData => {this.respData = respData
        console.log(JSON.parse(respData.entity.output));
        this.getScheduledGrid(requestId, version, "","");
      })    
  }

  runScheduleRequest (requestId, version){
								
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
             
             if (this.modifyScheduler === true){
                 configuartion.certificationOptionListFlags = this.certificationListFlags;
             }
                                      
          let uploadUrl = "/RequestScheduleService/runScheduleRequest",
         
          data = {
              "requestId": requestId,
              "version": version,
              "data": configuartion
            },
          Data = JSON.stringify(data);
          
          this.commonService.postData(uploadUrl, Data)
          .subscribe(respData => {this.respData = respData
                
            //$state.go('dashboardReqDetails', { dashboardReq_Details: requestId,version: version});
          })

           }else{
             //console.log("request canceled");
           }
        }
    });
    
    
  }

  saveConfiguration(event) {
    // if($rootScope.editReqDetailsFlag){
    //   $rootScope.saveConfigurationForEdit(event);
    // }
    // else{
    this.createConfSubmitted = true;
    var configuartion = JSON.parse(localStorage.getItem('configuartion'));

    this.searchUrl = "/ConfigMngmntService/createConfigurationDcm"
    this.commonService.postData(this.searchUrl, configuartion)
    .subscribe(respData => {this.respData = respData       
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
          //$state.go('dashboardReqDetails', { dashboardReq_Details: response.data.requestId,version: response.data.version, pageName: 'Configuration'});
          window.open('http://localhost:4200/requestdetails?dashboardReq_Details='+respData.requestId+'&version='+respData.version+'&page=Configuration','_self');
        }
      });
    })

    //}
  }

  scheduleAction (action, requestId, version){
    var router = this.router
    if(action == "reschedule"){
      this.reSchedule = true;
      this.saveSchedule = false;
      this.eventDisable = false;
      this.requestId = requestId;
      this.version = version;
      this.cancelSchedule = false;
    }else if (action == "run"){
      this.saveSchedule = false;
      this.runScheduleRequest(requestId, version);
    }else if(action == "ok"){
      this.saveSchedule = false;
      if (this.cancelSchedule === true){
        var reqId = this.reqId,
        vers = this.vers
        this.abortRequest(reqId, vers);
      }else{
        router.navigateByUrl("/dashboard");
      }									
    }else if(action == "cancel"){
      this.saveSchedule = false;
      this.cancelSchedule = true;
      this.eventDisable = true;
      this.reqId = requestId;
      this.vers = version;
      this.cancelSchedulerReq(requestId, version)
    }else if(action == "close"){
      this.saveSchedule = false;
      if (this.cancelSchedule === true){
        var reqId = this.reqId,
        vers = this.vers
        this.abortRequest(reqId, vers);
      }else if(this.saveSchedule === false && this.scedulerDatePicker !== undefined){
        this.closeSchedulePopUp('schedulePopUp')			
      }else{
        router.navigateByUrl("/dashboard");
      }
    }
  }	

  getScheduledData = function(evt){
    this.saveSchedule = true;
    if (this.scedulerDatePicker == "" || this.scedulerDatePicker == undefined){
      return false;
    }else{
      var configuartion = JSON.parse(localStorage.getItem('configuartion'));
      configuartion.scheduledTime = this.scedulerDatePicker;
      let urlPath = "", data = {}, Data ={};

      if (this.reSchedule == true){
          urlPath = "/RequestScheduleService/recheduleRequest";
          data = {
            "requestId": this.requestId,
            "version": this.version,
            "scheduledTime": this.scedulerDatePicker
          }
          Data = JSON.stringify(data);
      }else if(this.modifyScheduler === true){
        var editRequestDetails = this.editRequestDetails;
        var certifictionFlags = this.certificationListFlags;
        editRequestDetails.scheduledTime = this.scedulerDatePicker;
        urlPath = "/ModifyConfiguration/modify";
        data = {
          editeData : editRequestDetails,
          certificationOptionListFlags : certifictionFlags
        }
        Data = JSON.stringify(data);
      }else{
        urlPath = "/ConfigMngmntService/createConfigurationDcm";
        Data = JSON.stringify(configuartion);
      }
        var uploadUrl = urlPath;   
        this.commonService.postData(uploadUrl, Data)
        .subscribe(respData => {this.respData = respData

          if (this.reSchedule == true){
            this.getScheduledGrid(this.requestId, this.version);
           }else{
            this.getScheduledGrid(respData.data.requestId, respData.data.version);
           }
           
           this.scedulerDatePicker = "";
           this.eventDisable = evt;     
          
        })      
    }
    
  }

  getScheduledGrid (requestId, version, templateID, evntFromReqDetail): void {
    this.searchUrl = "/RequestScheduleService/getScheduledHistory"
    this.postData = {'requestId': requestId, 'version': version, 'templateID' : templateID}
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(respData => {this.respData = respData
      this.requestsList = JSON.parse(respData.entity.output)      
      if (evntFromReqDetail === true && this.requestsList.length > 0){
        this.eventDisable = true;
      }
            
          for (let itm of this.requestsList) {
            itm.showActionReschdule = false;
            itm.showActionRun = false;
            itm.showActionCancel = false;
          }
          if (this.requestsList.length >= 1){
            if(this.cancelSchedule === true){
              this.requestsList[0].showActionReschdule = true;
              this.requestsList[0].showActionRun = false;
              this.requestsList[0].showActionCancel = false;					            			   
            }else if(this.modifyReSchedule === true){
              this.requestsList[0].showActionReschdule = false;
              this.requestsList[0].showActionRun = false;
              this.requestsList[0].showActionCancel = false;
              this.modifyReSchedule = false;
            }else{					            		   	   
              this.requestsList[0].showActionReschdule = true;
              this.requestsList[0].showActionRun = true;
              this.requestsList[0].showActionCancel = true;
            }					            		   					            		   
          }else{
                this.modifyReSchedule = false;
          }
    })
    
  }

  checkSchedulerIndex(index){
    this.schedulerOption = index;
      if (this.schedulerOption == '1'){
        this.eventDisable = true;
      }else {
        this.eventDisable = false;
      }	
  }

}

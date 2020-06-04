import { Component, OnInit, Input } from '@angular/core';
import { Global } from'../global';
import { CommonService } from '../common.service';
import { Router } from '@angular/router';
import {Observable} from 'rxjs/Rx';
declare var bootbox:any; 
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [ CommonService]
})
export class HomeComponent implements OnInit {
  userInfo;
  Url;
  user:{};
  postData;
  showPrivilegeLeveAdmin : string;
  showPrivilegeLeveAdminFlag : boolean;
  respData;
  notificationCount;
  notiTemplateList;
  alertPopUp;
  constructor(private global: Global, private commonService: CommonService, private router: Router) { 
    global.userInfo.username = localStorage.getItem("user")    
    this.userInfo = global.userInfo;
	
    this.notificationCount = global.notificationCount
    global.user.username = localStorage.getItem("username")
    global.user.password = localStorage.getItem("password")
    global.user.keepmelogin = JSON.parse(localStorage.getItem("keepmelogin"))
    this.user = global.user
    this.alertPopUp = global.alertPopUp
  }

  logOut(): void {
    var user = this.user,
    commonService = this.commonService,
    router = this.router,
    userInfo = this.userInfo;
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
              
              this.Url = "/LogoutService/logout"
              this.postData = user;
              commonService.getData(this.Url, this.postData)
              .subscribe(respData => {this.respData = respData
                if (respData.entity.Message){
                  console.log(respData.entity.Message)
                  user = {};
                  userInfo = {};
                  router.navigateByUrl("/login");
                }
                
              })
            } 
          }
      });
      
  }

  pendingTemplateList(){
    this.Url = "/GetNotifications/get"
    this.postData ="";
    this.commonService.getData(this.Url, this.postData)
      .subscribe(respData => {this.respData = respData
        if (JSON.parse(respData.entity.TemplateList)){
          this.notiTemplateList = JSON.parse(respData.entity.TemplateList);        
        }       
        
      })    
  }

  refreshNotificationCount(){
    this.Url = "/GetNotifications/get"
    this.postData ="";
    this.commonService.getData(this.Url, this.postData)
      .subscribe(respData => {this.respData = respData        
        if (JSON.parse(respData.entity.NotificationCount) >= 0){
          localStorage.setItem("notificationCount", respData.entity.NotificationCount);
	 	     this.notificationCount = localStorage.getItem("notificationCount");
        }
        
      }) 
  }  

  helpPopUp(){
    this.alertPopUp("Help", "Work In Progress");
  }

  ngOnInit() {
    this.refreshNotificationCount()
    Observable.interval(1800000).subscribe(x => {
      this.refreshNotificationCount()
    });
    this.showPrivilegeLeveAdmin = localStorage.getItem("PrivilegeLevelFlag");
    if(this.showPrivilegeLeveAdmin == "true"){
        this.showPrivilegeLeveAdminFlag = true;
    }else{
      this.showPrivilegeLeveAdminFlag = false;
    }
  }

  
}

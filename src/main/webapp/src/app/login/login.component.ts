import { Component, OnInit, Input } from '@angular/core';
import { CommonService } from '../common.service';
import { Router } from '@angular/router';
import { Global } from'../global';
import * as $ from 'jquery';
declare var bootbox:any; 
@Component({ 
  selector: 'app-login',
  templateUrl: './login.component.html', 
  styleUrls: ['./login.component.css'],
  providers: [CommonService]
})




export class LoginComponent implements OnInit {
  user;  
  Url: string;
  postData;
  respData;
  Output;
  userInfo;
  notificationCount;
  alertPopUp;
  showPrivilegeLeveAdmin: boolean;
  constructor(private commonService: CommonService, private global: Global, 
    private router: Router) {
    this.user = global.user
    this.notificationCount = global.notificationCount 
    this.userInfo = global.userInfo
    this.alertPopUp = global.alertPopUp
   }

   ngOnInit() {
    this.keepMelogin();
   }

   keepMelogin () {    
    
    this.user.keepmelogin = JSON.parse(localStorage.getItem("keepmelogin"))
    if (this.user.keepmelogin === true){
      this.user.username = localStorage.getItem("username");
      this.user.password = localStorage.getItem("password");
    }else {
      this.user.username = "";
      this.user.password = "";
    } 
    
   }

   login() : void { 
     
    if (this.user.username == "" && this.user.password =="" || this.user.username == "" || this.user.password =="" || this.user.username == undefined && this.user.password == undefined){
      this.alertPopUp('Login Page','Please enter Username and Password');
    }else {
      this.Url = "/LoginService/login"
      this.postData = this.user
      this.commonService.postData(this.Url, this.postData)
      .subscribe(respData => {this.respData = respData
        if (JSON.parse(respData.entity.Result)){

          localStorage.setItem("notificationCount",respData.entity.NotificationCount);
          this.notificationCount = localStorage.getItem("notificationCount");

          /* Admin Tab will only be shown to Admin User*/
          var PrivilegeLevel = respData.entity.PrivilegeLeve;
          if (PrivilegeLevel === 2){
            this.showPrivilegeLeveAdmin = true;
            localStorage.setItem("PrivilegeLevelFlag", String(this.showPrivilegeLeveAdmin) );
          }else{
            this.showPrivilegeLeveAdmin = false;
            localStorage.setItem("PrivilegeLevelFlag", String(this.showPrivilegeLeveAdmin) );
          }

          /* Storing username in localStorage so it will be available after refresh in localStorage */
          if (typeof(Storage) !== "undefined") {
                  
            localStorage.setItem("user",this.user.username );
            console.log("user" + localStorage.getItem('user'));
          } else {
            
            console.log ( "browser does not support Web Storage...");
            
          }

          if (!!this.user.keepmelogin) {
            localStorage.setItem("username", this.user.username);
            localStorage.setItem("password", this.user.password);
            localStorage.setItem("keepmelogin", 'remember');
          } else {
            localStorage.setItem("username", '');
            localStorage.setItem("password", '');
            localStorage.setItem("keepmelogin", 'forget');
          }
          
          /* Storing username in rootScope*/

          localStorage.setItem("username",this.user.username );
          localStorage.setItem("password",this.user.password );
          localStorage.setItem("keepmelogin",this.user.keepmelogin );

          localStorage.setItem("user",this.user.username );
          this.userInfo.username = this.user.username;    
          this.userInfo.password = this.user.password;
          this.router.navigateByUrl("/home");         

        }else {
          this.alertPopUp('Login Page', respData.entity.Message);
          // bootbox.alert(respData.entity.Message);
           console.log(respData.entity);
           this.user.username = "";
           this.user.password = ""
           this.user.keepmelogin = false;

        }
        

      })
    }

   }
  
}

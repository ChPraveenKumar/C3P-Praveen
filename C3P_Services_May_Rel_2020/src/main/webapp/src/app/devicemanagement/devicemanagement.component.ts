import { Component, OnInit, Input } from '@angular/core';
import { Global } from'../global';
import { CommonService } from '../common.service';
import { Router } from '@angular/router';
declare var bootbox:any; 
@Component({
  selector: 'app-devicemanagement',
  templateUrl: './devicemanagement.component.html',
  styleUrls: ['./devicemanagement.component.css'],
  providers: [ CommonService]
})
export class DevicemanagementComponent implements OnInit {
  menubar:{};
  deviceMgtUsername: string = "";
  deviceMgtPassword: string = "";
  Url: string = "";
  postData;
  respData;
  getData;
  getUserData;
  constructor(private commonService: CommonService, 
    private global: Global, private router: Router) {
      this.menubar = global.menubar
     }

  submitDeviceMgtData(myForm){
    var flag = this.commonService.requiredValidation(myForm);
    if(!flag){
    var router = this.router
    this.Url = "/addDeviceManagementUser/updateRouterCredential"
    this.postData = {username:this.deviceMgtUsername, password:this.deviceMgtPassword}
    this.commonService.postData(this.Url, this.postData)
    .subscribe(respData => {this.respData = respData
      if (respData){
        bootbox.confirm({
          title : 'Request Status',
          message : respData.entity.Message,
          buttons : {
              
            confirm : {
              label : 'Ok',
              className : 'btn-default'
            },
  
          },
          callback : function(result) {
            if (result)
            router.navigateByUrl("/devicemanagement");
             
          }
        });
      }     

    })
  }
  }

  getUserDetails(){
    this.Url = "/addDeviceManagementUser/getRouterCredentials";
    this.getData = ""
    this.commonService.getData(this.Url, this.getData) 
      .subscribe(getUserData => {this.getUserData = getUserData
        this.deviceMgtUsername = getUserData.entity.username;
        this.deviceMgtPassword = getUserData.entity.password;
      });
  }

  ngOnInit() {
    this.makeMeActive('Admin');
    this.getUserDetails();
  }

  makeMeActive = function(key){
    this.activetab =this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }

}

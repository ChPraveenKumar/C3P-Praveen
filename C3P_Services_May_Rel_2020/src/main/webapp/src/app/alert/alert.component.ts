import { Component, OnInit } from '@angular/core';
import { CommonService } from '../common.service';
import { Router, ActivatedRoute} from '@angular/router';
import { Global } from '../global';
declare var bootbox: any;
@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {
  NewAlertTypeItems = [ "Select","Alert", "Notification"];
  NewAlertCategoryItems = [ "Select","Configuration generation", "Pre Test", "Certification Test"  ];
  url: string="";
  searchData: any[];
  requestsList: any[];
  gridDataOutput : any[];
  originalData: Array<any> = [];
  rowCollection = '';
  errorMessege: Boolean;
  errorFlag: boolean;
  searchUrl: string;
  postData:object;
  alertNotiCode:string;
  alertNotiDesc: string;
  NewAlertUrl: string;
  alertList: any[];
  respData: any;
  addAlertNotiType:any;
  addAlertNotiCategory:any;
  addAlertNotiDesc:any;
  addAlertNotiCode:any;
  requiredValidationFlag: boolean = false;
  private menubar:{};
  order;
  reverse: boolean = false;
  sortKey: string = '';
  newalertCode:string;
  AlertCode: any;
  newcode: Number;
  SelectedType:any;
  GenAlertNotiCode: any;
  editAlertNotiCode : string;
  editAlertNotiCategory : string;
  editAlertNotiDesc : string;
  editAlertNotiType : string;
  showLoader: boolean = false;

  constructor( private commonService: CommonService, private router: Router,  private global: Global,
  private activatedroute : ActivatedRoute) {
    this.router = router;
    this.menubar = global.menubar;
   }

  ngOnInit() {
    this.errorFlag = false;
    this.addAlertNotiType = this.NewAlertTypeItems[0];
    this.addAlertNotiCategory = this.NewAlertCategoryItems[0];
    this.getAllNotiDataOnLoad();
    this.makeMeActive('Admin');
  }

  flushAlertNotiData(){
    this.commonService.openAddPopUp('addPopUp');
  }
  getNewAlertPopupData(newAlert){
    this.alertList=newAlert.value
  }
  resetAlertNotiData() {
    $('.ng-invalid').css('border', '');
    this.addAlertNotiType = "";
    this.addAlertNotiCategory = "";
    this.addAlertNotiDesc = "";
    this.addAlertNotiCode = "";
  }
  makeMeActive = function (key) {
    this.activetab = this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }

  setOrder(value: string)
   {
    this.order =   this.commonService.setGridOrder(value, this.gridDataOutput)
    this.reverse = this.order.reverse
    this.sortKey =this.order.sortKey 
  }
  
  generateNewCode(addAlertNotiType){
    this.SelectedType=addAlertNotiType;
    this.url="/GetLastAlertID/getLastAlertId"
    this.commonService.getServiceData(this.url)
    .subscribe(newalertCode => {
      this.newalertCode = newalertCode;
      this.AlertCode = JSON.parse(newalertCode.entity.LastID);
      if(this.SelectedType == "Alert")
        {
          this.newcode=parseInt(this.AlertCode) + 1;
          this.addAlertNotiCode = "A" + this.newcode;
        }
      else
        {
          this.newcode=parseInt(this.AlertCode) + 1;
          this.addAlertNotiCode = "N" + this.newcode;
        }
    });
  } 
   
  getAllNotiDataOnLoad() {
    let url = "/GetAllAlertData/getAll"
    this.commonService.getServiceData(url)
      .subscribe(searchData => {
        this.searchData = searchData
        this.requestsList = JSON.parse(searchData.entity.output)
        this.originalData = this.requestsList;
        this.rowCollection = JSON.parse(searchData.entity.output)
        this.gridDataOutput = JSON.parse(searchData.entity.output)
        if (this.requestsList.length == 0) {
          this.errorFlag = true;
        }
        this.gridDataOutput = JSON.parse(searchData.entity.output)
        if (this.gridDataOutput.length == 0) {
          this.errorFlag = true;
        }
      })
  }

  searchAlertNotiRecord(alertNotiCode, alertNotiDesc) {
    this.searchUrl = "/SearchAllAlertNotification/search"
    this.postData = { alert_code: this.alertNotiCode, alert_description: this.alertNotiDesc }
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(searchData => {
      this.searchData = searchData
      if (searchData.entity.output == "") {
        this.errorMessege = true;
        this.gridDataOutput = [];
      }
      
      else if (searchData.entity.output && searchData.entity.output.length > 0) {
        this.errorMessege = false;
        this.requestsList = JSON.parse(searchData.entity.output);
        this.gridDataOutput = JSON.parse(searchData.entity.output);
        if (this.requestsList.length == 0) {
          this.errorMessege = true;
        } else {
          this.errorMessege = false;
        }
      } else if ((alertNotiCode || alertNotiDesc) == "") {
        this.requestsList = this.originalData;
        this.gridDataOutput = this.originalData;
      }
    })
  }
 
  closeNewAddPopUp(addPopUp){
    this.commonService.closeAddPopUp(addPopUp);
    this.resetAlertNotiData();
  }

  addNewAlertNotiData(newAlertNoti, addAlertNotiType, addAlertNotiCategory,addAlertNotiDesc,addAlertNotiCode){

    var router = this.router,
    commonService=this.commonService,
    getAllNotiDataOnLoad = this.getAllNotiDataOnLoad;
    var requiredValidation = false;
    $('.addPopUpField').each(function (index, item) {
      if ($(item).hasClass("ng-invalid")) {
        $('.ng-invalid').css('border', '1px solid rgb(222, 52, 52)');
        $('#' + newAlertNoti).css('border', 'none');
        requiredValidation = true;

      }
    }); 
    if (requiredValidation) {
      this.commonService.alertPopUp("Error", "Please fill all the mandatory fields");
    }
    if (!requiredValidation) {
    this.NewAlertUrl = "/AddNewAlertNotificationService/add";
    this.postData={
      alert_type : addAlertNotiType,
      alert_category : addAlertNotiCategory,
      alert_description : addAlertNotiDesc,
      alert_code : addAlertNotiCode
    }
    this.commonService.postData(this.NewAlertUrl,this.postData)
    .subscribe(respData => {
      this.respData = respData
      if (respData) {
        bootbox.confirm({
          title: 'Request Status',
          message: respData.entity.status,
          buttons: {
            confirm: {
              label: 'Ok',
              className: 'btn-default'
            },
          },
          callback: function (result) {
            if (result) {
              this.showLoader = true;
              router.navigateByUrl("/alert");
              let url = "/GetAllAlertData/getAll";
              commonService.getServiceData(url)
              .subscribe(searchData => {
                this.searchData = searchData
                this.requestsList = JSON.parse(searchData.entity.output)
                this.originalData = this.requestsList;
                this.rowCollection = JSON.parse(searchData.entity.output)
                this.gridDataOutput = JSON.parse(searchData.entity.output)
                if (this.requestsList.length == 0) {
                  this.errorMessege = true;
                }
                this.gridDataOutput = JSON.parse(searchData.entity.output)
                if (this.gridDataOutput.length == 0) {
                  this.errorMessege = true;
                }
              });
              
              setTimeout(function () {
                $("#loadTable").load("#loadTable");
                this.showLoader = false;
              },155);
              commonService.closeAddPopUp('addPopUp');
            }
            
          }
          
        });
      }
    })
}

}

closeEditPopUp(editPopUp){
this.commonService.closeAddPopUp(editPopUp);
}

editAlertNotiData( alert_type, alert_category, alert_description, alert_code){
  this.commonService.openAddPopUp('editPopUp')
  this.editAlertNotiType = alert_type;
  this.editAlertNotiCategory = alert_category;
  this.editAlertNotiDesc = alert_description;
  this.editAlertNotiCode = alert_code;
}

updateAlertNotiData(editAlertNotiType, editAlertNotiCategory, editAlertNotiDesc, editAlertNotiCode) {
  var requiredValidation = false;
  var router = this.router,
    commonService = this.commonService;

  $('.updateAlertNoti').each(function (index, item) {
    if ($(item).hasClass("ng-invalid")) {
      $('.ng-invalid').css('border', '1px solid rgb(222, 52, 52)');
      requiredValidation = true;
    }
  });

  if (requiredValidation) {
    this.commonService.alertPopUp("Error", "Please fill all the mandatory fields");
  }

  if (!requiredValidation) {
    var data = {
      //alert_type: editAlertNotiType,
     // alert_category: editAlertNotiCategory,
     description: editAlertNotiDesc,
     alertCode: editAlertNotiCode
    };
    this.url = "/UpdateAlertDBService/update";
            this.commonService.postData(this.url, data)
            .subscribe(respData => {
              this.respData = respData
              if (respData) {
                bootbox.confirm({
                  title: 'Request Status',
                  message: respData.entity.output,
                  buttons: {
                    confirm: {
                      label: 'Ok',
                      className: 'btn-default'
                    },
                  },
                  
            callback: function (result) {
              if (result) {
                this.showLoader = true;
                router.navigateByUrl("/alert");
                let url = "/GetAllAlertData/getAll";
                commonService.getServiceData(url)
                .subscribe(searchData => {
                  this.searchData = searchData
                  this.requestsList = JSON.parse(searchData.entity.output)
                  this.originalData = this.requestsList;
                  this.rowCollection = JSON.parse(searchData.entity.output)
                  this.gridDataOutput = JSON.parse(searchData.entity.output)
                  if (this.requestsList.length == 0) {
                    this.errorMessege = true;
                  }
                  this.gridDataOutput = JSON.parse(searchData.entity.output)
                  if (this.gridDataOutput.length == 0) {
                    this.errorMessege = true;
                  }
                });
                setTimeout(function () {
                  $("#loadTable").load("#loadTable");
                  this.showLoader = false;
                },150);
                commonService.closeAddPopUp('editPopUp');
              }
            }
          });
        }

      })
  }
}
}

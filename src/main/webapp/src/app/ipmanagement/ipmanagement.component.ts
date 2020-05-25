import { Component, OnInit } from '@angular/core';
import { IpmanagementserviceService } from '../services/ipmanagementservice.service';
import { CommonService } from '../common.service';
import { Router } from '@angular/router';
import { Global } from '../global';
declare var bootbox: any;
@Component({
  selector: 'app-ipmanagement',
  templateUrl: './ipmanagement.component.html',
  styleUrls: ['./ipmanagement.component.css'],
  providers: [CommonService, IpmanagementserviceService]
})
export class IpmanagementComponent implements OnInit {
  searchItems = ["Customer", "Site ID", "Service", "IP"];
 
  ipamSite = "";
  ipamCustomerName = "";
  ipammask = "";
  ipamservice = "";
  ipamip = "";
  sortKey = "";
  reverse = false;
  requestsList: Array<any> = [];
  originalData: Array<any> = [];
  rowCollection = '';
  updateSite = "";
  updateIp = "";
  updateMask = "";
  updateService = "";
  updateCustomer = "";
  updateRegion = "";
  url: string = "";
  searchData: any[];
  gridDataOutput: any[];
  errorFlag: boolean;
  editIPmgmtCustomer = "";
  editIPmgmtSite = "";
  editIPmgmtRegion = "";
  editIPmgmtService = "";
  editIPmgmtIPadd = "";
  editIPmgmtMask = "";
  postData;
  respData: any;
  requiredValidationFlag: boolean = false;
  menubar: {};
  errormessage: boolean = false;
  order;
  searchField: string;
  searchInput: string;
  private searchUrl: string;

  constructor(private commonService: CommonService, private global: Global, private ipmanagementServc: IpmanagementserviceService, private router: Router, private globalComp: Global) {
    this.router = router;
    this.menubar = global.menubar;
  }

  ngOnInit() {
    this.searchField = this.searchItems[0];
    this.makeMeActive('Admin');
    this.getallipamData();
  }

  tableScroll(): void {
    $('.pane-vScroll').width($('.pane-hScroll').width() + $('.pane-hScroll').scrollLeft());
  }

  public getallipamData() {
    this.url = "/GetAllIpamData/getAll"
    this.commonService.getServiceData(this.url)
      .subscribe(searchData => {
        this.searchData = searchData
        this.requestsList = JSON.parse(searchData.entity.output)
        this.originalData = this.requestsList;
        this.rowCollection = JSON.parse(searchData.entity.output)
        if (this.requestsList.length == 0) {
          this.errorFlag = true;
        }
        this.gridDataOutput = JSON.parse(searchData.entity.output)
        if (this.gridDataOutput.length == 0) {
          this.errorFlag = true;
        }
      })
  }

  clearSearch = function () {
    this.searchInput = "";
    this.errormessage = false;
  }

  // searchIPAMRecord(query: string) {

  //   var filteredData;
  //   let colArray = [];

  //   /* Table level Search */
  //   filteredData = this.originalData;
  //   if (query !== '') {
  //     filteredData = this.originalData.filter(function (obj) {
  //       for (let i = 0; i < Object.keys(obj).length; i++) {
  //         if (typeof obj[Object.keys(obj)[i]] != 'boolean') {
  //           if (obj[Object.keys(obj)[i]] != null && obj[Object.keys(obj)[i]].toString().toLowerCase().indexOf(query.toLowerCase()) !== -1) {
  //             return obj[Object.keys(obj)[i]].toString().toLowerCase().indexOf(query.toLowerCase()) !== -1;
  //           }
  //         }
  //       }
  //     });
  //   }

  //   if(filteredData.length == 0) {
  //     this.errormessage = true;
  //     this.requestsList = filteredData;
  //   } else {
  //     this.errormessage = false;
  //     this.requestsList = filteredData;
  //   }
  // }

  editIPMgmtData(status, site, region, ip, mask, service, customer) {
    this.commonService.openAddPopUp('editIPPopUp');
    this.editIPmgmtCustomer = customer;
    this.editIPmgmtSite = site;
    this.editIPmgmtRegion = region;
    this.editIPmgmtService = service;
    this.editIPmgmtIPadd = ip;
    this.editIPmgmtMask = mask;
  }

  addNewIP() {
    this.commonService.openAddPopUp('addPopUp');
    this.resetIpMgmtData();
  }

  closeAddPopUp(popupID) {
    this.commonService.closeAddPopUp(popupID);
  }
 
  resetIpMgmtData() {
    $('.ng-invalid').css('border', '');
    this.updateSite = "";
    this.updateRegion = "";
    this.updateIp = "";
    this.updateMask = "";
    this.updateService = "";
    this.updateCustomer = "";
  }

  searchIPAMRecord(ipCustName, ipSite, ipServ, ipIp) {
    var data = {
      site: ipSite,
      customer: ipCustName,
      service: ipServ,
      ip: ipIp
    };
    this.url = "/SearchAllIpamData/search";
    this.commonService.postData(this.url, data)
      .subscribe(searchData => {
        this.searchData = searchData;
        if (searchData != undefined && searchData != null) {
          this.errormessage = false;
          this.requestsList = JSON.parse(searchData.entity.output);
          this.gridDataOutput = JSON.parse(searchData.entity.output);
          if (this.requestsList.length == 0) {
            this.errormessage = true;
          } else {
            this.errormessage = false;
          }
        }

      })
  }

  validateIP(enteredValue) {
    this.commonService.validateIPAddress(enteredValue);
  }

  setOrder(value: string) {
    this.order = this.commonService.setGridOrder(value, this.gridDataOutput)
    this.reverse = this.order.reverse
    this.sortKey = this.order.sortKey
  }

  updateIpamData(editIpMgmt, editIPmgmtCustomer, editIPmgmtSite, editIPmgmtRegion, editIPmgmtService, editIPmgmtIPadd, editIPmgmtMask) {

    var data = {
      ip: editIPmgmtIPadd,
      mask: editIPmgmtMask,
      customer: editIPmgmtCustomer,
      site: editIPmgmtSite
    };
    var requiredValidation = false;
    var router = this.router,
      commonService = this.commonService;

    $('.editip').each(function (index, item) {
      if ($(item).hasClass("ng-invalid")) {
        $('.ng-invalid').css('border', '1px solid rgb(222, 52, 52)');
        $('#' + editIpMgmt).css('border', 'none');
        requiredValidation = true;
      }
    });

    if (requiredValidation) {
      this.commonService.alertPopUp("Error", "Please fill all the mandatory fields");
    }

    if (!requiredValidation) {
      this.url = "/UpdateIpamDBService/update";
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
                  this.url = "/GetAllIpamData/getAll"
                  commonService.getServiceData(this.url)
                    .subscribe(searchData => {
                      this.searchData = searchData
                      this.requestsList = JSON.parse(searchData.entity.output)
                      this.rowCollection = JSON.parse(searchData.entity.output)
                      this.gridDataOutput = JSON.parse(searchData.entity.output)
                      if (this.requestsList.length == 0) {
                        this.errorFlag = true;
                      }
                    })
                  setTimeout(function () {

                    $("#loadTable").load("#loadTable");
                  }, 130);
                  commonService.closeAddPopUp('editIPPopUp');

                }
              }
            });
          }

        })
    }
  }


  addIpamData(newIpMgmt, updateCustomer, updateSite, updateRegion, updateService, updateIp, updateMask) {

    var router = this.router,
      flag = false,
      commonService = this.commonService;
    //false
    var requiredValidation = false;
    $('.addPopUpField').each(function (index, item) {
      if ($(item).hasClass("ng-invalid")) {
        $('.ng-invalid').css('border', '1px solid rgb(222, 52, 52)');
        $('#' + newIpMgmt).css('border', 'none');
        requiredValidation = true;

      }
    });
    if (requiredValidation) {
      this.commonService.alertPopUp("Error", "Please fill all the mandatory fields");
    }

    //true
    if (!requiredValidation) {
      this.url = "/UpdateIpamDBService/add";
      this.postData = {
        site: updateSite.toLowerCase(), 
        region: updateRegion,
        ip: updateIp,
        mask: updateMask,
        service: updateService,
        customer: updateCustomer.toLowerCase()
      }
      this.commonService.postData(this.url, this.postData)
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
                  router.navigateByUrl("/ipmanagement");
                  this.url = "/GetAllIpamData/getAll"
                  commonService.getServiceData(this.url)
                    .subscribe(searchData => {
                      this.searchData = searchData
                      this.requestsList = JSON.parse(searchData.entity.output)
                      this.rowCollection = JSON.parse(searchData.entity.output)
                      if (this.requestsList.length == 0) {
                        this.errorFlag = true;
                      }
                    })
                  commonService.closeAddPopUp('addPopUp');

                }
              }
            });
          }
        })
    }

  }

  makeMeActive = function (key) {
    this.activetab = this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }

  subnetMaskValidation(event) {
    var maskValue = event.target.value;
    var mask = event.target.value;
    var maskId = event.target.id;
    this.commonService.validateSubnetMask(event)
  }


}

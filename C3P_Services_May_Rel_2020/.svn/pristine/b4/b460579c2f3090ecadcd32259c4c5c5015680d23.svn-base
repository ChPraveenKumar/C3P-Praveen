import { Component, OnInit } from '@angular/core';
import { Global } from '../global';
import { CommonService } from '../common.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import * as $ from 'jquery';

@Component({
  selector: 'app-template-admin-mgmt',
  templateUrl: './template-admin-mgmt.component.html',
  styleUrls: ['./template-admin-mgmt.component.css'],
  providers: [CommonService]
})
export class TemplateAdminMgmtComponent implements OnInit {
  showMe: {};
  menubar: {};
  gridData: any[];
  gridDataOutput: any[];
  originalData: any[];
  getConfigurationAdminData: any[];
  order;
  sortclass;
  reverse: boolean = false;
  sortKey: string = '';
  dataArray: any[];
  intData: number;
  errormessage: boolean;
  errorFlag: boolean;
  rowCollection: any[];
  searchData: any[];
  private setUrl: string;
  private searchUrl: string;
  searchItems = ["Template ID", "Device Type", "Vendor", "Model", "OS", "OS Version"];
  searchField: string;
  searchInput: string;
  postData: object;
  getData;
  requestsList;
  constructor(private commonService: CommonService, private global: Global) {
    this.menubar = global.menubar
  }

  ngOnInit() {
    this.searchField = this.searchItems[0];
    this.errormessage = false;
    this.getTemplateList();
    this.makeMeActive('Admin');
    this.errorFlag = false;

  }
  closeReportPopUp = function (popUpId) {
    this.commonService.closeAddPopUp(popUpId);
  }
  makeMeActive = function (key) {
    this.activetab = this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }
  // clearSearch = function() {
  //   this.searchInput="";
  //   this.errormessage = false;
  // }
  templateSearchRequest(searchField: string, searchInput: string): void {
    this.searchUrl = "/SearchTemplateList/search"
    this.postData = { key: this.searchField, value: this.searchInput }
    this.commonService.postData(this.searchUrl, this.postData)
      .subscribe(searchData => {
        this.searchData = searchData
        if (searchData.entity.output && searchData.entity.output.length > 0) {
          this.errormessage = false;
          this.requestsList = JSON.parse(searchData.entity.output);
          this.gridDataOutput = JSON.parse(searchData.entity.output);
          if (this.requestsList.length == 0) {
            this.errormessage = true;
          } else {
            this.errormessage = false;
          }
        } else if (searchInput == "") {
          this.requestsList = this.originalData;
          this.gridDataOutput = this.originalData;
        }

      })
  }

  getTemplateList(): void {
    this.searchUrl = "/GetTemplateConfigurationData/getTemplateList"
    this.commonService.getServiceData(this.searchUrl)
      .subscribe(searchData => {
        this.searchData = searchData
        this.gridDataOutput = JSON.parse(searchData.entity.output);
        this.originalData = this.gridDataOutput;
        if (this.gridDataOutput.length == 0) {
          this.errorFlag = true;
        }
      })
  }

  setOrder(value: string) {
    this.order = this.commonService.setGridOrder(value, this.gridDataOutput)
    this.reverse = this.order.reverse
    this.sortKey = this.order.sortKey
  }

  getTempConfData(event): void {
    this.commonService.openAddPopUp('templateConfPopUp');
    this.searchUrl = "/GetTemplateConfigurationData/getTemplateViewForTemplateVersion"
    this.postData = { templateid: event.target.innerText }
    this.commonService.postData(this.searchUrl, this.postData)
      .subscribe(searchData => {
        this.searchData = searchData
        this.getConfigurationAdminData = JSON.parse(searchData.entity.output)
      })
  }
  
  tableScroll(): void {
    $('.pane-vScroll').width($('.pane-hScroll').width() + $('.pane-hScroll').scrollLeft());
    (<any>$('[data-toggle="tooltip"]')).tooltip();
  }

  expandCollapseParent($event) {
    if ($event.target.className == "ico-expand") {
      // expand
      $($event.target).removeClass("ico-expand");
      $($event.target).addClass("ico-collapse");
      $($event.target.parentElement.parentElement.parentElement.parentElement.children[1]).show();
    } else if ($event.target.className == "ico-collapse") {
      //collapse
      $($event.target).removeClass("ico-collapse");
      $($event.target).addClass("ico-expand");
      $($event.target.parentElement.parentElement.parentElement.parentElement.children[1]).hide();
    }
  }
}

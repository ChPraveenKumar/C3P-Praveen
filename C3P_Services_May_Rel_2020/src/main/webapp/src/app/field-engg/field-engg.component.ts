import { Component, OnInit } from '@angular/core';
import { CommonService } from '../common.service';
import { Router } from '@angular/router';
import { Global } from '../global'

@Component({
  selector: 'app-field-engg',
  templateUrl: './field-engg.component.html',
  styleUrls: ['./field-engg.component.css'],
  providers: [CommonService]
})
export class FieldEnggComponent implements OnInit {
  private menubar: {};
  errormessage: boolean;
  errorFlag: boolean;
  searchField: string;
  SearchItems = ["Select","Request ID", "Status"];
  private searchUrl: string;
  postData: object;
  searchInput: string;
  searchData: any[];
  requestsList: any;
  gridDataOutput: any[];
  url: string = "";
  originalData: Array<any> = [];
  rowCollection = '';
  message: string;
  order;
  reverse: boolean = false;
  sortKey: string = '';
  reqId: string = "";
  version: string = "";

  constructor(private commonService: CommonService, private router: Router, private global: Global) {
    this.menubar = global.menubar;
  }

  ngOnInit() {
    this.searchField = this.SearchItems[0];
    this.errormessage = false;
    this.errorFlag = false;
    this.getallfemData();
    this.makeMeActive('Configuration');
  }

  //fieldDetails(fieldReqId){
  //this.commonService.reqID = fieldReqId;
  //this.commonService.changeMessage(fieldReqId);
  //this.router.navigateByUrl("/field-eng-details");
  //}

  public getallfemData() {
    this.url = "/GetNotifications/get"
    this.commonService.getServiceData(this.url)
      .subscribe(searchData => {
        this.searchData = searchData
        this.requestsList = JSON.parse(searchData.entity.FERequestDetailedList)
        this.originalData = this.requestsList;
        this.gridDataOutput = JSON.parse(searchData.entity.FERequestDetailedList)
        if (this.gridDataOutput.length == 0) {
          this.errorFlag = true;
        }
        this.gridDataOutput = JSON.parse(searchData.entity.FERequestDetailedList)
        if (this.gridDataOutput.length == 0) {
          this.errorFlag = true;
        }
      })

  }
  //searchRequest(searchField: string, searchInput: string, searchVersion: string)
  //this.reqId=searchInput.substring(0,10);
  //this.version=searchInput.substring(12,16);
  //this.postData = { key:searchField, value:this.reqId, version:this.version}
  searchRequest(searchField: string, searchInput: string) {
    this.searchUrl = "/SearchRequestService/search"
    this.postData = { key: searchField, value: searchInput, page: 'fedashboard' }
    this.commonService.postData(this.searchUrl, this.postData)
      .subscribe(searchData => {
        this.searchData = searchData
        if (searchData.entity.output == "") {
          this.errormessage = true;
          this.gridDataOutput = [];
        }

        else if (searchData.entity.output && searchData.entity.output.length > 0) {
          this.errormessage = false;
          this.requestsList = JSON.parse(searchData.entity.output);
          this.gridDataOutput = JSON.parse(searchData.entity.output);
          if (this.requestsList.length == 0) {
            this.errormessage = true;
          } else {
            this.errormessage = false;
          }
        } else if (searchInput == "") {
          this.errormessage = false;
		   this.gridDataOutput = this.originalData;
        }
      })
  }
  //this.requestsList = this.originalData;
  //this.gridDataOutput = this.originalData;
  /*searchRequest(): void {
    this.searchUrl = "/SearchRequestServiceWithVersion/search"
    this.postData = {key:this.searchField, value:this.searchInput, page:'field-engg'}
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(searchData => {this.searchData = searchData
      this.gridDataOutput = JSON.parse(searchData.entity.output)

      if (this.gridDataOutput.length == 0){
        this.errormessage = true;
      }

    })
    
  }*/

  setOrder(value: string) {
    this.order = this.commonService.setGridOrder(value, this.gridDataOutput)
    this.reverse = this.order.reverse
    this.sortKey = this.order.sortKey
  }

  makeMeActive = function (key) {
    this.activetab = this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }



}

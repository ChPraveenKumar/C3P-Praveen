import { Component, OnInit } from '@angular/core';
import { CommonService } from '../common.service';
import { Global } from '../global';
import { CommaExpr } from '@angular/compiler/src/output/output_ast';
import * as $ from 'jquery';
import { Http, Headers, Response } from '@angular/http';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DOCUMENT } from '@angular/platform-browser';
import { Router, ActivatedRoute, Params } from '@angular/router';
declare var comparison: any;
@Component({
  selector: 'app-configcomparison',
  templateUrl: './configcomparison.component.html',
  styleUrls: ['./configcomparison.component.css']
})
export class ConfigcomparisonComponent implements OnInit {
  searchUrl: string = "";
  gridDataOutput: any[];
  searchData: any[];
  errorFlag: boolean;
  order;
  sortclass;
  reverse: boolean = false;
  sortKey: string = '';
  searchItems = ["Service Request", "Status"];
  searchField: string = "";
  searchInput: string = "";
  postData: object;
  errormessage: boolean;
  requestsList;
  menubar: {};
  showMe: boolean = false;
  showMeChild: boolean = false;
  originalData: Array<any> = [];
  txt: string;
  newTxt: string;
  url: string;
  data: any;
  fileData: any;

  constructor(private commonService: CommonService, private global: Global, private router: Router) {
    this.menubar = global.menubar;
    this.router = router;
  }

  ngOnInit() {
    //this.colorCoding();
    this.searchField = this.searchItems[0];
    this.makeMeActive('Configuration');
    //this.getConfigList();
  }

  makeMeActive = function (key) {
    this.activetab = this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }

  expandCollapseParent($event) {
    if ($event.target.className == "ico-expand") {
      // expand
      $($event.target).removeClass("ico-expand");
      $($event.target).addClass("ico-collapse");
      $($event.target.parentElement.parentElement.children[2]).show();
      $($event.target.parentElement.parentElement.children[3]).show();
    } else if ($event.target.className == "ico-collapse") {
      //collapse
      $($event.target).removeClass("ico-collapse");
      $($event.target).addClass("ico-expand");
      $($event.target.parentElement.parentElement.children[2]).hide();
      $($event.target.parentElement.parentElement.children[3]).hide();
      $($event.target.parentElement.parentElement.children[4]).hide();
    }
  }

  summaryExpandCollapse($event) {
    if ($event.target.className == "ico-expand-summarychild") {
      // expand
      $($event.target).removeClass("ico-expand-summarychild");
      $($event.target).addClass("ico-collapse-summarychild");
      $($event.target.parentElement.parentElement.parentElement.children[1]).show();
    } else if ($event.target.className == "ico-collapse-summarychild") {
      //collapse
      $($event.target).removeClass("ico-collapse-summarychild");
      $($event.target).addClass("ico-expand-summarychild");
      $($event.target.parentElement.parentElement.parentElement.children[1]).hide();
    }
  }

  detailsExpandCollapse($event) {
    if ($event.target.className == "ico-expand-detailchild") {
      // expand
      $($event.target).removeClass("ico-expand-detailchild");
      $($event.target).addClass("ico-collapse-detailchild");
      $($event.target.parentElement.parentElement.parentElement.parentElement.children[4]).show();
    } else if ($event.target.className == "ico-collapse-detailchild") {
      //collapse
      $($event.target).removeClass("ico-collapse-detailchild");
      $($event.target).addClass("ico-expand-detailchild");
      $($event.target.parentElement.parentElement.parentElement.parentElement.children[4]).hide();
    }
  }

  getConfigList(): void {
    this.searchUrl = "/diffService/getDashboardData"
    this.commonService.getServiceData(this.searchUrl)
      .subscribe(searchData => {
        this.searchData = searchData
        this.gridDataOutput = JSON.parse(searchData.entity.file1)
        this.originalData = this.gridDataOutput;
        if (this.gridDataOutput.length == 0) {
          this.errorFlag = true;
        }
      })
  }

  setOrder(value: string) {
    this.order = this.commonService.setGridOrder(value, this.gridDataOutput);
    this.reverse = this.order.reverse;
    this.sortKey = this.order.sortKey;
  }

  configCompareSearchRequest(searchField: string, searchInput: string): void {
    this.searchUrl = "/diffService/search"
    this.postData = { key: searchField, value: searchInput }
    this.commonService.postData(this.searchUrl, this.postData)
      .subscribe(searchData => {
        this.searchData = searchData
        if (searchData.entity.output == "") {
          this.errormessage = true;
          this.gridDataOutput = [];
        } else {
          this.gridDataOutput = JSON.parse(searchData.entity.output)
        }
      })
  }

  clearSearch = function () {
    this.searchInput = "";
    this.errormessage = false;
  }

  reqid: string;
  comb: string;
  typ: string;
  openDetailsPopUp(requestId: string, combination: string, type: string) {
    console.log("Request ID:: ", requestId);
    console.log("Key:: ", combination);
    console.log("File clicked:: ", type);
    this.reqid = requestId;
    this.comb = combination;
    this.typ = type;
    // this.commonService.openAddPopUp('detailsPopUp');
    this.commonService.openAddPopUp('detailsPopUp');

    //this.router.navigateByUrl("/displayComparison");
  }
  scrolltop: any;
  scrollheight: any;
  windowheight: any;
  scrolloffset: any;
  scrollalert() {
    //let para = document.getElementById('summaryData');
    //let compStyles = window.getComputedStyle(para);
    this.scrolltop = $('#summaryData').css('scrollTop');
    this.scrollheight = $('#summaryData').attr('scrollHeight');
    this.windowheight = $('#summaryData').attr('clientHeight');
    this.scrolloffset = 20;
    //if (this.scrolltop >= (this.scrollheight - (this.windowheight + this.scrolloffset))) {
      //fetch new items  
      $('#loadText').text('Loading more items...');  
      //this.getIterations(this.reqid, this.comb, this.typ);
      // $.get('#summaryData', '', function(newitems){  
      //   this.newit = newitems;
      //    // $('#summaryData').append(newitems);  
      //     //updatestatus();  
      // });  
   // }
    setTimeout(function () {

      if (this.scrolltop >= (this.scrollheight - (this.windowheight + this.scrolloffset))) {
        this.getIterations(this.reqid, this.comb, this.typ);
        this.updatestatus();
      }
    }, 1500);
    //setTimeout('this.scrollalert();', 1500);  
  }

  updatestatus(){  
    //Show number of loaded items  
    //var totalItems=$('#content p').length;  
    $('#status').text('Loaded Items');  
}

  openSummaryPopUp(requestId: string, combination: string, type: string) {
    console.log("Request ID:: ", requestId);
    console.log("Key:: ", combination);
    console.log("File clicked:: ", type);
    this.scrollalert();
    this.commonService.openAddPopUp('summaryPopUp');
    this.getIterations(requestId, combination, type);
    //this.commonService.getIterations(requestId, combination, type);
    //this.router.navigateByUrl("/displayComparison");

  }

  urlvariable: any;
  ifLoader: boolean = true;
  uiData: any;
  loader: boolean = false;
  getFileComparisons(req_id, fileName) {
    //document.getElementById('loading').style.visibility = 'visible';
    this.ifLoader = false;
    var html = '';
    var respData, data, jsonResponse, Text;
    //alert("inside getComparison() function");
    var requestParams = {
      "requestId": "abc",
      "fileName": fileName,
      "sectionName": "abc"
    }
    let _self = this;
    this.urlvariable = "text";
    var url = "http://localhost:8024/getSectionContent/content";
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
      if (xhttp.readyState == XMLHttpRequest.DONE) {
        //alert(JSON.parse(xhttp.responseText.entity.output));
        data = xhttp.responseText;
        jsonResponse = JSON.parse(data);
        console.log(jsonResponse["entity"]);
        respData = jsonResponse["entity"];
        console.log("Response Data ::", respData);
        html += '<div>' + respData.output + '</div>';
        console.log("HTML :: ", html);
        document.getElementById("summaryData").innerHTML += respData.section_name + html;
        document.getElementById("accordion").innerHTML += respData.section_name + html;
        // $('#summaryData').append(newitems); 
      }
    }
    xhttp.open("POST", url);
    xhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhttp.send(JSON.stringify(requestParams));

    //xhttp.responseText = "Testing";
  }

  getIterations(requestId, combination, type) {
    var respData, data, jsonResponse, Text, outArray;

    var requestParams = {
      "requestID": "D9C61BA2-B",
      "combination": combination,
      "type": type
    }
    var url = "http://localhost:8024/getSectionList/get";
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", url);
    xhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhttp.send(JSON.stringify(requestParams));
    let _self = this;
    xhttp.onreadystatechange = function () {
      if (xhttp.readyState == XMLHttpRequest.DONE) {
        //alert(JSON.parse(xhttp.responseText.entity.output));
        data = xhttp.responseText;
        jsonResponse = JSON.parse(data);
        respData = jsonResponse["entity"];
        console.log(jsonResponse);
        outArray = respData.output;
        for (var i = 0; i < outArray.length; i++) {
          console.log("calling getComparison()");
          _self.getFileComparisons("abc", outArray[i]);
          // _self.loader = true;
        }
      }
    }
  }
  closesummaryPopUp() {
    this.commonService.closeAddPopUp('summaryPopUp');
  }

  closedetailsPopUp(detailsPopUp) {
    this.commonService.closeAddPopUp('detailsPopUp');
  }

  //Search through Front end (getting issue while column specific sorting; hence haven't used it)
  searchConfigRecord(query: string) {
    var filteredData;
    let colArray = [];

    /* Table level Search */
    filteredData = this.originalData;
    if (query !== '') {
      filteredData = this.originalData.filter(function (obj) {
        for (let i = 0; i < Object.keys(obj).length; i++) {
          if (typeof obj[Object.keys(obj)[i]] != 'boolean') {
            if (obj[Object.keys(obj)[i]] != null && obj[Object.keys(obj)[i]].toString().toLowerCase().indexOf(query.toLowerCase()) !== -1) {
              return obj[Object.keys(obj)[i]].toString().toLowerCase().indexOf(query.toLowerCase()) !== -1;
            }
          }
        }
      });
    }

    if (filteredData.length == 0) {
      this.errormessage = true;
      this.gridDataOutput = filteredData;
    } else {
      this.errormessage = false;
      this.gridDataOutput = filteredData;
    }
  }

  colorCoding() {
    var re = /(?:^|[ ])~([a-zA-Z]+)/gm;
    this.txt = "Last configuration change at ~21:08:18 UTC ~Wed ~Mar ~7 2018 ~by ~action_tool";

    //var str = 'Hey I love #apple and #orange and #grapes!@ also #banana';
    var m;

    while ((m = re.exec(this.txt)) != null) {
      if (m.index === re.lastIndex) {
        re.lastIndex++;
      }
      // View your result using the m-variable.
      // eg m[0] etc.
      console.log("0th :: ", m[0]);
      $($(m[0]).css('color', 'red'));
      this.newTxt = this.txt;
    }
  }
}

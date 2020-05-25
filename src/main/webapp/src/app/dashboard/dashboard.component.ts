import { Component, OnInit, Input } from '@angular/core';
import { Global } from'../global';
import { DashBoardService } from '../dash-board.service';
import { CommonService } from '../common.service';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  providers: [DashBoardService, CommonService]
})
export class DashboardComponent implements OnInit {
  menubar: {};
  gridData: any[];
  gridDataOutput : any[];
  order;
  sortclass;
  reverse: boolean = false;
  sortKey: string = '';
  dataArray: any[];
  intData: number;
  errormessage :boolean;
  rowCollection: any[];
  successRequests: number;
  failureRequests: number;
  TotalRequests: number;
  MaxElapsedTime: string = '';
  MinElapsedTime: string = '';
  AvgElapsedTime: string = '';
  searchData: any[];
  private setUrl: string;
  private searchUrl: string;
  SearchItems = [ "Request ID", "Region",
            "Vendor", "Model", "Status" ];
  searchField: string;
  searchInput: string;
  postData:object;
  getData;
  constructor(private dashboardService: DashBoardService, private commonService: CommonService, 
    private global: Global) {
    this.menubar = global.menubar
  }

  ngOnInit() {
    this.getDashboardData();
    this.errormessage = true;
    this.searchField = this.SearchItems[0];
    this.makeMeActive('Dashboard');
  }
  
  searchRequest(): void {
    this.searchUrl = "/SearchRequestService/search"
    this.postData = {key:this.searchField, value:this.searchInput, page:'dashboard'}
    this.commonService.postData(this.searchUrl, this.postData)
    .subscribe(searchData => {this.searchData = searchData
      this.gridDataOutput = JSON.parse(searchData.entity.output)

      if (this.gridDataOutput.length == 0){
        this.errormessage = true;
      }

    })
    
  }

  getDashboardData(): void {
    this.setUrl = "/GetAllRequestDashboardViewService/GetAllDashboardViewJSON";
    this.getData = ""
    this.commonService.getData(this.setUrl, this.getData) 
      .subscribe(gridData => {this.gridData = gridData
        this.gridDataOutput = JSON.parse(gridData.entity.output)
        this.rowCollection = JSON.parse(gridData.entity.output);
        this.successRequests = JSON.parse(gridData.entity.SuccessfulRequests);
        this.failureRequests = JSON.parse(gridData.entity.FailureRequests);
        this.TotalRequests = JSON.parse(gridData.entity.TotalRequests);
        this.MaxElapsedTime=gridData.entity.MaxElapsedTime;
        this.MinElapsedTime=gridData.entity.MinElapsedTime;
        this.AvgElapsedTime=gridData.entity.AvgElapsedTime;

        if (this.gridDataOutput.length > 0){
          this.clearSearch();
        }
      });
  }

  clearSearch = function() {
    this.searchInput="";
    this.errormessage = false;
  }

 getUpdatedGridData = function(){  
  this.gridDataOutput = [];
    this.getDashboardData();
  };

  setOrder(value: string) {
    this.order =   this.commonService.setGridOrder(value, this.gridDataOutput)
    this.reverse = this.order.reverse
    this.sortKey =this.order.sortKey
  }

  makeMeActive = function(key){
    this.activetab =this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }
 	

}



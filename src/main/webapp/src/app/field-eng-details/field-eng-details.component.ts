import { Component, OnInit } from '@angular/core';
import { CommonService } from '../common.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Subscription } from 'rxjs/subscription';
//import { saveAs } from 'file-saver/FileSaver';
//import { Http, Headers } from '@angular/http';
//import 'rxjs/add/operator/toPromise';

declare var bootbox: any;
@Component({
  selector: 'app-field-eng-details',
  templateUrl: './field-eng-details.component.html',
  styleUrls: ['./field-eng-details.component.css'],
  providers: [CommonService]
})
export class FieldEngDetailsComponent implements OnInit {

  url: string;
  postData: object;
  popUpdata: any[];
  searchData: any[];
  requestsList: any;
  feDashboardList: any;
  originalData: Array<any> = [];
  message: string;
  requestId: string;
  Version: string;
  readFlag: string;
  proceedData: any;
  route: any;
  HoldData: any;
  display_request_id: string = "";
  Status: string;
  ProceedreqId: string;
  ProceedreqV: string;
  HoldreqId: string;
  HoldreqV: string;
  Hstatus: boolean = false;
  Pstatus: boolean = true;
  DownloadData: string;
  DownoadRequetid: string;
  DownoadVersion: string;
  DownoadStatus: string;
  downloadData: any;
  downloadUrl: string;
  private _subscription: Subscription;
  constructor(private commonService: CommonService, private router: Router,
    private activatedRoute: ActivatedRoute ) { this.route = router; }
    //private http: Http
  ngOnInit() {
    this.getRouteParam();
    // this._subscription = this.commonService.currentMessage.subscribe(message => {
    //   this.getmsg(message)});
    //this.requestId = this.commonService.reqID
    this.getaBasicConfiguration(this.display_request_id, this.Version, this.readFlag = "1");
  }

  getRouteParam() {
    this._subscription = this.activatedRoute.queryParams.subscribe((params: Params) => {
      this.display_request_id = params['dashboardReq_Details'];
      this.Version = params['request_version'];
      this.DownoadStatus = params['request_status'];

      this.ProceedreqId = params['dashboardReq_Details'];
      this.ProceedreqV = params['request_version'];
      this.HoldreqId = params['dashboardReq_Details'];
      this.HoldreqV = params['request_version'];
      // (+) converts string 'id' to a number
      //this.requestId = request.display_request_id; 
      //this.version = request.request_version;
    });
  }

  // getmsg(msg: string) {
  //   this.message = msg;
  // }

  public getaBasicConfiguration(requestId, version, readFlag) {
    this.url = "/configuration/getBasicConfiguration"
    this.postData = { requestId, version, readFlag }
    this.commonService.postData(this.url, this.postData)
      .subscribe(searchData => {
        this.searchData = searchData
        this.feDashboardList = JSON.parse(searchData.entity.output)
        //this.originalData = this.feDashboardList;
      }
      )
  }



  clickProceedButton() {
    this.url = "/configuration/responsefromfe";
    this.postData = { requestId: this.ProceedreqId, version: this.ProceedreqV, status: this.Pstatus }
    this.commonService.postData(this.url, this.postData)
      .subscribe(proceedData => {
        if (proceedData = true) {
          this.proceedData = proceedData
          this.router.navigateByUrl("/field-engg");
          //this.feDashboardList = JSON.parse(proceedData.entity.response)
          //this.originalData = this.feDashboardList;
        }
      })
  }

  clickHoldButton() {
    this.url = "/configuration/responsefromfe";
    this.postData = { requestId: this.HoldreqId, version: this.HoldreqV, status: this.Hstatus }
    this.commonService.postData(this.url, this.postData)
      .subscribe(HoldData => {
        if(HoldData = true ){
          this.router.navigateByUrl("/field-engg");
        }
          //if (HoldData != undefined) {
          //this.HoldData = HoldData
          //this.requestsList = JSON.parse(HoldData.entity.response)
      })
    //setTimeout(function () {
     //$("#tbl_dashboard").load("#tbl_dashboard");
    //},10);
  }

     /*Download() {
       this.url = "/GetBasicConfigurationFile/getIDandVersion";
       this.postData = { requestId: this.ProceedreqId, version: this.ProceedreqV }
       this.commonService.postData(this.url, this.postData)
         .subscribe(downloadData => {
           this.downloadData = downloadData;
           this.downloadUrl = downloadData.entity;
          
           const headers = new Headers();
           headers.append('Accept', 'text/plain');
           //this.commonService.getServiceData(this.downloadUrl, { headers: headers })
           this.http.get(this.downloadUrl, { headers: headers})
           .subscribe(response => this.saveToFileSystem(response));

         });
         
    }
   
   saveToFileSystem(response) {
     //response.set('Access-Control-Expose-Headers', 'Content-Disposition')
     const contentDispositionHeader: string = response.headers.get('Content-Disposition');
     const filename = contentDispositionHeader[0]
     //const parts: string[] = contentDispositionHeader.split(';');
     //const filename = parts[1].split('=')[1];
     const blob = new Blob([response._body], { type: 'text/plain' }, );
     saveAs(blob, filename)
  }*/
}

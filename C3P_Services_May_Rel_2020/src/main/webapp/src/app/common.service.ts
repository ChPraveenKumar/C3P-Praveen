import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';
import * as $ from 'jquery';
import { Global } from './global';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class CommonService {
  key: string = '';
  order: string = '';
  private reverseOrder: boolean = false;
  sortKey: string = '';
  sortclass; 
  alertPopUp;
  dataArray: any[];
  requiredValidationFlag: boolean = false;
  intData: number;
  private getUrl: string;
  private baseUrl = "http://localhost:8080/C3P";
  private Data;
  private data;
  private menubar: {};
  validator = require('is-my-ip-valid')
  validate = this.validator()
  validate4 = this.validator({ version: 4 })

  constructor(private global: Global, private http: HttpClient) {
    this.alertPopUp = global.alertPopUp
  }


  /** GET heroes from the server */
  getData(url, data): Observable<any> {
    this.getUrl = this.baseUrl + url
    this.Data = data
    this.data = JSON.stringify(this.Data)
    return this.http.get(this.getUrl)
      .map(this.extractData)
      .pipe(
      //tap(heroes => this.log(`fetched heroes`)),
      catchError(this.handleError('getGridData', []))
      );
  }

  /** GET heroes from the server */
  getServiceData(url): Observable<any> {
    this.getUrl = this.baseUrl + url
    return this.http.get(this.getUrl)
      .map(this.extractData)
      .pipe(
      //tap(heroes => this.log(`fetched heroes`)),
      catchError(this.handleError('getGridData', []))
      );
  }

  getDeatiledSummary(requestId, summarykey, summaryfileName, url) {
    this.getUrl = this.baseUrl + url;
    let inputJson = {
      "requestid": requestId,
      "summary": 1,
      "detailedcomparison": -1
    }
    return this.http.post(this.getUrl, inputJson, httpOptions).map(this.extractData).pipe(catchError(this.handleError('getGridData', []))
    );
  }



  postData(url, data): Observable<any> {
    this.Data = data
    this.data = JSON.stringify(this.Data)
    this.getUrl = this.baseUrl + url
    return this.http.post(this.getUrl, this.data, httpOptions)
      .map(this.extractData)
      .pipe(
      //tap(heroes => this.log(`fetched heroes`)),
      catchError(this.handleError('searchRequest', []))
      );
  }

  private extractData(res: Response) {
    let body = res

    return body || {};
  }
 
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      //this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  setGridOrder(value: string, gridDataOutput) {

    this.order = value;
    if (this.order === value) {
      this.dataArray = gridDataOutput;
      for (let data in this.dataArray) {
        this.intData = +data
        if (this.intData < this.dataArray.length - 1) {
          if (this.dataArray[this.intData][value] !== this.dataArray[this.intData + 1][value]) {
            $('#' + value).toggleClass('sort-ascent').toggleClass('sort-descent');
            this.reverseOrder = !this.reverseOrder;
            this.sortKey = value;
            break
          }
        }
      }

    }
    return {
      reverse: this.reverseOrder,
      sortKey: this.sortKey
    }
  }


  activeTabs(key, menubar) {

    Object.keys(menubar).forEach(function (k) {
      if (k == key)
        menubar[k] = true;
      else
        menubar[k] = false;
    });

    return menubar;
  }

  isLoggedIn(): boolean {
    return false;
  }

  openAddPopUp(popUpId) {
    $("#" + popUpId).css("display", "block");
  }

  closeAddPopUp(popUpId) {
    $("#" + popUpId).css("display", "none");
  }

  requiredValidation(formId): boolean {
    var requiredValidation = false;
    $('input, select').each(function (index, item) {
      if ($(item).hasClass("ng-invalid")) {
        $('.ng-invalid').css('border', '1px solid rgb(222, 52, 52)');
        $('#' + formId).css('border', 'none');
        requiredValidation = true;

      }
    });
    if (requiredValidation) {
      this.alertPopUp("Error", "Please fill all the mandatory fields");
    }
    return this.requiredValidationFlag = requiredValidation;
  }

  validateIPAddress(ip: any) {
    var ipFlag = this.validate4(ip);
    if (ip == "") {
      $(".ipInput").css("background-color", "white");
    } else {
      if (ipFlag == false) {
        $(".ipInput").css("background-color", "pink");
      } else {
        $(".ipInput").css("background-color", "white");
      }
    }
  }

  validateSubnetMask(event: any) {
    var maskValue = event.target.value;
    var mask = event.target.value;
    var maskId = event.target.id;
    maskValidation(maskValue);
    function maskValidation(maskValue) {
      switch (maskValue) {
        case "0.0.0.0":
          maskValue = true;
          break;
        case "128.0.0.0":
          maskValue = true;
          break;
        case "192.0.0.0":
          maskValue = true;
          break;
        case "224.0.0.0":
          maskValue = true;
          break;
        case "240.0.0.0":
          maskValue = true;
          break;
        case "248.0.0.0":
          maskValue = true;
          break;
        case "252.0.0.0":
          maskValue = true;
          break;
        case "254.0.0.0":
          maskValue = true;
          break;
        case "255.0.0.0":
          maskValue = true;
          break;
        case "255.128.0.0":
          maskValue = true;
          break;
        case "255.192.0.0":
          maskValue = true;
          break;
        case "255.224.0.0":
          maskValue = true;
          break;
        case "255.240.0.0":
          maskValue = true;
          break;
        case "255.248.0.0":
          maskValue = true;
          break;
        case "255.252.0.0":
          maskValue = true;
          break;
        case "255.254.0.0":
          maskValue = true;
          break;
        case "255.255.0.0":
          maskValue = true;
          break;
        case "255.255.128.0":
          maskValue = true;
        case "255.255.192.0":
          maskValue = true;
          break;
        case "255.255.224.0":
          maskValue = true;
          break;
        case "255.255.240.0":
          maskValue = true;
          break;
        case "255.255.248.0":
          maskValue = true;
          break;
        case "255.255.252.0":
          maskValue = true;
          break;
        case "255.255.254.0":
          maskValue = true;
          break;
        case "255.255.255.0":
          maskValue = true;
          break;
        case "255.255.255.128":
          maskValue = true;
          break;
        case "255.255.255.192":
          maskValue = true;
          break;
        case "255.255.255.224":
          maskValue = true;
          break;
        case "255.255.255.240":
          maskValue = true;
          break;
        case "255.255.255.248":
          maskValue = true;
          break;
        case "255.255.255.252":
          maskValue = true;
          break;
        case "255.255.255.254":
          maskValue = true;
          break;
        case "255.255.255.255":
          maskValue = true;
          break;
        default:
          maskValue = false;
      }
      if (mask == "") {
        $("#" + maskId).css("background-color", "white");
      } else {
        if (maskValue == true) {
          $("#" + maskId).removeClass("ng-invalid");
          $("#" + maskId).addClass("ng-valid");
          $("#" + maskId).css("background-color", "white");
          $("#" + maskId).css("border", "black");
        } else {
          console.log("print :: ", ("#" + maskId));
          $("#" + maskId).removeClass("ng-valid");
          $("#" + maskId).addClass("ng-invalid");
          $("#" + maskId).css("border", "red");
          $("#" + maskId).css("background-color", "#FFCCCC");
        }
      }
    }
  }

  deleteData(delete_url, ddName): Observable<any> {
    this.getUrl = this.baseUrl + delete_url;
    return this.http.delete(this.getUrl)
      .map((res) => {
        console.log(res);
        return res;
      }); 
  }

  

}

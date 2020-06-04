import { Component, OnInit ,NgZone } from '@angular/core';
import { CommonService } from '../common.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';


@Component({
  selector: 'app-global-list-mgmt',
  templateUrl: './global-list-mgmt.component.html',
  styleUrls: ['./global-list-mgmt.component.css'],
  providers: [CommonService]
})

export class GlobalListMgmtComponent implements OnInit {
  private setUrl: string;
  searchInput: string;
  postData:object;
  searchData: any[];
  getData;
  attributeList: any[];
  avoidSpaceMthd : any[];
  searchUrl :string;
  leftInterfaceList : any[];
  rightInterfaceList : any[];
  getRequiredAtbList: any[];
  getAvailabeAtrList: any[];
  getReqattributeList: any[];
  getAllReqattributeList: any[];
  maxLength : number;
  attributeName : string;
  selAttributeName : string;
  url : string;
  VendoravailabeAtrList : any[];
  newAttributeName : string;
  selectedattributeList : any[];
  getAvailabeAtrListVendor : any[];
  getAvailabeAtrListDeviceType : any[];
  getAvailabeAtrListModel : any[];
  getAvailabeAtrListOS : any[];
  vendorName : string;
  deviceTypeName : string;
  getAvailabeAtrListValue : any[];
  availabeAtrList_embedded : any[];
  newAttributeNameValue : String;
  
  constructor(private commonService: CommonService,private ngZone: NgZone) { }

  ngOnInit() {
    this.getAvailabeAtrListVendor = ['deviceType1','deviceType2','deviceType3'];
    this.getAvailabeAtrListDeviceType =  ['deviceType1','deviceType2','deviceType3'];
    this.getAvailabeAtrListModel = [];
    this.getAvailabeAtrListOS = [];
    this.getAttributeList();
    this.newAttributeName = '';
    this.rightInterfaceList = [];
    this.leftInterfaceList =['Ethernet','FastEthernet','GigabitEthernet',
      'TenGigabitEthernet','Serial','ATM','POS'];
    this.getAllReqattributeList =[  
      {  
          "id":'Vendor',
          "name" : 'Vendor',
          "maxLength" : 50,
          "getAvailabeAtrList" : "getAvailabeAtrListVendor",
          "requiredAtbList": [],
          "saveUrl":""
      },
      {  
        "id":'Devicetype',
        "name" :"Devicetype",
        "getAvailabeAtrList" : "getAvailabeAtrListVendor",
        "maxLength" : 50,
        "requiredAtbList":  [{"name":'Vendor','attribute':'Vendor','getAvailabeAtrList' :  this.getAvailabeAtrListVendor}],
      },
      {  
        "id":'Model',
        "name" :"Model",
        "maxLength" : 50,
        "method" : "AvoidSpace",
        "requiredAtbList":  [{ "name":'Vendor','attribute':'Vendor','getAvailabeAtrList' :this.getAvailabeAtrListVendor}, { "name":'Device Type','attribute':'Device Type','getAvailabeAtrList' :  this.getAvailabeAtrListDeviceType},{ "name":'OS' ,'attribute':'OS','getAvailabeAtrList' : this.getAvailabeAtrListOS}],
      },
      {  
        "id":'OS',
        "name" :"OS",
        "maxLength" : 24,
        "requiredAtbList":  [{ "name":'Vendor','attribute':'Vendor','getAvailabeAtrList' :this.getAvailabeAtrListVendor}, { "name":'Device Type','attribute':'Device Type','getAvailabeAtrList' : this.getAvailabeAtrListDeviceType}],
      },
      {  
        "id":'OSVersion',
        "name" :"OS Version",
        "method" : "AvoidSpace",
        "maxLength" : 24,
        "requiredAtbList":  [{ "name":'Model','attribute':'Model','getAvailabeAtrList' : this.getAvailabeAtrListModel}, { "name":'OS' ,'attribute':'OS','getAvailabeAtrList' : this.getAvailabeAtrListOS}],
      }
  ];
  }

  getAttributeList(): void {
    this.setUrl = "http://localhost:8080/C3P/globallistdata";
    /*this.commonService.getmicroServiceData(this.setUrl)
      .subscribe(globallistdata => {
      //this.attributeList = globallistdata._embedded.globallistdata;
      this.attributeList = ['Vendor','Devicetype','Model'];
      console.log(globallistdata);
      });*/
  }
 

  onAttributeChange(): void {
    this.newAttributeName = '';
    this.newAttributeNameValue ='';
    this.getRequiredAtbList = [];
    this.attributeList.forEach(item => { 
    if (this.attributeName == item.globallist){
        this.url = item.url;
    }
    });
    this.setUrl = "http://localhost:8080/C3P" + this.url;
    /*this.commonService.getmicroServiceData(this.setUrl)
      .subscribe(selectedDataList => {
        if(this.attributeName != ''){
          var data = this.url.slice( 1 );
          this.selectedattributeList = selectedDataList._embedded[data];
         }
      });*/
    }

    transferFromLeftToRight(event) : void{
       var elementIndexValue = document.getElementById("leftList") as HTMLSelectElement;
      var elementIndex  = elementIndexValue.selectedIndex;
      var removed_element = this.leftInterfaceList.splice(elementIndex, 1);
      this.rightInterfaceList.push(removed_element);
      
    }

    transferFromtRightToLeft(event) : void{
        var elementIndexValue = document.getElementById("rightList") as HTMLSelectElement;
        var elementIndex  = elementIndexValue.selectedIndex;
        var removed_element = this.rightInterfaceList.splice(elementIndex, 1);
        this.leftInterfaceList.push(removed_element);
    }    
    
    getAvailableAtrList(attributeName,attributeValue,getAvailabeAtrListValue): void {
       /* this.getAllReqattributeList.forEach(item => { 
        if (attributeName == item.name){
          this.ngZone.run(() => {
            this.getAvailabeAtrListDeviceType = ['deviceTypeNew1','deviceTypeNew2','deviceTypeNew3']
          });
          
        }
      });*/
      }
   
    AvoidSpace(event): boolean {
      var k = event ? event.which : event.keyCode;
        if (k == 32) return false;
    }
    onAttributeAdd(): void {
      //this.getAvailabeAtrList =  ['Select'];
      this.newAttributeName =  this.attributeName;
          this.getAllReqattributeList.forEach(item => { 
          if (this.attributeName == item.name){
              this.getRequiredAtbList = item.requiredAtbList;
              this.maxLength = item.maxLength;
              this.avoidSpaceMthd = item.method;
              this.getAvailabeAtrList = item.getAvailabeAtrList; 
            
              //this.getAvailabeAtrList = ['vendor1','vendor2','vendor3'];
          }
        });
       }
      onAttributeSave(): void {
        this.getAvailabeAtrListVendor = ['deviceType','deviceType','deviceType'];
        this.newAttributeNameValue;
        this.searchUrl = this.url;
        this.postData = {key:this.newAttributeNameValue}
        //this.commonService.postServiceData(this.searchUrl, this.postData)
        //.subscribe(searchData => {this.searchData = searchData
        // console.log('In save');
         //   })
      }
}

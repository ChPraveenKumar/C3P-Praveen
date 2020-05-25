import { Component, OnInit } from '@angular/core';
import { CommonService } from '../common.service';
import { Global } from '../global';

@Component({
  selector: 'app-globallist',
  templateUrl: './globallist.component.html', 
  styleUrls: ['./globallist.component.css']
})
export class GloballistComponent implements OnInit { 
  menubar: {};
  vendorDropdownList = [];
  selectedVendorList = [];
  vendorDropdownSettings = {};  
  deviceDropdownList = []; 
  selectedDeviceList = [];
  deviceDropdownSettings = {};

  vendorDropDown: Array<any> = [];
  deviceTypeDropDown: Array<any> = [];
  setUrl: string = "";
  newdata = {};
  ifAddVendorSucess: boolean = false;
  ifAddVendorFails: boolean = false;
  newVendor: string = "";
  newDeviceType: string = "";
  newObj = {};
  multiselectVendor = {};
  updatedDTJson = {};
  vendors = [];
  checkedvendorname = [];
  sendDT = {};
  checkvendor: any;
  dtarray = [];
  finalJson = {};
  addedDeviceMsg = "";
  addedDeviceMsgSuccess = false;
  addedDeviceMsgFailure = false;
  noVendorMsg: boolean = false;
  dispNoVendorMsg: string = "";
  noModelMsg: boolean = false;
  dispNoModelMsg: string = "";
  addedVendorMsg: string = "";
  addedDeviceMsgFail: string = "";
  regionDropDown: Array<any> = [];
  serviceDropDown: Array<any> = [];

  ifAddRegionSucess: boolean = false;
  ifAddRegionFails: boolean = false;
  addedRegionMsg: string = "";
  newRegion: string = "";
  regiondata = {};

  ifAddServiceSucess: boolean = false;
  ifAddServiceFails: boolean = false;
  addedServiceMsg: string = "";
  newService: string = "";
  servicedata = {};
  addedDeviceFail: boolean = false;
  osDropDown: Array<any> = [];
  noOSassociated: boolean = false;
  noOSassociatedMsg: string = "";
  ifAddOSSucess: boolean = false;
  ifAddOSFails: boolean = false;
  addedOSMsg: string = "";
  addedOSMsgFail: string = "";
  newOS: string = "";
  osData = {};
  singleVendorDropdownList: Array<any> = [];
  singleVendorDropdownSettings = {};
  singleselectVendor = {};
  singleSelectedVendor: Array<any> = [];
  formatSingleSelectVendor = {};
  ifBlankOS: boolean = false;


  modelDropDown: Array<any> = [];
  osVersionDropDown: Array<any> = [];
  singleSelectedDvcTypSettings = {};
  singleDevTypDropdownList: Array<any> = [];
  singleSelectedDvcTyp = {};
  singleselectDT = {};
  newModel: string = "";
  ifAddModelSucess: boolean = false;
  ifAddModelFails: boolean = false;
  addedModelMsg: string = "";
  addedModelMsgFail: string = "";
  singleSelectedDT: Array<any> = [];
  ifBlankModel: boolean = false;
  modelData = {};

  newOSVersion: string = "";
  ifAddOSVerSucess: boolean = false;
  ifAddOSVerFails: boolean = false;
  singleSelectedOSSettings = {};
  singleselectOS = {};
  singleOSDropdownList: Array<any> = [];
  multiModelDropdownList: Array<any> = [];
  singleSelectedOS: Array<any> = [];
  multiSelectedModel: Array<any> = [];
  selectedModelList = [];
  multiSelectedModelSettings = {};
  multiselectModel = {};
  osWrapper = {};
  ifBlankOSVer: boolean = false;

  singleVendorDropdownListForModel: Array<any> = [];
  singleSelectedVendorForModel: Array<any> = [];
  singleSelectedVendorSettingsForModel = {};
  singleselectVen = {};

  checkedModel: Array<any> = [];
  modelOuterWrapper: Array<any> = [];
  mainOSVersionWrapper: Array<any> = [];
  finalOSVersion = {};
  addedOSVerMsg: any;
  addedOSVerMsgFail: any

  selectedOSVerList: Array<any> = [];
  multOSVersionDropdownList: Array<any> = [];
  multiSelectedOSVersion: any;
  multiSelectedOSVersionSettings = {};
  multiselectOSver = {};

  multIntfDropdownList: Array<any> = [];
  multiSelectedIntf: any;
  multiSelectedIntfSettings = {};
  selectedIntfList: Array<any> = [];
  multiselectIntf = {};
  noDTAssociated: boolean = false;
  noDTAssociatedMsg: string = "";

  ifDuplicateRegion: boolean = false;
  duplicateRegionMsg: string = "";

  ifDuplicateService: boolean = false;
  duplicateServiceMsg: string = "";

  ifDuplicateVendor: boolean = false;
  duplicateVendorMsg: string = "";
  region: any;
  service: any;
  devicetype: any;
  models: any;
  os: any;
  osversion: any;
  vendor: any;
  selectedVenArr: Array<any> = [];

  selectedSingleVendor: any;
  selectedVendor= [];
  constructor(private commonService: CommonService, private global: Global) {
    this.menubar = global.menubar;
  }

  ngOnInit() {
    this.makeMeActive('Admin'); 
    this.getVendorList();
    this.getRegion();
    this.getService();
  }

  getRegion() {
    this.setUrl = "/regions";
    this.commonService.getServiceData(this.setUrl)
      .subscribe(regionList => {
        this.regionDropDown = regionList.entity;
        console.log(this.regionDropDown);
      });
  }

  getService() {
    this.setUrl = "/services";
    this.commonService.getServiceData(this.setUrl)
      .subscribe(servcList => {
        this.serviceDropDown = servcList.entity;
        console.log(this.serviceDropDown);
      });
  }

  getVendorList() {
    this.setUrl = "/vendor";
    this.vendorDropDown = [];
    this.commonService.getServiceData(this.setUrl)
      .subscribe(vendorList => {
        if (vendorList != "" && vendorList != undefined && vendorList.entity.length > 0) {
          for (let i = 0; i < vendorList.entity.length; i++) {
            this.vendorDropDown.push(vendorList.entity[i].vendor);
          }
        }
      });
  }

  getModels() {
    this.setUrl = "/models";
    this.commonService.getServiceData(this.setUrl)
      .subscribe(modelsList => {
        if (modelsList != "" && modelsList != undefined && modelsList.entity.length > 0) {
          for (let i = 0; i < modelsList.entity.length; i++) {
            this.modelDropDown.push(modelsList.entity[i].model);
          }
        }
      });
  }

  getOSVersions() {
    this.setUrl = "/osversions";
    this.osVersionDropDown = [];
    this.commonService.getServiceData(this.setUrl)
      .subscribe(osverList => {
        if (osverList != "" && osverList != undefined && osverList.entity.length > 0) {
          for (let i = 0; i < osverList.entity.length; i++) {
            this.osVersionDropDown.push(osverList.entity[i].osversion);
          }
        }
      });
  }

  getListOfOS() {
    this.setUrl = "/os";
    this.osDropDown = [];
    this.commonService.getServiceData(this.setUrl)
      .subscribe(osList => {
        if (osList.entity.length > 0) {
          for (let i = 0; i < osList.entity.length; i++) {
            this.osDropDown.push(osList.entity[i].os);
          }
        }
        //this.vendorDropDown = JSON.parse(vendorList.entity.output);
        //console.log(this.vendorDropDown);
      });
  }
  
  getDeviceTypeOSOnVendor(vendorname: string) {
    console.log("selected vendor :: ", vendorname);
    if (vendorname == undefined) {
      this.deviceTypeDropDown = [];
      this.osDropDown = [];
      this.osVersionDropDown = [];
	    this.modelDropDown = [];
      this.noDTAssociated = false;
      this.noOSassociated = false;
      this.devicetype = undefined;
      this.models = undefined;
    } else {
      this.noVendorMsg = false;
      this.setUrl = "/devicetype?vendor=" + vendorname;
      this.deviceTypeDropDown = [];
      this.osDropDown = [];
      this.osVersionDropDown = [];
	  this.modelDropDown = [];
      // to get Device Type list
      this.commonService.getServiceData(this.setUrl)
        .subscribe(deviceList => {
          if (vendorname == undefined) {
            this.noVendorMsg = true;
            this.dispNoVendorMsg = deviceList.entity;
          } else {
            if (deviceList.entity.length > 0) {
              this.noDTAssociated = false;
              this.os = undefined;
              this.devicetype = undefined;
              this.osversion = undefined;
              for (let i = 0; i < deviceList.entity.length; i++) {
                this.deviceTypeDropDown.push(deviceList.entity[i].devicetype);
              }
              console.log("this.deviceTypeDropDown", this.deviceTypeDropDown);
            } else {
              this.noDTAssociated = true;
              this.noDTAssociatedMsg = "There is no Device Type for this vendor";
            }
          }

        })

      // to get OS List
      this.setUrl = "/os?vendor=" + vendorname;
      this.commonService.getServiceData(this.setUrl)
        .subscribe(osList => {
          if (osList.status == 200) {
            this.noOSassociated = false;
            if (osList.entity.length > 0) {
              this.os = undefined;
              this.devicetype = undefined;
              this.osversion = undefined;
              for (let i = 0; i < osList.entity.length; i++) {
                this.osDropDown.push(osList.entity[i].os);
              }
              console.log("this.osDropDown", this.osDropDown);
            }
          } else {
            this.noOSassociated = true;
            this.noOSassociatedMsg = osList.entity;
          }

        })
    }
  }

  /* Vendor */
  openAddVendorPopUp() {
    this.commonService.openAddPopUp('newVendorPopUp');
    this.newVendor = "";
    this.ifAddVendorSucess = false;
    this.ifAddVendorFails = false;
    this.ifDuplicateVendor = false;
  }

  closenewVendorPopUp() {
    this.commonService.closeAddPopUp('newVendorPopUp');
    this.vendorDropDown = [];
    this.vendor = undefined;
    this.getVendorList();
    //service call to get list of updated vendors
  }

  addVendor(newVendor: string) {

    if (newVendor != undefined && newVendor != null && newVendor != "") {
      this.newdata = {
        vendor: newVendor,
      }
      //service call to add vendor
      this.setUrl = "/vendor";
      this.commonService.postData(this.setUrl, this.newdata)
        .subscribe(vendorList => {
          if (vendorList && vendorList.status == 200) {
            vendorList = vendorList.entity;
            this.ifAddVendorSucess = true;
            this.ifAddVendorFails = false;
            this.ifDuplicateVendor = false;
            this.addedVendorMsg = vendorList;
            this.commonService.closeAddPopUp('newVendorPopUp');
            this.commonService.openAddPopUp('showSuccessVendorPopup');
          } else {
            vendorList = vendorList.entity;
            this.ifAddVendorSucess = false;
            this.ifAddVendorFails = false;
            this.ifDuplicateVendor = true;
            this.duplicateVendorMsg = vendorList;
            //this.addedVendorMsg = vendorList;
          }
        })
    } else {
      this.ifAddVendorFails = true;
      this.ifAddVendorSucess = false;
      this.ifDuplicateVendor = false;
    }
  }

  clearVendorData() {
    this.newVendor = "";
    this.ifAddVendorSucess = false;
    this.ifAddVendorFails = false;
    this.ifDuplicateVendor = false;
  }

  /* Region */
  openAddRegionPopUp() {
    this.commonService.openAddPopUp('newRegionPopUp');
    this.newRegion = "";
    this.ifAddRegionSucess = false;
    this.ifAddRegionFails = false;
    this.ifDuplicateRegion = false;
  }
 
  closenewRegionPopUp() {
    this.commonService.closeAddPopUp('newRegionPopUp');
    this.regionDropDown = []; 
    this.getRegion();
    //service call to get list of updated vendors
  }

  addRegion(newRegion: string) {
    if (newRegion != undefined && newRegion != null && newRegion != "") {
      this.regiondata = {
        region: newRegion,
      }
      //service call to add vendor
      this.setUrl = "/regions";
      this.commonService.postData(this.setUrl, this.regiondata)
        .subscribe(regionList => {
          if (regionList && regionList.status == 200) {
            regionList = regionList.entity;
            this.ifAddRegionSucess = true;
            this.ifAddRegionFails = false;
            this.ifDuplicateRegion = false;
            this.addedRegionMsg = regionList;
            this.commonService.closeAddPopUp('newRegionPopUp');
            this.commonService.openAddPopUp('showSuccessRegionPopup');
          } else {
            regionList = regionList.entity;
            this.ifAddRegionSucess = false;
            this.ifDuplicateRegion = true;
            this.duplicateRegionMsg = regionList;
          }


        })
    } else {
      this.ifAddRegionFails = true;
      this.ifAddRegionSucess = false;
      this.ifDuplicateRegion = false;
    }
  }

  clearRegionData() {
    this.newRegion = "";
    this.ifAddRegionSucess = false;
    this.ifAddRegionFails = false;
    this.ifDuplicateRegion = false;
  }


  /* Service */
  openAddServicePopUp() {
    this.commonService.openAddPopUp('newServicePopUp');
    this.newService = "";
    this.ifAddServiceSucess = false;
    this.ifAddServiceFails = false;
    this.ifDuplicateService = false;
  }

  closenewServicePopUp() {
    this.commonService.closeAddPopUp('newServicePopUp');
    this.serviceDropDown = [];
    this.getService();
    //service call to get list of updated vendors
  }

  addService(newService: string) {
    if (newService != undefined && newService != null && newService != "") {
      this.servicedata = {
        service: newService,
      }
      //service call to add vendor
      this.setUrl = "/services";
      this.commonService.postData(this.setUrl, this.servicedata)
        .subscribe(servcList => {

          if (servcList && servcList.status == 200) {
            servcList = servcList.entity;
            this.ifAddServiceSucess = true;
            this.ifAddServiceFails = false;
            this.ifDuplicateService = false;
            this.addedServiceMsg = servcList;
            this.commonService.closeAddPopUp('newServicePopUp');
            this.commonService.openAddPopUp('showSuccessServicePopup');
          } else {
            servcList = servcList.entity;
            this.ifAddServiceSucess = false;
            this.ifDuplicateService = true;
            this.duplicateServiceMsg = servcList;
          }
         // servcList = servcList.entity
        })
    } else {
      this.ifAddServiceFails = true;
      this.ifAddServiceSucess = false;
      this.ifDuplicateService = false;
    }
  }

  clearServiceData() {
    this.newService = "";
    this.ifAddServiceSucess = false;
    this.ifAddServiceFails = false;
    this.ifDuplicateService = false;
  }

  /* Device Type */

  clearDeviceTypeData() {
    this.newDeviceType = "";
    //this.vendorDropdownList = [];
    this.addedDeviceMsgSuccess = false;
    this.addedDeviceMsgFailure = false;
    this.addedDeviceFail = false;
    this.selectedVendor = undefined;
  }

  openAddDeviceTypePopUp() {
    this.addedDeviceMsgSuccess = false;
    this.addedDeviceMsgFailure = false;
    this.commonService.openAddPopUp('newDeviceTypePopUp');
    this.setUrl = "/vendor";
    this.newDeviceType = "";
    this.vendorDropdownList = [];
    this.selectedVendor = [];
    this.addedDeviceFail = false;
    
    //this.vendorDropDown = [];

    this.vendorDropdownSettings = {
      singleSelection: false,
      idField: 'vendor_id',
      textField: 'vendor_text',

      itemsShowLimit: 2,
      allowSearchFilter: true
    };

    let _self = this;
    this.commonService.getServiceData(this.setUrl)
      .subscribe(vendorList => {
        if (vendorList.entity.length > 0) {
          for (let i = 0; i < vendorList.entity.length; i++) {
            _self.multiselectVendor = {
              vendor_id: vendorList.entity[i].id,
              vendor_text: vendorList.entity[i].vendor
            }
            _self.vendorDropdownList.push(_self.multiselectVendor);
          }
          console.log("vendorDropdownList :: ", _self.vendorDropdownList);
          if(_self.vendorDropdownList.length > 0) {
            for(let i=0; i< _self.vendorDropdownList.length; i++) {
              if(this.vendor != undefined && this.vendor == _self.vendorDropdownList[i].vendor_text) {
                this.selectedVendor.push(_self.vendorDropdownList[i]);
                console.log("inside fun() this.selectedVendor ::",this.selectedVendor);
              }
            }
          }
          // if(this.vendor != undefined && this.vendor != "") {
          //   let prevendObj = {
          //      vendor_id: "1",
          //      vendor_text: this.vendor
          //    }
          //    this.selectedVendor = prevendObj;
          //  }
        }
        console.log("outside fun() this.selectedVendor ::",this.selectedVendor);
      });
  }

  closenewDeviceTypePopUp() {
    this.deviceTypeDropDown = [];
    this.commonService.closeAddPopUp('newDeviceTypePopUp');
    if (this.vendor != undefined) {
      this.getDeviceListBasedOnVendor(this.vendor);
    }

  }

  getDeviceListBasedOnVendor(ven: string) {
    this.setUrl = "/devicetype?vendor=" + ven;
    this.deviceTypeDropDown = [];
   // this.osDropDown = [];
   // this.osVersionDropDown = [];
    // to get Device Type list
    this.commonService.getServiceData(this.setUrl)
      .subscribe(deviceList => {
        if (ven == undefined) {
         // this.noVendorMsg = true;
         // this.dispNoVendorMsg = deviceList.entity;
         this.devicetype = undefined;
        } else {
          if (deviceList && deviceList.entity.length > 0) {
            this.noDTAssociated = false;
            for (let i = 0; i < deviceList.entity.length; i++) {
              this.deviceTypeDropDown.push(deviceList.entity[i].devicetype);
            }
           // console.log("this.deviceTypeDropDown", this.deviceTypeDropDown);
          } else {
            this.devicetype = undefined;
           // this.noDTAssociated = true;
           // this.noDTAssociatedMsg = "There is no Device Type for this vendor";
          }
        }
      });
  }
  
  getDeviceList() {
    this.setUrl = "/devicetypes";
    this.deviceTypeDropDown = [];
    this.commonService.getServiceData(this.setUrl)
      .subscribe(devicetypeList => {
        if (devicetypeList.entity.length > 0) {
          for (let i = 0; i < devicetypeList.entity.length; i++) {
            this.deviceTypeDropDown.push(devicetypeList.entity[i].devicetype);
          }
        }
        //this.vendorDropDown = JSON.parse(vendorList.entity.output);
        //console.log(this.vendorDropDown);
      });
  }

  addDeviceType(selectedVendor, newDeviceType: string) {
    //this.vendors = [];
    this.addedDeviceMsgSuccess = false;
    this.addedDeviceMsgFailure = false;
    this.addedDeviceFail = false;
    console.log("selectedVendor :: ", selectedVendor);
    this.selectedVenArr.push(selectedVendor);
    console.log("selectedVenArr", this.selectedVenArr);
    if ((newDeviceType == "") || (this.selectedVendor == undefined) || (this.selectedVendor.length == 0)) {
      this.addedDeviceMsgFailure = true;
      this.addedDeviceMsgSuccess = false;

    } else {
    
      this.vendors = [];
      this.dtarray = [];
      this.finalJson = {};
      this.newdata = {
        value: newDeviceType,
        displayName: newDeviceType
      }
      this.sendDT = {
        "devicetype": newDeviceType
      }
      this.dtarray.push(this.sendDT);
      this.newObj = {};
      this.updatedDTJson = {};
      this.vendors = [];
      this.finalJson = {};
      // this.newObj = {
      //   "deviceTpye": newDeviceType,
      //   "vendors": this.selectedVendorList
      // }

      if (this.selectedVendor.length > 0) {
        for (let i = 0; i < this.selectedVendor.length; i++) {
          this.checkedvendorname.push(this.selectedVendor[i].vendor_text);
          if (this.checkedvendorname.length > 0) {
            for (let j = 0; j < this.checkedvendorname.length; j++) {
              this.checkvendor = this.checkedvendorname[i];
              console.log("checkvendorname ::", this.checkedvendorname);
              this.updatedDTJson = {
                "vendor": this.checkvendor,
                "devicetypes": this.dtarray
              }
            }
            this.vendors.push(this.updatedDTJson);
          }

        }
      }

      //this.vendors.push(this.updatedDTJson);
      // console.log("data :: ", this.newObj);
      this.finalJson = {
        "vendors": this.vendors
      }
      console.log("updatedDTJson :: ", this.finalJson);
      // this.deviceTypeDropDown.push(this.newdata);
      this.setUrl = "/devicetype";
      this.commonService.postData(this.setUrl, this.finalJson)
        .subscribe(devcList => {

          // this.deviceTypeDropDown = JSON.parse(devcList);
          if (devcList.status == 200) {
            this.addedDeviceMsgSuccess = true;
            this.addedDeviceMsgFailure = false;
            devcList = devcList.entity;
            this.addedDeviceMsg = devcList;
            console.log("this.addedDeviceMsg ::",this.addedDeviceMsg);
            this.commonService.closeAddPopUp('newDeviceTypePopUp');
            this.commonService.openAddPopUp('showSuccessDTPopup');
          } else {
            this.addedDeviceMsgSuccess = false;
            this.addedDeviceFail = true;
            this.addedDeviceMsgFail = devcList.entity;
          }
        })
    }
  }

  /* OS */
  openAddOSPopUp() {
    this.commonService.openAddPopUp('newOSPopUp');
    this.newOS = "";
    this.ifAddOSSucess = false;
    this.ifAddOSFails = false;
    this.ifBlankOS = false;
    this.setUrl = "/vendor";
    this.selectedSingleVendor = [];
    this.singleVendorDropdownList = [];
    this.singleVendorDropdownSettings = {
      singleSelection: true,
      idField: 'single_vendor_id',
      textField: 'single_vendor_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 2,
      allowSearchFilter: true
    };

    let _self = this;
    this.commonService.getServiceData(this.setUrl)
      .subscribe(vendorList => {
        if (vendorList.entity.length > 0) {
          for (let i = 0; i < vendorList.entity.length; i++) {
            _self.singleselectVendor = {
              single_vendor_id: vendorList.entity[i].id,
              single_vendor_text: vendorList.entity[i].vendor
            }
            _self.singleVendorDropdownList.push(_self.singleselectVendor);
          }
          console.log("singleVendorDropdownList :: ", _self.singleVendorDropdownList);

          if(_self.singleVendorDropdownList.length > 0) {
            for(let i=0; i< _self.singleVendorDropdownList.length; i++) {
              if(this.vendor != undefined && this.vendor != "undefined" && this.vendor !=  "" && _self.singleVendorDropdownList[i].single_vendor_text == this.vendor) {
                this.selectedSingleVendor.push(_self.singleVendorDropdownList[i]);
              }
            }
          }
        }
      });
  }
 
  closenewOSPopUp() {
    this.commonService.closeAddPopUp('newOSPopUp');
    this.osDropDown = [];
    if (this.vendor != undefined) {
      this.getOSListBasedOnVendor(this.vendor);
    }
  }

  getOSListBasedOnVendor(vendor: string) {
    this.setUrl = "/os?vendor=" + vendor;
    this.osDropDown = [];
   // this.osDropDown = [];
   // this.osVersionDropDown = [];
    // to get Device Type list
    this.commonService.getServiceData(this.setUrl)
      .subscribe(osList => {
        if (vendor == undefined) {
         // this.noVendorMsg = true;
         // this.dispNoVendorMsg = deviceList.entity;
         this.os = undefined;
         this.osDropDown = [];
        } else {
          if (osList && osList.entity.length > 0) {
            this.noOSassociated = false;
            for (let i = 0; i < osList.entity.length; i++) {
              this.osDropDown.push(osList.entity[i].os);
            }
           // console.log("this.deviceTypeDropDown", this.deviceTypeDropDown);
          } else {
            this.os = undefined;
            this.osDropDown = [];
           // this.noDTAssociated = true;
           // this.noDTAssociatedMsg = "There is no Device Type for this vendor";
          }
        }
      });
  }

  getOSList(ven) {
    this.setUrl = "/os?vendor=" + ven;
    this.osDropDown = [];
    if(ven == undefined) {
      this.os = undefined;
    } else {
      this.commonService.getServiceData(this.setUrl)
      .subscribe(osList => {
        if (osList.entity.length > 0) {
          for (let i = 0; i < osList.entity.length; i++) {
            this.osDropDown.push(osList.entity[i].os);
          }
        } else {
          this.os = undefined;
        }
        //this.vendorDropDown = JSON.parse(vendorList.entity.output);
        //console.log(this.vendorDropDown);
      });
    }
    
  }

  addOS(newOS: string) {
    this.ifBlankOS = false;
    if ((newOS != undefined && newOS != null && newOS != "") || (this.selectedSingleVendor == undefined)) {
      this.formatSingleSelectVendor = {
        vendor: this.singleselectVendor
      }
      var dummyarray = [];
      var text, newText: string = "";
      dummyarray.push(this.singleSelectedVendor);
      console.log("formatSingleSelectedVendor ", this.formatSingleSelectVendor);
      if (dummyarray.length > 0) {
        for (let i = 0; i < dummyarray.length; i++) {
          text = dummyarray[i];
          for (let j = 0; j < dummyarray[i].length; j++) {
            newText = dummyarray[i][j].single_vendor_text;
          }
        }
      }
      var objwrapper = {};
      objwrapper = {
        vendor: newText
      }

      this.osData = {
        os: newOS,
        vendor: objwrapper
      };
      console.log("Single selected vendor :: ", this.osData);

      this.setUrl = "/os";
      this.commonService.postData(this.setUrl, this.osData)
        .subscribe(osList => {

          // this.deviceTypeDropDown = JSON.parse(devcList);
          if (osList.status == 200) {
            this.ifAddOSSucess = true;
            this.ifAddOSFails = false;
            this.ifBlankOS = false;
            osList = osList.entity;
            this.addedOSMsg = osList;
            this.commonService.closeAddPopUp('newOSPopUp');
            this.commonService.openAddPopUp('showSuccessOSPopup');
          } else {
            this.ifAddOSSucess = false;
            this.ifAddOSFails = true;
            this.ifBlankOS = false;
            this.addedOSMsgFail = osList.entity;
          }
        })
    } else {
      this.ifBlankOS = true;
      this.ifAddOSFails = false;
      this.ifAddOSSucess = false;
    }
  }

  clearOSData() {
    this.newOS = "";
    //this.vendorDropdownList = [];
    this.ifAddOSSucess = false;
    this.ifAddOSFails = false;
    this.ifBlankOS = false;
    this.selectedSingleVendor = undefined;
  }
 
  getOSversionOnOS(selectedos: string) {
    console.log("Selected OS ::", selectedos);
    this.setUrl = "/osversionforos?os=" + selectedos;
    this.osVersionDropDown = [];
    // to get Device Type list
    this.commonService.getServiceData(this.setUrl)
      .subscribe(osverList => {
        if (selectedos == undefined) {
          //this.noVendorMsg = true;
          //this.dispNoVendorMsg = deviceList.entity;
        } else {
          if (osverList.entity.length > 0) {
            for (let i = 0; i < osverList.entity.length; i++) {
              this.osVersionDropDown.push(osverList.entity[i].osversion);
            }
            console.log("this.osVersionDropDown", this.osVersionDropDown);
          }
        }

      })
  }

  /* Model */
  getModelOnDeviceType(selectedVendor: string, selectedDeviceType: string) {
    console.log("Selected Device Type ::", selectedDeviceType);
    console.log("Selected Vendor ::", selectedVendor);
    this.setUrl = "/model?devicetype=" + selectedDeviceType + "&vendor=" + selectedVendor;
    this.modelDropDown = [];
    // to get Device Type list
    this.commonService.getServiceData(this.setUrl)
      .subscribe(modelList => {
        if (selectedDeviceType == undefined || selectedVendor == undefined) {
          this.noModelMsg = true;
          this.dispNoModelMsg = modelList.entity;
        } else {
          if (modelList.entity.length <= 0) {
           this.modelDropDown = []
          } else {
            for (let i = 0; i < modelList.entity.length; i++) {
              this.modelDropDown.push(modelList.entity[i].model);
            }
            console.log("this.modelDropDown", this.modelDropDown);
          }
        }

      })
  }

  openAddModelPopUp() { 
    this.commonService.openAddPopUp('newModelPopUp');
    this.newOS = "";
    this.newModel = "";
    this.singleDevTypDropdownList = [];
    this.selectedDeviceList = [];
    this.ifAddOSSucess = false;
    this.ifAddOSFails = false;
    this.ifAddModelSucess = false;
    this.ifAddModelFails = false;
    this.ifBlankModel = false;
    this.singleSelectedVendorForModel = undefined;
    this.singleSelectedDvcTyp = undefined;
    this.multiSelectedIntf = undefined;
    this.multiSelectedOSVersion = undefined;
    this.singleVendorDropdownListForModel = [];
    this.multIntfDropdownList = [];
    this.singleSelectedVendorForModel = [];
    this.singleSelectedDvcTypSettings = {
      singleSelection: true,
      idField: 'single_dt_id',
      textField: 'single_dt_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 2,
      allowSearchFilter: true
    };

    this.singleSelectedVendorSettingsForModel = {
      singleSelection: true,
      idField: 'single_ven_id',
      textField: 'single_ven_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 2,
      allowSearchFilter: true
    }

    this.multiSelectedOSVersionSettings = {
      singleSelection: false,
      idField: 'multi_osver_id',
      textField: 'multi_osver_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 2,
      allowSearchFilter: true
    }

    this.multiSelectedIntfSettings = {
      singleSelection: false,
      idField: 'multi_intf_id',
      textField: 'multi_intf_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 2,
      allowSearchFilter: true
    }

    let _self = this;
    this.singleSelectedDvcTyp = undefined;

    this.setUrl = "/vendor";
    this.commonService.getServiceData(this.setUrl)
      .subscribe(venList => {
        if (venList.entity.length > 0) {
          for (let i = 0; i < venList.entity.length; i++) {
            _self.singleselectVen = {
              single_ven_id: venList.entity[i].id,
              single_ven_text: venList.entity[i].vendor
            }
            _self.singleVendorDropdownListForModel.push(_self.singleselectVen);
          }
          //console.log("singleVendorDropdownListForModel :: ", _self.singleVendorDropdownListForModel);

          if(_self.singleVendorDropdownListForModel.length > 0) {
            for(let i=0; i< _self.singleVendorDropdownListForModel.length; i++) {
              if(this.vendor != undefined && this.vendor == _self.singleVendorDropdownListForModel[i].single_ven_text) {
                this.singleSelectedVendorForModel.push(_self.singleVendorDropdownListForModel[i]);
                //console.log("inside fun() this.selectedVendor ::",this.selectedVendor);
                if(this.singleSelectedVendorForModel.length > 0 && this.singleSelectedVendorForModel != undefined) {
                  this.onsingleSelectedVendorSelectForModel(this.singleSelectedVendorForModel[0]);
                }
              }
            }
            
          }

        }
      });

   

    this.setUrl = "/osversions";
    this.multOSVersionDropdownList = [];
    this.commonService.getServiceData(this.setUrl)
      .subscribe(osverList => {
        _self.multOSVersionDropdownList = [];
        if (osverList.entity.length > 0) {
          for (let i = 0; i < osverList.entity.length; i++) {
            _self.multiselectOSver = {
              multi_osver_id: osverList.entity[i].id,
              multi_osver_text: osverList.entity[i].osversion
            }
            _self.multOSVersionDropdownList.push(_self.multiselectOSver);
          }
          console.log("multOSVersionDropdownList :: ", _self.multOSVersionDropdownList);
        }
      });
    
      
      this.setUrl = "/interfaces";
      this.multIntfDropdownList = [];
      this.commonService.getServiceData(this.setUrl)
        .subscribe(intfList => {
          _self.singleDevTypDropdownList = []; 
          //intfList.entity = [{ id: 1, interfaces: 'Ethernet' }, { id: 2, interfaces: 'FastEthernet' }, { id: 1, interfaces: 'GigabitEthernet' }, { id: 1, interfaces: 'TenGigabitEthernet' }, { id: 1, interfaces: 'Serial' }, { id: 1, interfaces: 'ATM' }, { id: 1, interfaces: 'POS' }, { id: 1, interfaces: 'Loopback' }];
          if (intfList.entity.length > 0) {
            for (let i = 0; i < intfList.entity.length; i++) {
              _self.multiselectIntf = {
                multi_intf_id: intfList.entity[i].id,
                multi_intf_text: intfList.entity[i].interfaces
              }
              _self.multIntfDropdownList.push(_self.multiselectIntf);
            }
            console.log("multIntfDropdownList :: ", _self.multIntfDropdownList);
          }
        });
 
  }

  closenewModelPopUp() {
    this.commonService.closeAddPopUp('newModelPopUp');
    this.modelDropDown = [];
    //this.getOSList();
  }

  clearModelData() {
    this.newModel = "";
    this.ifAddModelSucess = false;
    this.ifBlankModel = false;
    this.ifAddModelFails = false;
    this.singleSelectedVendorForModel = undefined;
    this.singleSelectedDvcTyp = undefined;
    this.multiSelectedIntf = undefined;
    this.multiSelectedOSVersion = undefined;
  }

  addModel(newModel: string) {

    if (newModel != undefined && newModel != null && newModel != "") { 
      var modelarray = [];
      var modeltext, innerModelText: string = "";
      modelarray.push(this.singleSelectedDT);
      if (modelarray.length > 0) {
        for (let i = 0; i < modelarray.length; i++) {
          modeltext = modelarray[i];
          for (let j = 0; j < modelarray[i].length; j++) {
            innerModelText = modelarray[i][j].single_dt_text;
          }
        }
      } 
      var modelObjwrapper, mainModelWrapper = {};
      modelObjwrapper = {
        devicetype: innerModelText
      }

      var vendorWrapper = {};
      var venname;
      if (this.singleSelectedVendorForModel.length > 0) {
        for (let i = 0; i < this.singleSelectedVendorForModel.length; i++) {
          venname = this.singleSelectedVendorForModel[i].single_ven_text;
        }
      }
      vendorWrapper = {
        vendor: venname
      }

      var intObj = {};
      var intArr = [];
      if (this.selectedIntfList.length > 0) {
        for (let i = 0; i < this.selectedIntfList.length; i++) {
          //intArr.push(this.selectedIntfList[i].multi_intf_text);
          intObj = {
            interfaces: this.selectedIntfList[i].multi_intf_text
          }
          intArr.push(intObj);
        }
      }

      var osverObj = {};
      var osverArr = [];

      if (this.selectedOSVerList.length > 0) {
        for (let i = 0; i < this.selectedOSVerList.length; i++) {
          //intArr.push(this.selectedIntfList[i].multi_intf_text);
          osverObj = {
            osversion: this.selectedOSVerList[i].multi_osver_text
          }
          osverArr.push(intObj);
        }
      }

      var modelArr = [];
      this.modelData = {
        model: newModel,
        devicetype: modelObjwrapper,
        vendor: vendorWrapper,
        osversion: osverArr
      };

      modelArr.push(this.modelData);

      mainModelWrapper = {
        models: modelArr,
        interfaces: intArr
      }

      console.log("mainModelWrapper ::", mainModelWrapper);
      this.setUrl = "/models";
      this.commonService.postData(this.setUrl, mainModelWrapper)
        .subscribe(modelList => {

          // this.deviceTypeDropDown = JSON.parse(devcList);
          if (modelList && modelList.status == 200) {
            modelList = modelList.entity;
            this.ifAddModelSucess = true;
            this.ifAddModelFails = false;
            this.addedModelMsg = modelList;
            this.commonService.closeAddPopUp('newModelPopUp');
            this.commonService.openAddPopUp('showSuccessModelPopup');
          } else {
            modelList = modelList.entity;
            this.ifAddModelSucess = false;
            this.ifAddModelFails = true;
            this.addedModelMsgFail = modelList;
          }
          
        })
    } else {
      this.ifBlankModel = true;
      this.ifAddModelSucess = false;
      this.ifAddModelFails = false;
    }
  }


  /* OS Version*/
  getOSVersionsBasedOnOS(os: string) {
    this.setUrl = "/osversions?os="+os;
    this.osVersionDropDown = [];
    this.commonService.getServiceData(this.setUrl)
      .subscribe(osverList => {
        if (osverList != "" && osverList != undefined && osverList.entity.length > 0) {
          for (let i = 0; i < osverList.entity.length; i++) {
            this.osVersionDropDown.push(osverList.entity[i].osversion);
          }
        }
      });  
  }

  openAddOSVersionPopUp() {
    this.commonService.openAddPopUp('newOSVersionPopUp');
    this.newOSVersion = "";
    this.ifAddOSVerSucess = false;
    this.ifAddOSVerFails = false;
    this.ifBlankOSVer = false;
    this.singleSelectedOS = [];
    this.multiSelectedModel = [];
    this.singleOSDropdownList = [];
    this.singleSelectedOSSettings = {
      singleSelection: true,
      idField: 'single_os_id',
      textField: 'single_os_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 2,
      allowSearchFilter: true
    };

    this.multiSelectedModelSettings = {
      singleSelection: false,
      idField: 'multi_model_id',
      textField: 'multi_model_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 2,
      allowSearchFilter: true
    }

    let _self = this;
    this.setUrl = "/oss";
    this.commonService.getServiceData(this.setUrl)
      .subscribe(osList => {
        if (osList.entity.length > 0) {
          for (let i = 0; i < osList.entity.length; i++) {
            _self.singleselectOS = {
              single_os_id: osList.entity[i].id,
              single_os_text: osList.entity[i].os
            }
            _self.singleOSDropdownList.push(_self.singleselectOS);
          }
          console.log("singleOSDropdownList :: ", _self.singleOSDropdownList);
          if(this.singleOSDropdownList.length > 0) {
            for(let i=0; i< this.singleOSDropdownList.length; i++) {
              if(this.os != undefined && this.os != "undefined" && this.os !=  "" && this.singleOSDropdownList[i].single_os_text == this.os) {
                this.singleSelectedOS.push(this.singleOSDropdownList[i]);
              }
            }
          }
          
        }
      });

    this.setUrl = "/models";
    _self.multiModelDropdownList = [];
    this.commonService.getServiceData(this.setUrl)
      .subscribe(modelList => {
        if (modelList.entity.length > 0) {
          for (let i = 0; i < modelList.entity.length; i++) {
            _self.multiselectModel = {
              multi_model_id: modelList.entity[i].id,
              multi_model_text: modelList.entity[i].model
            }
            _self.multiModelDropdownList.push(_self.multiselectModel);
          }
          console.log("multiselectModel :: ", _self.multiModelDropdownList);

          if( _self.multiModelDropdownList.length > 0) {
            for(let i=0; i< _self.multiModelDropdownList.length; i++) {
              if(this.models != undefined && this.models != "undefined" &&  _self.multiModelDropdownList[i].multi_model_text == this.models) {
                this.multiSelectedModel.push( _self.multiModelDropdownList[i])
              }
            }
          }
        }
      });
  }

  closenewOSVersionPopUp() {
    this.commonService.closeAddPopUp('newOSVersionPopUp');
    this.osVersionDropDown = [];
    if (this.os != undefined) {
      this.getOSVersionsBasedOnOS(this.os);
    }
  }

  clearOSVersion() {
    this.newOSVersion = "";
    this.ifAddOSVerSucess = false;
    this.ifAddOSVerFails = false;
    this.ifBlankOSVer = false;
    this.singleSelectedOS = undefined;
    this.multiSelectedModel = undefined;
  }

  addOSVersion(newOSVersion: string) {
    if (newOSVersion != undefined && newOSVersion != "" && newOSVersion != null && this.singleSelectedOS != undefined) {
      var osarray = [];
      this.osWrapper = {};
      this.modelOuterWrapper = [];
      var modelWrapper = {}; 
      this.mainOSVersionWrapper = [];
      this.finalOSVersion = {};
      this.addedOSVerMsgFail = "";
      this.addedOSVerMsg = "";
      var ostext, innerOSText: string = "";
      osarray.push(this.singleSelectedOS);
      if (osarray.length > 0) {
        for (let i = 0; i < osarray.length; i++) {
          ostext = osarray[i];
          for (let j = 0; j < osarray[i].length; j++) {
            innerOSText = osarray[i][j].single_os_text;
          }
        }
      }
      var osObjwrapper, mainOSWrapper = {};
      var modelArr = [];
      osObjwrapper = {
        os: innerOSText
      }
      console.log("selectedModelList", this.selectedModelList);
      console.log("osObjwrapper : ", osObjwrapper);
      if (this.selectedModelList.length > 0) {
        for (let i = 0; i < this.selectedModelList.length; i++) {
          //this.checkedModel.push(this.selectedModelList[i].multi_model_text);
          modelWrapper = {
            model: this.selectedModelList[i].multi_model_text
          }
          // modelArr.push(modelWrapper);
        }
      }

      console.log("modelWrapper :: ", modelWrapper);
      this.modelOuterWrapper.push(modelWrapper);
      //}

      this.osWrapper = {
        osversion: newOSVersion,
        os: osObjwrapper,
        models: this.modelOuterWrapper
      }

      this.mainOSVersionWrapper.push(this.osWrapper)
      this.finalOSVersion = {
        osversions: this.mainOSVersionWrapper
      }
      console.log("finalOSVersion:: ", this.finalOSVersion);
      this.setUrl = "/osversion";
      this.commonService.postData(this.setUrl, this.finalOSVersion)
        .subscribe(osversionList => {
          // this.deviceTypeDropDown = JSON.parse(devcList);
          if (osversionList.status == 200) {
            this.ifAddOSVerSucess = true;
            this.ifAddOSVerFails = false;
            osversionList = osversionList.entity;
            this.addedOSVerMsg = osversionList;
            this.commonService.closeAddPopUp('newOSVersionPopUp');
            this.commonService.openAddPopUp('showSuccessOSVersionPopup');
          } else {
            this.ifAddOSVerSucess = false;
            this.ifAddOSVerFails = true;
            this.addedOSVerMsgFail = osversionList.entity;
          }
        })
    } else {
      this.ifBlankOSVer = true;
      this.ifAddOSVerSucess = false;
      this.ifAddOSVerFails = false;
    }
  }
  /* Vendor multi-select*/
  onVendorSelect(item: any) {
    console.log(item);
    this.selectedVendor = [];
    this.selectedVendor.push(item);
    //let venObj = {};
    //venObj = this.selectedVendorList;
    console.log(this.selectedVendor);
  }

  onVendorSelectAll(items: any) {
    console.log(items);
  }

  onSingleVendorSelect(item: any) {
    console.log("Selected Item :: ", item);
    this.singleSelectedVendor.push(item);
  }

  onsingleSelectedVendorSelectForModel(itemVendorModel: any) {
    console.log("Selected vendor for model addition ::", itemVendorModel);
    this.singleSelectedVendorForModel.push(itemVendorModel);
    this.setUrl = "/devicetype?vendor="+itemVendorModel.single_ven_text;
    let _self = this;
    this.commonService.getServiceData(this.setUrl)
      .subscribe(dtList => {
        _self.singleDevTypDropdownList = [];
        if (dtList.entity.length > 0) {
          for (let i = 0; i < dtList.entity.length; i++) {
            _self.singleselectDT = {
              single_dt_id: dtList.entity[i].id,
              single_dt_text: dtList.entity[i].devicetype
            }
            _self.singleDevTypDropdownList.push(_self.singleselectDT);
          }
          console.log("singleDevTypDropdownList :: ", _self.singleDevTypDropdownList);
        }
      });
    
  }

  /* Device type multi-select*/
  onDeviceSelect(item: any) {
    console.log(item);
    this.selectedDeviceList.push(item);
    console.log(this.selectedDeviceList);
  }

  onDeviceSelectAll(items: any) {
    console.log(items);
  }

  onsingleSelectedDvcTypSelect(item: any) {
    console.log("Selected Item :: ", item);
    this.singleSelectedDT.push(item);
  }

  /* OS */
  onsingleSelectedOS(item: any) {
    console.log("Selected Item :: ", item);
    this.singleSelectedOS.push(item);
  }

  onmultiDeselectedModel(item: any) {
    console.log("Deselected item :: ", item);
    this.selectedModelList.slice(item);
  }

  onmultiSelectedModel(items: any) {
    console.log(items);
    this.selectedModelList.push(items);
    console.log(this.selectedModelList);
  }


  /* OS Version multi select */
  onmultiSelectedOSVersionSelect(multiOSVerItems: any) {
    console.log("multi selected OS version :: ", multiOSVerItems);
    this.selectedOSVerList.push(multiOSVerItems);
  }

  /* Interface multi select */
  onmultiSelectedIntfSelect(multiIntf: any) {
    console.log("multi selected OS version :: ", multiIntf);
    this.selectedIntfList.push(multiIntf);
  }

  makeMeActive = function (key) {
    this.activetab = this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }


  /* Delete Functionality */
  showRegDltMsg: boolean = false;
  deletedRegMsg: string = "";

  showServDltMsg: boolean = false;
  deletedServMsg: string = "";

  showVenDltMsg: boolean = false;
  deletedVenMsg: string = "";

  showDTDltMsg: boolean = false;
  deletedDTMsg: string = "";

  showModelDltMsg: boolean = false;
  deletedModelMsg: string = "";

  showOSDltMsg: boolean = false;
  deletedOSMsg: string = "";

  showOSVerDltMsg: boolean = false;
  deletedOSVerMsg: string = "";
  /* REGION */
  openDeleteRegionPopUp(region: string) {
    this.showRegDltMsg = false;
    this.commonService.openAddPopUp('showDeleteRegionPopup');
  }

  closeDelteRegionPopUp(region: string) {
    this.commonService.closeAddPopUp('showDeleteRegionPopup');
    this.getRegion();
  }

  deleteRegion(region: string) {
    var regionObj = {
      region: region
    }
    this.setUrl = "/regions?regions=" + region;
    this.commonService.deleteData(this.setUrl, region).
      subscribe(resp => {
        if (resp.entity != undefined && resp.entity != "") {
          this.showRegDltMsg = true;
          this.deletedRegMsg = resp.entity;
          this.region = undefined;
          this.closeDelteRegionPopUp(region);
        }
      });
  }

  /* SERVICE */
  openDeleteServicePopUp(service: string) {
    this.showServDltMsg = false;
    this.commonService.openAddPopUp('showDeleteServicePopup');
  }

  closeDelteServicePopUp(service: string) {
    this.commonService.closeAddPopUp('showDeleteServicePopup');
    this.getService();
  }

  deleteService(service: string) {
    this.setUrl = "/services?services=" + service;
    this.commonService.deleteData(this.setUrl, service).
      subscribe(resp => {
        if (resp.entity != undefined && resp.entity != "") {
          this.showServDltMsg = true;
          this.deletedServMsg = resp.entity;
          this.service = undefined;
          this.closeDelteServicePopUp(service);
        }
      });
  }

  /* VENDOR */
  openDeleteVendorPopUp(vendor: string) {
    this.showVenDltMsg = false;
    this.commonService.openAddPopUp('showDeleteVendorPopup');
  }

  closeDelteVendorPopUp(vendor: string) {
    this.commonService.closeAddPopUp('showDeleteVendorPopup');
    this.getVendorList();
    this.noOSassociated = false;
    this.noDTAssociated = false;
  }

  deleteVendor(vendor: string) {
    this.setUrl = "/vendor?vendor=" + vendor;
    this.commonService.deleteData(this.setUrl, vendor).
      subscribe(resp => {
        if (resp.entity != undefined && resp.entity != "") {
          this.showVenDltMsg = true;
          this.deletedVenMsg = resp.entity;
          this.vendor = undefined;
          this.deviceTypeDropDown = [];
          this.devicetype = undefined;
          this.modelDropDown = [];
          this.models = undefined;
          this.closeDelteVendorPopUp(vendor);
        }
      });
  }

  /* DEVICE TYPE */
  openDeleteDeviceTypePopUp(devicetype: string) {
    this.showDTDltMsg = false;
    this.commonService.openAddPopUp('showDeleteDTPopup');
  }

  closeDelteDTPopUp(devicetypevendor: string) {
    this.commonService.closeAddPopUp('showDeleteDTPopup');
    this.getDeviceListBasedOnVendor(this.vendor);
  }

  deleteDT(devicetype: string) {
    this.setUrl = "/devicetype?devicetype=" + devicetype;
    this.commonService.deleteData(this.setUrl, devicetype).
      subscribe(resp => {
        if (resp.entity != undefined && resp.entity != "") {
          this.showDTDltMsg = true;
          this.deletedDTMsg = resp.entity;
          this.devicetype = undefined;
          this.closeDelteDTPopUp(devicetype);
        }
      });
  }

  /* Model */
  openDeleteModelPopUp(model: string) {
    this.showModelDltMsg = false;
    this.commonService.openAddPopUp('showDeleteModelPopup');
  }

  closeDelteModelPopUp(model: string) {
    this.commonService.closeAddPopUp('showDeleteModelPopup');
    this.getModels();
  }

  deleteModel(model: string) {
    this.setUrl = "/model?model=" + model;
    this.commonService.deleteData(this.setUrl, model).
      subscribe(resp => {
        if (resp.entity != undefined && resp.entity != "") {
          if(this.vendor != undefined && this.devicetype != undefined) {
            this.showModelDltMsg = true;
            //this.deletedModelMsg = resp.entity;
            this.models = undefined;
           this.modelDropDown = [];
           this.getModelOnDeviceType(this.vendor, this.devicetype);
            this.closeDelteModelPopUp(model);
          } else {
            this.showModelDltMsg = true;
            this.deletedModelMsg = resp.entity;
            this.models = undefined;
            this.modelDropDown = [];
            this.closeDelteModelPopUp(model);
          }
          
        }
      });
  }

  /* OS */
  openDeleteOSPopUp(os: string) {
    this.showOSDltMsg = false;
    this.commonService.openAddPopUp('showDeleteOSPopup');
  }

  closeDelteOSPopUp(os: string) {
    this.commonService.closeAddPopUp('showDeleteOSPopup');
    this.getListOfOS();
    this.os = undefined;
  }

  deleteOS(os: string) {
    this.setUrl = "/os?os=" + os;
    this.commonService.deleteData(this.setUrl, os).
      subscribe(resp => {
        if (resp.entity != undefined && resp.entity != "") {
          this.showOSDltMsg = true;
          this.deletedOSMsg = resp.entity;
          this.os = undefined;
          this.osversion = undefined;
          this.osVersionDropDown = [];
          this.closeDelteOSPopUp(os);
        }
      });
  }

  /* OS VERSION*/
  openDeleteOSVersionPopUp(osversion: string) {
    this.showOSVerDltMsg = false;
    this.commonService.openAddPopUp('showDeleteOSVerPopup');
  }

  closeDelteOSVerPopUp(osversion: string) {
    this.commonService.closeAddPopUp('showDeleteOSVerPopup');
    this.getOSVersions();
  }

  deleteOSVer(osversion: string) {
    this.setUrl = "/osversion?osversion=" + osversion;
    this.commonService.deleteData(this.setUrl, osversion).
      subscribe(resp => {
        if (resp.entity != undefined && resp.entity != "") {
          this.showOSVerDltMsg = true;
          this.deletedOSVerMsg = resp.entity;
          this.osversion = undefined;
          this.closeDelteOSVerPopUp(osversion);
        }
      });
  }


  /* SUCCESSFUL ADDITION MSGS */
  closeSuccessRegionPopUp() {
    this.commonService.closeAddPopUp('showSuccessRegionPopup');
    this.regionDropDown = [];
    this.getRegion();
    
  }

  closeSuccessServicePopUp() {
    this.commonService.closeAddPopUp('showSuccessServicePopup');
    this.serviceDropDown = [];
    this.getService();
  }

  closeSuccessVendorPopUp() {
    this.commonService.closeAddPopUp('showSuccessVendorPopup');
    this.vendorDropDown = [];
    this.vendor = undefined;
    this.getVendorList();
  }

  closeSuccessDTPopUp() {
    this.commonService.closeAddPopUp('showSuccessDTPopup');
    this.deviceTypeDropDown = [];
    if (this.vendor != undefined) {
      this.getDeviceListBasedOnVendor(this.vendor);
    } else {
      this.devicetype = undefined;
      this.deviceTypeDropDown = [];
    }
  }

  closeSuccessModelPopUp() {
    this.commonService.closeAddPopUp('showSuccessModelPopup');
    if(this.vendor != undefined && this.devicetype != undefined) {
      this.getModelOnDeviceType(this.vendor, this.devicetype);
    } else {
      this.modelDropDown = [];
    }
    
  }

  closeSuccessOSPopUp() {
    this.commonService.closeAddPopUp('showSuccessOSPopup');
    this.osDropDown = [];
    if (this.vendor != undefined) {
      this.getOSListBasedOnVendor(this.vendor);
    }  
  }

  closeSuccessOSVersionPopUp() {
    this.commonService.closeAddPopUp('showSuccessOSVersionPopup');
    this.osVersionDropDown = [];
    if (this.os != undefined) {
      this.getOSVersionsBasedOnOS(this.os);
    }
  }
   /* EDIT FUNCTIONALITY */
    selectedEditDeviceTypeItems = [];
    openEditDeviceTypePopUp(selectedVendor, editdevicetype: string, selectedEditDT: Array<any>,selectedVenArr) {
      this.commonService.openAddPopUp('showEditDeviceTypePopup');
      this.setUrl = "/vendor";
      this.newDeviceType = "";
      this.vendorDropdownList = [];
      console.log("selectedVenArr edit pp:",this.selectedVenArr);
      console.log("selectedVendor :",selectedVendor);
      //this.vendorDropDown = [];
      this.selectedEditDeviceTypeItems = selectedEditDT;
      console.log("selectedEditDeviceTypeItems :: ", this.selectedEditDeviceTypeItems);
      this.vendorDropdownSettings = {
        singleSelection: false,
        idField: 'vendor_id',
        textField: 'vendor_text',

        itemsShowLimit: 2,
        allowSearchFilter: true
      };

      let _self = this;
      this.commonService.getServiceData(this.setUrl)
        .subscribe(vendorList => {
          if (vendorList.entity.length > 0) {
            for (let i = 0; i < vendorList.entity.length; i++) {
              _self.multiselectVendor = {
                vendor_id: vendorList.entity[i].id,
                vendor_text: vendorList.entity[i].vendor
              }
              _self.vendorDropdownList.push(_self.multiselectVendor);
            }
            console.log("vendorDropdownList :: ", _self.vendorDropdownList);
          }
        });
    } 

    closeEditRegionPopUp(region: string) {
      this.commonService.closeAddPopUp('showEditDeviceTypePopup');
      this.getRegion();
    }

    editRegions(editregion: string) {
      console.log("inside edit region")
      this.addRegion(editregion);
    }
  //keyCode: any;
  //To restrict spaces 
  avoidSpace(event) {
    var k = event ? event.which : window.event.keyCode;
    if (k == 32) return false;
  }
}

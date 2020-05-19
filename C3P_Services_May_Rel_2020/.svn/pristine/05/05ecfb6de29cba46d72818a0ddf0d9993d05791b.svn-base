import { Component, OnInit } from '@angular/core';
import { CommonService } from '../common.service';

@Component({
  selector: 'app-devicedetails',
  templateUrl: './devicedetails.component.html',
  styleUrls: ['./devicedetails.component.css']
})
export class DevicedetailsComponent implements OnInit {
  getNewConfig: boolean = false;
  deviceDetails: boolean = false;
  configFeature: boolean = false;
  finalView: boolean = false;
  vendorDropDown: Array<any> = [];
  regionDropDown: Array<any> = [];
  deviceDropDown: Array<any> = [];
  modelDropDown: Array<any> = [];
  osDropDown: Array<any> = [];
  iosDropDown: Array<any> = [];
  setUrl: string = "";
  constructor(private commonService: CommonService) {

  }

  ngOnInit() {
    //service to get vendor List
    //service to get region List
    this.getVendorList();
    this.getRegionList();

  }

  getVendorList() {
    this.setUrl = "/GetConfigurationData/getVendor";
    this.commonService.getServiceData(this.setUrl)
      .subscribe(vendorList => {
        this.vendorDropDown = JSON.parse(vendorList.entity.output);
        console.log(this.vendorDropDown);
      });
  }

  getRegionList() {
    this.setUrl = "/GetConfigurationData/getRegion";
    this.commonService.getServiceData(this.setUrl)
      .subscribe(regionList => {
        this.regionDropDown = JSON.parse(regionList.entity.output);
        console.log(this.regionDropDown);
      });
  }

  /* To get list of Device Types on selection of Vendor */
  getDeviceTypeOnVendor(selectedVendor: string) {
    this.deviceDropDown = [];
    console.log("selectedVendor :: ", selectedVendor);
    //Post call
    var selectedVendorName = {
      vendor: selectedVendor,
    };
    this.setUrl = "/GetConfigurationData/getDeviceType";
    this.commonService.postData(this.setUrl, selectedVendorName)
      .subscribe(deviceList => {
        deviceList = deviceList.entity.output;
        this.deviceDropDown = JSON.parse(deviceList);
        console.log("this.deviceDropDown");
      })
  }

  getOSModelOnDeviceType(selectedVendor: string, selectedDevice: string) {
    this.modelDropDown = [];
    console.log("selectedDevice : ", selectedVendor);
    //Post call for Model 
    var selectedVendorNameDeviceName = {
      vendor: selectedVendor,
      deviceType: selectedDevice
    };
    this.setUrl = "/GetConfigurationData/model";
    this.commonService.postData(this.setUrl, selectedVendorNameDeviceName)
      .subscribe(modelList => {
        modelList = modelList.entity.output;
        this.modelDropDown = JSON.parse(modelList);
        console.log("this.modelDropDown", this.modelDropDown);
      })

    this.osDropDown = [];
    //Post call for OS 

    this.setUrl = "/GetConfigurationData/os";
    this.commonService.postData(this.setUrl, selectedVendorNameDeviceName)
      .subscribe(osList => {
        osList = osList.entity.output;
        this.modelDropDown = JSON.parse(osList);
        console.log("this.osDropDown", this.osDropDown);
      })
  }
 
  getIOSonOS(os: string, model:string) {
    this.iosDropDown = [];
    console.log("selectedos : ", os);
    //Post call for Model 
    var selectedOSNameModelName = { 
      os: os,
      model: model
    };
    this.setUrl = "/GetConfigurationData/ios";
    this.commonService.postData(this.setUrl, selectedOSNameModelName)
      .subscribe(iosList => {
        iosList = iosList.entity.output;
        this.iosDropDown = JSON.parse(iosList);
        console.log("this.iosDropDown", this.iosDropDown);
      })
  }

  makeMeActive = function (key) {
    this.activetab = this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }

}

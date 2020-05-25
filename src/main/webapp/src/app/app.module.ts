import { NgModule }       from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule }    from '@angular/forms';
import { HttpClientModule }    from '@angular/common/http';
import { Global } from './global';
/*import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService }  from './in-memory-data.service';*/
import {A2Edatetimepicker} from 'ng2-eonasdan-datetimepicker';
import { AppRoutingModule }     from './app-routing.module';

import { AppComponent }         from './app.component';

import { OrderModule } from 'ngx-order-pipe';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HeaderComponent } from './header/header.component';
import { HomeComponent } from './home/home.component';
import { ConfigurationComponent } from './configuration/configuration.component';
import { ReportComponent } from './report/report.component';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { ModalModule } from 'ngx-bootstrap/modal';
import { LoginComponent } from './login/login.component';
import { ChartModule } from 'angular-highcharts';
import { DevicemanagementComponent } from './devicemanagement/devicemanagement.component';
import { IpmanagementComponent } from './ipmanagement/ipmanagement.component';
import { GlobalListMgmtComponent } from './global-list-mgmt/global-list-mgmt.component';
import { TemplateAdminMgmtComponent } from './template-admin-mgmt/template-admin-mgmt.component';
import { IpmanagementserviceService } from './services/ipmanagementservice.service';
import { CommonService } from './common.service';
import { HttpModule } from '@angular/http';
//import { ConfigcomparisonComponent } from './configcomparison/configcomparison.component';
import { AngularDraggableModule } from 'angular2-draggable';
import { NgxPopperModule } from 'ngx-popper';
//import { ResizableModule } from 'angular-resizable-element';
//import { TreeviewModule } from 'ngx-treeview';
import { DevicedetailsComponent } from './devicedetails/devicedetails.component';
import { FieldEnggComponent } from './field-engg/field-engg.component'; 
import { FieldEngDetailsComponent } from './field-eng-details/field-eng-details.component';
import { AlertComponent } from './alert/alert.component';
import { GloballistComponent } from './globallist/globallist.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { ConfigcomparisonComponent } from './configcomparison/configcomparison.component';
import { FooterComponent } from './footer/footer.component';
import { RequestdetailsComponent } from './requestdetails/requestdetails.component';
import { ScheduleComponent } from './schedule/schedule.component';

@NgModule({
  imports: [
    BrowserModule,
    OrderModule,  
    FormsModule,
    HttpModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    AngularDraggableModule,
    NgxPopperModule,
    //ResizableModule,
    //TreeviewModule,
    BsDropdownModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    NgMultiSelectDropDownModule.forRoot(),
    A2Edatetimepicker,
   // TreeviewModule.forRoot(), 
    ChartModule   // add ChartModule to your imports
    // The HttpClientInMemoryWebApiModule module intercepts HTTP requests
    // and returns simulated server responses.
    // Remove it when a real server is ready to receive requests.
    /*HttpClientInMemoryWebApiModule.forRoot(
      InMemoryDataService, { dataEncapsulation: false }
    )*/
  ],
  declarations: [
    AppComponent,
    DashboardComponent,
    HeaderComponent,
    HomeComponent,
    ConfigurationComponent,
    ReportComponent,
    LoginComponent,
    DevicemanagementComponent,
    IpmanagementComponent,
    GlobalListMgmtComponent,
    TemplateAdminMgmtComponent,
    //ConfigcomparisonComponent,
    DevicedetailsComponent,
    FieldEnggComponent,
    FieldEngDetailsComponent,
    AlertComponent,
    GloballistComponent,
    ConfigcomparisonComponent,
    FooterComponent,
    RequestdetailsComponent,
    ScheduleComponent, 
    //SearchPipe,
    
  ],
  providers: [  
    Global,
    IpmanagementserviceService,CommonService
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
 
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent }      from './home/home.component';
import { DashboardComponent }   from './dashboard/dashboard.component';
import { ConfigurationComponent }   from './configuration/configuration.component';
import { ReportComponent }   from './report/report.component';
import { LoginComponent }   from './login/login.component';
import { DevicemanagementComponent }   from './devicemanagement/devicemanagement.component';
import { IpmanagementComponent }   from './ipmanagement/ipmanagement.component';
import { GlobalListMgmtComponent }   from './global-list-mgmt/global-list-mgmt.component';
import { TemplateAdminMgmtComponent }   from './template-admin-mgmt/template-admin-mgmt.component';
import { DevicedetailsComponent } from './devicedetails/devicedetails.component';
import { FieldEnggComponent } from './field-engg/field-engg.component'
import { FieldEngDetailsComponent } from './field-eng-details/field-eng-details.component';
import { AlertComponent } from './alert/alert.component'
import { GloballistComponent } from './globallist/globallist.component';
import { ConfigcomparisonComponent } from './configcomparison/configcomparison.component';
import { RequestdetailsComponent } from './requestdetails/requestdetails.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'configuration', component: ConfigurationComponent },
  { path: 'requestdetails', component: RequestdetailsComponent },
  { path: 'report', component: ReportComponent },
  { path: 'devicemanagement', component: DevicemanagementComponent },
  { path: 'ipmanagement', component: IpmanagementComponent },
  { path: 'global-list-mgmt', component: GlobalListMgmtComponent },
  { path: 'globallist', component: GloballistComponent },
  { path: 'template-admin-mgmt', component: TemplateAdminMgmtComponent },
  { path: 'device-details', component: DevicedetailsComponent },
  { path: 'config-comparison', component: ConfigcomparisonComponent },
  { path: 'field-engg', component: FieldEnggComponent },
  { path: 'field-eng-details', component: FieldEngDetailsComponent },
  { path: 'alert', component: AlertComponent },
];


@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  
  exports: [ RouterModule ]
})

export class AppRoutingModule { }
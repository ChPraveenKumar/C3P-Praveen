import { Component, OnInit } from '@angular/core';
import { Chart } from 'angular-highcharts';
import { CommonService } from '../common.service';
import { Global } from'../global';
@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css'],
  providers: [ CommonService]
})
export class ReportComponent implements OnInit {
  menubar: {};
  setUrl: string;
  getData;
  getPieReport;
  getColReport;
  reportStatus;
  pieChartContainer;
  columnChartContainer;
  constructor( private commonService: CommonService, 
    private global: Global) {
    this.menubar = global.menubar
  }

  ngOnInit() {
    this.makeMeActive('Report');
    this.getPieChart();
    this.getcolumnChart();
  }

  getPieChart (){
    this.setUrl = "/GetReportData/getRequestStatusData";
    this.getData = ""
    this.commonService.getData(this.setUrl, this.getData) 
      .subscribe(getPieReport => {this.getPieReport = getPieReport
        this.reportStatus = getPieReport.entity;
        var successCount= this.reportStatus.Success;
        var failureCount= this.reportStatus.Failure;		
        var InProgressCount=  this.reportStatus.InProgress;
        
        this.pieChartContainer = new Chart({
          chart: {
            type: 'pie'
          },
          subtitle: {
            text: 'Total : ' + this.reportStatus.Total
          },
          title: {
            text: 'Configuration Request Status Report'
          },
          tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
          },
          credits: {
            enabled: false
          },
          colors: ['#7cb5ec', '#ef4c4c', '#90ed7d'],
          plotOptions: {
            pie: {
              allowPointSelect: true,
              cursor: 'pointer',
              depth: 35,
              showInLegend: true,
              dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %'
              }
            }
          },
          series: [{
            type: 'pie',
            name: 'Request Status',
            data:[
              ['In Progress', InProgressCount],
              ['Failure',failureCount],
              {
                name: 'Success',
                y:  successCount,
                sliced: true,
                selected: true
              }
             ]
          }]

      });
        
      });
  }

  getcolumnChart(){
    this.setUrl = "/GetReportData/getStatusReportWeekly";
    this.getData = ""
    var dataColChart = {},
				totalCount;
    this.commonService.getData(this.setUrl, this.getData) 
      .subscribe(getColReport => {this.getColReport = getColReport
        dataColChart = JSON.parse(getColReport.entity.Output);
        totalCount = JSON.parse(getColReport.entity.TotalRequests)
        
        this.columnChartContainer = new Chart ({
          chart: {
            type: 'column'
          },
          title: {
            text: 'Weekly Status Report'
          },
          subtitle: {
            text: 'Total : '+totalCount
          },
          credits: {
            enabled: false
          },
          xAxis: {
            categories: dataColChart[3].Dates,
            crosshair: true,
            title: {
                text: 'Date'
            }
          },
          yAxis: {
              min: 0,
              title: {
                  text: 'Requests'
              }
          },
          tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.0f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        }, 
        colors: ['#90ed7d','#7cb5ec', '#ef4c4c'],
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: [ {
            name: dataColChart[0].Success.name,
            data: dataColChart[0].Success.data

        },{
            name: dataColChart[2].InProgress.name,
            data: dataColChart[2].InProgress.data

        }, {
            name: dataColChart[1].Failure.name,
            data: dataColChart[1].Failure.data

          },]
        })
      })
  }


  makeMeActive = function(key){
    this.activetab =this.commonService.activeTabs(key, this.menubar);
    this.menubar = this.activetab
  }

}

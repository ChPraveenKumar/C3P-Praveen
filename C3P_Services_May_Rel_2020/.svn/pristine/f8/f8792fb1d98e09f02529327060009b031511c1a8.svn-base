C3PApp.controller('reportController',['$scope','$rootScope','$http', '$state','dataFactory','activeTabs',function($scope, $rootScope, $http, $state, dataFactory, activeTabs) {
							
							/// Higlighted Active tab goes here--------
							activeTabs.activeTabsFunc('Report');
							$rootScope.menubar = activeTabs.menubar;
							/// Higlighted Active tab goes here--------
							
							/*Merthod for Pie Chart*/
							$scope.getPieChart = function(){
								$scope.reportStatus="";
								
							$http(
									{
										url : localStorage.getItem('path') + "/GetReportData/getRequestStatusData",
										//url : "http://localhost:8023/GetAllIpamData/getAll",
										method : "GET"

									})
									.then(
											function(response) {
												console.log(response)
												$scope.reportStatus = response.data.entity;
												var successCount=$scope.reportStatus.Success;
												var failureCount=$scope.reportStatus.Failure;		
												var InProgressCount=$scope.reportStatus.InProgress;
												var sheduleCount=$scope.reportStatus.Scheduled;
												var chart = new Highcharts.Chart({
											        chart: {
											            type: 'pie',
											            renderTo: 'pieChartContainer'
											        },
													      subtitle: {
												          text: 'Total : ' + $scope.reportStatus.Total
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
												      colors: ['#7cb5ec', '#ef4c4c','#ffcc00', '#90ed7d'],
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
												        	  ['Scheduled',sheduleCount],
												        	  {
												                  name: 'Success',
												                  y:  successCount,
												                  sliced: true,
												                  selected: true
												              }
												           ]
												      }]

												  });
												})
						
								
							}
							/*Merthod for Donut Chart*/
							$scope.getDonutChart = function(){
										$http(
										{
											url : localStorage.getItem('path') + "/GetReportData/getElapsedTimeData",
											//url : "http://localhost:8023/GetAllIpamData/getAll",
											method : "GET"

										})
										.then(
												function(response) {
													console.log(response)
													$scope.donutGraphData = response.data.entity;
													var avgTime=$scope.donutGraphData.avgTime;
													var maxTime=$scope.donutGraphData.maxTime;		
													var minTime=$scope.donutGraphData.minTime;
										var chart = new Highcharts.Chart({
										 chart: {
										        type: 'pie',
										        renderTo: 'donutChartContainer',
										        options3d: {
										            enabled: true,
										            alpha: 45
										        }
										    },
										    title: {
										        text: 'Elapsed time Report'
										    },
										    colors: ['#DF775E', '#6094C6', '#B9A427'],
										    credits: {
										          enabled: false
										      },
										    subtitle: {
										        text: 'Request Success'
										    },
										    plotOptions: {
										        pie: {
										            innerSize: 100,
										            depth: 45,
										            showInLegend: true
										        }
										    
										    },
										    series: [{
										        name: 'Total Time',
										        data: [
										            ['Avg Elapsed Time', avgTime],
										            ['Max Elapsed Time', maxTime],
										            ['Min Elapsed Time', minTime]
										           
										        ]
										    }]

									  });
									})
									
							
							}
							/*Merthod for Bar Chart*/
							/*$scope.getBarChart = function(){

								$http(
								{
									url : localStorage.getItem('path') + "/GetReportData/getRequestStatusData",
									//url : "http://localhost:8023/GetAllIpamData/getAll",
									method : "GET"

								})
								.then(
										function(response) {
											console.log(response)
											$scope.requestsList = JSON
													.parse(response.data.entity.output);
											})
							
							var chart = new Highcharts.Chart({
								 chart: {
								        type: 'bar',
								        renderTo: 'barChartContainer',
								    },
								    colors: ['#DF775E', '#6094C6', '#B9A427'],
								    title: {
								        text: 'Elapsed Time bar chart'
								    },
								    xAxis: {
								        categories: ['daily', 'Weekly', 'Monthly']
								    },
								    credits: {
								          enabled: false
								      },
								    yAxis: {
								        min: 0,
								        title: {
								            text: 'Total Time consumption'
								        }
								    },
								    legend: {
								        reversed: true
								    },
								    plotOptions: {
								        series: {
								            stacking: 'normal'
								        }
								    },
								    series: [{
								        name: 'Avg Elapsed Time',
								        data: [5, 3, 4]
								    }, {
								        name: 'Max Elapsed Time',
								        data: [2, 2, 3]
								    }, {
								        name: 'Min Elapsed Time',
								        data: [3, 4, 4]
								    }]

							  });
								
							}*/
							/*Merthod for column Chart*/
							$scope.getcolumnChart = function(){
								urlbase = localStorage.getItem('path') + '/GetReportData/getStatusReportWeekly'
								var dataColChart = {},
								allObj,
								totalCount;
								dataFactory.getDataFact(urlbase)
								.then(function(response){
									
									dataColChart = JSON.parse(response.data.entity.Output);
									allObj = JSON.parse(response.data.entity.Output)
									//totalCount =10;
									totalCount = allObj[6].totalCount; 
									var chart = new Highcharts.Chart({
										 chart: {
										        type: 'column',
										        renderTo: 'columnChartContainer',
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
										    colors: [ '#90ed7d','#7cb5ec', '#ef4c4c','#ffcc00'],
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

										    }, 
										    {
										        name: dataColChart[1].Failure.name,
										        data: dataColChart[1].Failure.data

										    },
										    {
										        name: dataColChart[3].Scheduled.name,
										        data: dataColChart[3].Scheduled.data

										    },]
									 });
								});
								
								}
								
							
							$scope.getPieChart();
							//$scope.getDonutChart(); 
							$scope.getcolumnChart();
							}
							])
C3PApp.controller('iosUpdateController',
    ['$scope', '$rootScope', '$http', '$state', 'activeTabs',
        function ($scope, $rootScope, $http, $state, activeTabs) {
            $scope.ipAddress = "";
            $scope.zipcode = "";
            $scope.selarr = [];
            $scope.csr = {};
            $scope.loading = false;

            $scope.searchIPAMRecord = function () {
                console.log("In search function");
                console.log($scope.ipAddress);
                console.log($scope.zipcode);
                $scope.loading = true;
                //this.items= [{"ip":"10.1.1.1","name":"Router1","region":"US","status":0},{"ip":"10.1.1.2","name":"Router1","region":"US","status":2},{"ip":"10.1.1.3","name":"Router1","region":"US","status":1},{"ip":"10.1.1.4","name":"Router1","region":"US","status":2},{"ip":"10.1.1.5","name":"Router1","region":"US","status":2}];
                $http(
                    {
                        url: "http://localhost:8024/GetAllXMLData/get?ipaddress=" + $scope.ipAddress + "&zipcode=" + $scope.zipcode,
                        //url : "http://localhost:8023/SearchAllIpamData/search",
                        method: "GET"
                    })
                    .then(
                        function (response) {
                            console.log(JSON.parse(response.data.output));
                            $scope.loading = false;
                            $scope.csr = JSON.parse(response.data.output);
                        })

            }

            $scope.checkbox = function (item) {
                if ($scope.selarr.find(x => x == item)) {
                    $scope.selarr.splice($scope.selarr.indexOf(item), 1);

                }
                else {
                    $scope.selarr.push(item);

                }
                console.log($scope.selarr);
            }

            $scope.upgradeOSversion = function () {
                console.log("Inside upgradeOSversion");
                $scope.loading = true;
                for (router in $scope.selarr) {
                    console.log($scope.selarr[router]);
                    var Data = {
                        "c3p_interface": {
                            "ip": "",
                            "mask": "",
                            "name": "",
                            "encapsulation": "",
                            "speed": "",
                            "bandwidth": ""
                        },
                        "internetLcVrf": {
                            "networkIp_subnetMask": "",
                            "networkIp": "",
                            "customerNo": ""
                        },
                        "certificationOptionListFlags": {
                            "Interfaces status": 1,
                            "Platform & IOS": 1,
                            "WAN Interface": 0,
                            "BGP neighbor": 0,
                            "Throughput": 0,
                            "FrameLoss": 1,
                            "Latency": 1
                        },
                        "templateId": "USCI7200IO12.4_V1.0",
                        "importFile": "1",
                        "networkType": $scope.selarr[router].networktype,
                        "customer": "sdf",
                        "siteid": "sdf",
                        "region": $scope.selarr[router].region,
                        "os": $scope.selarr[router].os,
                        "deviceType": "Router",
                        "model": $scope.selarr[router].model,
                        "osVersion": $scope.selarr[router].version,
                        "vendor": $scope.selarr[router].vendor,
                        "service": $scope.selarr[router].service,
                        "hostname": $scope.selarr[router].name,
                        "lanTnterface": "",
                        "loopBackType": "",
                        "enablePassword": $scope.selarr[router].password,
                        "managementIp": $scope.selarr[router].ip,
                        "requestType": "IOSUPGRADE"
                    };
                    console.log(Data);
                    console.log(JSON.stringify(Data));
                    $http(
                        {
                            url: "http://localhost:8080/C3P/ConfigMngmntService/createConfigurationDcm",
                            data: JSON.stringify(Data),
                            method: "POST",
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        })
                        .then(
                            function (response) {
                                
                                $scope.loading = false;
                                
                                bootbox.confirm({
                                    title : 'IOS update',
                                    message : "Request "+ response.data.output,
                                    buttons : {
                                            
                                        confirm : {
                                            label : 'Ok',
                                            className : 'btn-default'
                                        },

                                    },
                                    callback : function(result) {
                                        if (result)
                                        $state.go('Dashboard');
                                        
                                       
                                    }
                                });
                                
                            })
                }
            }
        }])
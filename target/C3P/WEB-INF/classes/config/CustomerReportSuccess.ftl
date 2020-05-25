
<br>
<br>
<h4><b>Device ${(configRequest.hostname)!""} is successfully certified</b></h4>
<br>
<h5><b><u>Device details</u>:</b></h5>
<div class="row">
<div class="col-lg-6">
<div class="col-lg-5"><b>Customer Name:</b></div>
<div class="col-lg-7">${(configRequest.customer)!""}</div>
<div class="col-lg-5"><b>Hostname:</b></div>
<div class="col-lg-7">${(configRequest.hostname)!""}</div>
<div class="col-lg-5"><b>Device Model:</b></div>

<div class="col-lg-7">${(configRequest.model)!""}</div>

</div>

<div class="col-lg-6"> 
<div class="col-lg-6"><b>Site ID:</b></div>
<div class="col-lg-6">${(configRequest.siteid)!""}</div>
<div class="col-lg-6"><b>Management Address:</b></div>
<div class="col-lg-6">${(configRequest.managementIp)!""} </div>
                                  				
</div>
</div>
<br>
<br>


<h5><b><u>Test summary</u>:</b></h5><br>
<b style="font-size: 14px;"><u>Configuration generated </u>:</b>${(configRequest.generate_config)!""}
<br><br>

<h5><b><u>Device Pre-test</u>:</b></h5>
	<div class="row">
         <div class="col-lg-3"><b>Device Reachability test:</b></div>
	     <div class="col-lg-9"> ${(configRequest.deviceReachabilityTest)!""}</div>
 	     <div class="col-lg-3"><b>Vendor test:</b></div>
	     <div class="col-lg-9"> ${(configRequest.vendorTest)!""}</div>
 	     <div class="col-lg-3"><b>Device Model test:</b></div>
	     <div class="col-lg-9"> ${(configRequest.deviceModelTest)!""}</div>
 	     <div class="col-lg-3"><b>OS test:</b></div>
	     <div class="col-lg-9"> ${(configRequest.iosVersionTest)!""}</div>
	</div>
        	
<br>
<b style="font-size: 14px;"><u>Configuration Delivered</u>:</b>${(configRequest.deliever_config)!""}<br><br><br>


     

<h5><b><u>Network test</u>: </b></h5>

	<div class="row">
	      <div class="col-lg-3"><b>Interfaces status:</b></div>
	 <div class="col-lg-9"> 
	   	<#if ((configRequest.network_test_interfaceStatus)?exists && (configRequest.network_test_interfaceStatus) == "Passed")>
			${(configRequest.networkStatusValue)!""}/${(configRequest.networkProtocolValue)!""} &nbsp; &nbsp; &nbsp; &nbsp; ${(configRequest.network_test_interfaceStatus)!""}
		</#if>
		<#if ((configRequest.network_test_interfaceStatus)?exists && (configRequest.network_test_interfaceStatus) == "Not Conducted")>
 		&nbsp; ${(configRequest.network_test_interfaceStatus)!""}
		</#if>
	 </div>
 	 <div class="col-lg-3"><b>WAN Interface:</b></div>
	 <div class="col-lg-9"> 
	   	<#if ((configRequest.network_test_wanInterface)?exists && (configRequest.network_test_wanInterface) == "Passed")>
			 &nbsp; &nbsp; &nbsp; &nbsp; ${(configRequest.network_test_wanInterface)!""}
		</#if>
		<#if ((configRequest.network_test_wanInterface)?exists && (configRequest.network_test_wanInterface) == "Not Conducted")>
 		&nbsp; ${(configRequest.network_test_wanInterface)!""}
		</#if>
	 </div>
 	<div class="col-lg-3"><b>Platform & IOS:</b></div>
	 <div class="col-lg-9"> 
	   	<#if ((configRequest.network_test_platformIOS)?exists && (configRequest.network_test_platformIOS) == "Passed")>
			 &nbsp; &nbsp; &nbsp; &nbsp; ${(configRequest.network_test_platformIOS)!""}
		</#if>
		<#if ((configRequest.network_test_platformIOS)?exists && (configRequest.network_test_platformIOS) == "Not Conducted")>
 		&nbsp; ${(configRequest.network_test_platformIOS)!""}
		</#if>
	 </div>
	 <div class="col-lg-3"><b>BGP Neighbor:</b></div>
	 <div class="col-lg-9"> 
	   	<#if ((configRequest.network_test_BGPNeighbor)?exists && (configRequest.network_test_BGPNeighbor) == "Passed")>
			 &nbsp; &nbsp; &nbsp; &nbsp; ${(configRequest.network_test_BGPNeighbor)!""}
		</#if>
		<#if ((configRequest.network_test_BGPNeighbor)?exists && (configRequest.network_test_BGPNeighbor) == "Not Conducted")>
 		&nbsp; ${(configRequest.network_test_BGPNeighbor)!""}
		</#if>
	 </div>
	</div>

<h5><b><u>HealthCheck test</u>: </b></h5>

	<div class="row">
	      <div class="col-lg-3"><b>Throughput test:</b></div>
	 <div class="col-lg-9"> 
	   	<#if ((configRequest.throughput)?exists && (configRequest.throughput) != "-1")>
			${(configRequest.throughput)!""}Kbps &nbsp; ${(configRequest.health_checkup)!""}
		</#if>
		<#if ((configRequest.throughput)?exists && (configRequest.throughput) == "-1")>
 		&nbsp; Not Conducted
		</#if>
	 </div>
 	 <div class="col-lg-3"><b>Frameloss test:</b></div>
	 <div class="col-lg-9">
		<#if ((configRequest.frameLoss)?exists && (configRequest.frameLoss) != "-1")>
			${(configRequest.frameLoss)!""}%&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;Passed
		</#if>
		<#if ((configRequest.frameLoss)?exists && (configRequest.frameLoss) == "-1")>
			&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;Not Conducted
		</#if>
	</div>
 	<div class="col-lg-3"><b>Latency test:</b></div>
	<div class="col-lg-9"> 
	     <#if ((configRequest.latency)?exists && (configRequest.latency) != "-1")>
			${(configRequest.latency)!""}ms&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;Passed
		 </#if>
		 <#if ((configRequest.latency)?exists && (configRequest.latency) == "-1")>
			&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;Not Conducted
		 </#if>
	     </div>
	</div>


<br>
<h5><b><u>Suggestions</u>:</b></h5>
NA
<br><br><br>





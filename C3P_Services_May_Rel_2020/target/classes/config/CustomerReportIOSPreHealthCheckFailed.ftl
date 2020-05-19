
<br>
<br>
<h4><b>Device ${(configRequest.hostname)!""} could not be upgraded</b></h4>
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


<h5><b><u>Device Pre-test</u>:</b></h5>
	<div class="row">
 	     <div class="col-lg-3"><b>CPU Usage:</b></div>
	     <div class="col-lg-9"> ${(configRequest.pre_cpu_usage_percentage)!""}</div>
 	     <div class="col-lg-3"><b>Memory usage:</b></div>
	     <div class="col-lg-9"> ${(configRequest.pre_memory_info)!""}</div>
 	     <div class="col-lg-3"><b>Power Information:</b></div>
	     <div class="col-lg-9"> ${(configRequest.pre_power_info)!""}</div>
	</div>
        	
<br>
<br><br><br>





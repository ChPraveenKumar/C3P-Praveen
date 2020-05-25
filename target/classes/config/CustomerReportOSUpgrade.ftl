
<br>

<br>
<h4><b>Device ${(configRequest.hostname)!""} is successfully upgraded</b></h4>
<br>
<br>
<h5><b><u>Service Request Details</u>:</b></h5>

<div class="row">
<div class="col-lg-6">
<div class="col-lg-5"><b>Customer Name:</b></div>
<div class="col-lg-7">${(configRequest.customer)!""}</div>
<div class="col-lg-5"><b>Region:</b></div>
<div class="col-lg-7">${(configRequest.region)!""}</div>
<div class="col-lg-5"><b>Zip code:</b></div>
<div class="col-lg-7">${(configRequest.zipcode)!""}</div>
<div class="col-lg-5"><b>Hostname:</b></div>
<div class="col-lg-7">${(configRequest.hostname)!""}</div>
<div class="col-lg-5"><b>Vendor:</b></div>
<div class="col-lg-7">${(configRequest.vendor)!""}</div>
<div class="col-lg-5"><b>Service:</b></div>
<div class="col-lg-7">${(configRequest.service)!""}</div>
<div class="col-lg-5"><b>OS:</b></div>
<div class="col-lg-7">${(configRequest.os)!""}</div>
</div>

<div class="col-lg-6"> 
<div class="col-lg-6"><b>Site ID:</b></div>
<div class="col-lg-6">${(configRequest.siteid)!""} </div>
<div class="col-lg-6"><b>Management Address:</b></div>
<div class="col-lg-6">${(configRequest.managementIp)!""} </div>
<div class="col-lg-6"><b>Downtime Required:</b></div>
<div class="col-lg-6">${(configRequest.downTimeRequired)!""} </div>
<div class="col-lg-6"><b>Last Upgraded On:</b></div>
<div class="col-lg-6">${(configRequest.lastUpgradedOn)!""} </div>
<div class="col-lg-6"><b>Managed:</b></div>
<div class="col-lg-6">${(configRequest.managed)!""} </div>
</div>
</div>
<br>
<br>
<h5><b><u>OS Upgrade Summary</u>:</b></h5><br>
<br>
<table class="preValidatTable" style="width:100%">
  <tr bgcolor="#BEBEBE">
    <th>Step</th>
    <th>Status</th>
  </tr>
  <tr>
    <td>Login</td>
    <td>${(configRequest.os_upgrade_dilevary_login_flag)!""}</td>
  </tr>
  <tr>
    <td>Flash size availability</td>
    <td>${(configRequest.os_upgrade_dilevary_flash_size_flag)!""}</td>
  </tr>
  <tr>
    <td>Back up</td>
    <td>${(configRequest.os_upgrade_dilevary_backup_flag)!""}</td>
  </tr>
  <tr>
    <td>OS Download</td>
    <td>${(configRequest.os_upgrade_dilevary_os_download_flag)!""}</td>
  </tr>
   <tr>
    <td>Boot system flash</td>
    <td>${(configRequest.os_upgrade_dilevary_boot_system_flash_flag)!""}</td>
  </tr>
   <tr>
    <td>Reload</td>
    <td>${(configRequest.os_upgrade_dilevary_reload_flag)!""}</td>
  </tr>
   <tr>
    <td>Post login</td>
    <td>${(configRequest.os_upgrade_dilevary_post_login_flag)!""}</td>
  </tr>
</table>
<br>


<h5><b><u>Health Check Summary</u>:</b></h5><br>
<br>
<table class="preValidatTable" style="width:100%">
  <tr bgcolor="#BEBEBE">
    <th>Previous Version</th>
    <th>Current Version</th>
  </tr>
  <tr>
    <td>${(configRequest.pre_version_info)!""}</td>
    <td>${(configRequest.post_version_info)!""}</td>
  </tr>
 
</table>
<br>
<table class="preValidatTable" style="width:100%">
  <tr bgcolor="#BEBEBE">
    <th>Health Check</th>
    <th>Pre Upgrade Value</th>
    <th>Post Upgrade Vlaue</th> 
    <th> Outcome </th>
  </tr>
  <tr>
  <td>CPU Usage</td>
    <td>${(configRequest.pre_cpu_usage_percentage)!""}</td>
    <td>${(configRequest.post_cpu_usage_percentage)!""}</td>
    <td>Pass</td>
  </tr>
  <tr>
   <td>Memory usage (%)</td>
    <td>${(configRequest.pre_memory_info)!""}</td>
    <td>${(configRequest.post_memory_info)!""}</td>
    <td>Pass</td>
  </tr>
  <tr>
   <td>Power Information</td>
    <td>${(configRequest.pre_power_info)!""}</td>
    <td>${(configRequest.post_power_info)!""}</td>
    <td>Pass</td>
  </tr>
  
</table>
<br>
<br><br>
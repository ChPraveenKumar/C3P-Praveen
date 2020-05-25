<h4><b>Device Pre-Test report</b></h4> <br>
<h5><b>Device Reachability test:${(preValidateTest.deviceReachableStatus)!""}</b></h5><br>

<table class="preValidatTable" style="width:100%">
  <tr bgcolor="#BEBEBE">
    <th>CPE Parameter</th>
    <th>User Input</th>
    <th>CPE Value </th> 
    <th>Status</th>
  </tr>
  <tr>
  <td>Vendor</td>
    <td>${(preValidateTest.vendorGUIValue)!""}</td>
    <td>${(preValidateTest.vendorActualValue)!""}</td>
    <td>${(preValidateTest.vendorTestStatus)!""}</td>
  </tr>
  <tr>
   <td>Device Model</td>
    <td>${(preValidateTest.modelGUIValue)!""}</td>
    <td>${(preValidateTest.modelActualValue)!""}</td>
    <td>${(preValidateTest.modelTestStatus)!""}</td>
  </tr>
  <tr>
   <td>IOS Version</td>
    <td>${(preValidateTest.osVersionGUIValue)!""}</td>
    <td>${(preValidateTest.osVersionActualValue)!""}</td>
    <td>${(preValidateTest.osVersionTestStatus)!""}</td>
  </tr>
</table>
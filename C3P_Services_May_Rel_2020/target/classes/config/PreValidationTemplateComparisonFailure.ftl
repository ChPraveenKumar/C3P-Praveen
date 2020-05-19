<h4><b>Device Pre-Test report</b></h4> <br>
<h5><b>Device Reachability test:</b></h5>Pass<br>

<table class="preValidatTable" style="width:100%">
  <tr bgcolor="#BEBEBE">
    <th>Field Name</th>
    <th>UI value</th>
    <th>Router Value</th> 
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
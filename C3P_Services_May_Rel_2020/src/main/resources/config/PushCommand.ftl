config t
version ${(configRequest.osVersion)!""}
service nagle
no service pad
service tcp-keepalives-in
service timestamps debug datetime localtime show-timezone msec
service timestamps log datetime localtime show-timezone msec
service password-encryption
no service finger
no service udp-small-servers
no service tcp-small-servers
no service config
service compress-config
!
hostname ${(configRequest.hostname)!""}
!
boot-start-marker
boot-end-marker
!
logging buffered 32768
<#if ((configRequest.enablePassword)?exists && (configRequest.enablePassword) != "")>
enable secret ${(configRequest.enablePassword)!""}
</#if>
!
no aaa new-model
memory-size iomem 32
!
no ipv6 unicast-routing
no ipv6 cef
!
ip subnet-zero
no ip source-route
ip routing
no ip finger
!
no ip domain lookup
ip cef
multilink bundle-name authenticated
!
<#if ((configRequest.loopBackType)?exists && (configRequest.loopBackType) != "")>
logging source-interface ${(configRequest.loopBackType)!""}
ip tftp source-interface ${(configRequest.loopBackType)!""}
ip ftp source-interface ${(configRequest.loopBackType)!""}
!
</#if>
buffers fastEthernet0/0 permane	nt 8192
!
!
<#if ((configRequest.vrfName)?exists && (configRequest.vrfName) != "")>
ip vrf ${(configRequest.vrfName)!""}
description TestVRF
rd ${(configRequest.bgpASNumber)!""}:700
  route-target export ${(configRequest.bgpASNumber)!""}:200
  route-target import ${(configRequest.bgpASNumber)!""}:100
!
  route-target export ${(configRequest.bgpASNumber)!""}:400
  route-target import ${(configRequest.bgpASNumber)!""}:300
exit
!
</#if>
<#if ((configRequest.snmpHostAddress)?exists && (configRequest.snmpHostAddress) != "")>
access-list 26 remark Customer hosts allowed for SNMP read-only
access-list 26 permit ${(configRequest.snmpHostAddress)!""}
access-list 26 deny any log
!
</#if>
<#if ((configRequest.routingProtocol)?exists && (configRequest.routingProtocol) != "")>
ip access-list standard green			
permit 172.125.248.0 0.0.3.255
permit 192.235.79.0 0.0.0.255
permit 192.235.85.0 0.0.0.255
permit 192.235.89.0 0.0.0.255
permit 192.235.97.0 0.0.0.255
permit 192.235.108.0 0.0.0.255
permit 192.235.123.0 0.0.0.255
permit 192.235.125.0 0.0.0.255
permit 10.69.182.128 0.0.0.63
permit 10.69.183.0 0.0.0.127
!			
ip access-list standard grey
permit 10.92.0.0 0.3.255.255
permit 10.68.0.0 0.3.255.255
permit 172.96.0.0 0.0.255.255
permit 172.170.0.0 0.0.255.255
permit 192.235.64.0 0.0.63.255
permit 192.47.0.0 0.0.255.255
permit 192.108.96.0 0.0.15.255
permit 192.61.0.0 0.0.255.255
permit 192.208.0.0 0.1.255.255
permit 10.59.128.0 0.0.31.255
permit 10.59.160.0 0.0.15.255
permit 10.59.176.0 0.0.3.255
permit 10.59.180.0 0.0.1.255
permit 10.59.183.0 0.0.0.255
permit 10.59.184.0 0.0.3.255
permit 10.59.188.0 0.0.1.255
!
ip access-list standard red
deny any
!
</#if>
ip access-list extended MIS_LC_NAT_POOL
permit ip 11.170.47.0 0.0.0.255 any
exit			
!
<#if ((configRequest.routingProtocol)?exists && (configRequest.routingProtocol) != "")>			
route-map bgp-to-igp permit 10
match ip address green
!
route-map bgp-to-igp deny 30
match ip address grey
!
route-map bgp-to-igp deny 40
match ip address red
!
route-map bgp-to-igp permit 50
!
route-map igp-to-bgp permit 10
exit
!

</#if>
<#if ((configRequest.loopBackType)?exists && (configRequest.loopBackType) == "Loopback0")>
interface ${(configRequest.loopBackType)!""}
description Connection to Grey VPN
ip address ${(configRequest.loopbackIPaddress)!""} ${(configRequest.loopbackSubnetMask)!""}
no ip directed-broadcast
no ip mask-reply
no ip redirects
no ip proxy-arp
!
!
</#if>
<#if ((configRequest.name)?exists && (configRequest.name) == "GigabitEthernet 0/0")>
interface GigabitEthernet 0/0
<#if ((configRequest.description)?exists && (configRequest.description) != "")>	
description ${(configRequest.description)!""}
</#if>
ip address ${(configRequest.ip)!""} ${(configRequest.mask)!""}
load-interval 30
no ip redirects
no ip unreachables
no ip proxy-arp
duplex full
<#if (configRequest.speed)??>
speed ${(configRequest.speed)}
</#if>
no shutdown
!
</#if>
<#if ((configRequest.lanInterface)?exists && (configRequest.lanInterface) == "GigabitEthernet2/0")>
interface ${(configRequest.lanInterface)!""}
<#if (configRequest.lanDescription)??>	
description ${(configRequest.lanDescription)!""}
</#if>
ip address ${(configRequest.lanIp)!""} ${(configRequest.lanMaskAddress)!""}
no shutdown
negotiation auto
!
</#if>
<#if ((configRequest.name)?exists && (configRequest.name) == "Serial0/0/1")>
interface ${(configRequest.name)!""}
<#if (configRequest.description)??>
description ${(configRequest.description)!""}
</#if>
ip address ${(configRequest.ip)!""}  ${(configRequest.mask)!""}
<#if (configRequest.bandwidth)??>
bandwidth  ${(configRequest.bandwidth)}
</#if>
encapsulation ${(configRequest.encapsulation)!""}
serial restart-delay 0
exit
!
</#if>

<#if ((configRequest.name)?exists && (configRequest.name) == "Serial1/0")>
interface ${(configRequest.name)!""}
<#if (configRequest.description)??>
description ${(configRequest.description)!""}
</#if>
ip address ${(configRequest.ip)!""}  ${(configRequest.mask)!""}
<#if (configRequest.bandwidth)??>
bandwidth  ${(configRequest.bandwidth)}
</#if>
encapsulation ${(configRequest.encapsulation)!""}
serial restart-delay 0
exit
!
</#if>
<#if ((configRequest.routingProtocol)?exists && (configRequest.routingProtocol) != "")>
router bgp ${(configRequest.bgpASNumber)!""}
bgp router-id 123.1.1.1
bgp log-neighbor-changes
no bgp default ipv4-unicast
redistribute connected			
bgp log-neighbor-changes
  network ${(configRequest.networkIp)!""} mask ${(configRequest.networkIp_subnetMask)!""}
  neighbor ${(configRequest.neighbor1)!""} remote-as ${(configRequest.neighbor1_remoteAS)!""}
  neighbor ${(configRequest.neighbor1)!""} ebgp-multihop 3
   neighbor ${(configRequest.neighbor1)!""} password 0 testpassword
  no neighbor ${(configRequest.neighbor1)!""} shutdown
  no auto-summary
  no synchronization
  neighbor ${(configRequest.neighbor2)!""} description *** MIS LC ***
  neighbor ${(configRequest.neighbor2)!""} remote-as ${(configRequest.neighbor2_remoteAS)!""}		
  neighbor ${(configRequest.neighbor2)!""} route-map bgp-to-igp out			
  no neighbor ${(configRequest.neighbor2)!""} shutdown
exit
!
</#if>
ip nat translation tcp-timeout 360
ip nat translation udp-timeout 360
!
!
!
!
!
exit
!
ip forward-protocol nd
!
ip classless
ip bgp-community new-format
no ip http server
!
logging trap debugging
dialer-list 1 protocol ip permit
!
no cdp run
!
<#if ((configRequest.snmpHostAddress)?exists && (configRequest.snmpHostAddress) != "")>
snmp-server view clientview system included
snmp-server view clientview interfaces included
snmp-server view clientview ip included
snmp-server view clientview ip.12 excluded
snmp-server view clientview ip.21 excluded
snmp-server view clientview ip.23 excluded
snmp-server view clientview ip.24 excluded
snmp-server view clientview lsystem included
snmp-server view clientview chassis included
snmp-server view clientview lsystem.5 excluded
snmp-server view clientview lsystem.6 excluded
snmp-server view clientview lsystem.48 excluded
snmp-server view clientview lsystem.49 excluded
snmp-server view clientview lsystem.51 excluded
snmp-server view clientview lsystem.52 excluded
snmp-server view clientview lsystem.70 excluded
snmp-server view clientview lsystem.71 excluded
snmp-server view clientview lsystem.72 excluded
snmp-server view clientview lsystem.73 excluded
snmp-server view clientview lsystem.74 excluded
snmp-server view clientview lifEntry included
snmp-server view clientview ifMIB included
snmp-server view clientview cpmCPUTotalTable.1 included
snmp-server view clientview ciscoMemoryPoolMIB.1.1 included
				
snmp-server community ${(configRequest.snmpString)!""} view clientview RO 26
!
</#if>
<#if ((configRequest.banner)?exists && (configRequest.banner) != "")>
banner login ^
${(configRequest.banner)!""}
^ 
banner motd ^ ABCD_20150722_1_initial^
!
control-plane
exit
!
!
</#if>
line con 0
exec-timeout 0 0
escape-character BREAK
stopbits 1
exit
line aux 0
exec-timeout 0 0
escape-character BREAK
stopbits 1
exit
line vty 0 4
session-timeout 30
exec-timeout 999 0
password 7 045802150C2E
  length 0
transport input ssh
escape-character BREAK
exit
line vty 5 15
exec-timeout 0 0
length 0
transport input ssh
exit
!
scheduler allocate 20000 1000
!
end

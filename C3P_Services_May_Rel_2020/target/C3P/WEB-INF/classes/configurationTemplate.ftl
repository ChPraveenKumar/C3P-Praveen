! Please use ${(configRequest.release)!""} : ${(configRequest.deviceName)!""}${(configRequest.model)!""}-universalk9-mz.SPA.154-3.M1.bin
! *** Router ${(configRequest.deviceName)!""}${(configRequest.model)!""} (Single), region: ${(configRequest.region)!""}, service: ${(configRequest.service)!""}
version ${(configRequest.version)!""} 
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
enable secret ${(configRequest.secret)!""}
!
no aaa new-model
memory-size iomem 15
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
crypto pki token default removal timeout 0
!
license udi pid CISCO1921/K9 sn FTX16258047
license accept end user agreement
license boot module c1900 technology-package datak9
!
ip telnet source-interface Loopback1
ip ftp source-interface Loopback1
ip tftp source-interface Loopback1
!
 buffers fastswitching permanent 8192
!
!
vrf definition ${(configRequest.vrf)!""}
 description *** Internet LC VPN ****
 rd ${(configRequest.internetLcVrf.routerBgp65K)!""}
 route-target export ${(configRequest.internetLcVrf.routerBgp65K)!""}
 route-target import ${(configRequest.internetLcVrf.routerBgp65K)!""}
 !
 address-family ipv4
  route-target export ${(configRequest.internetLcVrf.routerBgp65K)!""}
  route-target import ${(configRequest.internetLcVrf.routerBgp65K)!""}
 exit-address-family
!

<#list configRequest.interface as interFace>
<#if ((interFace.name)?exists && (interFace.name) == "Loopback1")>
interface Loopback1
description ${(interFace.description)!""}
ip address ${(interFace.ip)!""} ${(interFace.mask)!""}
!
</#if>
<#if ((interFace.name)?exists && (interFace.name) == "Loopback14")>
interface Loopback14
 description ${(interFace.description)!""}
 vrf forwarding ${(configRequest.vrf)!""}
 ip address ${(interFace.ip)!""} ${(interFace.mask)!""}
!
</#if>
<#if ((interFace.name)?exists && (interFace.name) == "GigabitEthernet 0/0")>
interface GigabitEthernet 0/0
description ${(interFace.description)!""}
ip address ${(interFace.ip)!""} ${(interFace.mask)!""}
load-interval 30
no ip redirects
no ip unreachables
no ip proxy-arp
duplex full
<#if (interFace.speed)??>
speed ${(interFace.speed)}
</#if>
no shutdown
!
</#if>
<#if ((interFace.name)?exists && (interFace.name)?contains("GigabitEthernet 0/0."))>
interface GigabitEthernet 0/0.100
description ${(interFace.description)!""}
<#if (interFace.encapsulation)??>
encapsulation ${(interface.encapsulation)!""}
</#if>
vrf forwarding ${(configRequest.vrf)!""}
ip nat outside
ip unnumbered Loopback14
ip address 1 
ip access-group in
no ip unreachables
no ip proxy-arp
no cdp enable
no shutdown
!
</#if>
<#if ((interFace.name)?exists && (interFace.name)?contains("GigabitEthernet 0/1"))>
interface GigabitEthernet0/1
 description ${(interFace.description)!""}
 vrf forwarding ${(configRequest.vrf)!""}
 ip address ${(interFace.ip)!""} ${(interFace.mask)!""}
 ip nat inside
 ip virtual-reassembly in
 ipv6 enable
!
</#if>
</#list>

<#if (configRequest.internetLcVrf.routerBgp65K)??>
router bgp ${(configRequest.ASNumber)!""}
bgp router-id ${(configRequest.internetLcVrf.routerBgp65K)!""}
bgp log-neighbor-changes
no bgp default ipv4-unicast
address-family ipv4 vrf ${(configRequest.vrf)!""}
  bgp router-id ${(configRequest.internetLcVrf.routerBgp65K)!""}
  network ${(configRequest.internetLcVrf.networkIp)!""} mask 255.255.255.255
  neighbor ${(configRequest.internetLcVrf.neighbor1)!""} remote-as ${(configRequest.internetLcVrf.remotePort)!""}
  neighbor ${(configRequest.internetLcVrf.neighbor2)!""} description *** MIS LC ***
  neighbor ${(configRequest.internetLcVrf.neighbor3)!""} ebgp-multihop 3
  neighbor ${(configRequest.internetLcVrf.neighbor4)!""} activate
exit-address-family
!
</#if>
ip route vrf ${(configRequest.vrf)!""} ${(configRequest.misArPe.routerVrfVpnDIp)!""} ${(configRequest.misArPe.routerVrfVpnDGateway)!""} FastEthernet0/1/1.5 ${(configRequest.misArPe.fastEthernetIp)!""}

!
ip nat translation tcp-timeout 360
ip nat translation udp-timeout 360
!
!
!
!
!
!
ip forward-protocol nd
!
 ip classless 
ip bgp-community new-format
no ip http server
no ip http secure-server
!
logging trap debugging
dialer-list 1 protocol ip permit
!
no cdp run
 !
banner login ^
${(configRequest.banner)!""}
^ 
banner motd ^ ABCD_20150722_1_initial^
!
control-plane
!
line con 0
exec-timeout <line-con-exec-timeout> 0
password <line-con-pwd>
login
escape-character 3
line aux 0
exec-timeout 15 0
password temp
login
modem Dialin
transport input none
escape-character 3
speed 9600
line 2
no activation-character
no exec
transport preferred none
transport input all
stopbits 1
!
line vty null null
exec-timeout null 0
password null
login
transport input null
escape-character 3
!
scheduler allocate 20000 1000
end
conf t
Interface GigabitEthernet2/0
Ip address 20.0.0.2 255.255.255.0
Speed 1000
No shutdown
exit

Ip domain-name test.com

Ip ssh version 2
Crypto key generate rsa
1024
Username cisco privilage 15 secrete 0 cisco
Line vty 0 4
Transport input ssh
Login local
exit
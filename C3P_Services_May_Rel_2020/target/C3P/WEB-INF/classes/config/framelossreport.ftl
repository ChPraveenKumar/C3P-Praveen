----------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------

Frame loss Test Parameters

----------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------


RequestID					:${(configRequest.release)!""}
Protocol					:IP
TargetIPaddress				:<#list configRequest.interface as interFace>${(interFace.ip)!""}</#list>
TrialDurationInSeconds		:120
BandwidthGranularityInMbp	:10
SourceAddress				:10.10.52.1
TypeOfService				:0
FrameSize					:512

----------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------

Frame loss Test Response

----------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------

RequestID					:${(configRequest.release)!""}

Test 1 Response

	Throughput				:1000Mbps
	TxPackets				:100
	RxPackets				:14
	LossPackets				:86
	LossPercentage			:86
	
Test 2 Response

	Throughput				:900Mbps
	TxPackets				:100
	RxPackets				:26
	LossPackets				:74
	LossPercentage			:74

Conclusion: Frame loss is 86% at 1000Mbps and 74% at 900Mbps.

----------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------

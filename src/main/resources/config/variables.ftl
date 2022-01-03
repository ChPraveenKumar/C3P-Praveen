variable "cloudParams" {
  type = map
  default = {
        "cloudPlatform": "${(cloudPojo.cloudPlatform)!""}"
        "cloudProject": "${(cloudPojo.cloudProject)!""}"
  }
}

variable "cloudCusterDetails" {
  type = map
  default = {
        "clusterName":  "${(cloudPojo.clusterName)!""}"
        "clusterNetworkName": "${(cloudPojo.clusterNetworkName)!""}"
        "clusterNodePoolName": "${(cloudPojo.clusterNodePoolName)!""}"
        "machineType": "${(cloudPojo.machineType)!""}"
        "diskSize": "${(cloudPojo.diskSize)!""}"
        "numberOfNodes": "${(cloudPojo.numberOfNodes)!""}"
        "clusterLocation": "${(cloudPojo.clusterLocation)!""}"
        "clusterId": ""
  }
}
variable "cloudPodDetails" {
  type = map
  default = {
        "podName": "${(cloudPojo.podName)!""}"
        "podClusterName": "${(cloudPojo.podClusterName)!""}"
        "podClusterId": ""
        "numberOfPods": "${(cloudPojo.numberOfPods)!""}"
        "podImagename": "${(cloudPojo.podImagename)!""}"
        "podNamespace": "${(cloudPojo.podNamespace)!""}"
  }
}
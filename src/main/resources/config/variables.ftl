variable "var" {
  default = {
    cloudParams = {
        cloudPlatform: "${(cloudPojo.cloudPlatform)!""}"
        cloudProject: "${(cloudPojo.cloudProject)!""}"
    }
    cloudCusterDetails = {
        clusterName: "${(cloudPojo.clusterName)!""}"
        clusterNetworkName: "${(cloudPojo.clusterNetworkName)!""}"
        clusterNodePoolName: "${(cloudPojo.clusterNodePoolName)!""}"
        machineType: "${(cloudPojo.machineType)!""}"
        diskSize: "${(cloudPojo.diskSize)!""}"
        numberOfNodes: "${(cloudPojo.numberOfNodes)!""}"
        clusterLocation: "${(cloudPojo.clusterLocation)!""}"
        clusterId: ""
    }
    cloudPodDetails = {
        podName: "${(cloudPojo.podName)!""}"
        podClusterName: "${(cloudPojo.podClusterName)!""}"
        podClusterId: ""
        numberOfPods: "${(cloudPojo.numberOfPods)!""}"
        podImagename: "${(cloudPojo.podImagename)!""}"
        podNamespace: "${(cloudPojo.podNamespace)!""}"
    }
  }
}
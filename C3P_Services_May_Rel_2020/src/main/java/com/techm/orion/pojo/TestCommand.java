package com.techm.orion.pojo;

import java.io.Serializable;

public class TestCommand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1329863860361346988L;
	String RequestID,Protocol,TargetIPaddress,RepeatCount,DatagramSize,TimeoutInSeconds,ExtendedCommands,SourceAddress,TypeOfService,SetDFBitInIPHeader,ValidateReplyData,DataPattern,SweepRangeOfSizes;
	String TrialDurationInSecond,NoOfTrials,LatencyThresholdInMicroseconds,FrameLossTolerance,MeasurementAccuracyInMbps,BandwidthGranularityInMbps;
	public String getRequestID() {
		return RequestID;
	}
	public void setRequestID(String requestID) {
		RequestID = requestID;
	}
	public String getProtocol() {
		return Protocol;
	}
	public void setProtocol(String protocol) {
		Protocol = protocol;
	}
	public String getTargetIPaddress() {
		return TargetIPaddress;
	}
	public void setTargetIPaddress(String targetIPaddress) {
		TargetIPaddress = targetIPaddress;
	}
	public String getRepeatCount() {
		return RepeatCount;
	}
	public void setRepeatCount(String repeatCount) {
		RepeatCount = repeatCount;
	}
	public String getDatagramSize() {
		return DatagramSize;
	}
	public void setDatagramSize(String datagramSize) {
		DatagramSize = datagramSize;
	}
	public String getTimeoutInSeconds() {
		return TimeoutInSeconds;
	}
	public void setTimeoutInSeconds(String timeoutInSeconds) {
		TimeoutInSeconds = timeoutInSeconds;
	}
	public String getExtendedCommands() {
		return ExtendedCommands;
	}
	public void setExtendedCommands(String extendedCommands) {
		ExtendedCommands = extendedCommands;
	}
	public String getSourceAddress() {
		return SourceAddress;
	}
	public void setSourceAddress(String sourceAddress) {
		SourceAddress = sourceAddress;
	}
	public String getTypeOfService() {
		return TypeOfService;
	}
	public void setTypeOfService(String typeOfService) {
		TypeOfService = typeOfService;
	}
	public String getSetDFBitInIPHeader() {
		return SetDFBitInIPHeader;
	}
	public void setSetDFBitInIPHeader(String setDFBitInIPHeader) {
		SetDFBitInIPHeader = setDFBitInIPHeader;
	}
	public String getValidateReplyData() {
		return ValidateReplyData;
	}
	public void setValidateReplyData(String validateReplyData) {
		ValidateReplyData = validateReplyData;
	}
	public String getDataPattern() {
		return DataPattern;
	}
	public void setDataPattern(String dataPattern) {
		DataPattern = dataPattern;
	}
	public String getSweepRangeOfSizes() {
		return SweepRangeOfSizes;
	}
	public void setSweepRangeOfSizes(String sweepRangeOfSizes) {
		SweepRangeOfSizes = sweepRangeOfSizes;
	}
	public String getTrialDurationInSecond() {
		return TrialDurationInSecond;
	}
	public void setTrialDurationInSecond(String trialDurationInSecond) {
		TrialDurationInSecond = trialDurationInSecond;
	}
	public String getNoOfTrials() {
		return NoOfTrials;
	}
	public void setNoOfTrials(String noOfTrials) {
		NoOfTrials = noOfTrials;
	}
	public String getLatencyThresholdInMicroseconds() {
		return LatencyThresholdInMicroseconds;
	}
	public void setLatencyThresholdInMicroseconds(
			String latencyThresholdInMicroseconds) {
		LatencyThresholdInMicroseconds = latencyThresholdInMicroseconds;
	}
	public String getFrameLossTolerance() {
		return FrameLossTolerance;
	}
	public void setFrameLossTolerance(String frameLossTolerance) {
		FrameLossTolerance = frameLossTolerance;
	}
	public String getMeasurementAccuracyInMbps() {
		return MeasurementAccuracyInMbps;
	}
	public void setMeasurementAccuracyInMbps(String measurementAccuracyInMbps) {
		MeasurementAccuracyInMbps = measurementAccuracyInMbps;
	}
	public String getBandwidthGranularityInMbps() {
		return BandwidthGranularityInMbps;
	}
	public void setBandwidthGranularityInMbps(String bandwidthGranularityInMbps) {
		BandwidthGranularityInMbps = bandwidthGranularityInMbps;
	}
}

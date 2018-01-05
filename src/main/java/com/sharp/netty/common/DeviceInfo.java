package com.sharp.netty.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * DeviceInfo表对应的entity
 * 
 * @author QinMingrui
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DeviceInfo implements Serializable {

	/**
	 * 设备信息表
	 */
	private static final long serialVersionUID = -7050565178325326535L;

	private String deviceId;   //不带-
	private String deviceName;
	private String macAddress; //带-
	private String workState;
	private String workMode;
	private String timerState;
	private String deviceType;
	private String deviceKind;
	private String openTime;
	private String stopTime;
	private String yearNumber;
	private String latitude;
	private String longitude;
	private String cityName;
	private String cityNamePinyin;
	private String wifiVersion;
	private String purifierVersion;
	private Date createTime;
	private String createUser;
	private Date updateTime;
	private String updateUser;
	private String autoFlag;
	private String pmOutDoorThreshold;
	private String pmInDoorThreshold;
	private String province;//省
	private String zone;//区
	private String detailLocation;//详细地址
	
	
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getDetailLocation() {
		return detailLocation;
	}

	public void setDetailLocation(String detailLocation) {
		this.detailLocation = detailLocation;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getWorkState() {
		return workState;
	}

	public void setWorkState(String workState) {
		this.workState = workState;
	}

	public String getWorkMode() {
		return workMode;
	}

	public void setWorkMode(String workMode) {
		this.workMode = workMode;
	}

	public String getTimerState() {
		return timerState;
	}

	public void setTimerState(String timerState) {
		this.timerState = timerState;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceKind() {
		return deviceKind;
	}

	public void setDeviceKind(String deviceKind) {
		this.deviceKind = deviceKind;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	public String getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(String yearNumber) {
		this.yearNumber = yearNumber;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityNamePinyin() {
		return cityNamePinyin;
	}

	public void setCityNamePinyin(String cityNamePinyin) {
		this.cityNamePinyin = cityNamePinyin;
	}

	public String getWifiVersion() {
		return wifiVersion;
	}

	public void setWifiVersion(String wifiVersion) {
		this.wifiVersion = wifiVersion;
	}

	public String getPurifierVersion() {
		return purifierVersion;
	}

	public void setPurifierVersion(String purifierVersion) {
		this.purifierVersion = purifierVersion;
	}

	public String getAutoFlag() {
		return autoFlag;
	}

	public void setAutoFlag(String autoFlag) {
		this.autoFlag = autoFlag;
	}

	public String getPmOutDoorThreshold() {
		return pmOutDoorThreshold;
	}

	public void setPmOutDoorThreshold(String pmOutDoorThreshold) {
		this.pmOutDoorThreshold = pmOutDoorThreshold;
	}

	public String getPmInDoorThreshold() {
		return pmInDoorThreshold;
	}

	public void setPmInDoorThreshold(String pmInDoorThreshold) {
		this.pmInDoorThreshold = pmInDoorThreshold;
	}

	@Override
	public String toString() {
		return "DeviceInfo [deviceId=" + deviceId + ", deviceName=" + deviceName + ", macAddress=" + macAddress
				+ ", workState=" + workState + ", workMode=" + workMode + ", timerState=" + timerState + ", deviceType="
				+ deviceType + ", deviceKind=" + deviceKind + ", openTime=" + openTime + ", stopTime=" + stopTime
				+ ", yearNumber=" + yearNumber + ", latitude=" + latitude + ", longitude=" + longitude + ", cityName="
				+ cityName + ", cityNamePinyin=" + cityNamePinyin + ", wifiVersion=" + wifiVersion
				+ ", purifierVersion=" + purifierVersion + ", createTime=" + createTime + ", createUser=" + createUser
				+ ", updateTime=" + updateTime + ", updateUser=" + updateUser + ", autoFlag=" + autoFlag
				+ ", pmOutDoorThreshold=" + pmOutDoorThreshold + ", pmInDoorThreshold=" + pmInDoorThreshold
				+ ", province=" + province + ", zone=" + zone + ", detailLocation=" + detailLocation + "]";
	}
	
}

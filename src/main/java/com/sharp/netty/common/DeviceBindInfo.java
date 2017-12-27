package com.sharp.netty.common;

import java.io.Serializable;
import java.util.Date;

/**
 * DeviceBindInfo表对应的entity
 * 
 * @author QinMingrui
 *
 */
public class DeviceBindInfo implements Serializable{

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 7593801470329570493L;

	private String bindId;
	private String deviceId;
	private String openId;
	private String firstBindFlg;
	private String deleteFlg;
	private String deviceAlias;
	private String advanceBindFlg;
	private String agreeFlg;
	private Date createTime;
	private String createUser;
	private Date updateTime;
	private String updateUser;
	
	public String getBindId() {
		return bindId;
	}
	public void setBindId(String bindId) {
		this.bindId = bindId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getFirstBindFlg() {
		return firstBindFlg;
	}
	public void setFirstBindFlg(String firstBindFlg) {
		this.firstBindFlg = firstBindFlg;
	}
	public String getDeleteFlg() {
		return deleteFlg;
	}
	public void setDeleteFlg(String deleteFlg) {
		this.deleteFlg = deleteFlg;
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
	public String getDeviceAlias() {
		return deviceAlias;
	}
	public void setDeviceAlias(String deviceAlias) {
		this.deviceAlias = deviceAlias;
	}
	public String getAdvanceBindFlg() {
		return advanceBindFlg;
	}
	public void setAdvanceBindFlg(String advanceBindFlg) {
		this.advanceBindFlg = advanceBindFlg;
	}
	public String getAgreeFlg() {
		return agreeFlg;
	}
	public void setAgreeFlg(String agreeFlg) {
		this.agreeFlg = agreeFlg;
	}
	@Override
	public String toString() {
		return "DeviceBindInfo [bindId=" + bindId + ", deviceId=" + deviceId + ", openId=" + openId + ", firstBindFlg="
				+ firstBindFlg + ", deleteFlg=" + deleteFlg + ", deviceAlias=" + deviceAlias + ", advanceBindFlg="
				+ advanceBindFlg + ", agreeFlg=" + agreeFlg + ", createTime=" + createTime + ", createUser="
				+ createUser + ", updateTime=" + updateTime + ", updateUser=" + updateUser + "]";
	}
	
}

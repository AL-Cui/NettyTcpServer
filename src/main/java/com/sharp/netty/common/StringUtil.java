package com.sharp.netty.common;

/**
 * 字符串工具
 * 
 * @author Pactera
 *
 */
public class StringUtil {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param inputValue
	 *            字符串
	 * @return 判断结果
	 */
	public static boolean isNotBlank(String inputValue) {
		if (inputValue == null) {
			return false;
		}
		if (!"".equals(inputValue.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * wifi固件版本更新
	 * 
	 * @param newWifiVersion
	 * @param oldWifiVersion
	 * @return
	 * 
	 *         “SHARP_”为固定前缀（HMS要求） “机种”、“机型编号”、“firmware版本”之间以下划线“_”分隔
	 *         “机种”为产品种类英文字母缩写，最多5个字符。目前空气净化器为AP
	 *         “机型编号”为同一产品种类下不同机型的编号，数字形式，最长5位数
	 *         。目前KING和PRINCE为1，以后扩展不同机型需维护一张机型编号表。
	 *         “firmware版本”由3个以“.”分隔的数字组成，每个数字最长4位，即最大为9999.9999.9999
	 */
	public static boolean isCompareTo(String newWifiVersion, String oldWifiVersion) {
		// SHARP_AP_1_0.1.9
		boolean isUpdate = false;
		try {
			String[] newArray1 = newWifiVersion.split("_");
			String[] oldArray1 = oldWifiVersion.split("_");

			String[] newArray2 = newArray1[3].split("\\.");
			String[] oldArray2 = oldArray1[3].split("\\.");

			String new0 = newArray1[1];
			String old0 = oldArray1[1];
			String new1 = newArray1[2];
			String old1 = oldArray1[2];

			if (new0.equals(old0) && new1.equals(old1)) {
				int new2 = Integer.parseInt(newArray2[0]);
				int old2 = Integer.parseInt(oldArray2[0]);

				if (new2 > old2) {
					isUpdate = true;
				} else if (new2 == old2) {
					int new3 = Integer.parseInt(newArray2[1]);
					int old3 = Integer.parseInt(oldArray2[1]);

					if (new3 > old3) {
						isUpdate = true;
					} else if (new3 == old3) {
						int old4 = Integer.parseInt(oldArray2[2]);
						int new4 = Integer.parseInt(newArray2[2]);

						if (new4 > old4) {
							isUpdate = true;
						}
					}
				}
			}
		} catch (Exception e) {
			isUpdate = false;
		}

		return isUpdate;
	}

	/**
	 * 本体固件版本更新
	 * 
	 * @param newUpdateVersion
	 * @param oldUpdateVersion
	 * @return
	 * 
	 *         Company and Country：中国向け機器として、SHARPPCIANRSで固定されます。 Soft Parts
	 *         Code: 3桁10進数で構成する(000 ～　999) Main Version: 2桁10進数で構成する(00 ～ 99)
	 *         Sub Version: 2桁10進数で構成する(00 ～　99)
	 */
	public static boolean isCompareToUpdateVerson(String newUpdateVersion, String oldUpdateVersion) {
		// SHARPPCIANRS_AP_990_02_00.bin
		boolean isUpdate = false;
		try {
			String[] newArray1 = newUpdateVersion.split("_");
			String[] oldArray1 = oldUpdateVersion.split("_");

			String new0 = newArray1[3];
			String old0 = oldArray1[3];
			String new1 = newArray1[4];
			String old1 = oldArray1[4];

			if (newArray1[2].equals(oldArray1[2])) {
				if (new0.equals(old0)) {
					int new2 = Integer.parseInt(new1);
					int old2 = Integer.parseInt(old1);

					if (new2 > old2) {
						isUpdate = true;
					}
				}else{
					int new2 = Integer.parseInt(new0);
					int old2 = Integer.parseInt(old0);
					if (new2 > old2) {
						isUpdate = true;
					}

				}
			}
		} catch (Exception e) {
			isUpdate = false;
		}

		return isUpdate;
	}
}
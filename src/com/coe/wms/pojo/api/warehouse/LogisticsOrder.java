package com.coe.wms.pojo.api.warehouse;

/**
 * SF文档 7.13. SKU入库指令
 * 
 * @author Administrator
 * 
 */
public class LogisticsOrder {

	private String logisticsType;

	private String carrierCode;

	private String mailNo;

	private SkuDetail skuDetail;

	public String getLogisticsType() {
		return logisticsType;
	}

	public void setLogisticsType(String logisticsType) {
		this.logisticsType = logisticsType;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public SkuDetail getSkuDetail() {
		return skuDetail;
	}

	public void setSkuDetail(SkuDetail skuDetail) {
		this.skuDetail = skuDetail;
	}

	public String getMailNo() {
		return mailNo;
	}

	public void setMailNo(String mailNo) {
		this.mailNo = mailNo;
	}
}

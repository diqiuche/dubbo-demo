package com.coe.wms.model.warehouse.storage.record;

import java.io.Serializable;

/**
 * 上架表
 * 
 * @author Administrator
 * 
 */
public class OnShelf implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7518195625677660678L;
	/**
	 * 主键
	 */
	private Long id;
	/**
	 * 仓库id
	 */
	private Long warehouseId;

	/**
	 * 操作员Id
	 */
	private Long userIdOfOperator;

	/**
	 * 操作员Id
	 */
	private Long userIdOfCustomer;

	/**
	 * 
	 */
	private Long inWarehouseRecordId;

	/**
	 * 批次号
	 * 
	 * 可空
	 */
	private String batchNo;

	private String trackingNo;

	/**
	 * 货位
	 */
	private String seatCode;

	/**
	 * sku下商品数量
	 * 
	 * 必填
	 */
	private Integer quantity;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getUserIdOfOperator() {
		return userIdOfOperator;
	}

	public void setUserIdOfOperator(Long userIdOfOperator) {
		this.userIdOfOperator = userIdOfOperator;
	}

	public Long getUserIdOfCustomer() {
		return userIdOfCustomer;
	}

	public void setUserIdOfCustomer(Long userIdOfCustomer) {
		this.userIdOfCustomer = userIdOfCustomer;
	}

	public Long getInWarehouseRecordId() {
		return inWarehouseRecordId;
	}

	public void setInWarehouseRecordId(Long inWarehouseRecordId) {
		this.inWarehouseRecordId = inWarehouseRecordId;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getTrackingNo() {
		return trackingNo;
	}

	public void setTrackingNo(String trackingNo) {
		this.trackingNo = trackingNo;
	}

	public String getSeatCode() {
		return seatCode;
	}

	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * 
	 * sku
	 * 
	 * 必填
	 */
	private String sku;

	/**
	 * 创建时间 (收货时间)
	 */
	private Long createdTime;
}

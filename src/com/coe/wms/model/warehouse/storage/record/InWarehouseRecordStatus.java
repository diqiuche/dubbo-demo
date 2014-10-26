package com.coe.wms.model.warehouse.storage.record;

import java.io.Serializable;

import com.google.code.ssm.api.CacheKeyMethod;

/**
 * 大包状态
 * 
 * @author Administrator
 * 
 */
public class InWarehouseRecordStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8724055406707917657L;

	public class InWarehouseRecordStatusCode {
		/**
		 * 全未上架
		 */
		public static final String NONE = "NONE";
		/**
		 * 部分上架
		 */
		public static final String PART = "PART";
		/**
		 * 完成上架
		 */
		public static final String COMPLETE = "COMPLETE";
	}
	

	private Long id;

	/**
	 * 
	 */
	private String code;

	/**
	 * 状态
	 */
	private String cn;

	/**
	 * 状态
	 */
	private String en;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@CacheKeyMethod
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getEn() {
		return en;
	}

	public void setEn(String en) {
		this.en = en;
	}
}

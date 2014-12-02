package com.coe.wms.service.product.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.coe.wms.dao.product.IProductDao;
import com.coe.wms.dao.product.IProductTypeDao;
import com.coe.wms.dao.user.IUserDao;
import com.coe.wms.model.product.Product;
import com.coe.wms.model.product.ProductType;
import com.coe.wms.model.user.User;
import com.coe.wms.service.product.IProductService;
import com.coe.wms.util.Constant;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.Pagination;
import com.coe.wms.util.StringUtil;

@Service("productService")
public class ProductServiceImpl implements IProductService {

	private static final Logger logger = Logger
			.getLogger(ProductServiceImpl.class);

	@Resource(name = "productDao")
	private IProductDao productDao;

	@Resource(name = "productTypeDao")
	private IProductTypeDao productTypeDao;

	@Resource(name = "userDao")
	private IUserDao userDao;

	@Override
	public Pagination findProduct(Product product,
			Map<String, String> moreParam, Pagination page) {
		List<Product> productList = productDao.findProduct(product, moreParam,
				page);
		List<Object> list = new ArrayList<Object>();
		for (Product pro : productList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", pro.getId());
			User user = userDao.getUserById(pro.getUserIdOfCustomer());
			map.put("userNameOfCustomer", user.getLoginName());
			map.put("productName", pro.getProductName());
			ProductType productType = productTypeDao.getProductTypeById(pro
					.getProductTypeId());
			map.put("productTypeName", productType.getProductTypeName());
			map.put("sku", pro.getSku());
			map.put("warehouseSku", pro.getWarehouseSku());
			map.put("remark", pro.getRemark());
			map.put("currency", pro.getCurrency());
			map.put("customsWeight", pro.getCustomsWeight());
			map.put("isNeedBatchNo", pro.getIsNeedBatchNo());
			map.put("model", pro.getModel());
			map.put("customsValue", pro.getCustomsValue());
			map.put("origin", pro.getOrigin());
			if (pro.getLastUpdateTime() != null) {
				map.put("lastUpdateTime", DateUtil.dateConvertString(new Date(
						pro.getLastUpdateTime()), DateUtil.yyyy_MM_ddHHmmss));
			}
			if (pro.getCreatedTime() != null) {
				map.put("createdTime", DateUtil.dateConvertString(
						new Date(pro.getCreatedTime()),
						DateUtil.yyyy_MM_ddHHmmss));
			}
			map.put("taxCode", pro.getTaxCode());
			map.put("volume", pro.getVolume());
			list.add(map);
		}
		page.total = productDao.countProduct(product, moreParam);
		page.rows = list;
		return page;
	}

	@Override
	public Product getProductById(Long id) {
		return productDao.getProductById(id);
	}

	@Override
	public Map<String, String> deleteProductById(Long productId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (productId == null) {
			map.put(Constant.MESSAGE, "产品id为空，无法处理");
			return map;
		}
		int updateCount = productDao.deleteProductById(productId);
		if (updateCount > 0) {
			map.put(Constant.STATUS, Constant.SUCCESS);
			map.put(Constant.MESSAGE, "删除产品成功");
		} else {
			map.put(Constant.MESSAGE, "执行数据库更新失败，删除失败");
		}
		return map;
	}

	@Override
	public Map<String,String> updateProductById(Product product) {
		Long lastUpdateTime = System.currentTimeMillis();
		product.setLastUpdateTime(lastUpdateTime);
		productDao.updateProductById(product);
		return null;
	}

	/**
	 * 新增产品
	 */
	@Override
	public Map<String, String> saveAddProduct(String productName,
			Long productTypeId, Long userIdOfCustomer, String isNeedBatchNo,
			String sku, String warehouseSku, String model, Double volume,
			Double customsWeight, String currency, Double customsValue,
			String taxCode, String origin, String remark) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (StringUtil.isNull(productName)) {
			map.put(Constant.MESSAGE, "产品名称不能为空");
			return map;
		}
		if (StringUtil.isNull(isNeedBatchNo)) {
			map.put(Constant.MESSAGE, "是否需要批次不能为空");
			return map;
		}
		if (StringUtil.isNull(sku)) {
			map.put(Constant.MESSAGE, "SKU不能为空");
			return map;
		}
		if (StringUtil.isNull(warehouseSku)) {
			map.put(Constant.MESSAGE, "仓库SKU不能为空");
			return map;
		}
		if (StringUtil.isNull(model)) {
			map.put(Constant.MESSAGE, "规格型号不能为空");
			return map;
		}
		if (StringUtil.isNull(currency)) {
			map.put(Constant.MESSAGE, "币种不能为空");
			return map;
		}
		if (StringUtil.isNull(taxCode)) {
			map.put(Constant.MESSAGE, "行邮税号不能为空");
			return map;
		}
		if (StringUtil.isNull(origin)) {
			map.put(Constant.MESSAGE, "原产地不能为空");
			return map;
		}
		Long createdTime = System.currentTimeMillis();
		Product product = new Product();
		product.setProductName(productName);
		product.setProductTypeId(productTypeId);
		product.setUserIdOfCustomer(userIdOfCustomer);
		product.setIsNeedBatchNo(isNeedBatchNo);
		product.setModel(model);
		product.setSku(sku);
		product.setWarehouseSku(warehouseSku);
		product.setVolume(volume);
		product.setCustomsWeight(customsWeight);
		product.setCurrency(currency);
		product.setCustomsValue(customsValue);
		product.setTaxCode(taxCode);
		product.setOrigin(origin);
		product.setRemark(remark);
		product.setCreatedTime(createdTime);
		long count = productDao.saveProduct(product);
		if(count>0){
			map.put(Constant.MESSAGE, "新增产品成功");
			map.put(Constant.STATUS, Constant.SUCCESS);
		}
		return map;
	}
}

package com.coe.wms.dao.product;

import java.util.List;
import java.util.Map;

import com.coe.wms.model.product.Product;
import com.coe.wms.model.product.ProductType;
import com.coe.wms.util.Pagination;

public interface IProductDao {
	public List<Product> findAllProduct(Product product,Map<String, String> moreParam, Pagination page);
	
	public Long countProduct(Product product,Map<String, String> moreParam);
	
	public Product getProductById(Long id);
	
	public long saveProduct(Product product);
	
}

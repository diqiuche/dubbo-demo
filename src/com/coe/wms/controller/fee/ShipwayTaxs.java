package com.coe.wms.controller.fee;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coe.wms.model.fee.ShipwayTax;
import com.coe.wms.model.unit.Currency.CurrencyCode;
import com.coe.wms.model.warehouse.shipway.Shipway.ShipwayCode;
import com.coe.wms.service.fee.IShipwayTaxService;
import com.coe.wms.service.product.IProductService;
import com.coe.wms.service.product.IProductTypeService;
import com.coe.wms.service.storage.IStorageService;
import com.coe.wms.service.unit.IUnitService;
import com.coe.wms.service.user.IUserService;
import com.coe.wms.util.GsonUtil;

@Controller("shipwayTaxs")
@RequestMapping("/shipwayTaxs")
public class ShipwayTaxs {

	Logger logger = Logger.getLogger(ShipwayTaxs.class);

	@Resource(name = "storageService")
	private IStorageService storageService;

	@Resource(name = "userService")
	private IUserService userService;

	@Resource(name = "productService")
	private IProductService productService;

	@Resource(name = "productTypeService")
	private IProductTypeService productTypeService;

	@Resource(name = "unitService")
	private IUnitService unitService;

	@Resource(name = "shipwayTaxService")
	private IShipwayTaxService shipwayTaxService;

	/**
	 * 根据商品Id删除商品
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendShipwayTax")
	public String sendShipwayTax(HttpServletRequest request, Long id) {
		ShipwayTax shipwayTax = new ShipwayTax();
		shipwayTax.setCreatedTime(System.currentTimeMillis());
		shipwayTax.setCustomerReferenceNo("30017");
		shipwayTax.setOrderId(18l);
		shipwayTax.setOrderType(ShipwayTax.ORDER_TYPE_STORAGE);
		shipwayTax.setOrderValue(10000d);
		shipwayTax.setOrderValueCurrency(CurrencyCode.CNF);
		shipwayTax.setShipwayCode(ShipwayCode.ETK);
		shipwayTax.setTaxCurrency(CurrencyCode.CNF);
		shipwayTax.setTaxValue(1000d);
		shipwayTax.setTaxTime(System.currentTimeMillis());
		shipwayTax.setUserIdOfCustomer(3l);
		Map<String, String> map = shipwayTaxService.sendShipwayTax(shipwayTax);
		return GsonUtil.toJson(map);
	}
}

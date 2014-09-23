package com.coe.wms.controller.api;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coe.wms.service.api.IStorageService;
import com.coe.wms.util.StreamUtil;
import com.coe.wms.util.XmlUtil;

/**
 * 仓配API
 * 
 * 创建预报入库订单,出库订单
 * 
 * @author Administrator
 * 
 */
@Controller("apiStorage")
@RequestMapping("/api/storage")
public class Storage {

	private static final Logger logger = Logger.getLogger(Storage.class);

	@Resource(name = "storageService")
	private IStorageService storageService;

	/**
	 * 创建入库订单(预报商品信息)
	 * 
	 * 不代表入库, 无产生InWareHouseRecord,无更改ProductInventory
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/inWarehouse", produces = "plain/text; charset=UTF-8", method = RequestMethod.POST)
	public String inWarehouse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestBody = StreamUtil.streamToString(request.getInputStream());
		logger.info("仓配入库订单 xml:" + requestBody);
		
		String xml = null;
		return xml;
	}
}

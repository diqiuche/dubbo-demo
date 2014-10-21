package com.coe.wms.controller.warehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.coe.wms.controller.Application;
import com.coe.wms.service.print.IPrintService;
import com.coe.wms.service.storage.IStorageService;
import com.coe.wms.service.user.IUserService;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.StringUtil;

@Controller("print")
@RequestMapping("/warehouse/print")
public class Print {
	private static final Logger logger = Logger.getLogger(Print.class);

	@Resource(name = "storageService")
	private IStorageService storageService;

	@Resource(name = "userService")
	private IUserService userService;

	@Resource(name = "printService")
	private IPrintService printService;

	/**
	 * 
	 * 打印捡货单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/printPackageList", method = RequestMethod.GET)
	public ModelAndView printPackageList(HttpServletRequest request, HttpServletResponse response, String orderIds) throws IOException {
		ModelAndView view = new ModelAndView();
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		if (StringUtil.isNull(orderIds)) {
			return view;
		}
		// 返回页面的list,装map 每个map 是每个订单的数据
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		String orderIdArray[] = orderIds.split(",");
		for (int i = 0; i < orderIdArray.length; i++) {
			Long orderId = Long.valueOf(orderIdArray[i]);
			Map<String, Object> map = printService.getPrintPackageListData(orderId);
			if (map != null) {
				mapList.add(map);
			}
		}
		view.addObject("mapList", mapList);
		view.addObject("timeNow", DateUtil.dateConvertString(new Date(), DateUtil.yyyy_MM_ddHHmmss));
		view.setViewName("warehouse/print/printPackageList");
		return view;
	}

	/**
	 * 
	 * 打印出库发货单
	 * 
	 * 根据出库渠道 判断打印顺丰运单还是ETK运单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/printShipLabel", method = RequestMethod.GET)
	public ModelAndView printShipLabel(HttpServletRequest request, HttpServletResponse response, String orderIds) throws IOException {
		HttpSession session = request.getSession();
		ModelAndView view = new ModelAndView();
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		if (StringUtil.isNull(orderIds)) {
			return view;
		}
		// 返回页面的list,装map 每个map 是每个订单的数据
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		String orderIdArray[] = orderIds.split(",");
		for (int i = 0; i < orderIdArray.length; i++) {
			Long orderId = Long.valueOf(orderIdArray[i]);
			Map<String, Object> map = printService.getPrintPackageListData(orderId);
			if (map != null) {
				mapList.add(map);
			}
		}
		view.addObject("mapList", mapList);
		view.addObject("timeNow", DateUtil.dateConvertString(new Date(), DateUtil.yyyy_MM_ddHHmmss));
		
		
		// 根据出库渠判断打印顺丰运单还是ETK运单判断
		view.setViewName("warehouse/print/printSfLabel");
		// view.setViewName("warehouse/print/printEtkLabel");
		return view;
	}

}

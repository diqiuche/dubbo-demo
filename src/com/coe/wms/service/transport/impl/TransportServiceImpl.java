package com.coe.wms.service.transport.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.coe.wms.dao.product.IProductDao;
import com.coe.wms.dao.user.IUserDao;
import com.coe.wms.dao.warehouse.ISeatDao;
import com.coe.wms.dao.warehouse.IShelfDao;
import com.coe.wms.dao.warehouse.ITrackingNoDao;
import com.coe.wms.dao.warehouse.IWarehouseDao;
import com.coe.wms.dao.warehouse.storage.IOnShelfDao;
import com.coe.wms.dao.warehouse.storage.IOutShelfDao;
import com.coe.wms.dao.warehouse.storage.IReportDao;
import com.coe.wms.dao.warehouse.storage.IReportTypeDao;
import com.coe.wms.dao.warehouse.transport.IBigPackageAdditionalSfDao;
import com.coe.wms.dao.warehouse.transport.IBigPackageDao;
import com.coe.wms.dao.warehouse.transport.IBigPackageReceiverDao;
import com.coe.wms.dao.warehouse.transport.IBigPackageSenderDao;
import com.coe.wms.dao.warehouse.transport.IBigPackageStatusDao;
import com.coe.wms.dao.warehouse.transport.ILittlePackageDao;
import com.coe.wms.dao.warehouse.transport.ILittlePackageItemDao;
import com.coe.wms.dao.warehouse.transport.ILittlePackageStatusDao;
import com.coe.wms.exception.ServiceException;
import com.coe.wms.model.user.User;
import com.coe.wms.model.warehouse.Warehouse;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrder;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderItem;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderReceiver;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderSender;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderStatus;
import com.coe.wms.model.warehouse.transport.BigPackage;
import com.coe.wms.model.warehouse.transport.BigPackageAdditionalSf;
import com.coe.wms.model.warehouse.transport.BigPackageReceiver;
import com.coe.wms.model.warehouse.transport.BigPackageSender;
import com.coe.wms.model.warehouse.transport.BigPackageStatus;
import com.coe.wms.model.warehouse.transport.BigPackageStatus.BigPackageStatusCode;
import com.coe.wms.model.warehouse.transport.LittlePackage;
import com.coe.wms.model.warehouse.transport.LittlePackageStatus.LittlePackageStatusCode;
import com.coe.wms.pojo.api.warehouse.Buyer;
import com.coe.wms.pojo.api.warehouse.ClearanceDetail;
import com.coe.wms.pojo.api.warehouse.ErrorCode;
import com.coe.wms.pojo.api.warehouse.EventBody;
import com.coe.wms.pojo.api.warehouse.LogisticsDetail;
import com.coe.wms.pojo.api.warehouse.LogisticsOrder;
import com.coe.wms.pojo.api.warehouse.Response;
import com.coe.wms.pojo.api.warehouse.Responses;
import com.coe.wms.pojo.api.warehouse.SenderDetail;
import com.coe.wms.pojo.api.warehouse.TradeDetail;
import com.coe.wms.pojo.api.warehouse.TradeOrder;
import com.coe.wms.service.transport.ITransportService;
import com.coe.wms.util.Config;
import com.coe.wms.util.Constant;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.NumberUtil;
import com.coe.wms.util.Pagination;
import com.coe.wms.util.StringUtil;
import com.coe.wms.util.XmlUtil;

/**
 * 仓配服务
 * 
 * @author Administrator
 * 
 */
@Service("transportService")
public class TransportServiceImpl implements ITransportService {

	private static final Logger logger = Logger.getLogger(TransportServiceImpl.class);

	@Resource(name = "warehouseDao")
	private IWarehouseDao warehouseDao;

	@Resource(name = "reportTypeDao")
	private IReportTypeDao reportTypeDao;

	@Resource(name = "reportDao")
	private IReportDao reportDao;

	@Resource(name = "trackingNoDao")
	private ITrackingNoDao trackingNoDao;

	@Resource(name = "seatDao")
	private ISeatDao seatDao;

	@Resource(name = "shelfDao")
	private IShelfDao shelfDao;

	@Resource(name = "onShelfDao")
	private IOnShelfDao onShelfDao;

	@Resource(name = "outShelfDao")
	private IOutShelfDao outShelfDao;

	@Resource(name = "userDao")
	private IUserDao userDao;

	@Resource(name = "productDao")
	private IProductDao productDao;

	@Resource(name = "config")
	private Config config;

	@Resource(name = "bigPackageAdditionalSfDao")
	private IBigPackageAdditionalSfDao bigPackageAdditionalSfDao;

	@Resource(name = "bigPackageDao")
	private IBigPackageDao bigPackageDao;

	@Resource(name = "bigPackageReceiverDao")
	private IBigPackageReceiverDao bigPackageReceiverDao;

	@Resource(name = "bigPackageSenderDao")
	private IBigPackageSenderDao bigPackageSenderDao;

	@Resource(name = "bigPackageStatusDao")
	private IBigPackageStatusDao bigPackageStatusDao;

	@Resource(name = "littlePackageItemDao")
	private ILittlePackageItemDao littlePackageItemDao;

	@Resource(name = "littlePackageDao")
	private ILittlePackageDao littlePackageDao;

	@Resource(name = "littlePackageStatusDao")
	private ILittlePackageStatusDao littlePackageStatusDao;

	@Override
	public String warehouseInterfaceSaveTransportOrder(EventBody eventBody, Long userIdOfCustomer, String warehouseNo) throws ServiceException {
		Responses responses = new Responses();
		List<Response> responseItems = new ArrayList<Response>();
		Response response = new Response();
		response.setSuccess(Constant.FALSE);
		responseItems.add(response);
		responses.setResponseItems(responseItems);
		// 取 tradeDetail 中tradeOrderId 作为客户订单号
		TradeDetail tradeDetail = eventBody.getTradeDetail();
		if (tradeDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventBody对象获取TradeDetail对象得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		List<TradeOrder> tradeOrderList = tradeDetail.getTradeOrders();
		if (tradeOrderList == null || tradeOrderList.size() == 0) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeDetail对象获取TradeOrders对象得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		TradeOrder tradeOrder = tradeOrderList.get(0);
		// 客户订单号
		String customerReferenceNo = tradeOrder.getTradeOrderId();
		if (StringUtil.isNull(customerReferenceNo)) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeOrder对象获取tradeOrderId得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		// 交易备注,等于打印捡货单上的买家备注
		String tradeRemark = tradeOrder.getTradeRemark();
		Buyer buyer = tradeOrder.getBuyer();
		if (buyer == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeOrder对象获取Buyer对象得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		// 小包
		LogisticsDetail logisticsDetail = eventBody.getLogisticsDetail();
		if (logisticsDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventBody对象获取LogisticsDetail对象得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		ClearanceDetail clearanceDetail = eventBody.getClearanceDetail();
		if (clearanceDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventBody对象获取ClearanceDetail对象得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		List<LogisticsOrder> logisticsOrders = logisticsDetail.getLogisticsOrders();
		if (logisticsOrders == null || logisticsOrders.size() == 0) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("LogisticsDetail对象获取LogisticsOrders对象得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		// 2014-12-09顺丰确认 多个 logisticsOrder的SenderDetail 是一样的. 所以仅需保存一份
		LogisticsOrder logisticsOrderFirst = logisticsOrders.get(0);
		SenderDetail senderDetail = logisticsOrderFirst.getSenderDetail();
		if (senderDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("LogisticsOrders对象获取SenderDetail对象得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		Warehouse warehouse = warehouseDao.getWarehouseByNo(warehouseNo);
		if (warehouse == null) {
			response.setReason(ErrorCode.B0003_CODE);
			response.setReasonDesc("根据仓库编号(eventTarget)获取仓库得到Null");
			return XmlUtil.toXml(Responses.class, responses);
		}
		Long warehouseId = warehouse.getId();
		// 检测大包是否重复
		BigPackage bigPackageParam = new BigPackage();
		Long count = bigPackageDao.countBigPackage(bigPackageParam, null);
		if (count > 0) {
			response.setReason(ErrorCode.B0200_CODE);
			response.setReasonDesc("客户订单号(tradeOrderId)重复,保存失败");
			return XmlUtil.toXml(Responses.class, responses);
		}
		// 创建大包
		BigPackage bigPackage = new BigPackage();
		bigPackage.setCreatedTime(System.currentTimeMillis());
		bigPackage.setCustomerReferenceNo(customerReferenceNo);
		bigPackage.setTradeRemark(tradeRemark);
		bigPackage.setStatus(BigPackageStatusCode.WWC);
		bigPackage.setUserIdOfCustomer(userIdOfCustomer);
		bigPackage.setWarehouseId(warehouseId);
		// 顺丰指定,出货运单号和渠道
		bigPackage.setTrackingNo(clearanceDetail.getMailNo());
		bigPackage.setShipwayCode(clearanceDetail.getCarrierCode());
		Long bigPackageId = bigPackageDao.saveBigPackage(bigPackage);// 保存大包,得到大包id

		// // 顺丰标签附加内容
		BigPackageAdditionalSf additionalSf = new BigPackageAdditionalSf();
		// 顺丰指定,出货运单号和渠道
		additionalSf.setCarrierCode(clearanceDetail.getCarrierCode());
		additionalSf.setCustId(clearanceDetail.getCustId());
		additionalSf.setDeliveryCode(clearanceDetail.getDeliveryCode());
		additionalSf.setMailNo(clearanceDetail.getMailNo());
		additionalSf.setPayMethod(clearanceDetail.getPayMethod());
		additionalSf.setSenderAddress(clearanceDetail.getSenderAddress());
		additionalSf.setShipperCode(clearanceDetail.getShipperCode());
		additionalSf.setBigPackageId(bigPackageId);
		bigPackageAdditionalSfDao.saveBigPackageAdditionalSf(additionalSf);

		// 大包收件人
		BigPackageReceiver bigPackageReceiver = new BigPackageReceiver();
		bigPackageReceiver.setAddressLine1(buyer.getStreetAddress());
		bigPackageReceiver.setCity(buyer.getCity());
		bigPackageReceiver.setCountryCode(OutWarehouseOrderReceiver.CN);
		bigPackageReceiver.setCountryName(OutWarehouseOrderReceiver.CN_VALUE);
		bigPackageReceiver.setCounty(buyer.getDistrict());
		bigPackageReceiver.setEmail(buyer.getEmail());
		bigPackageReceiver.setName(buyer.getName());
		bigPackageReceiver.setPhoneNumber(buyer.getPhone());
		bigPackageReceiver.setPostalCode(buyer.getZipCode());
		bigPackageReceiver.setStateOrProvince(buyer.getProvince());
		bigPackageReceiver.setMobileNumber(buyer.getMobile());
		bigPackageReceiver.setBigPackageId(bigPackageId);
		bigPackageReceiverDao.saveBigPackageReceiver(bigPackageReceiver); // 保存收件人

		// 发件人信息
		BigPackageSender bigPackageSender = new BigPackageSender();
		bigPackageSender.setAddressLine1(senderDetail.getStreetAddress());
		bigPackageSender.setCity(senderDetail.getCity());
		bigPackageSender.setCountryCode(senderDetail.getCountry());
		bigPackageSender.setCountryName(senderDetail.getCountry());
		bigPackageSender.setCounty(senderDetail.getDistrict());
		bigPackageSender.setEmail(senderDetail.getEmail());
		bigPackageSender.setName(senderDetail.getName());
		bigPackageSender.setPhoneNumber(senderDetail.getPhone());
		bigPackageSender.setPostalCode(senderDetail.getZipCode());
		bigPackageSender.setStateOrProvince(senderDetail.getProvince());
		bigPackageSender.setMobileNumber(senderDetail.getMobile());
		bigPackageSender.setBigPackageId(bigPackageId);
		bigPackageSenderDao.saveBigPackageSender(bigPackageSender);
		// 保存小包
		for (int i = 0; i < logisticsOrders.size(); i++) {
			LogisticsOrder logisticsOrder = logisticsOrders.get(i);
			if (logisticsOrder == null) {
				continue;
			}
			// 小包, 到货运单
			LittlePackage littlePackage = new LittlePackage();
			littlePackage.setBigPackageId(bigPackageId);
			littlePackage.setCarrierCode(logisticsOrder.getCarrierCode());
			littlePackage.setCreatedTime(System.currentTimeMillis());
			littlePackage.setPoNo(logisticsOrder.getPoNo());
			littlePackage.setTrackingNo(logisticsOrder.getMailNo());
			littlePackage.setStatus(LittlePackageStatusCode.WWR);
			littlePackage.setUserIdOfCustomer(userIdOfCustomer);
			littlePackage.setWarehouseId(warehouseId);
			littlePackage.setRemark(logisticsOrder.getLogisticsRemark());
			littlePackage.setBigPackageId(bigPackageId);
			littlePackageDao.saveLittlePackage(littlePackage);
		}
		response.setSuccess(Constant.TRUE);
		return XmlUtil.toXml(Responses.class, responses);
	}

	@Override
	public List<BigPackageStatus> findAllBigPackageStatus() throws ServiceException {
		return bigPackageStatusDao.findAllBigPackageStatus();
	}

	/**
	 * 获取转运订单列表数据
	 */
	@Override
	public Pagination getBigPackageData(BigPackage param, Map<String, String> moreParam, Pagination pagination) {
		List<BigPackage> bigPackageList = bigPackageDao.findBigPackage(param, moreParam, pagination);
		List<Object> list = new ArrayList<Object>();
		for (BigPackage bigPackage : bigPackageList) {
			Map<String, Object> map = new HashMap<String, Object>();
			Long bigPackageId = bigPackage.getId();
			map.put("id", bigPackageId);
			if (bigPackage.getCreatedTime() != null) {
				map.put("createdTime", DateUtil.dateConvertString(new Date(bigPackage.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
			}
			map.put("shipwayCode", bigPackage.getShipwayCode());
			map.put("trackingNo", bigPackage.getTrackingNo());
			// 回传称重
			if (StringUtil.isEqual(bigPackage.getCallbackSendWeightIsSuccess(), Constant.Y)) {
				map.put("callbackSendWeightIsSuccess", "成功");
			} else {
				if (bigPackage.getCallbackSendWeighCount() != null && bigPackage.getCallbackSendWeighCount() > 0) {
					map.put("callbackSendWeightIsSuccess", "失败次数:" + bigPackage.getCallbackSendWeighCount());
				} else {
					map.put("callbackSendWeightIsSuccess", "未回传");
				}
			}
			// 回传出库
			if (StringUtil.isEqual(bigPackage.getCallbackSendStatusIsSuccess(), Constant.Y)) {
				map.put("callbackSendStatusIsSuccess", "成功");
			} else {
				if (bigPackage.getCallbackSendStatusCount() != null && bigPackage.getCallbackSendStatusCount() > 0) {
					map.put("callbackSendStatusIsSuccess", "失败次数:" + bigPackage.getCallbackSendStatusCount());
				} else {
					map.put("callbackSendStatusIsSuccess", "未回传");
				}
			}
			// 查询用户名
			User user = userDao.getUserById(bigPackage.getUserIdOfCustomer());
			map.put("userNameOfCustomer", user.getLoginName());
			map.put("customerReferenceNo", bigPackage.getCustomerReferenceNo());
			if (NumberUtil.greaterThanZero(bigPackage.getWarehouseId())) {
				Warehouse warehouse = warehouseDao.getWarehouseById(bigPackage.getWarehouseId());
				if (warehouse != null) {
					map.put("warehouse", warehouse.getWarehouseName());
				}
			}
			map.put("remark", bigPackage.getRemark());
			BigPackageStatus bigPackageStatus = bigPackageStatusDao.findBigPackageStatusByCode(bigPackage.getStatus());
			if (bigPackageStatus != null) {
				map.put("status", bigPackageStatus.getCn());
			}
			// 收件人信息
			BigPackageReceiver bigPackageReceiver = bigPackageReceiverDao.getBigPackageReceiverByPackageId(bigPackageId);
			if (bigPackageReceiver != null) {
				map.put("receiverAddressLine1", bigPackageReceiver.getAddressLine1());
				map.put("receiverAddressLine2", bigPackageReceiver.getAddressLine2());
				map.put("receiverCity", bigPackageReceiver.getCity());
				map.put("receiverCompany", bigPackageReceiver.getCompany());
				map.put("receiverCountryCode", bigPackageReceiver.getCountryCode());
				map.put("receiverCountryName", bigPackageReceiver.getCountryName());
				map.put("receiverCounty", bigPackageReceiver.getCounty());
				map.put("receiverEmail", bigPackageReceiver.getEmail());
				map.put("receiverFirstName", bigPackageReceiver.getFirstName());
				map.put("receiverLastName", bigPackageReceiver.getLastName());
				map.put("receiverMobileNumber", bigPackageReceiver.getMobileNumber());
				map.put("receiverName", bigPackageReceiver.getName());
				map.put("receiverPhoneNumber", bigPackageReceiver.getPhoneNumber());
				map.put("receiverPostalCode", bigPackageReceiver.getPostalCode());
				map.put("receiverStateOrProvince", bigPackageReceiver.getStateOrProvince());
			}
			// 发件人
			BigPackageSender bigPackageSender = bigPackageSenderDao.getBigPackageSenderByPackageId(bigPackageId);
			if (bigPackageSender != null) {
				map.put("senderName", bigPackageSender.getName());
			}
			// 物品明细(目前仅展示SKU*数量)
//			String itemStr = "";
//			OutWarehouseOrderItem outWarehouseOrderItemParam = new OutWarehouseOrderItem();
//			outWarehouseOrderItemParam.setOutWarehouseOrderId(bigPackageId);
//			List<OutWarehouseOrderItem> outWarehouseOrderItemList = outWarehouseOrderItemDao.findOutWarehouseOrderItem(outWarehouseOrderItemParam, null, null);
//			for (OutWarehouseOrderItem outWarehouseOrderItem : outWarehouseOrderItemList) {
//				itemStr += outWarehouseOrderItem.getSku() + "*" + outWarehouseOrderItem.getQuantity() + " ";
//			}
//			map.put("items", itemStr);
			list.add(map);
		}
		pagination.total = bigPackageDao.countBigPackage(param, moreParam);
		pagination.rows = list;
		return pagination;
	}

}

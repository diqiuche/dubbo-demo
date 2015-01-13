package com.coe.wms.service.exportorder.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.coe.wms.dao.user.IUserDao;
import com.coe.wms.dao.warehouse.ISeatDao;
import com.coe.wms.dao.warehouse.IShelfDao;
import com.coe.wms.dao.warehouse.ITrackingNoDao;
import com.coe.wms.dao.warehouse.IWarehouseDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderItemDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderStatusDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseRecordDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseRecordItemDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseRecordStatusDao;
import com.coe.wms.dao.warehouse.storage.IItemInventoryDao;
import com.coe.wms.dao.warehouse.storage.IItemShelfInventoryDao;
import com.coe.wms.dao.warehouse.storage.IOnShelfDao;
import com.coe.wms.dao.warehouse.storage.IOutShelfDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderAdditionalSfDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderItemDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderItemShelfDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderReceiverDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderSenderDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderStatusDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehousePackageDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehousePackageItemDao;
import com.coe.wms.dao.warehouse.storage.IReportDao;
import com.coe.wms.dao.warehouse.storage.IReportTypeDao;
import com.coe.wms.exception.ServiceException;
import com.coe.wms.model.user.User;
import com.coe.wms.model.warehouse.Warehouse;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderStatus;
import com.coe.wms.service.exportorder.IExportService;
import com.coe.wms.util.Config;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.FileUtil;
import com.coe.wms.util.POIExcelUtil;
import com.coe.wms.util.StringUtil;

/**
 * 仓配服务
 * 
 * @author Administrator
 * 
 */
@Service("exportService")
public class ExportServiceImpl implements IExportService {

	private static final Logger logger = Logger.getLogger(ExportServiceImpl.class);

	private static final String OUT_WAREHOUSE_EXPORT_SHEET_TITLE = "单品出库订单";

	private static final String[] OUT_WAREHOUSE_EXPORT_HEAD = { "序号", "仓库编号", "客户帐号", "客户订单号", "跟踪单号", "商品条码", "商品SKU", "商品数量", "商品名称", "订单状态", "创建时间" };

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
	@Resource(name = "inWarehouseOrderDao")
	private IInWarehouseOrderDao inWarehouseOrderDao;

	@Resource(name = "inWarehouseOrderStatusDao")
	private IInWarehouseOrderStatusDao inWarehouseOrderStatusDao;

	@Resource(name = "inWarehouseRecordStatusDao")
	private IInWarehouseRecordStatusDao inWarehouseRecordStatusDao;

	@Resource(name = "outWarehouseOrderItemShelfDao")
	private IOutWarehouseOrderItemShelfDao outWarehouseOrderItemShelfDao;

	@Resource(name = "inWarehouseOrderItemDao")
	private IInWarehouseOrderItemDao inWarehouseOrderItemDao;

	@Resource(name = "outWarehouseOrderDao")
	private IOutWarehouseOrderDao outWarehouseOrderDao;

	@Resource(name = "outWarehouseOrderStatusDao")
	private IOutWarehouseOrderStatusDao outWarehouseOrderStatusDao;

	@Resource(name = "outWarehouseOrderItemDao")
	private IOutWarehouseOrderItemDao outWarehouseOrderItemDao;

	@Resource(name = "outWarehouseOrderSenderDao")
	private IOutWarehouseOrderSenderDao outWarehouseOrderSenderDao;

	@Resource(name = "outWarehouseOrderReceiverDao")
	private IOutWarehouseOrderReceiverDao outWarehouseOrderReceiverDao;

	@Resource(name = "inWarehouseRecordDao")
	private IInWarehouseRecordDao inWarehouseRecordDao;

	@Resource(name = "inWarehouseRecordItemDao")
	private IInWarehouseRecordItemDao inWarehouseRecordItemDao;

	@Resource(name = "outWarehousePackageItemDao")
	private IOutWarehousePackageItemDao outWarehousePackageItemDao;

	@Resource(name = "userDao")
	private IUserDao userDao;

	@Resource(name = "itemInventoryDao")
	private IItemInventoryDao itemInventoryDao;

	@Resource(name = "itemShelfInventoryDao")
	private IItemShelfInventoryDao itemShelfInventoryDao;

	@Resource(name = "outWarehouseOrderAdditionalSfDao")
	private IOutWarehouseOrderAdditionalSfDao outWarehouseOrderAdditionalSfDao;

	@Resource(name = "config")
	private Config config;

	@Resource(name = "outWarehousePackageDao")
	private IOutWarehousePackageDao outWarehousePackageDao;

	@Override
	public String executeExportOutWarehouseOrder(Long warehouseId, String status, String userLoginName, String createdTimeStart, String createdTimeEnd, String isQuantityOnly1) throws ServiceException {
		Long userIdOfCustomer = null;
		if (StringUtil.isNotNull(userLoginName)) {
			userIdOfCustomer = userDao.findUserIdByLoginName(userLoginName);
		}
		List<Map<String, Object>> singleBarcodeOutWarehouseOrderList = outWarehouseOrderDao.findSingleBarcodeOutWarehouseOrder(warehouseId, status, userIdOfCustomer, createdTimeStart, createdTimeEnd, isQuantityOnly1);
		List<String[]> rows = new ArrayList<String[]>();
		try {
			int index = 0;
			for (Map<String, Object> map : singleBarcodeOutWarehouseOrderList) {
				String[] row = new String[11];
				index++;
				row[0] = index + "";
				Long resultWarehouseId = (Long) map.get("warehouseId");
				if (resultWarehouseId != null) {
					Warehouse warehouse = warehouseDao.getWarehouseById(resultWarehouseId);
					row[1] = warehouse.getWarehouseNo();// 仓库编号
				}
				Long resultUserIdOfCustomer = (Long) map.get("userIdOfCustomer");
				if (resultUserIdOfCustomer != null) {
					User user = userDao.getUserById(resultUserIdOfCustomer);
					row[2] = user.getLoginName();
				}
				row[3] = (String) map.get("customerReferenceNo");
				row[4] = (String) map.get("trackingNo");
				row[5] = (String) map.get("barcode");
				row[6] = (String) map.get("sku");
				row[7] = (Integer) map.get("quantity") + "";
				row[8] = (String) map.get("productName");
				String resultStatus = (String) map.get("status");
				if (StringUtil.isNotNull(resultStatus)) {
					OutWarehouseOrderStatus outWarehouseOrderStatus = outWarehouseOrderStatusDao.findOutWarehouseOrderStatusByCode(resultStatus);
					row[9] = outWarehouseOrderStatus.getCn();
				}
				row[10] = DateUtil.dateConvertString(new Date((Long) map.get("createdTime")), DateUtil.yyyy_MM_ddHHmmss);
				rows.add(row);
			}
			String filePath = config.getRuntimeFilePath() + "/export/";
			FileUtil.mkdirs(filePath);
			// 文件保存地址
			String filePathAndName = filePath + "-" + OUT_WAREHOUSE_EXPORT_SHEET_TITLE + "-" + System.currentTimeMillis() + ".xls";
			POIExcelUtil.createExcel(OUT_WAREHOUSE_EXPORT_SHEET_TITLE, OUT_WAREHOUSE_EXPORT_HEAD, rows, filePathAndName);
			return filePathAndName;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("导出单票出库订单IO异常:" + e, e);
		}
		return null;
	}
}

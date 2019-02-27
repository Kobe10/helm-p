package com.bjshfb.helm.cmpt.service.date;

import com.bjshfb.helm.util.EntityInfoUtils;
import com.shfb.oframe.core.common.CoreConstant;
import com.shfb.oframe.core.component.impl.AbstractComponent;
import com.shfb.oframe.core.dao.domin.DaoParam;
import com.shfb.oframe.core.dao.domin.EntityInfo;
import com.shfb.oframe.core.dao.domin.ResultParam;
import com.shfb.oframe.core.service.impl.ExecuteContext;
import com.shfb.oframe.core.util.common.NumberUtil;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.excel.ExcelReader;
import com.shfb.oframe.core.util.exception.BusinessException;
import com.shfb.oframe.core.util.exception.CheckException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: helm
 * @description: 数据模板导入
 * @author: fuzq
 * @create: 2018-09-13 15:23
 **/
@Service("mapDateImportCmpt")
public class MapDateImportCmpt extends AbstractComponent {

    /**
     * 报文根路径
     */
    private final String rootPath = "OpData.";

    /**
     * 默认处理数据行数
     */
    private final int ROW_SIZE = 50;

    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {

        //文件路径
        String filePath = executeParam.getStrValue(rootPath + "filePath");
        //配置行号，从0开始计算
        int configRowNum = executeParam.getIntValue(rootPath + "configRowNum");
        //执行服务名称
        String entityName = executeParam.getStrValue(rootPath + "entityName");
        //扩展实体类型
        String type = executeParam.getStrValue(rootPath + "type");
        //一次处理行数
        int dealRowSize = executeParam.getIntValue(rootPath + "dealRowSize");
        if (dealRowSize == 0) {
            dealRowSize = ROW_SIZE;
        }
        //开始时间
        long startM = System.currentTimeMillis();
        excelReader(StringUtil.formatFilePath(filePath), configRowNum, entityName, dealRowSize, executeContext, type);
        log.debug("耗时:***" + ((System.currentTimeMillis() - startM) / 1000) + "***秒");
        return new XmlBean();
    }

    /**
     * 执行导入开始
     *  @param
     * @param filePath       文件绝对路径
     * @param configRowNum   配置行
     * @param dealRowSize    一次处理行数
     * @param executeContext
     * @param type
     */
    public void excelReader(String filePath, int configRowNum, String entityName, int dealRowSize, ExecuteContext executeContext, String type) throws BusinessException, CheckException {
        File file = new File(filePath);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new BusinessException(CoreConstant.MODEL_NAME, e, CoreConstant.ERROR_MESSAGE_400041, filePath);
        }
        ExcelReader excelReader = new ExcelReader(inputStream, file.getName());
        //列对应的路径  cellNum,path  配置数据Map   这个就是获取第一行的参数名称
        Map<Integer, String> cellPathMap = initCellPathMap((HSSFRow) excelReader.getRow(configRowNum));
        int rowNum = configRowNum + 1;
        int maxRowNum = excelReader.getRowCount();
        int endRowNum = 0;
        int count = 0;
        while (maxRowNum != endRowNum) {
            count++;
            //数据行
            HSSFRow row = (HSSFRow) excelReader.getRow(rowNum);
            //序号 获取第一列的序号
            double index = NumberUtil.getDoubleFromObj(this.getValue(row.getCell(0)).trim());
            int cellValue = (int) index;
            //结束行  获取下一行的数据
            endRowNum = getNextNotNull(rowNum, excelReader, maxRowNum);
            //获取报文参数
            XmlBean xmlBean = getExtXml(rowNum, endRowNum, excelReader, cellPathMap, entityName, executeContext, type);

            rowNum = endRowNum;
            //入库
        }
    }

    /**
     * @Description: TODO 获取报文参数 拼接入库报文
     * @Param: [rowNum, endRow, excelReader, cellPathMap]
     * @return: com.shfb.oframe.core.util.common.XmlBean
     * @Author: fuzq
     * @Date: 2018/9/20 14:32
     **/
    private XmlBean getExtXml(int rowNum, int endRow, ExcelReader excelReader, Map<Integer, String> cellPathMap, String entityName, ExecuteContext executeContext, String type) throws BusinessException, CheckException {
        XmlBean xmlBean = new XmlBean();
        XmlBean expanBean = new XmlBean();
        HSSFRow row = (HSSFRow) excelReader.getRow(rowNum);
        com.bjshfb.helm.cmpt.common.EntityInfo entityInfo = EntityInfoUtils.getEntityInfo(type);

        int cellNum = row.getLastCellNum();
        //遍历每一列
        for (int i = 0; i < cellNum; i++) {
            HSSFCell cell = row.getCell(i);
            try {
                String value = getValue(cell).trim();
                String config = cellPathMap.get(i);
                if (i == 1) {
                    //查询图层Id
                    DaoParam daoParam = new DaoParam("MapLayerDef");
                    daoParam.addEqualCondition("mapLayerDefCd", value);
                    daoParam.addQueryParams("mapLayerDefId");
                    ResultParam resultParam = entityDao.queryPageData(daoParam);
                    if (resultParam.isHaveData()) {
                        xmlBean.setValue(rootPath + config, resultParam.getStrValueByAttr("mapLayerDefId"));
                    }
                } else {
                    if (StringUtil.isEqual("MapDataInfo", entityName)) {
                        if (StringUtil.isEqual("dataAttrCd", config) || StringUtil.isEqual("dataAttrValue", config)) {
                            //拼接伪属性  报表后续需要优化
                            xmlBean.setStrValue(rootPath + "CamDataRhts.CamDataRht[0]." + config, value);
                        } else {
                            xmlBean.setValue(rootPath + config, value);
                        }
                    } else {
                        expanBean.setValue(rootPath + config, value);
                    }
                }
            } catch (Exception e) {
                String msg = "";
                if (e instanceof BusinessException) {
                    msg = ((BusinessException) e).getErrMsg();
                }
                throw new BusinessException(CoreConstant.MODEL_NAME, e, CoreConstant.ERROR_MESSAGE_400039, rowNum + 1 + "," + (i + 1), msg);
            }
        }
        if (!StringUtil.isEqual("1", type)) {
            expanBean.setStrValue(rootPath + entityInfo.getEntityId(), "${expanId" + rowNum + "}");
            this.saveEntityInfo(executeContext, entityInfo.getEntityName(), expanBean);
        }
        xmlBean.setStrValue(rootPath + "mapDataId", "${mapDataId" + rowNum + "}");
        this.saveEntityInfo(executeContext, "MapDataInfo", xmlBean);
        return xmlBean;
    }

    /**
     * @Description: TODO 初始化列对应路径关系
     * @Param: [row]
     * @return: java.util.Map<java.lang.Integer , java.lang.String>
     * @Author: fuzq
     * @Date: 2018/9/20 14:31
     **/
    private Map<Integer, String> initCellPathMap(HSSFRow row) {
        Map<Integer, String> cellPathMap = new HashMap<Integer, String>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellType(XSSFCell.CELL_TYPE_STRING);
            HSSFCell cell = row.getCell(i);
            if (cell != null) {
                String cellValue = cell.getStringCellValue();
                if (StringUtil.isNotEmptyOrNull(cellValue)) {
                    cellPathMap.put(i, cellValue);
                }
            }
        }
        return cellPathMap;
    }

    /**
     * @Description: TODO 获取下一个非空的行号
     * @Param: [rowNum, excelReader, maxRowNum]
     * @return: int
     * @Author: fuzq
     * @Date: 2018/9/20 14:26
     **/
    private int getNextNotNull(int rowNum, ExcelReader excelReader, int maxRowNum) {

        int nullValue = 0;
        HSSFRow row;
        while (nullValue == 0) {
            rowNum++;
            row = (HSSFRow) excelReader.getRow(rowNum);
            if (rowNum == maxRowNum) {
                return rowNum;
            }
            nullValue = NumberUtil.getIntFromObj(this.getValue(row.getCell(0)).trim());
        }
        return rowNum;
    }

    /**
     * 获取值
     *
     * @param cell HSSFCell 对象
     * @return String类型的值
     */
    private String getValue(HSSFCell cell) {
        if (cell == null) {
            return "";
        }
        int type = cell.getCellType();
        String retStr = "";
        if (type == Cell.CELL_TYPE_STRING) {
            retStr = cell.getStringCellValue();
        } else if (type == Cell.CELL_TYPE_NUMERIC) {
            double numericCellValue = cell.getNumericCellValue();
            cell.setCellType(Cell.CELL_TYPE_STRING);
            retStr = cell.getRichStringCellValue().getString();
            cell.setCellValue(numericCellValue);
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        } else if (type == Cell.CELL_TYPE_BLANK) {
            retStr = StringUtil.obj2Str(cell.getStringCellValue());
        } else {
            System.out.println("其它类型数据" + type);
        }
        return retStr;
    }
}

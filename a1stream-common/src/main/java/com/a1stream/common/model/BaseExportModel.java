package com.a1stream.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseExportModel implements Serializable {

    private static final long serialVersionUID = 3995711410342717347L;

    //private XSSFWorkbook workbook;

    private Integer dataStartRowNo;

    private String sheetName;

    private List<Map<String, Object>> dataList;

    //private JSONObject cellNameIndex;

    private List<BaseExportModel> moreSheetsList;

    private short height = 0;
}

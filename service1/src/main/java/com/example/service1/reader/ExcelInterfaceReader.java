package com.example.service1.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelInterfaceReader {

    private static String inParamGroup = "inParamGroup";
    private static String inParam = "inParam";
    private static String outParamGroup = "outParamGroup";
    private static String outParam = "outParam";
    private static String string = "String";
    private static String group = "Group";
    private static ObjectMapper objectMapper = new ObjectMapper();


    @SuppressWarnings("resource")
    public Map<String, Object> convert(InputStream io) {
        Map<String, Object> reMap = new HashMap<String, Object>();
        ZipSecureFile.setMinInflateRatio(0);
        XSSFWorkbook wb;

        try {
            // 잠겨있는 파일은 읽어올 수 없습니다.
            wb = new XSSFWorkbook(io);

            /*
                전문 요청 시트 : 3번째
                전문 응답 시트 : 4번째
             */
            XSSFSheet inSheet = wb.getSheetAt(2);
            XSSFSheet outSheet = wb.getSheetAt(3);

            /*
                인터페이스ID : 4행 C열
                송신서비스ID : 35행 G열
                수신서비스ID : 54행 G열
             */
            String itfId = getCellData(inSheet,3,2);
            String sndSvcId = getCellData(inSheet,34,6);
            String rcvSvcId = getCellData(outSheet,53,6);

            String in_info = maker(inSheet, inParam);
            String out_info = maker(outSheet, outParam);
            String fixed1 = dataFix(in_info);
            String fixed2 = dataFix(out_info);


            reMap.put("inInfo", fixed1.replaceAll("	", "").replaceAll("\r\n", ""));
            reMap.put("outInfo", fixed2.replaceAll("	", "").replaceAll("\r\n", ""));
            reMap.put("sndSvcId", sndSvcId);
            reMap.put("rcvSvcId", rcvSvcId);
            reMap.put("itfId", itfId);

            return reMap;
        } catch (IOException e) {
            System.out.println(e.getMessage());

            reMap.put("inInfo", "{}");
            reMap.put("outInfo", "{}");
            reMap.put("sndSvcId", "");
            reMap.put("rcvSvcId", "");
            reMap.put("itfId", "");

            return reMap;
        }
    }


    private String getCellData(XSSFSheet sheet, int rowIdx, int colIdx) {
        String reStr = "";
        XSSFRow row = sheet.getRow(rowIdx);
        if (row != null) {
            XSSFCell cell = row.getCell(colIdx);
            if (cell != null && cell.getCellType().toString().equals("STRING")) {
                reStr = cell.getStringCellValue();
            }
        }

        return reStr;
    }


    private String groupBuilder(String[] b, String type) {
        int index = Integer.parseInt(b[0])-1;
        int size = Integer.parseInt(b[4]);

        if (size == -1) type = "object";
        if (size == 0) type = "list";

        String a = "{\r\n\t\t\"nodeName\":\"" + b[1] + "\",\r\n" + "\t\t\"nodeType\":\"" + "G" + "\",\r\n" + "\t\t\"nodeValue\":{\r\n" + "\t\t\t\"type\":\"" + type + "\",\r\n" + "\t\t\t\"index\":\"" + index + "\",\r\n" + "\t\t\t\"size\":\"" + b[4] + "\",\r\n" + "\t\t\t\"visible\":true,\r\n" + "\t\t\t\"nullable\":true\r\n" + "\t\t},\r\n" + "\t\t\"nodes\":[";
        return a;
    }

    private String similarBuilder(String[] b, String type) {

        int index = Integer.parseInt(b[0])-1;

        String a = "{\t\"nodeName\":\"" + b[1] + "\",\r\n" + "\t\"nodeType\":\"" + "F" + "\",\r\n" + "\t\"nodeValue\":{\r\n" + "\t\t\"index\":\"" + index + "\",\r\n" + "\t\t\"desc\":\"" + b[2] + "\",\r\n" + "\t\t\"type\":\"" + type + "\",\r\n" + "\t\t\"size\":\"" + b[4] + "\",\r\n" + "\t\t\"fsize\":\"" + b[5] + "\",\r\n" + "\t\t\"nullable\":true,\r\n" + "\t\t\"visible\":true\r\n" + "\t}\r\n" + "}\r\n";
        return a;
    }


    private String maker(XSSFSheet sheet, String sep) {
        StringBuffer sb = new StringBuffer();
        String[] cellVal = new String[13];
        int[] cellOrder = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
//        String[] result = new String[2];
//        String[] ifParm = new String[2];

        List<Integer> cutPoint = new ArrayList<>();
        int startCut = 0;
        int endCut = 0;

        XSSFRow row = null;
        XSSFCell cell = null;

        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            if (row != null) {
                cell = row.getCell(0);
                if (cell.getCellType().toString().equals("STRING") && cell.getStringCellValue().contains("일련")) {
                    cutPoint.add(i);
                }
//                if (i == 3) {
//                    cell = row.getCell(2);
//                    ifParm[0] = cell.getStringCellValue();
//                    result[0] = "{\"adaptor.mci.telegram.system-common.INF_ID\":\"" + ifParm[0] + "\", \"adaptor.mci.telegram.system-common.SCRN_ID\":\"" + ifParm[0] + "\", ";
//                } else if(i == 8) {
//                    cell = row.getCell(2);
////                    itf.setReceiveSys(cell.getStringCellValue());
//                } else if (i == 53) {
//                    cell = row.getCell(6);
//                    ifParm[1] = cell.getStringCellValue();
//                    result[1] = "\"adaptor.mci.telegram.system-common.RCV_SV_ID\":\"" + ifParm[1] + "\"}";
////                    String query = result[0] + result[1];
////                    itf.setInterfaceParam(result[0] + result[1]);
//                }
            }
        }

        if (cutPoint.size() == 2) {
            if (sep == inParam) {
                startCut = cutPoint.get(0) + 2;
                endCut = cutPoint.get(1);
            } else {
                startCut = cutPoint.get(1) + 2;
                endCut = sheet.getLastRowNum() + 1;
            }
        }

        String type = "";
        int beforeLevel = 1;
        int currentLevel = 1;
        int groupLevel = 1;

        sb.append("[");

        for (int i = startCut; i < endCut; i++) {

            row = sheet.getRow(i);

            for (int j = 0; j < cellOrder.length; j++) {
                cell = row.getCell(cellOrder[j]);
                if (cell == null) {
                    cellVal[j] = "";
                } else if (cell.getCellType().toString().equals("STRING")) {
                    cellVal[j] = row.getCell(cellOrder[j]).getStringCellValue();
                } else if (cell.getCellType().toString().equals("NUMERIC")) {
                    cellVal[j] = Integer.toString(((int) row.getCell(cellOrder[j]).getNumericCellValue()));
                    cellVal[6] = row.getCell(cellOrder[6]).getStringCellValue();
                }
            }
            if (cellVal[3].equals(group)) {
                type = group.toLowerCase();
            } else if (cellVal[3].equals("NUMBER")) {
                if (cellVal[6] != null && cellVal[6].equals("Y")) {
                    type = "float";
                } else {
                    type = "int";
                }
            } else {
                type = string.toLowerCase();
            }

            currentLevel = Integer.parseInt(cellVal[12]);

            if (currentLevel == beforeLevel) {

                if (!cellVal[0].equals("1"))
                    sb.append(",");

                if (type.equals(group.toLowerCase())) {
                    sb.append(groupBuilder(cellVal, type));
                } else {
                    sb.append(similarBuilder(cellVal, type));
                }

                if (i == endCut - 1) {
                    for (int j = 0; j < groupLevel - 1; j++) {
                        sb.append("]}");
                    }
                }
            } else if (currentLevel < beforeLevel) {

                for (int j = 0; j < groupLevel - Integer.parseInt(cellVal[12]); j++) {
                    sb.append("]}");
                }
                sb.append(",");

                if (type.equals(group.toLowerCase())) {
                    sb.append(groupBuilder(cellVal, type));
                } else {
                    sb.append(similarBuilder(cellVal, type));

                }

                groupLevel = Integer.parseInt(cellVal[12]);

            } else {

                if (type.equals(group.toLowerCase())) {
                    sb.append(groupBuilder(cellVal, type));
                } else {
                    sb.append(similarBuilder(cellVal, type));
                }

                groupLevel = Integer.parseInt(cellVal[12]);

                if (i == endCut - 1) {
                    for (int j = 0; j < groupLevel - 1; j++) {
                        sb.append("]}");
                    }
                }
            }

            beforeLevel = currentLevel;
        }

        sb.append("]");

        return sb.toString();
    }

    public String dataFix(String info) throws JsonProcessingException {
        List<Map<String, Object>> jsonData = objectMapper.readValue(info, new TypeReference<List<Map<String, Object>>>() {
        });

//        for (int i = 0; i < jsonData.size(); i++) {
//            if (jsonData.get(i).get("nodes") != null) {
//                List<Map<String, Object>> nodes = (List<Map<String, Object>>) jsonData.get(i).get("nodes");
//                for (int j = 0; j < nodes.size(); j++) {
//                    Map<String, Object> node = nodes.get(j);
//                    if (node.get("nodeValue") != null) {
//                        Map<String, Object> nodeValue = (Map<String, Object>) node.get("nodeValue");
//                        String copied = (String) nodeValue.get("pram_desc");
//                        nodeValue.put("param_comment", copied);
//                        if (nodeValue.get("param_header_visible") != null) {
//                            nodeValue.remove("param_header_visible");
//                        }
////                        if (nodeValue.get("param_encode").equals("0")) {
////                            nodeValue.remove("param_encode");
////                        }
////                        if (nodeValue.get("param_decode").equals("0")) {
////                            nodeValue.remove("param_decode");
////                        }
//
//                    }
//                    Map<String, Object> jsonDatas = (Map<String, Object>) jsonData.get(i);
//                    if (jsonDatas.get("nodeType").equals("inParamGroup")) {
//                        Map<String, Object> eachNodeValue = (Map<String, Object>) jsonDatas.get("nodeValue");
//                        eachNodeValue.put("param_value_from", "map.client.request");
//                    }
//                    if (node.get("nodeType").equals("inParam")) {
//                        Map<String, Object> nodesNodeValue = (Map<String, Object>) node.get("nodeValue");
//                        nodesNodeValue.put("param_value_from", "map.engine.parent");
//                    }
//                    if (jsonDatas.get("nodeType").equals("outParamGroup")) {
//                        Map<String, Object> eachNodeValue = (Map<String, Object>) jsonDatas.get("nodeValue");
//                        eachNodeValue.put("param_value_from", "map.legacy.response");
//                    }
//                    if (node.get("nodeType").equals("outParam")) {
//                        Map<String, Object> nodesNodeValue = (Map<String, Object>) node.get("nodeValue");
//                        nodesNodeValue.put("param_value_from", "map.engine.parent");
//                    }
////                     if(jsonData[i].nodes[j].nodeValue.param_size === '-1') {
////                         delete jsoData[i].nodes[j].nodeValue.param_size
////                     }
//                }
//                Map<String, Object> jsonDatas = (Map<String, Object>) jsonData.get(i).get("nodeValue");
//
////                if (jsonDatas.get("param_type").equals("group")) {
////                    jsonDatas.put("param_type", "map");
////                }
////                if (jsonDatas != null) {
////                    jsonDatas.put("param_parent", "dataBody");
////                }
////                 if(jsonDatas.get("param_size").equals("-1")) {
////                     delete jsonData[i].nodeValue.param_size;
////                 }
//
//
//
//
//
//            } else if (jsonData.get(i).get("nodeValue") != null) {
//                Map<String, Object> jsonDatas = (Map<String, Object>) jsonData.get(i).get("nodeValue");
//                String copied = (String) jsonDatas.get("param_desc");
//                jsonDatas.put("param_comment", copied);
//                // if(jsonData[i].nodeValue.param_header_visible) {
//                //     delete jsonData[i].nodeValue.param_header_visible;
//                // }
//                if (jsonDatas.get("param_encode").equals("0")) {
//                    jsonDatas.remove("param_encode");
//                }
////                if (jsonDatas.get("param_decode").equals("0")) {
////                    jsonDatas.remove("param_decode");
////                }
//                if (jsonData.get(i).get("nodeType").equals("outParam")) {
//                    jsonDatas.put("param_value_from", "map.legacy.response");
////                    jsonData[i].nodeValue.param_value_from = 'map.legacy.response';
//                }
//                if (jsonData.get(i).get("nodeType").equals("inParam")) {
//                    jsonDatas.put("param_value_from", "map.client.request");
////                    jsonData[i].nodeValue.param_value_from = 'map.client.request';
//                }
//                // if(jsonData[i].nodeValue.param_size === '-1') {
//                //     delete jsonData[i].nodeValue.param_size;
//                // }
////                if (jsonDatas != null) {
////                    jsonDatas.put("param_parent", "dataBody");
//////                    jsonData[i].nodeValue.param_parent = 'dataBody';
////                }
//            }
//        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData);
    }
}

package com.example.service1.transform;

import com.example.service1.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class Transform {

    /*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*        JSON > 전문         *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     */
    public String transformJsonToFixedLength(String json, ItfInfo rootItfInfo) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        // 결과를 저장할 StringBuilder 생성
        StringBuilder resultBuilder = new StringBuilder();

        // json to Map
        Map<?, ?> resultMap = objectMapper.convertValue(rootNode, Map.class);

        String result = traverseItfInfo(resultMap, rootItfInfo, resultBuilder);
        log.info("result > {}" , result);
        return result;

    }

    public String traverseItfInfo(Object currentObj, ItfInfo currentItfInfo, StringBuilder resultBuilder) throws JsonProcessingException {
        // currentObj가 Map인 경우
        if (currentObj instanceof Map<?, ?>) {
            Map<String, Object> currentMap = (Map<String, Object>) currentObj;

            // NodeValue의 인덱스에 따라 nodes 리스트를 정렬
            List<ItfInfo> sortedNodes = new ArrayList<>(currentItfInfo.getNodes());
            sortedNodes.sort(Comparator.comparingInt(a -> a.getNodeValue().getIndex()));

            for (ItfInfo childItfInfo : sortedNodes) {
                Object childObj = currentMap.get(childItfInfo.getFieldName());

                // 필드 타입이 'F'인 경우 값을 직접 추출
                if (childItfInfo.getItfType() == ItfType.FIELD.getType() && childObj != null) {
                    String paddStr = handleFieldLength(childObj.toString(), childItfInfo);
                    resultBuilder.append(paddStr);
                } else if (childItfInfo.getItfType() == ItfType.GROUP.getType() && childObj != null) {
                    // 필드 타입이 'G'인 경우 재귀적으로 처리
                    traverseItfInfo(childObj, childItfInfo, resultBuilder);
                }
            }
        } else if (currentObj instanceof List<?> currentList) {
            // currentObj가 List인 경우
            for (Object item : currentList) {
                traverseItfInfo(item, currentItfInfo, resultBuilder);
            }
        }
        return resultBuilder.toString();
    }

    public String paddingInt(String str, int size) {
        byte[] buff = str.getBytes();

        if (size < buff.length) {
            return new String(buff, 0, size);
        } else if (size == buff.length) {
            return str;
        }

        int i=0;
        byte[] nbuff = new byte[size];
        for (i = 0; i < (size - buff.length); i++) {
            nbuff[i] = (byte)'0';
        }
        System.arraycopy(buff, 0, nbuff, i, buff.length);
        return new String(nbuff);
     }

    public String paddingFloat(String str, int size, int fsize) {
        StringBuffer strBuf = new StringBuffer();
        String[] tokens = null;
        if (str != null && str.contains(".")) {
            tokens = str.split("\\.");
        } else {
            tokens = new String[]{str, ""};
        }

        if (tokens.length == 2) {
            assert tokens[0] != null;
            strBuf.append(paddingInt(tokens[0], size - fsize - 1));
            strBuf.append(".");
            strBuf.append(paddingStr(tokens[1], fsize, StringUtils.BYTE_ZERO, StringUtils.CHARSET_UTF8));
        }

        return strBuf.toString();
    }


    // UTF-8 바이트 길이에 따라 문자열을 처리하는 메서드
    private String handleFieldLength(String str, ItfInfo itfInfo) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        int size = itfInfo.getNodeValue().getSize();
        String type = itfInfo.getNodeValue().getType();

        if (strBytes.length > size) {
            // 바이트 길이에 맞게 문자열을 잘라냄
            return cutStringToByteLength(str, size);
        } else {
            // 바이트 길이에 맞게 문자를 추가하여 패딩
            return switch (type) {
                case StringUtils.TYPE_INT -> paddingInt(str, size);
                case StringUtils.TYPE_FLOAT -> paddingFloat(str, size, itfInfo.getNodeValue().getFsize());
                default -> paddingStr(str, size, StringUtils.BYTE_SPACE, StringUtils.CHARSET_UTF8);
            };
        }
    }

    // UTF-8 바이트 길이에 맞게 문자열을 잘라내는 메서드
    public String cutStringToByteLength(String str, int maxLengthInBytes) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        if (strBytes.length <= maxLengthInBytes) {
            return str;  // 입력 길이가 제한보다 작거나 같다면 변환 없이 반환
        }

        int byteCount = 0; // 실제 사용할 바이트 수를 계산
        int i = 0;  // 바이트 배열을 탐색하는 인덱스
        while (i < strBytes.length && byteCount < maxLengthInBytes) {
            if ((strBytes[i] & 0x80) == 0) { // 1바이트 문자
                byteCount++;
            } else if ((strBytes[i] & 0xE0) == 0xC0) { // 2바이트 문자 시작
                if (byteCount + 2 > maxLengthInBytes) break; // 추가하면 maxLength 초과하므로 중단
                byteCount += 2;
            } else if ((strBytes[i] & 0xF0) == 0xE0) { // 3바이트 문자 시작
                if (byteCount + 3 > maxLengthInBytes) break; // 추가하면 maxLength 초과하므로 중단
                byteCount += 3;
            } else if ((strBytes[i] & 0xF8) == 0xF0) { // 4바이트 문자 시작
                if (byteCount + 4 > maxLengthInBytes) break; // 추가하면 maxLength 초과하므로 중단
                byteCount += 4;
            }
            i++;
        }

        String resultString = new String(strBytes, 0, byteCount, StandardCharsets.UTF_8); // 최대 길이 내의 문자열 생성

        return paddingStr(resultString, maxLengthInBytes, StringUtils.BYTE_SPACE, StringUtils.CHARSET_UTF8);

    }

    // 바이트 길이에 맞게 문자열에 '-'를 추가하여 패딩하는 메서드
    private String padWithCharacter(String str, int maxLengthInBytes, char c) {
        StringBuilder stringBuilder = new StringBuilder(str);
        byte[] strBytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        while (strBytes.length < maxLengthInBytes) {
            stringBuilder.append(c);
            strBytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        }
        return stringBuilder.toString();
    }

    public String paddingStr(String str, int size, byte FILTER, String charset) {
        try {
            byte[] buff = null;
            if (charset == null || "".equals(charset)) {
                buff = str.getBytes();
            } else {
                buff = str.getBytes(charset);
            }

            int i=0;
            byte[] nbuff = new byte[size];
            System.arraycopy(buff, 0, nbuff, 0, buff.length);
            for (i = buff.length; i < size; i++) {
                nbuff[i] = FILTER;
            }

            return new String(nbuff, charset);
        } catch (Exception e){
            log.debug(e.getMessage(), e);
        }
        return null;
    }




    /*
        *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*        전문 > JSON         *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     */


    public Map<String, Object> transformFixedLengthToMap(String fixedLengthStr, ItfInfo rootItfInfo) {
        Map<String, Object> resultMap = new HashMap<>();
        byte[] dataBytes = fixedLengthStr.getBytes(StandardCharsets.UTF_8);
        parseFixedLengthString(dataBytes, 0, rootItfInfo, resultMap);
        return resultMap;
    }

    // 메소드 정의: 고정 길이의 바이트 배열을 파싱하여 Map 객체로 변환
    private int parseFixedLengthString(byte[] dataBytes, int start, ItfInfo info, Map<String, Object> map) {
        // 현재 처리 위치 초기화
        int localStart = start;

        List<ItfInfo> sortedNodes = new ArrayList<>(info.getNodes());
        sortedNodes.sort(Comparator.comparingInt(a -> a.getNodeValue().getIndex()));

        for (ItfInfo child : sortedNodes) {
            String key = child.getFieldName();
            int fieldLength = child.getNodeValue().getSize();
            int count = 1;

            String countKey = key + StringUtils.CNT_SUFFIX;
            if (map.containsKey(countKey)) {
                count = Integer.parseInt((String) map.get(countKey));
            }

            for (int i = 0; i < count; i++) {
                // Parse data but do not add to map if not visible
                if (!child.getNodeValue().isVisible()) {
                    localStart += (child.getItfType() == ItfType.FIELD.getType()) ? fieldLength : parseFixedLengthString(dataBytes, localStart, child, new HashMap<>());
                    continue; // Skip adding to map
                }

                if (child.getItfType() == ItfType.FIELD.getType()) {
                    String fieldValue = new String(dataBytes, localStart, fieldLength, StandardCharsets.UTF_8).trim();
                    localStart += fieldLength;

                    if (StringUtils.TYPE_LIST.equals(child.getNodeValue().getType()) || count > 1) {
                        List<Object> list = (List<Object>) map.getOrDefault(key, new ArrayList<>());
                        list.add(fieldValue);
                        map.put(key, list);
                    } else {
                        map.put(key, fieldValue);
                    }
                } else if (child.getItfType() == ItfType.GROUP.getType()) {
                    Map<String, Object> childMap = new HashMap<>();
                    int parsedLength = parseFixedLengthString(dataBytes, localStart, child, childMap);

                    if (StringUtils.TYPE_LIST.equals(child.getNodeValue().getType()) || count > 1) {
                        List<Map<String, Object>> childList = (List<Map<String, Object>>) map.getOrDefault(key, new ArrayList<>());
                        childList.add(childMap);
                        map.put(key, childList);
                    } else {
                        map.put(key, childMap);
                    }
                    localStart += parsedLength;
                }
            }
        }

        return localStart - start;
    }

    // JSON 테스트 출력용
    public String mapToJson(Map<String, Object> map) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
    }

}

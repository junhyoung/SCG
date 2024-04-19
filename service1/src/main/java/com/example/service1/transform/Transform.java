package com.example.service1.transform;

import com.example.service1.apis.ApiContext;
import com.example.service1.resolver.Resolver;
import com.example.service1.resolver.ResolverFactory;
import com.example.service1.util.SecurityUtil;
import com.example.service1.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class Transform {

    private final ResolverFactory resolverFactory;
    @Autowired
    public Transform(ResolverFactory resolverFactory) {
        this.resolverFactory = resolverFactory;
    }

    // 임시로 선언해둠
    private ApiContext apiContext;


    /*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*        JSON > 전문         *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     */
    public String transformJsonToFixedLength(String json, NodeInfo rootNodeInfo, ApiContext apiContext) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        this.apiContext = apiContext;

        // 결과를 저장할 StringBuilder 생성
        StringBuilder resultBuilder = new StringBuilder();

        // json to Map
        Map<?, ?> resultMap = objectMapper.convertValue(rootNode, Map.class);

        String result = traverseNodeInfo(resultMap, rootNodeInfo, resultBuilder);
        log.info("result > {}" , result);
        return result;

    }

    public String traverseNodeInfo(Object currentObj, NodeInfo currentNodeInfo, StringBuilder resultBuilder) throws JsonProcessingException {
        // currentObj가 Map인 경우
        if (currentObj instanceof Map<?, ?>) {
            Map<String, Object> currentMap = (Map<String, Object>) currentObj;

            // NodeValue의 인덱스에 따라 nodes 리스트를 정렬
            List<NodeInfo> sortedNodes = new ArrayList<>(currentNodeInfo.getNodes());
            sortedNodes.sort(Comparator.comparingInt(a -> a.getNodeValue().getIndex()));

            for (NodeInfo childNodeInfo : sortedNodes) {
                Object childObj = currentMap.get(childNodeInfo.getFieldName());

                // 필드 타입이 'F'인 경우 값을 직접 추출
                if (childNodeInfo.getItfType() == NodeType.FIELD.getType() && childObj != null) {

                    String fieldValue = childObj.toString();

                    // 여기에서부터 필드관련 처리를 진행한다. 복호화 등

                    if (childNodeInfo.getNodeValue().getDecrypt() != null) {
                        log.info("복호화 전 >> " + fieldValue);
                        Resolver resolver = resolverFactory.getResolver(childNodeInfo.getNodeValue().getDecrypt());
                        fieldValue = resolver.resolve(fieldValue, SecurityUtil.getEncryptKey(apiContext.getClientSecret(), apiContext.getTimestamp())).toString();
                        log.info("복호화 후 >> " + fieldValue);
                    }

                    String paddStr = handleFieldLength(fieldValue, childNodeInfo);

                    resultBuilder.append(paddStr);
                } else if (childNodeInfo.getItfType() == NodeType.GROUP.getType() && childObj != null) {
                    // 필드 타입이 'G'인 경우 재귀적으로 처리
                    traverseNodeInfo(childObj, childNodeInfo, resultBuilder);
                }
            }
        } else if (currentObj instanceof List<?> currentList) {
            // currentObj가 List인 경우
            for (Object item : currentList) {
                traverseNodeInfo(item, currentNodeInfo, resultBuilder);
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
            strBuf.append(paddingStr(tokens[1], fsize, StringUtil.BYTE_ZERO, StringUtil.CHARSET_UTF8));
        }

        return strBuf.toString();
    }


    // UTF-8 바이트 길이에 따라 문자열을 처리하는 메서드
    private String handleFieldLength(String str, NodeInfo nodeInfo) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        int size = nodeInfo.getNodeValue().getSize();
        String type = nodeInfo.getNodeValue().getType();

        if (strBytes.length > size) {
            // 바이트 길이에 맞게 문자열을 잘라냄
            return cutStringToByteLength(str, size);
        } else {
            // 바이트 길이에 맞게 문자를 추가하여 패딩
            return switch (type) {
                case StringUtil.TYPE_INT -> paddingInt(str, size);
                case StringUtil.TYPE_FLOAT -> paddingFloat(str, size, nodeInfo.getNodeValue().getFsize());
                default -> paddingStr(str, size, StringUtil.BYTE_SPACE, StringUtil.CHARSET_UTF8);
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

        return paddingStr(resultString, maxLengthInBytes, StringUtil.BYTE_SPACE, StringUtil.CHARSET_UTF8);

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


    public Map<String, Object> transformFixedLengthToMap(String fixedLengthStr, NodeInfo rootNodeInfo) {
        Map<String, Object> resultMap = new HashMap<>();
        byte[] dataBytes = fixedLengthStr.getBytes(StandardCharsets.UTF_8);
        parseFixedLengthString(dataBytes, 0, rootNodeInfo, resultMap);
        return resultMap;
    }

    // 메서드 설명: 바이트 배열에서 시작 위치를 기준으로 데이터를 파싱하고 맵에 저장
    private int parseFixedLengthString(byte[] dataBytes, int start, NodeInfo info, Map<String, Object> map) {
        // 시작 인덱스 초기화
        int localStart = start;

        // info에서 제공하는 노드 정보를 기반으로 노드들을 정렬
        List<NodeInfo> sortedNodes = new ArrayList<>(info.getNodes());
        sortedNodes.sort(Comparator.comparingInt(a -> a.getNodeValue().getIndex())); // 노드를 인덱스 순으로 정렬

        // 정렬된 노드 리스트를 반복 처리
        for (NodeInfo child : sortedNodes) {
            String key = child.getFieldName();               // 현재 노드의 필드명
            int fieldLength = child.getNodeValue().getSize(); // 현재 노드의 데이터 크기
            int count = 1;                                   // 기본적으로 한 번만 처리

            // 반복 횟수 지정 (_cnt 접미사 확인)
            String countKey = key + StringUtil.CNT_SUFFIX;
            if (map.containsKey(countKey)) {
                count = Integer.parseInt((String) map.get(countKey)); // 맵에서 반복 횟수 가져오기
            }

            // 지정된 횟수만큼 노드 처리 반복
            for (int i = 0; i < count; i++) {
                // 노드가 visible 아닌 경우 데이터 파싱만 수행하고 저장은 생략
                if (!child.getNodeValue().isVisible()) {
                    // FIELD 타입은 길이만큼 증가, GROUP 타입은 재귀 호출
                    localStart += (child.getItfType() == NodeType.FIELD.getType()) ? fieldLength : parseFixedLengthString(dataBytes, localStart, child, new HashMap<>());
                    continue; // 맵에 추가하지 않고 다음 반복으로 넘어감
                }

                // FIELD 타입의 데이터 처리
                if (child.getItfType() == NodeType.FIELD.getType()) {
                    // 데이터를 문자열로 변환 및 앞뒤 공백 제거
                    String fieldValue = new String(dataBytes, localStart, fieldLength, StandardCharsets.UTF_8).trim();

                    // 암호화 및 마스킹 체크
                    if (child.getNodeValue().getEncrypt() != null) {
                        log.info("암호화 전 >> " + fieldValue);
                        Resolver resolver = resolverFactory.getResolver(child.getNodeValue().getEncrypt());
                        fieldValue = resolver.resolve(fieldValue, SecurityUtil.getEncryptKey(apiContext.getClientSecret(), apiContext.getTimestamp())).toString();
                        System.out.println("암호화 후 >> " + fieldValue);
                    }

                    if (child.getNodeValue().getMasking() != null) {
                        log.info("마스킹 전 >> " + fieldValue);
                        Resolver resolver = resolverFactory.getResolver(child.getNodeValue().getMasking());
                        fieldValue = resolver.resolve(fieldValue).toString();
                        System.out.println("마스킹 후 >> " + fieldValue);

                    }

                    localStart += fieldLength; // 처리 위치 업데이트

                    // 리스트 타입 또는 반복 횟수가 1보다 많은 경우 리스트 처리
                    if (StringUtil.TYPE_LIST.equals(child.getNodeValue().getType()) || count > 1) {
                        List<Object> list = (List<Object>) map.getOrDefault(key, new ArrayList<>());
                        list.add(fieldValue);
                        map.put(key, list);
                    } else {
                        map.put(key, fieldValue); // 단일 객체 처리
                    }
                } else if (child.getItfType() == NodeType.GROUP.getType()) {
                    // GROUP 타입의 데이터 처리 (하위 맵 객체 생성 및 재귀 호출로 파싱)
                    Map<String, Object> childMap = new HashMap<>();
                    int parsedLength = parseFixedLengthString(dataBytes, localStart, child, childMap);

                    // 리스트 처리 또는 단일 객체 처리
                    if (StringUtil.TYPE_LIST.equals(child.getNodeValue().getType()) || count > 1) {
                        List<Map<String, Object>> childList = (List<Map<String, Object>>) map.getOrDefault(key, new ArrayList<>());
                        childList.add(childMap);
                        map.put(key, childList);
                    } else {
                        map.put(key, childMap);
                    }
                    localStart += parsedLength; // 처리 위치 업데이트
                }
            }
        }

        return localStart - start; // 처리한 전체 길이 반환
    }

        // JSON 테스트 출력용
    public String mapToJson(Map<String, Object> map) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
    }

}

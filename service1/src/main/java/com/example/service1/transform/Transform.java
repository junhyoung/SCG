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

    public String formatIntWithZeroPadding(int number, int size) {
        // %0Nd를 사용하여 숫자를 N 길이의 문자열로 포맷팅하고, 왼쪽 누락된 자리는 0으로 채웁니다.
        String format = String.format("%%0%dd", size);
        return String.format(format, number);
    }

    public String formatDoubleWithZeroPadding(double number, int totalLength) {
        // 문자열로 변환하여 먼저 길이를 확인
        String numberStr = String.valueOf(number);
        // 실제 숫자 부분의 길이 계산 (소수점과 소수점 이하 자릿수 포함)
        int decimalPointIndex = numberStr.indexOf(".");
        int requiredPadding = totalLength - numberStr.length();

        // 필요한 0 패딩을 계산
        StringBuilder padding = new StringBuilder();
        for (int i = 0; i < requiredPadding; i++) {
            padding.append('0');
        }

        // 0 패딩을 숫자 앞에 추가
        return padding.toString() + numberStr;
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
                case StringUtils.TYPE_INT -> formatIntWithZeroPadding(Integer.parseInt(str), size);
                case StringUtils.TYPE_FLOAT -> formatDoubleWithZeroPadding(Double.parseDouble(str), size);
                default -> padWithCharacter(str, size, StringUtils.HYPHEN);
            };
        }
    }

    // UTF-8 바이트 길이에 맞게 문자열을 잘라내는 메서드
    private String cutStringToByteLength(String str, int maxLengthInBytes) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        if (strBytes.length <= maxLengthInBytes) {
            return str;
        }

        int actualLength = maxLengthInBytes;

        // 멀티바이트 문자의 경계를 조정합니다
        while (actualLength > 0 && (strBytes[actualLength - 1] & 0xC0) == 0x80) {
            actualLength--;  // 멀티바이트 문자의 시작 바이트로 이동
        }

        // UTF-8 첫 바이트의 경계에서 정확하게 자르기 위한 추가 검사
        if (actualLength > 0 && (strBytes[actualLength - 1] & 0xC0) == 0xC0) {
            actualLength--;  // 첫 바이트를 더 후퇴시켜 자릅니다.
        }

        String resultString = new String(strBytes, 0, actualLength, StandardCharsets.UTF_8);
        return padWithCharacter(resultString, maxLengthInBytes, StringUtils.HYPHEN);

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

    private int parseFixedLengthString(byte[] dataBytes, int start, ItfInfo info, Map<String, Object> map) {
        int localStart = start;
        List<ItfInfo> sortedNodes = new ArrayList<>(info.getNodes());
        sortedNodes.sort(Comparator.comparingInt(a -> a.getNodeValue().getIndex()));

        for (ItfInfo child : sortedNodes) {
            String key = child.getFieldName();
            int fieldLength = child.getNodeValue().getSize();
            int count = 1; // 기본적으로 1회 반복

            // _cnt 키의 존재 여부 확인하여 반복 횟수 설정
            String countKey = key + StringUtils.CNT_SUFFIX;
            if (map.containsKey(countKey)) {
                count = Integer.parseInt((String) map.get(countKey));
            }

            for (int i = 0; i < count; i++) {
                if (child.getItfType() == ItfType.FIELD.getType()) {
                    String fieldValue = new String(dataBytes, localStart, fieldLength, StandardCharsets.UTF_8).trim();
                    localStart += fieldLength;

                    if (StringUtils.TYPE_LIST.equals(child.getNodeValue().getType()) || count > 1) {
                        // 타입이 'List'이거나 _cnt 값이 1보다 큰 경우 배열 처리
                        List<Object> list = (List<Object>) map.getOrDefault(key, new ArrayList<>());
                        list.add(fieldValue);
                        map.put(key, list);
                    } else {
                        // 타입이 'List'가 아니고 _cnt 값이 1인 경우 단일 객체 처리
                        map.put(key, fieldValue);
                    }
                } else if (child.getItfType() == ItfType.GROUP.getType()) {
                    Map<String, Object> childMap = new HashMap<>();
                    int parsedLength = parseFixedLengthString(dataBytes, localStart, child, childMap);

                    if (StringUtils.TYPE_LIST.equals(child.getNodeValue().getType()) || count > 1) {
                        // 타입이 'List'이거나 _cnt 값이 1보다 큰 경우 배열 처리
                        List<Map<String, Object>> childList = (List<Map<String, Object>>) map.getOrDefault(key, new ArrayList<>());
                        childList.add(childMap);
                        map.put(key, childList);
                    } else {
                        // 타입이 'List'가 아니고 _cnt 값이 1인 경우 단일 객체 처리
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



    /*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*        MAIN 함수 TEST      *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     */


/*
    public static void main(String[] args) throws JsonProcessingException {
        ItfInfo title = new ItfInfo(
                "title",
                'F',
                new ItfInfo.NodeValue(1, 7, StringUtils.TYPE_STRING, "title", "test", true, false),
                null
        );
        ItfInfo personalItfInfo = new ItfInfo(
                "personalInfo",
                'G',
                new ItfInfo.NodeValue(0, 2, StringUtils.TYPE_OBJECT, "personalInfo", "Personal Information Group", true, false),
                Arrays.asList(
                        new ItfInfo("email", 'F', new ItfInfo.NodeValue(1, 30, StringUtils.TYPE_STRING, "email", "Email of the person", true, false), null),
                        new ItfInfo("name", 'F', new ItfInfo.NodeValue(0, 10, StringUtils.TYPE_STRING, "name", "Name of the person", true, false), null)
                )
        );
        ItfInfo education_cnt = new ItfInfo(
                "education_cnt",
                'F',
                new ItfInfo.NodeValue(2, 5, StringUtils.TYPE_INT, "education_cnt", "education_cnt", true, false),
                null
        );
        ItfInfo projects_cnt = new ItfInfo(
                "projects_cnt",
                'F',
                new ItfInfo.NodeValue(4, 5, StringUtils.TYPE_INT, "projects_cnt", "projects_cnt", true, false),
                null
        );
        ItfInfo education = new ItfInfo(
                "education",
                'G',
                new ItfInfo.NodeValue(3, 3, StringUtils.TYPE_LIST, "education", "Education Information Group", true, false),
                Arrays.asList(
                        new ItfInfo("degree", 'F', new ItfInfo.NodeValue(1, 50, StringUtils.TYPE_STRING, "degree", "Degree obtained", true, false), null),
                        new ItfInfo("institution", 'G', new ItfInfo.NodeValue(0, 2, StringUtils.TYPE_OBJECT, "institution", "Institution Information", true, false),
                                Arrays.asList(
                                        new ItfInfo("name", 'F', new ItfInfo.NodeValue(2, 20,  StringUtils.TYPE_STRING, "name", "name Test Field", true, false), null),
                                        new ItfInfo("test", 'F', new ItfInfo.NodeValue(1, 20,  StringUtils.TYPE_STRING, "test", "Test Field", true, false), null),
                                        new ItfInfo("test2", 'F', new ItfInfo.NodeValue(0, 20, StringUtils.TYPE_STRING, "test2", "Second Test Field", true, false), null)
                                )),
                        new ItfInfo("year", 'F', new ItfInfo.NodeValue(2, 10, StringUtils.TYPE_FLOAT, "year", "Year of Graduation", true, false), null)
                )
        );

        ItfInfo project = new ItfInfo(
                "projects",
                'G',
                new ItfInfo.NodeValue(5, 4, StringUtils.TYPE_LIST, "project1", "First Project", true, false),
                Arrays.asList(
                        new ItfInfo("title", 'F', new ItfInfo.NodeValue(0, 30, StringUtils.TYPE_STRING, "title", "Project Title", true, false), null),
                        new ItfInfo("description", 'F', new ItfInfo.NodeValue(1, 100, StringUtils.TYPE_STRING, "description", "Project Description", true, false), null),
                        new ItfInfo("technologies", 'F', new ItfInfo.NodeValue(2, 100, "Array", "technologies", "Technologies Used", true, false), null)
                )
        );

        ItfInfo root = new ItfInfo(
                "Root",
                'G',
                new ItfInfo.NodeValue(0, 3, StringUtils.TYPE_OBJECT, "root", "Root Node", true, false),
                Arrays.asList(title, personalItfInfo, education, project, education_cnt,projects_cnt)
        );


        String json = "{\n" +
                "  \"title\": \"타이틀\",\n" +
                "  \"personalInfo\": {\n" +
                "    \"name\": \"Jane Doe\",\n" +
                "    \"email\": \"jane.doe@example.com\",\n" +
                "    \"location\": \"New York, NY\"\n" +
                "  },\n" +
                "  \"education_cnt\": 2,\n" +
                "  \"education\": [\n" +
                "    {\n" +
                "      \"degree\": \"Bachelor of Science in Computer Science\",\n" +
                "      \"institution\": {\n" +
                "        \"name\": \"University of Example\",\n" +
                "        \"test\": \"Example Value 1\",\n" +
                "        \"test2\": \"Example Value 2\"\n" +
                "      },\n" +
                "      \"year\": 2020.122\n" +
                "    },\n" +
                "    {\n" +
                "      \"degree\": \"Master of Science in Artificial Intelligence\",\n" +
                "      \"institution\": {\n" +
                "        \"name\": \"Institute of Advanced Study\",\n" +
                "        \"test\": \"Advanced Example 1\",\n" +
                "        \"test2\": \"Advanced Example 2\"\n" +
                "      },\n" +
                "      \"year\": 2022\n" +
                "    }\n" +
                "  ],\n" +
                "  \"projects\": [\n" +
                "    {\n" +
                "      \"title\": \"Smart Agriculture System\",\n" +
                "      \"description\": \"Developed a system to optimize water usage for farming using IoT sensors and AI.\",\n" +
                "      \"technologies\": [\"IoT\", \"Artificial Intelligence\", \"Cloud Computing\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"title\": \"E-Health Record Management System\",\n" +
                "      \"description\": \"Designed and implemented a secure and scalable web application for managing patient records.\",\n" +
                "      \"technologies\": [\"Web Development\", \"Database Management\", \"Cybersecurity\"]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"projects_cnt\": 2\n" +
                "}\n";
        try {
           String fixedLengthStr = transformJsonToFixedLength(json, root);

            Map<String, Object> resultMap = transformFixedLengthToMap(fixedLengthStr, root);
            String jsonResult = mapToJson(resultMap);
            System.out.println("fixed to json >> " + jsonResult);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    */
}

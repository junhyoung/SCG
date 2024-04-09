package com.example.service1.transform;

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

    private static final char HYPHEN = '-';
    private static final char SPACE = ' ';

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
                    String paddStr = handleFieldLength(childObj.toString(), childItfInfo.getNodeValue().getSize());
                    resultBuilder.append(paddStr);
//                    resultBuilder.append(System.lineSeparator());
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

    // UTF-8 바이트 길이에 따라 문자열을 처리하는 메서드
    private String handleFieldLength(String str, int maxLengthInBytes) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        if (strBytes.length > maxLengthInBytes) {
            // 바이트 길이에 맞게 문자열을 잘라냄
            return cutStringToByteLength(str, maxLengthInBytes);
        } else {
            // 바이트 길이에 맞게 문자를 추가하여 패딩
            return padWithCharacter(str, maxLengthInBytes, HYPHEN);
        }
    }

    // UTF-8 바이트 길이에 맞게 문자열을 잘라내는 메서드
    private String cutStringToByteLength(String str, int maxLengthInBytes) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        if (strBytes.length <= maxLengthInBytes) {
            return str;
        }
        // 바이트 배열을 maxLengthInBytes까지 잘라냄
        byte[] resultBytes = Arrays.copyOf(strBytes, maxLengthInBytes);
        // 잘라낸 바이트 배열을 다시 문자열로 변환
        return new String(resultBytes, StandardCharsets.UTF_8);
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
    public static void main(String[] args) throws JsonProcessingException {
        ItfInfo title = new ItfInfo(
                "title",
                'F',
                new ItfInfo.NodeValue(1, 7, "String", "title", "test", true, false),
                null
        );
        ItfInfo personalItfInfo = new ItfInfo(
                "personalInfo",
                'G',
                new ItfInfo.NodeValue(0, 2, "Object", "personalInfo", "Personal Information Group", true, false),
                Arrays.asList(
                        new ItfInfo("email", 'F', new ItfInfo.NodeValue(1, 30, "String", "email", "Email of the person", true, false), null),
                        new ItfInfo("name", 'F', new ItfInfo.NodeValue(0, 10, "String", "name", "Name of the person", true, false), null)
                )
        );
        ItfInfo education = new ItfInfo(
                "education",
                'G',
                new ItfInfo.NodeValue(3, 3, "Object", "education", "Education Information Group", true, false),
                Arrays.asList(
                        new ItfInfo("degree", 'F', new ItfInfo.NodeValue(1, 50, "String", "degree", "Degree obtained", true, false), null),
                        new ItfInfo("institution", 'G', new ItfInfo.NodeValue(0, 2, "Object", "institution", "Institution Information", true, false),
                                Arrays.asList(
                                        new ItfInfo("name", 'F', new ItfInfo.NodeValue(2, 20, "String", "name", "name Test Field", true, false), null),
                                        new ItfInfo("test", 'F', new ItfInfo.NodeValue(1, 20, "String", "test", "Test Field", true, false), null),
                                        new ItfInfo("test2", 'F', new ItfInfo.NodeValue(0, 20, "String", "test2", "Second Test Field", true, false), null)
                                )),
                        new ItfInfo("year", 'F', new ItfInfo.NodeValue(2, 4, "Int", "year", "Year of Graduation", true, false), null)
                )
        );

        ItfInfo project = new ItfInfo(
                "projects",
                'G',
                new ItfInfo.NodeValue(2, 3, "Object", "project1", "First Project", true, false),
                Arrays.asList(
                        new ItfInfo("title", 'F', new ItfInfo.NodeValue(0, 30, "String", "title", "Project Title", true, false), null),
                        new ItfInfo("description", 'F', new ItfInfo.NodeValue(1, 100, "String", "description", "Project Description", true, false), null),
                        new ItfInfo("technologies", 'F', new ItfInfo.NodeValue(2, 100, "Array", "technologies", "Technologies Used", true, false), null)
                )
        );

        ItfInfo root = new ItfInfo(
                "Root",
                'G',
                new ItfInfo.NodeValue(0, 3, "Object", "root", "Root Node", true, false),
                Arrays.asList(title, personalItfInfo, education, project)
        );


        String json = "{\n" +
                "  \"title\": \"타이틀\",\n" +
                "  \"personalInfo\": {\n" +
                "    \"name\": \"Jane Doe\",\n" +
                "    \"email\": \"jane.doe@example.com\",\n" +
                "    \"location\": \"New York, NY\"\n" +
                "  },\n" +
                "  \"education\": [\n" +
                "    {\n" +
                "      \"degree\": \"Bachelor of Science in Computer Science\",\n" +
                "      \"institution\": {\n" +
                "        \"name\": \"University of Example\",\n" +
                "        \"test\": \"Example Value 1\",\n" +
                "        \"test2\": \"Example Value 2\"\n" +
                "      },\n" +
                "      \"year\": 2020\n" +
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
                "  ]\n" +
                "}\n";
        try {
            transformJsonToFixedLength(json, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 */
}

package com.example.service1.transform;

import com.example.service1.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransformTest {

    Transform transform = new Transform();

    @Test
    void cutStringToByteLengthTest() {
        String str = "호111";
        int size = 7;
        System.out.println(transform.cutStringToByteLength(str, size));
    }

    @Test
    void transformTest() {

        ItfInfo title = ItfInfo.builder()
                .fieldName("title")
                .itfType('F')
                .nodeValue(ItfInfo.NodeValue.builder()
                        .index(1)
                        .size(7)
                        .type(StringUtils.TYPE_STRING)
                        .visible(true)
                        .build())
                .build();
        ItfInfo personalInfo = ItfInfo.builder()
                .fieldName("personalInfo")
                .itfType('G')
                .nodeValue(ItfInfo.NodeValue.builder()
                        .index(0)
                        .size(2)
                        .type(StringUtils.TYPE_OBJECT)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        ItfInfo.builder().fieldName("name").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(0).size(10).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                        ItfInfo.builder().fieldName("email").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(1).size(30).type(StringUtils.TYPE_STRING).visible(true).build()).build()
                ))
                .build();
        ItfInfo education_cnt = ItfInfo.builder()
                .fieldName("education_cnt")
                .itfType('F')
                .nodeValue(ItfInfo.NodeValue.builder()
                        .index(2)
                        .size(5)
                        .type(StringUtils.TYPE_INT)
                        .visible(true)
                        .build())
                .build();
        ItfInfo projects_cnt = ItfInfo.builder()
                .fieldName("projects_cnt")
                .itfType('F')
                .nodeValue(ItfInfo.NodeValue.builder()
                        .index(4)
                        .size(5)
                        .type(StringUtils.TYPE_INT)
                        .visible(true)
                        .build())
                .build();
        ItfInfo education = ItfInfo.builder()
                .fieldName("education")
                .itfType('G')
                .nodeValue(ItfInfo.NodeValue.builder()
                        .index(3)
                        .size(3)
                        .type(StringUtils.TYPE_LIST)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        ItfInfo.builder().fieldName("degree").itfType('F').nodeValue(ItfInfo.NodeValue.builder()
                                                                                .index(0)
                                                                                .size(50)
                                                                                .type(StringUtils.TYPE_STRING)
                                                                                .visible(true)
                                                                                .build()).build(),
                        ItfInfo.builder().fieldName("year").itfType('F').nodeValue(ItfInfo.NodeValue.builder()
                                                                                .index(1)
                                                                                .size(10)
                                                                                .type(StringUtils.TYPE_INT)
                                                                                .visible(true)
                                                                                .build()).build(),
                        ItfInfo.builder().fieldName("institution").itfType('G').nodeValue(ItfInfo.NodeValue.builder()
                                                                                .index(2)
                                                                                .size(2)
                                                                                .type(StringUtils.TYPE_STRING)
                                                                                .visible(true).build())
                                                                .nodes( Arrays.asList(
                                                                        ItfInfo.builder().fieldName("name").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(0).size(20).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                                                                        ItfInfo.builder().fieldName("test").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(1).size(20).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                                                                        ItfInfo.builder().fieldName("test2").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(2).size(20).type(StringUtils.TYPE_STRING).visible(true).build()).build()
                                                                        )
                                                                ).build())
                ).build();

        ItfInfo project = ItfInfo.builder()
                .fieldName("projects")
                .itfType('G')
                .nodeValue(ItfInfo.NodeValue.builder()
                        .index(5)
                        .size(4)
                        .type(StringUtils.TYPE_LIST)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        ItfInfo.builder().fieldName("title").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(0).size(30).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                        ItfInfo.builder().fieldName("description").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(1).size(100).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                        ItfInfo.builder().fieldName("technologies").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(2).size(100).type(StringUtils.TYPE_ARRAY).visible(true).build()).build()
                ))
                .build();

        ItfInfo root = ItfInfo.builder()
                .fieldName("Root")
                .itfType('G')
                .nodeValue(ItfInfo.NodeValue.builder()
                        .index(0)
                        .size(1)
                        .type(StringUtils.TYPE_OBJECT)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(title, personalInfo, education, project, education_cnt,projects_cnt))
                .build();


        String json = "{\n" +
                "  \"title\": \"1호11\",\n" +
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
            String fixedLengthStr = transform.transformJsonToFixedLength(json, root);

            Map<String, Object> resultMap = transform.transformFixedLengthToMap(fixedLengthStr, root);
            String jsonResult = transform.mapToJson(resultMap);
            System.out.println("fixed to json >> " + jsonResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
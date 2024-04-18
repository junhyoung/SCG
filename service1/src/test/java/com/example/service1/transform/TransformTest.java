package com.example.service1.transform;

import com.example.service1.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransformTest {

    @Autowired
    Transform transform;

    @Test
    void cutStringToByteLengthTest() {
        String str = "호111";
        int size = 7;
        System.out.println(transform.cutStringToByteLength(str, size));
    }

    @Test
    void transformTest() {

        NodeInfo title = NodeInfo.builder()
                .fieldName("title")
                .itfType('F')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(1)
                        .size(20)
                        .type(StringUtils.TYPE_STRING)
                        .visible(true)
//                        .decrypt("decryptResolver")
                        .masking("maskingResolverName")
                        .build())
                .build();
        NodeInfo personalInfo = NodeInfo.builder()
                .fieldName("personalInfo")
                .itfType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(0)
                        .size(2)
                        .type(StringUtils.TYPE_OBJECT)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        NodeInfo.builder().fieldName("name").itfType('F').nodeValue(NodeInfo.NodeValue.builder().index(0).size(10).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                        NodeInfo.builder().fieldName("email").itfType('F').nodeValue(NodeInfo.NodeValue.builder().index(1).size(30).type(StringUtils.TYPE_STRING).visible(true).build()).build()
                ))
                .build();
        NodeInfo education_cnt = NodeInfo.builder()
                .fieldName("education_cnt")
                .itfType('F')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(2)
                        .size(5)
                        .type(StringUtils.TYPE_INT)
                        .visible(true)
                        .build())
                .build();
        NodeInfo projects_cnt = NodeInfo.builder()
                .fieldName("projects_cnt")
                .itfType('F')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(4)
                        .size(5)
                        .type(StringUtils.TYPE_INT)
                        .visible(true)
                        .build())
                .build();
        NodeInfo education = NodeInfo.builder()
                .fieldName("education")
                .itfType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(3)
                        .size(3)
                        .type(StringUtils.TYPE_LIST)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        NodeInfo.builder().fieldName("degree").itfType('F').nodeValue(NodeInfo.NodeValue.builder()
                                                                                .index(0)
                                                                                .size(50)
                                                                                .type(StringUtils.TYPE_STRING)
                                                                                .visible(true)
                                                                                .build()).build(),
                        NodeInfo.builder().fieldName("year").itfType('F').nodeValue(NodeInfo.NodeValue.builder()
                                                                                .index(1)
                                                                                .size(10)
                                                                                .type(StringUtils.TYPE_INT)
                                                                                .visible(true)
                                                                                .build()).build(),
                        NodeInfo.builder().fieldName("institution").itfType('G').nodeValue(NodeInfo.NodeValue.builder()
                                                                                .index(2)
                                                                                .size(2)
                                                                                .type(StringUtils.TYPE_STRING)
                                                                                .visible(true).build())
                                                                .nodes( Arrays.asList(
                                                                        NodeInfo.builder().fieldName("name").itfType('F').nodeValue(NodeInfo.NodeValue.builder().index(0).size(20).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                                                                        NodeInfo.builder().fieldName("test").itfType('F').nodeValue(NodeInfo.NodeValue.builder().index(1).size(20).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                                                                        NodeInfo.builder().fieldName("test2").itfType('F').nodeValue(NodeInfo.NodeValue.builder().index(2).size(20).type(StringUtils.TYPE_STRING).visible(true).build()).build()
                                                                        )
                                                                ).build())
                ).build();

        NodeInfo project = NodeInfo.builder()
                .fieldName("projects")
                .itfType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(5)
                        .size(4)
                        .type(StringUtils.TYPE_LIST)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        NodeInfo.builder().fieldName("title").itfType('F').nodeValue(NodeInfo.NodeValue.builder().index(0).size(30).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                        NodeInfo.builder().fieldName("description").itfType('F').nodeValue(NodeInfo.NodeValue.builder().index(1).size(100).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
                        NodeInfo.builder().fieldName("technologies").itfType('F').nodeValue(NodeInfo.NodeValue.builder().index(2).size(100).type(StringUtils.TYPE_ARRAY).visible(true).build()).build()
                ))
                .build();

        NodeInfo root = NodeInfo.builder()
                .fieldName("Root")
                .itfType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(0)
                        .size(1)
                        .type(StringUtils.TYPE_OBJECT)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(title, personalInfo, education, project, education_cnt,projects_cnt))
                .build();


        String json = "{\n" +
                "  \"title\": \"이지성\",\n" +
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
                "      \"title\": \"테스트용 제목\",\n" +
                "      \"description\": \"테스틑용 설명\",\n" +
                "      \"technologies\": [\"IoT\", \"Artificial Intelligence\", \"Cloud Computing\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"title\": \"E-Health Record Management System\",\n" +
                "      \"description\": \"Designed and implemented a secure and scalable web application for managing patient records.\",\n" +
                "      \"technologies\": [\"Web Development\", \"Database Management\", \"Cybersecurity\"]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"projects_cnt\": 3\n" +
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
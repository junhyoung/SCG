package com.example.service1.transform;

import com.example.service1.apis.ApiContext;
import com.example.service1.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    void getItfInfo() throws JsonProcessingException {
        String itfInfo = "[\n" +
                "    {\n" +
                "        \"nodeName\" : \"personalInfo\",\n" +
                "        \"nodeType\" : \"G\", \n" +
                "        \"nodeValue\" : {\n" +
                "            \"index\" : 0,\n" +
                "            \"size\" : 10,\n" +
                "            \"fsize\" : 0,\n" +
                "            \"type\" : \"object\",\n" +
                "            \"nodeId\" : \"personalInfo\",\n" +
                "            \"desc\" : \"G (Group) , F (Field)\",\n" +
                "            \"visible\" : true,\n" +
                "            \"nullable\" : false\n" +
                "        },\n" +
                "        \"nodes\" : [\n" +
                "            {\n" +
                "                \"nodeName\" : \"name\",\n" +
                "                \"nodeType\" : \"F\",\n" +
                "                \"nodeValue\" : {\n" +
                "                    \"index\" : 0,\n" +
                "                    \"size\" : 10,\n" +
                "                    \"fsize\" : 0,\n" +
                "                    \"type\" : \"string\",\n" +
                "                    \"nodeId\" : \"name\",\n" +
                "                    \"desc\" : \"string 테스트\",\n" +
                "                    \"visible\" : true,\n" +
                "                    \"nullable\" : false,\n" +
                "                    \"masking\" : \"NameMaskingResolver\"\n" +
                "                }   \n" +
                "            },\n" +
                "            {\n" +
                "                \"nodeName\" : \"age\",\n" +
                "                \"nodeType\" : \"F\",\n" +
                "                \"nodeValue\" : {\n" +
                "                    \"index\" : 1,\n" +
                "                    \"size\" : 5,\n" +
                "                    \"fsize\" : 0,\n" +
                "                    \"type\" : \"int\",\n" +
                "                    \"nodeId\" : \"age\",\n" +
                "                    \"desc\" : \"int 테스트\",\n" +
                "                    \"visible\" : true,\n" +
                "                    \"nullable\" : false\n" +
                "                }   \n" +
                "            },\n" +
                "            {\n" +
                "                \"nodeName\" : \"height\",\n" +
                "                \"nodeType\" : \"F\",\n" +
                "                \"nodeValue\" : {\n" +
                "                    \"index\" : 2,\n" +
                "                    \"size\" : 8,\n" +
                "                    \"fsize\" : 2,\n" +
                "                    \"type\" : \"float\",\n" +
                "                    \"nodeId\" : \"height\",\n" +
                "                    \"desc\" : \"float테스트\",\n" +
                "                    \"visible\" : true,\n" +
                "                    \"nullable\" : false,\n" +
                "                    \"encrypt\" : \"EncryptResolver\"\n" +
                "                }   \n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"nodeName\" : \"education_cnt\",\n" +
                "        \"nodeType\" : \"F\", \n" +
                "        \"nodeValue\" : {\n" +
                "            \"index\" : 1,\n" +
                "            \"size\" : 3,\n" +
                "            \"fsize\" : 0,\n" +
                "            \"type\" : \"int\",\n" +
                "            \"nodeId\" : \"education_cnt\",\n" +
                "            \"desc\" : \"educaiton 리스트 사이즈, 해당 리스트보다 인덱스가 작아야함\",\n" +
                "            \"visible\" : true,\n" +
                "            \"nullable\" : false\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"nodeName\" : \"education\",\n" +
                "        \"nodeType\" : \"G\", \n" +
                "        \"nodeValue\" : {\n" +
                "            \"index\" : 2,\n" +
                "            \"size\" : 3,\n" +
                "            \"fsize\" : 0,\n" +
                "            \"type\" : \"list\",\n" +
                "            \"nodeId\" : \"education\",\n" +
                "            \"desc\" : \"리스트 테스트\",\n" +
                "            \"visible\" : true,\n" +
                "            \"nullable\" : false\n" +
                "        },\n" +
                "        \"nodes\" : [\n" +
                "            {\n" +
                "                \"nodeName\" : \"degree\",\n" +
                "                \"nodeType\" : \"F\",\n" +
                "                \"nodeValue\" : {\n" +
                "                    \"index\" : 0,\n" +
                "                    \"size\" : 100,\n" +
                "                    \"fsize\" : 0,\n" +
                "                    \"type\" : \"string\",\n" +
                "                    \"nodeId\" : \"degree\",\n" +
                "                    \"desc\" : \"설명\",\n" +
                "                    \"visible\" : true,\n" +
                "                    \"nullable\" : false\n" +
                "                }   \n" +
                "            },\n" +
                "            {\n" +
                "                \"nodeName\" : \"year\",\n" +
                "                \"nodeType\" : \"F\",\n" +
                "                \"nodeValue\" : {\n" +
                "                    \"index\" : 1,\n" +
                "                    \"size\" : 4,\n" +
                "                    \"fsize\" : 0,\n" +
                "                    \"type\" : \"int\",\n" +
                "                    \"nodeId\" : \"year\",\n" +
                "                    \"desc\" : \"연도\",\n" +
                "                    \"visible\" : true,\n" +
                "                    \"nullable\" : false\n" +
                "                }   \n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]\n";

        ObjectMapper objectMapper = new ObjectMapper();

        List<NodeInfo> list = objectMapper.readValue(itfInfo, new TypeReference<List<NodeInfo>>() {});
        for (NodeInfo n : list) {
            System.out.println(n);
        }

        NodeInfo root = NodeInfo.builder()
                .nodeName("Root")
                .nodeType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(0)
                        .size(1)
                        .type(StringUtil.TYPE_OBJECT)
                        .visible(true)
                        .build())
                .nodes(list)
                .build();

        String json = "{\n" +
                "  \n" +
                "  \"education\" : [\n" +
                "    {\n" +
                "      \"year\" : 2020,\n" +
                "      \"degree\" : \"전자공학과\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"degree\" : \"경영학과\",\n" +
                "      \"year\" : 2022\n" +
                "    }\n" +
                "  ],\n" +
                "  \"education_cnt\" : 2,\n" +
                "  \"personalInfo\" : {\n" +
                "    \"name\" : \"로이킴\",\n" +
                "    \"age\" : 32,\n" +
                "    \"height\" : 175.5\n" +
                "  }\n" +
                "}";

        try {

            ApiContext apiContext = new ApiContext();
            apiContext.setClientSecret("jiseongtestsecret");
            apiContext.setTimestamp("20240419173600000");

            String fixedLengthStr = transform.transformJsonToFixedLength(json, root, apiContext);

            Map<String, Object> resultMap = transform.transformFixedLengthToMap(fixedLengthStr, root);
            String jsonResult = transform.mapToJson(resultMap);
            System.out.println("fixed to json >> " + jsonResult);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void transformTest() {

        NodeInfo title = NodeInfo.builder()
                .nodeName("title")
                .nodeType('F')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(1)
                        .size(20)
                        .type(StringUtil.TYPE_STRING)
                        .visible(true)
                        .decrypt("DecryptDefaultResolver")
                        .encrypt("EncryptDefaultResolver")
//                        .masking("NameMaskingResolver")
                        .build())
                .build();
        NodeInfo personalInfo = NodeInfo.builder()
                .nodeName("personalInfo")
                .nodeType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(0)
                        .size(2)
                        .type(StringUtil.TYPE_OBJECT)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        NodeInfo.builder().nodeName("name").nodeType('F').nodeValue(NodeInfo.NodeValue.builder().index(0).size(10).type(StringUtil.TYPE_STRING).visible(true).build()).build(),
                        NodeInfo.builder().nodeName("email").nodeType('F').nodeValue(NodeInfo.NodeValue.builder().index(1).size(30).type(StringUtil.TYPE_STRING).masking("EmailMaskingResolver").visible(true).build()).build()
                ))
                .build();
        NodeInfo education_cnt = NodeInfo.builder()
                .nodeName("education_cnt")
                .nodeType('F')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(2)
                        .size(5)
                        .type(StringUtil.TYPE_INT)
                        .visible(true)
                        .build())
                .build();
        NodeInfo projects_cnt = NodeInfo.builder()
                .nodeName("projects_cnt")
                .nodeType('F')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(4)
                        .size(5)
                        .type(StringUtil.TYPE_INT)
                        .visible(true)
                        .build())
                .build();
        NodeInfo education = NodeInfo.builder()
                .nodeName("education")
                .nodeType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(3)
                        .size(3)
                        .type(StringUtil.TYPE_LIST)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        NodeInfo.builder().nodeName("degree").nodeType('F').nodeValue(NodeInfo.NodeValue.builder()
                                                                                .index(0)
                                                                                .size(50)
                                                                                .type(StringUtil.TYPE_STRING)
                                                                                .visible(true)
                                                                                .build()).build(),
                        NodeInfo.builder().nodeName("year").nodeType('F').nodeValue(NodeInfo.NodeValue.builder()
                                                                                .index(1)
                                                                                .size(10)
                                                                                .type(StringUtil.TYPE_INT)
                                                                                .visible(true)
                                                                                .build()).build(),
                        NodeInfo.builder().nodeName("institution").nodeType('G').nodeValue(NodeInfo.NodeValue.builder()
                                                                                .index(2)
                                                                                .size(2)
                                                                                .type(StringUtil.TYPE_STRING)
                                                                                .visible(true).build())
                                                                .nodes( Arrays.asList(
                                                                        NodeInfo.builder().nodeName("name").nodeType('F').nodeValue(NodeInfo.NodeValue.builder().index(0).size(20).type(StringUtil.TYPE_STRING).visible(true).build()).build(),
                                                                        NodeInfo.builder().nodeName("test").nodeType('F').nodeValue(NodeInfo.NodeValue.builder().index(1).size(20).type(StringUtil.TYPE_STRING).visible(true).build()).build(),
                                                                        NodeInfo.builder().nodeName("test2").nodeType('F').nodeValue(NodeInfo.NodeValue.builder().index(2).size(20).type(StringUtil.TYPE_STRING).visible(true).build()).build()
                                                                        )
                                                                ).build())
                ).build();

        NodeInfo project = NodeInfo.builder()
                .nodeName("projects")
                .nodeType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(5)
                        .size(4)
                        .type(StringUtil.TYPE_LIST)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(
                        NodeInfo.builder().nodeName("title").nodeType('F').nodeValue(NodeInfo.NodeValue.builder().index(0).size(30).type(StringUtil.TYPE_STRING).visible(true).build()).build(),
                        NodeInfo.builder().nodeName("description").nodeType('F').nodeValue(NodeInfo.NodeValue.builder().index(1).size(100).type(StringUtil.TYPE_STRING).visible(true).build()).build(),
                        NodeInfo.builder().nodeName("technologies").nodeType('F').nodeValue(NodeInfo.NodeValue.builder().index(2).size(100).type(StringUtil.TYPE_ARRAY).visible(true).build()).build()
                ))
                .build();

        NodeInfo root = NodeInfo.builder()
                .nodeName("Root")
                .nodeType('G')
                .nodeValue(NodeInfo.NodeValue.builder()
                        .index(0)
                        .size(1)
                        .type(StringUtil.TYPE_OBJECT)
                        .visible(true)
                        .build())
                .nodes(Arrays.asList(title, personalInfo, education, project, education_cnt,projects_cnt))
                .build();


        String json = "{\n" +
                "  \"title\": \"qNzJJdefjVAlQVL/V5yIxaxr2WGBhPIwqBroXY1iQHc=\",\n" +
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

            ApiContext apiContext = new ApiContext();
            apiContext.setClientSecret("jiseongtestsecret");
            apiContext.setTimestamp("20240419173600000");

            String fixedLengthStr = transform.transformJsonToFixedLength(json, root, apiContext);

            Map<String, Object> resultMap = transform.transformFixedLengthToMap(fixedLengthStr, root);
            String jsonResult = transform.mapToJson(resultMap);
            System.out.println("fixed to json >> " + jsonResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
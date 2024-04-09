package com.example.service1.transform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItfInfo {

    private String fieldName;
    // 현재 노드가 그룹인지 필드인지 구분. (예를들면 G, F)
    private char itfType;
    private NodeValue nodeValue;
    private List<ItfInfo> nodes;

    @Data
    @AllArgsConstructor
    public static class NodeValue {
        // 상위 Info 에서 현재 필드의 인덱스
        private int index;
        // 현재 필드의 크기
        private int size;
        // 해당 필드 데이터타입 (예를들면 int, string, boolean, map 등)
        private String type;
        private String nodeId;
        private String desc;
        private boolean visible;
        private boolean nullable;
    }
}

package com.example.service1.transform;

import lombok.*;

import java.util.List;

@Data
@Builder
public class NodeInfo {

    private String fieldName;
    // 현재 노드가 그룹인지 필드인지 구분. (예를들면 G, F)
    private char itfType;
    private NodeValue nodeValue;
    private List<NodeInfo> nodes;

    @Data
    @Builder
    public static class NodeValue {
        // 상위 Info 에서 현재 필드의 인덱스
        private int index;
        // 현재 필드의 크기
        @Builder.Default
        private int size = 0;
        @Builder.Default
        private int fsize = 0;
        private String type;
        private String nodeId;
        private String desc;
        @Builder.Default
        private boolean visible = true;
        @Builder.Default
        private boolean nullable = false;

        @Builder
        public NodeValue(int index, int size, int fsize, String type, String nodeId, String desc, boolean visible, boolean nullable) {
            this.index = index;
            this.size = size;
            this.fsize = fsize;
            this.type = type;
            this.nodeId = nodeId;
            this.desc = desc;
            this.visible = visible;
            this.nullable = nullable;
        }
    }

    @Builder
    public NodeInfo(String fieldName, char itfType, NodeValue nodeValue, List<NodeInfo> nodes) {
        this.fieldName = fieldName;
        this.itfType = itfType;
        this.nodeValue = nodeValue;
        this.nodes = nodes;
    }
}

package com.example.service1.transform;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class NodeInfo {

    private String nodeName;
    // 현재 노드가 그룹인지 필드인지 구분. (예를들면 G, F)
    private char nodeType;
    private NodeValue nodeValue;
    private List<NodeInfo> nodes;

    @Data
    @Builder
    @NoArgsConstructor
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

        @Builder.Default
        private String encrypt = null;

        @Builder.Default
        private String decrypt = null;

        @Builder.Default
        private String masking = null;

        @Builder
        public NodeValue(int index, int size, int fsize, String type, String nodeId, String desc, boolean visible, boolean nullable, String encrypt, String decrypt, String masking) {
            this.index = index;
            this.size = size;
            this.fsize = fsize;
            this.type = type;
            this.nodeId = nodeId;
            this.desc = desc;
            this.visible = visible;
            this.nullable = nullable;
            this.encrypt = encrypt;
            this.decrypt = decrypt;
            this.masking = masking;
        }

    }

    @Builder
    public NodeInfo(String nodeName, char nodeType, NodeValue nodeValue, List<NodeInfo> nodes) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.nodeValue = nodeValue;
        this.nodes = nodes;
    }
}

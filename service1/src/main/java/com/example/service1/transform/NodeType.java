package com.example.service1.transform;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NodeType {
    GROUP ('G'),
    FIELD ('F');

    private final char type;
}

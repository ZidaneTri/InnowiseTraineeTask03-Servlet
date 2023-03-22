package com.innowise.task03.listener;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class HttpMapping {
    private final String path;
    private final HttpMethod method;


}

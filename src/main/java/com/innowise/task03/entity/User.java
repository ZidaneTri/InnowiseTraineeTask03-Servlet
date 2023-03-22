package com.innowise.task03.entity;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String login;
    private String password;
    private String role;
}

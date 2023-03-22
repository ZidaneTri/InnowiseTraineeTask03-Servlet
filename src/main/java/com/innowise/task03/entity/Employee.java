package com.innowise.task03.entity;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Employee {

    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String position;
    private String email;
    private Integer salary;

}

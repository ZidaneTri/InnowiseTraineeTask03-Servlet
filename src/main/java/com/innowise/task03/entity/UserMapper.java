package com.innowise.task03.entity;

import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserDTO userToUser(User user);
    User userDTOToUser(UserDTO UserDTO);
}

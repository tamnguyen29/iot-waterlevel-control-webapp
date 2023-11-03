package com.nvt.iot.service;

import com.nvt.iot.dto.UserDTO;
import com.nvt.iot.payload.request.UserRequest;
import com.nvt.iot.payload.response.UsersResponse;
import org.springframework.validation.BindingResult;

public interface UserService {
    UsersResponse getAllUsers(Integer pageNo, Integer pageSize);
    void addUser(UserRequest user, BindingResult bindingResult);
    UserDTO getUser(String id);
    void deleteUser(String id);
    void updateUser(String id, UserRequest userRequest, BindingResult bindingResult);
}

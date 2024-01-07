package com.nvt.iot.service;

import com.nvt.iot.dto.UserDTO;
import com.nvt.iot.payload.request.UserUpdateInformationRequest;
import com.nvt.iot.payload.response.UsersResponse;
import org.springframework.validation.BindingResult;

public interface UserService {
    UsersResponse getAllUsers(Integer pageNo, Integer pageSize);

    UserDTO getUser(String id);

    void deleteUser(String id);

    UserDTO updateInfoUser(String id, UserUpdateInformationRequest userRequest, BindingResult bindingResult);

    long getTotalAmount();
}

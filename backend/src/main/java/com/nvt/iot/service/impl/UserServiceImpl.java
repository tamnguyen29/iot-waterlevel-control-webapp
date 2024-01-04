package com.nvt.iot.service.impl;

import com.nvt.iot.document.UserDocument;
import com.nvt.iot.dto.UserDTO;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.mapper.UserDTOMapper;
import com.nvt.iot.model.Role;
import com.nvt.iot.payload.request.UserRequest;
import com.nvt.iot.payload.response.UsersResponse;
import com.nvt.iot.repository.ControlUnitRepository;
import com.nvt.iot.repository.UserRepository;
import com.nvt.iot.repository.WaterLevelStoreRepository;
import com.nvt.iot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDTOMapper userDTOMapper;
    private final UserRepository userRepository;
    private final ControlUnitRepository controlUnitRepository;
    private final PasswordEncoder passwordEncoder;
    private final WaterLevelStoreRepository waterLevelStoreRepository;

    @Override
    public UsersResponse getAllUsers(Integer pageNo, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<UserDocument> pagedResult = userRepository.findAll(paging);
        List<UserDTO> listUsersDTO = new ArrayList<>();

        if (pagedResult.hasContent()) {
            listUsersDTO = pagedResult.getContent().stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());
        }
        return UsersResponse.builder()
            .listUsers(listUsersDTO)
            .currentPage(pagedResult.getNumber())
            .totalItems(pagedResult.getNumberOfElements())
            .totalPages(pagedResult.getTotalPages())
            .build();
    }

    @Override
    public void addUser(UserRequest userRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }

        var userDoc = UserDocument.builder()
            .email(userRequest.getEmail())
            .fullName(userRequest.getFullName())
            .createAt(new Date(System.currentTimeMillis()))
            .updatedAt(new Date(System.currentTimeMillis()))
            .password(passwordEncoder.encode(userRequest.getPassword()))
            .role(userRequest.getRole().equals("ROLE_USER") ? Role.ROLE_USER : Role.ROLE_ADMIN)
            .build();

        userRepository.save(userDoc);
    }

    @Override
    public UserDTO getUser(String id) {
        validateUserIdFormat(id);

        UserDocument user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundCustomException("Not found user with id: " + id));
        return userDTOMapper.apply(user);
    }

    @Override
    public void deleteUser(String id) {
        validateUserIdFormat(id);

        if (userRepository.existsById(id)) {
            throw new NotFoundCustomException("Not found user with id: " + id);

        }
        userRepository.deleteById(id);
        controlUnitRepository.deleteAllByUserId(id);
        waterLevelStoreRepository.deleteAllByUserId(id);
    }

    @Override
    public void updateUser(String id, UserRequest userRequest, BindingResult bindingResult) {
        validateUserIdFormat(id);
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }

        UserDocument user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundCustomException("Not found user with id: " + id));

        user.setFullName(userRequest.getFullName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setUpdatedAt(new Date(System.currentTimeMillis()));
        user.setRole(userRequest.getRole().equals("ROLE_USER") ? Role.ROLE_USER : Role.ROLE_ADMIN);

        userRepository.save(user);
    }

    @Override
    public long getTotalAmount() {
        return userRepository.count();
    }

    private void validateUserIdFormat(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationCustomException("Invalid user id: " + id);
        }
    }
}

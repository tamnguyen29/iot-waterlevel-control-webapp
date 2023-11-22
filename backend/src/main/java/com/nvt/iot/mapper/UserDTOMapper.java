package com.nvt.iot.mapper;

import com.nvt.iot.document.UserDocument;
import com.nvt.iot.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserDTOMapper implements Function<UserDocument, UserDTO> {
    @Override
    public UserDTO apply(UserDocument userDocument) {
        return new UserDTO(
            userDocument.getId(),
            userDocument.getFullName(),
            userDocument.getEmail(),
            userDocument.getCreateAt(),
            userDocument.getUpdatedAt(),
            userDocument.getRole()
        );
    }
}

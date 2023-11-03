package com.nvt.iot.payload.response;

import com.nvt.iot.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponse {
   private List<UserDTO> listUsers;
   private int currentPage;
   private int totalItems;
   private int totalPages;
}

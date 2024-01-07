package com.nvt.iot.document;

import com.nvt.iot.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Document(collection = "Users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDocument implements UserDetails {
    @Id
    private String id;
    @Field(name = "email")
    @Indexed(unique = true)
    private String email;
    @Field(name = "full_name")
    private String fullName;
    @Field(name = "password")
    private String password;
    @Field(name = "created_at")
    private Date createAt;
    @Field(name = "updated_at")
    private Date updatedAt;
    @Field(name = "avatar")
    private String avatar;
    @Field(name = "phone_number")
    private String phoneNumber;
    @Field(name = "role")
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
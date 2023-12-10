package com.example.Security.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name="Users")
public class User implements UserDetails {

    @PrePersist
    protected void onCreate() {
        this.created_At = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_At =new Date(System.currentTimeMillis());
    }
    @Id
    private String user_id;
    @Column(length = 60)
    private String user_name;
    @Column(length = 60)
    private String email;
    @Column(length = 70)
    private String password;
    @Column(length = 20)
    private String mobile_number;
    @ManyToMany
    @JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "Users_id"),
    inverseJoinColumns = @JoinColumn(name = "Role_ID"))
    private Set<Role> roles=new HashSet<>();
    private Integer ClientId;
    private Date created_At;
    private Date updated_At;
    private String created_By;
    private String updated_By;

    public User(String mobile_number, String user_name, String email, String password, Set<Role> roles,String created_By,String updated_By) {
        this.user_id =email;
        this.mobile_number = mobile_number;
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.created_By=created_By;
        this.updated_By=updated_By;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.stream().forEach(i->authorities.add(new SimpleGrantedAuthority(i.getName())));
        return List.of(new SimpleGrantedAuthority(authorities.toString()));
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
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

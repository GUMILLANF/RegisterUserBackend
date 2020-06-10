package com.gustavofernandes.registeruserbackend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Data
@Table(name = "role")
@EqualsAndHashCode(callSuper = false, of = {"id"})
@ToString(of = {"id"})
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_role", length = 20, nullable = false)
    private String nameRole;

    @Override
    public String getAuthority() {
        return this.nameRole;
    }

}

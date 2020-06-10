package com.gustavofernandes.registeruserbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gustavofernandes.registeruserbackend.model.Enum.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity(name = "user")
@Data
@EqualsAndHashCode(callSuper = false, of = {"id"})
@ToString(of = {"id"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(unique = true)
    private String login;

    @NotNull
    private String passw;

    @NotNull
    @Size(min = 4, max = 70)
    private String name;

    @NotNull
    @Size(min = 5, max = 70)
    @Column(unique = true, nullable = false, length = 70)
    private String email;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_role", uniqueConstraints = @UniqueConstraint (
            columnNames = {"user_id","role_id"}, name = "unique_role_user"),
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", table = "user", unique = false,
                    foreignKey = @ForeignKey(name = "user_fk", value = ConstraintMode.CONSTRAINT)),

            inverseJoinColumns = @JoinColumn (name = "role_id", referencedColumnName = "id", table = "role", unique = false, updatable = false,
                    foreignKey = @ForeignKey (name="role_fk", value = ConstraintMode.CONSTRAINT)))
    private List<Role> roles; /*Os papeis ou acessos*/

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.passw;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.login;
    }

    @JsonIgnore
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

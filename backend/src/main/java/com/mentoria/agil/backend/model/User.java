package com.mentoria.agil.backend.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "especialidade")
    private String especialidade;

    @Column(name = "area_interesse")
    private String areaInteresse;

    @Column(name = "tipo_mentoria")
    private String tipoMentoria;

    private boolean ativo = true; 

    @OneToOne(mappedBy = "user")
    private PerfilMentor perfilMentor;

    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // --- LÓGICA DE AUTHORITIES (Unificada: Admin e Mentor) ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        if (this.role == Role.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        if (this.role == Role.MENTOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_MENTOR"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return ativo; }

    // --- GETTERS E SETTERS (Mantendo os seus e os dele) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getAreaInteresse() { return areaInteresse; }
    public void setAreaInteresse(String areaInteresse) { this.areaInteresse = areaInteresse; }

    public String getTipoMentoria() { return tipoMentoria; }
    public void setTipoMentoria(String tipoMentoria) { this.tipoMentoria = tipoMentoria; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public PerfilMentor getPerfilMentor(){ return perfilMentor; }
    public void setPerfilMentor(PerfilMentor perfilMentor){ this.perfilMentor = perfilMentor; }
}
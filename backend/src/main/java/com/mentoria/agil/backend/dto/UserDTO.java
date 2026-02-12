package com.mentoria.agil.backend.dto;

public class UserDTO {
    private String name;
    private String email;
    private String password;
    private String role;

public UserDTO() {} public UserDTO(String name, String email, String password, String role) { this.name = name; this.email = email; this.password = password; this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
package model;

import lombok.Data;

@Data

public class UserModel {
    private String email;
    private String password;
    private String name;

    public UserModel(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
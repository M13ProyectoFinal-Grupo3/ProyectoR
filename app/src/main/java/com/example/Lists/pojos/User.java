package com.example.Lists.pojos;

import java.io.Serializable;

public class User implements Serializable {
    public String nomuser;
    public String email;
    public Integer tipouser;
    public String password;

    public User(){};

    public User(String nomuser, String email, Integer tipouser, String password) {
        this.nomuser = nomuser;
        this.email = email;
        this.tipouser = tipouser;
        this.password = password;
    }

    public String getNomuser() {
        return nomuser;
    }

    public void setNomuser(String nomuser) {
        this.nomuser = nomuser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTipouser() {
        return tipouser;
    }

    public void setTipouser(Integer tipouser) {
        this.tipouser = tipouser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

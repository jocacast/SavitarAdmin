package com.example.savitaradmin;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class AuthorizedUser implements Serializable {
    private String email;
    private String condominium;
    private boolean isGuard;
    private boolean isEnabled;
    private String address;
    @Exclude
    private String id;

    public AuthorizedUser() {
    }

    public AuthorizedUser(String email, String condominium, boolean isGuard, boolean isEnabled, String address) {
        this.email = email;
        this.condominium = condominium;
        this.isGuard = isGuard;
        this.isEnabled = isEnabled;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCondominium() {
        return condominium;
    }

    public void setCondominium(String condominium) {
        this.condominium = condominium;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isGuard() {
        return isGuard;
    }

    public void setGuard(boolean guard) {
        isGuard = guard;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "AuthorizedUser{" +
                "email='" + email + '\'' +
                ", condominium='" + condominium + '\'' +
                ", isGuard=" + isGuard +
                ", isEnabled=" + isEnabled +
                ", address='" + address + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

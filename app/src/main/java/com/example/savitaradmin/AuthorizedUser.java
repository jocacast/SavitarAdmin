package com.example.savitaradmin;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class AuthorizedUser implements Serializable {
    private String email;
    private List<String> condominiums;
    private boolean isGuard;
    @Exclude
    private String id;

    public AuthorizedUser() {
    }


    public AuthorizedUser(String email, List<String> condominiums, boolean isGuard) {
        this.email = email;
        this.condominiums = condominiums;
        this.isGuard = isGuard;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getCondominiums() {
        return condominiums;
    }

    public void setCondominiums(List<String> condominiums) {
        this.condominiums = condominiums;
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

    @Override
    public String toString() {
        return "AuthorizedUser{" +
                "email='" + email + '\'' +
                ", condominiums=" + condominiums +
                ", isGuard=" + isGuard +
                ", id='" + id + '\'' +
                '}';
    }
}

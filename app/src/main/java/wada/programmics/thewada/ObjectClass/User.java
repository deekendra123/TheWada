package wada.programmics.thewada.ObjectClass;

public class User {


    private int id;
    private String username, email, number, ref_code, token;

    public User(int id, String username, String number, String ref_code, String token) {
        this.id = id;
        this.username = username;
        this.number = number;
        this.ref_code = ref_code;
        this.token = token;
    }


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }


    public String getNumber() {
        return number;
    }

    public String getRef_code() {
        return ref_code;
    }

    public String getToken() {
        return token;
    }
}

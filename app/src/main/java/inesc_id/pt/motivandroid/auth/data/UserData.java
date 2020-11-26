package inesc_id.pt.motivandroid.auth.data;

import com.google.gson.annotations.Expose;

@Deprecated
public class UserData {
    @Expose
    private String email;
    @Expose
    private int age;
    @Expose
    private String pnToken;
//    @Expose
//    private String userid;

    public UserData(String email, int age, String pnToken) {
        this.email = email;
        this.age = age;
        this.pnToken = pnToken;
//        this.userid = userid;
    }

    public UserData() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPnToken() {
        return pnToken;
    }

    public void setPnToken(String pnToken) {
        this.pnToken = pnToken;
    }

//    public String getUserid() {
//        return userid;
//    }
//
//    public void setUserid(String userid) {
//        this.userid = userid;
//    }

}

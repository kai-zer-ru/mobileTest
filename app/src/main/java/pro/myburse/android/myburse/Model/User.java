package pro.myburse.android.myburse.Model;

import java.util.Date;

public class User {

    private static User userInstance = null;
    private long id;
    private int socialNetworkId;
    private String socialNetworkName;
    private String extId;
    private String deviceId;
    private String phone;
    private String email;
    private String urlImage;
    private String urlImage_50;
    private String firstName;
    private String lastName;

    private Date birthday;


    public static User getInstance(){
        if (userInstance==null){
            userInstance = new User();
        }
        return userInstance;
    }

    private User(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSocialNetworkId() {
        return socialNetworkId;
    }

    public void setSocialNetworkId(int socialNetworkId) {
        this.socialNetworkId = socialNetworkId;
    }

    public String getSocialNetworkName() {
        return socialNetworkName;
    }

    public void setSocialNetworkName(String socialNetworkName) {
        this.socialNetworkName = socialNetworkName;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrlImage_50() {
        return urlImage_50;
    }

    public void setUrlImage_50(String urlImage_50) {
        this.urlImage_50 = urlImage_50;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getName(){
        return getFirstName()+" "+getLastName();
    }
}

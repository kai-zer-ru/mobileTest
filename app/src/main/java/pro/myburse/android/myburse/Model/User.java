package pro.myburse.android.myburse.Model;

import java.util.Date;

public class User {

    private static User userInstance = null;
    private String id;
    private int socialNetworkId;
    private String socialNetworkName;
    private String extId;
    private String deviceId;
    private String phone;
    private String login;
    private String email;
    private String urlImage;
    private String urlImage_50;
    private String firstName;
    private String middleName;
    private String lastName;
    private String password;
    private String city_name;
    private String region_name;
    private String country_name;
    private String birthday;
    private Integer balance_bids;
    private Integer balance_bonus;
    private Integer balance_money;
    private String access_key;


    public static User getInstance(){
        if (userInstance==null){
            userInstance = new User();
        }
        return userInstance;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return (firstName!=null)?firstName:"";
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return (middleName!=null)?middleName:"";
    }

    public void setMiddleName(String midName) {
        this.middleName = midName;
    }

    public String getLastName() {
        return (lastName!=null)?lastName:"";
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthday() {
        return (birthday==null?"":birthday);
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCityName() {
        return city_name;
    }

    public void setCityName(String city_name) {
        this.city_name = city_name;
    }

    public String getRegionName() {
        return region_name;
    }

    public void setRegionName(String region_name) {
        this.region_name = region_name;
    }

    public String getCountryName() {
        return country_name;
    }

    public void setCountryName(String country_name) {
        this.country_name = country_name;
    }

    public String getAccessKey() {
        return access_key;
    }

    public void setAccessKey(String access_key) {
        this.access_key = access_key;
    }

    public Integer getBalanceBids() {
        return balance_bids;
    }

    public void setBalanceBids(Integer balance_bids) {
        this.balance_bids = balance_bids;
    }

    public Integer getBalanceBonus() {
        return balance_bonus;
    }

    public void setBalanceBonus(Integer balance_bonus) {
        this.balance_bonus = balance_bonus;
    }

    public Integer getBalanceMoney() {
        return balance_money;
    }

    public void setBalanceMoney(Integer balance_money) {
        this.balance_money = balance_money;
    }

    public String getName(){
        String name = getFirstName()+" "+getMiddleName()+" "+getLastName();
        name = (name.trim().isEmpty())?getLogin():name;
        return name;
    }

    public String getLogin(){
        return this.login;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public Boolean isConnected(){
        return getAccessKey()!=null && !getAccessKey().isEmpty();
    }
}

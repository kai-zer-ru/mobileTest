package pro.myburse.android.myburse.Model;

import java.util.ArrayList;

public class User {

    public static final int SOCIAL_MB = 0;
    public static final int SOCIAL_VK = 1;
    public static final int SOCIAL_OK = 2;
    public static final int SOCIAL_FB = 3;
    private int id;
    private String access_key;
    private String device_id;
    private String first_name;
    private String last_name;
    private String middle_name;
    private String phone;
    private String email;
    private int balance_bids;
    private int balance_bonus;
    private int balance_money;
    private Country country;
    private Region region;
    private City city;
    private String avatar_url;
    private int social_type;
    private String birthday;
    private String social_id;
    private ArrayList<Friend> friends;


    public User(){

    }

    public int getId() {
        return id;
    }

    public void setId(int user_id) {
        this.id = user_id;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getMiddleName() {
        return middle_name;
    }

    public void setMiddleName(String middle_name) {
        this.middle_name = middle_name;
    }

    public int getBalanceBids() {
        return balance_bids;
    }

    public void setBalanceBids(int balance_bids) {
        this.balance_bids = balance_bids;
    }

    public int getBalanceBonus() {
        return balance_bonus;
    }

    public void setBalanceBonus(int balance_bonus) {
        this.balance_bonus = balance_bonus;
    }

    public int getBalanceMoney() {
        return balance_money;
    }

    public void setBalanceMoney(int balance_money) {
        this.balance_money = balance_money;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public String getAccessKey() {
        return access_key;
    }

    public void setAccessKey(String access_key) {
        this.access_key = access_key;
    }

    public String getDeviceId() {
        return device_id;
    }

    public void setDeviceId(String device_id) {
        this.device_id = device_id;
    }

    public Boolean isConnected(){
        return getAccessKey()!=null && !getAccessKey().isEmpty();
    }

    public int getSocialType() {
        return social_type;
    }

    public void setSocialType(int social_type) {
        this.social_type = social_type;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSocialId() {
        return social_id;
    }

    public void setSocialId(String social_id) {
        this.social_id = social_id;
    }

    public String getName(){
        return first_name + " " + (last_name==null?"":last_name);
    }
}

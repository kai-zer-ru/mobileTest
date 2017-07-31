package pro.myburse.android.myburse.Model;


import java.util.ArrayList;

public class Friend {

    private int id;
    private String avatar_url;
    private String first_name;
    private String last_name;
    private String middle_name;
    private  boolean is_friend;
    private  boolean is_online;
    private int balance_bids;
    private int balance_bonus;
    private int balance_money;
    private City city;
    private Region region;
    private Country country;
    private int social_type;
    private String birthday;
    private String last_online_time;
    private ArrayList<Friend> friends;

    public Friend(){

    }

}

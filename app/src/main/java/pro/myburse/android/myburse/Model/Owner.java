package pro.myburse.android.myburse.Model;


public class Owner {

    private long id;
    private int type; // 1 = юзер, 2 = магазин
    private String url;
    private String name;
    private String avatar;

    public Owner(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isUser(){
        return type ==1;
    }

    public boolean isShop(){
        return type ==2;
    }
}

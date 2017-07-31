package pro.myburse.android.myburse.Model;


public class Region {

    private int id;
    private int country_id;
    private String name;

    public Region(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountryId() {
        return country_id;
    }

    public void setCountryId(int country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

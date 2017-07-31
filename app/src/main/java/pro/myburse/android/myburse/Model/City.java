package pro.myburse.android.myburse.Model;


public class City {

    private int id;
    private int region_id;
    private String name;

    public City(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegionId() {
        return region_id;
    }

    public void setRegionId(int region_id) {
        this.region_id = region_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package pro.myburse.android.myburse.Json;

/**
 * Created by alexey on 04.07.17.
 */

public class New {

    public static String TYPE_PRODUCT = "product";
    public static String TYPE_WALL = "wall";
    public static String TYPE_BLOG = "blog";

    public static String PRODUCT_SCANDINAVIAN = "scandinavian";
    public static String PRODUCT_DROPBID = "dropbid";
    public static String PRODUCT_CLASSIC = "classic";
    public static String PRODUCT_REVERSE = "reverse";
    public static String PRODUCT_PRODUCT = "product";
    public static String PRODUCT_ADVERT = "advert";

    private long id;
    private String title;
    private String text;
    private String date_add;
    private String image;
    private int image_width;
    private int image_height;
    private String url;
    private double latitude;
    private double longitude;
    private double distance;
    private double owner_id;
    private String owner_name;
    private String owner_url;
    private String owner_avatar;

    private String item_type;
    private String product_type;
    private int comments_count;
    private int likes_count;
    private int reposts_count;


    public New(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate_add() {
        return date_add;
    }

    public void setDate_add(String date_add) {
        this.date_add = date_add;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImage_width() {
        return image_width;
    }

    public void setImage_width(int image_width) {
        this.image_width = image_width;
    }

    public int getImage_height() {
        return image_height;
    }

    public void setImage_height(int image_height) {
        this.image_height = image_height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(double owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_url() {
        return owner_url;
    }

    public void setOwner_url(String owner_url) {
        this.owner_url = owner_url;
    }

    public String getOwner_avatar() {
        return owner_avatar;
    }

    public void setOwner_avatar(String owner_avatar) {
        this.owner_avatar = owner_avatar;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(int reposts_count) {
        this.reposts_count = reposts_count;
    }
}

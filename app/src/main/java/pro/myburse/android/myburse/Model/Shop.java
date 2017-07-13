package pro.myburse.android.myburse.Model;



public class Shop {

    private long id;
    private int owner_id;
    private String owner_name;
    private String owner_url;
    private String owner_avatar;
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
    private boolean is_my_shop;
    private float rating;
    private int reviews_count;

    public Shop(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean is_my_shop() {
        return is_my_shop;
    }

    public void setIs_my_shop(boolean is_my_shop) {
        this.is_my_shop = is_my_shop;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviews_count() {
        return reviews_count;
    }

    public void setReviews_count(int reviews_count) {
        this.reviews_count = reviews_count;
    }
}

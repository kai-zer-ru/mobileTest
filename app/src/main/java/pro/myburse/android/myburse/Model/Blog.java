package pro.myburse.android.myburse.Model;



public class Blog {

    private long id;
    private String url;
    private String title;
    private String text;
    private String date_add;
    private String image;
    private int image_width;
    private int image_height;
    private double latitude;
    private double longitude;
    private double distance;
    private double owner_id;
    private String owner_name;
    private String owner_url;
    private String owner_avatar;
    private boolean is_my_post;
    private float rating;
    private int comments_count;
    private int likes_count;


    public Blog(){

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

    public String getDateAdd() {
        return date_add;
    }

    public void setDateAdd(String date_add) {
        this.date_add = date_add;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImageWidth() {
        return image_width;
    }

    public void setImageWidth(int image_width) {
        this.image_width = image_width;
    }

    public int getImageHeight() {
        return image_height;
    }

    public void setImageHeight(int image_height) {
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

    public double getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(double owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwnerName() {
        return owner_name;
    }

    public void setOwnerName(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwnerUrl() {
        return owner_url;
    }

    public void setOwnerUrl(String owner_url) {
        this.owner_url = owner_url;
    }

    public String getOwnerAvatar() {
        return owner_avatar;
    }

    public void setOwnerAvatar(String owner_avatar) {
        this.owner_avatar = owner_avatar;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getCommentsCount() {
        return comments_count;
    }

    public void setCommentsCount(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getLikesCount() {
        return likes_count;
    }

    public void setLikesCount(int likes_count) {
        this.likes_count = likes_count;
    }

    public boolean isMyPost() {
        return is_my_post;
    }

    public void setIsMyPost(boolean is_my_post) {
        this.is_my_post = is_my_post;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

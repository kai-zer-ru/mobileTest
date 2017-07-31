package pro.myburse.android.myburse.Model;


import java.util.Date;

public class Blog {

    private long id;
    private Owner owner;
    private String url;
    private String title;
    private String text;
    private Date created_at;
    private Date updated_at;
    private String updated_at_formated;
    private String created_at_formated;
    private Image image;
    private boolean is_mine;
    private boolean is_subscribed;
    private int comments_count;
    private int likes_count;
    private int reposts_count;

    public Blog(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getRepostsCount() {
        return reposts_count;
    }

    public void setRepostsCount(int reposts_count) {
        this.reposts_count = reposts_count;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getUpdatedAtFormated() {
        return updated_at_formated;
    }

    public void setUpdatedAtFormated(String updated_at_formated) {
        this.updated_at_formated = updated_at_formated;
    }

    public String getCreatedAtFormated() {
        return created_at_formated;
    }

    public void setCreatedAtFormated(String created_at_formatted) {
        this.created_at_formated = created_at_formatted;
    }

    public boolean isMine() {
        return is_mine;
    }

    public void isMine(boolean is_mine) {
        this.is_mine = is_mine;
    }

    public boolean isSubscribed() {
        return is_subscribed;
    }

    public void isSubscribed(boolean is_subscribed) {
        this.is_subscribed = is_subscribed;
    }
}

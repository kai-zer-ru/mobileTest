package pro.myburse.android.myburse.Model;


import java.util.Date;

public class Product {

    public static String PRODUCT_SCANDINAVIAN = "scandinavian";
    public static String PRODUCT_DROPBID = "dropbid";
    public static String PRODUCT_CLASSIC = "classic";
    public static String PRODUCT_REVERSE = "reverse";
    public static String PRODUCT_PRODUCT = "product";
    public static String PRODUCT_ADVERT = "advert";

    private long id;
    private Owner owner;
    private Date created_at;
    private Date updated_at;
    private String updated_at_formated;
    private String created_at_formatted;
    private String product_type;
    private float price;
    private String price_postfix;
    private float current_price;
    private String current_price_postfix;
    private Image image;
    private  int time_to_end;

    public Product(){}

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

    public String getCreatedAtFormatted() {
        return created_at_formatted;
    }

    public void setCreatedAtFormatted(String created_at_formatted) {
        this.created_at_formatted = created_at_formatted;
    }

    public String getProductType() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPrice_postfix() {
        return price_postfix;
    }

    public void setPrice_postfix(String price_postfix) {
        this.price_postfix = price_postfix;
    }

    public float getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(float current_price) {
        this.current_price = current_price;
    }

    public String getCurrent_price_postfix() {
        return current_price_postfix;
    }

    public void setCurrent_price_postfix(String current_price_postfix) {
        this.current_price_postfix = current_price_postfix;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getTime_to_end() {
        return time_to_end;
    }

    public void setTime_to_end(int time_to_end) {
        this.time_to_end = time_to_end;
    }
}

package wada.programmics.thewada.ObjectClass;

public class MainCategoryData {
    String categoryName, categoryDesc, location, image_url, long_des, discount,address;
    int imageUrl, id;

    public MainCategoryData(String categoryName, String categoryDesc, int imageUrl) {
        this.categoryName = categoryName;
        this.categoryDesc = categoryDesc;
        this.imageUrl = imageUrl;
    }

    public MainCategoryData(String categoryName, int id, String image_url) {
        this.categoryName = categoryName;
        this.id = id;
        this.image_url = image_url;
    }

    public MainCategoryData(String categoryName, String categoryDesc, String location, String image_url, int id) {
        this.categoryName = categoryName;
        this.categoryDesc = categoryDesc;
        this.location = location;
        this.image_url = image_url;
        this.id = id;
    }

    public MainCategoryData(String categoryName, String categoryDesc, String location, String image_url, String discount, int id, String address) {
        this.categoryName = categoryName;
        this.categoryDesc = categoryDesc;
        this.location = location;
        this.image_url = image_url;
        this.imageUrl = imageUrl;
        this.discount = discount;
        this.id = id;
        this.address = address;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLong_des() {
        return long_des;
    }

    public void setLong_des(String long_des) {
        this.long_des = long_des;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

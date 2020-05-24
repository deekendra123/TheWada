package wada.programmics.thewada.ObjectClass;

public class BannerData {


    private String image_drawable;

    private String service_images;
    private int service_id;

    public BannerData(String service_images, int service_id) {
        this.service_images = service_images;
        this.service_id = service_id;
    }
    public BannerData(){

    }


    public String getImage_drawable() {
        return image_drawable;
    }

    public void setImage_drawable(String image_drawable) {
        this.image_drawable = image_drawable;
    }

    public String getService_images() {
        return service_images;
    }

    public void setService_images(String service_images) {
        this.service_images = service_images;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }
}

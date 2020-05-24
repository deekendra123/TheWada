package wada.programmics.thewada.ObjectClass;

public class EntityImages {
    private int imageId;
    private String imageUrl;

    public EntityImages(int imageId, String imageUrl) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
    }


    public int getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

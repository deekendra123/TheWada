package wada.programmics.thewada.ObjectClass;

public class PointsData {
    int poins;
    int id, image;

    public PointsData(int poins, int id, int image) {
        this.poins = poins;
        this.id = id;
        this.image = image;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getPoins() {
        return poins;
    }

    public void setPoins(int poins) {
        this.poins = poins;
    }
}

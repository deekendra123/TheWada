package wada.programmics.thewada.ObjectClass;

public class NewsData {
    String newsTitle, newsDesc;
    int imageUrl;


    public NewsData(String newsTitle, String newsDesc, int imageUrl) {
        this.newsTitle = newsTitle;
        this.newsDesc = newsDesc;
        this.imageUrl = imageUrl;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDesc() {
        return newsDesc;
    }

    public void setNewsDesc(String newsDesc) {
        this.newsDesc = newsDesc;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}

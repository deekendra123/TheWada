package wada.programmics.thewada.ObjectClass;

public class NotificationData {
    private int id;
    private String title,message,date;

    public String getDate() {
        return date;
    }

    public NotificationData(int id, String title, String message, String date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}

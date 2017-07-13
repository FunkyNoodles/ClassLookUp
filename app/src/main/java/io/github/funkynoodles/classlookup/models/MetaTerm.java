package io.github.funkynoodles.classlookup.models;

public class MetaTerm {
    private String id;
    private String href;
    private String text;

    public MetaTerm(String id, String href, String text){
        this.id = id;
        this.href = href;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

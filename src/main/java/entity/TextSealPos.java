package entity;

public class TextSealPos {

    float x;

    float y;

    float size;

    public TextSealPos(float x,float y,float fontSize){
        this.x = x;
        this.y = y;
        this.size = fontSize;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}

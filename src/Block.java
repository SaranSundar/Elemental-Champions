import java.awt.*;

/**
 * Created by Saran on 5/4/15.
 */
public class Block {

    private Color color;
    private Rectangle box;

    public Block(Color color, Rectangle box) {
        this.box = box;
        this.color = color;
    }

    public Block(Rectangle box) {
        this.box = box;
        color = Color.green;
    }

    public boolean checkForCollisions(Block b) {
        if (getBox().intersects(b.getBox())) {
            return true;
        }
        return false;
    }

    public Color getColor() {
        return color;
    }

    public void setBox(Rectangle block){
        box = block;
    }

    public Rectangle getBox(){
        return box;
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(box.x,box.y,box.width,box.height);
    }
}

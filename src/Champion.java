import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class Champion {
    private ArrayList<Block> spirits;
    private Rectangle base;
    public Color spiritColor;
    private int dx, dy;
    private boolean collided = false;
    private HashSet<Block> duplicates;
    private HashMap<Integer, Integer> keyMap;
    private ArrayList<Rectangle> positions;
    private boolean updateCycle = true;
    //private int lx, ly;

    public Champion(Rectangle startingSpot, Color blockColor, HashMap<Integer, Integer> keyMap) {
        spirits = new ArrayList<Block>();
        //spirits.add(startingSpot);
        createChampion(4, startingSpot, -1, blockColor);
        spiritColor = blockColor;
        base = startingSpot;
        this.keyMap = keyMap;
        dx = 1;
        duplicates = new HashSet<Block>();
        positions = new ArrayList<Rectangle>();

    }

    public ArrayList<Block> getBody(){
        return spirits;
    }

    public boolean checkForCanPlaceFood(int x, int y, int w, int h){
        Rectangle rect = new Rectangle(x,y,w,h);
        boolean can = true;
        for(int i=0;i<spirits.size();i++){
            if(rect.intersects(spirits.get(i).getBox())){
                can = false;
                break;
            }
        }
        return can;
    }

    public ArrayList<Rectangle> getPositions(){
        positions.clear();
        for(int i=0;i<spirits.size();i++){
            positions.add(spirits.get(i).getBox());
        }
        return positions;
    }

    public boolean getUpdate(){
        return updateCycle;
    }

    public ArrayList<Block> getSpirits(){
        //duplicates = new HashSet<>(spirits);
        return spirits;
    }

    public void createChampion(int size, Rectangle box, int dir, Color color) {
        if (size <= 0) {
            return;
        } else {
            Rectangle next = new Rectangle(box);
            if(Math.abs(dir) == 1){
                next.x += box.width*dir;
            }
            else if(Math.abs(dir) == 2){
                next.y += box.height*(dir/2);
            }
            Block b = new Block(color, box);
            spirits.add(b);
            size--;
            createChampion(size, next, dir, color);
        }

    }

    public void draw(Graphics g) {
        //g.setColor(spiritColor);
        for (int i = 0; i < spirits.size(); i++) {
            spirits.get(i).draw(g);
        }

    }

    public void move(Block head) {
        spirits.add(head);
        for (int i = 0; i < spirits.size() - 1; i++) {
            Collections.swap(spirits, i, spirits.size() - 1);
        }
        spirits.remove(spirits.size() - 1);
        updateCycle = true;
    }

    public boolean checkForCollisions() {

        duplicates = new HashSet<>(spirits);
        if (duplicates.size() != spirits.size()) {
            return true;
        } else {
            return false;
        }
    }

    /*public void move(Rectangle head, int i) {
        if (i == spirits.size()) {
            return;
        } else {

            Rectangle next = new Rectangle(spirits.get(i).getBox());
            spirits.get(i).setBox(head);
            int n = i + 1;
            move(next, n);

        }

    }*/

    public boolean getCollided() {
        return collided;
    }

    public void update(ArrayList<Block> blocks) {//have a change monitored for x and y
        //calculate each blocks new position by the movement of the previous block
        Rectangle next = new Rectangle(spirits.get(0).getBox());

        switch (dx) {
            case 0:

                break;
            case 1:
                next.x += next.width;
                break;
            case -1:
                next.x -= next.width;
                break;
        }
        switch (dy) {
            case 0:

                break;
            case 1:
                next.y -= next.height;
                break;
            case -1:
                next.y += next.height;
                break;
        }
        if (next.x >= 800) {
            next.x = 0;
        } else if (next.x < 0) {
            next.x = 800;
        }
        if (next.y >= 600) {
            next.y = 0;
        } else if (next.y < 0) {
            next.y = 600;
        }
        checkForFood(blocks, next);
        if (dx != 0 || dy != 0) {
            if (checkForCollisions() == false) {
                Block b = new Block(spirits.get(0).getColor(),next);
                move(b);
            } else {
                collided = true;
            }
        }
    }

    public void checkForFood(ArrayList<Block> b, Rectangle head){
        int num = 0;
        boolean remove = false;
        int dir = 1;
        Rectangle one = spirits.get(spirits.size()-1).getBox();
        Rectangle two = spirits.get(spirits.size()-2).getBox();
        if(two.y == one.y){
            if(two.x > one.x){
                dir = -1;
            }
            else {
                dir = 1;
            }
        }
        else if(two.y < one.y){
            dir = -2;
        }
        else if(two.y > one.y){
            dir = 2;
        }

        for(int i=0;i<b.size();i++)
        {
            if(b.get(i).getBox().intersects(head)){
                createChampion(1,spirits.get(spirits.size()-1).getBox(),dir,spiritColor);
                num = i;
                remove = true;
                break;
            }

        }
        if(remove){
            b.remove(num);
        }
    }

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();
        if (keyMap.containsKey(key)) {
            int dir = keyMap.get(key);
            if (updateCycle == true) {
                switch (dir) {
                    case 0:
                        if (dx != 1) {
                            dx = -1;
                            dy = 0;
                            updateCycle = false;
                        }

                        break;
                    case 1:
                        if (dx != -1) {
                            dx = 1;
                            dy = 0;
                            updateCycle = false;
                        }

                        break;
                    case 2:
                        if (dy != -1) {
                            dy = 1;
                            dx = 0;
                            updateCycle = false;
                        }

                        break;
                    case 3:
                        if (dy != 1) {
                            dy = -1;
                            dx = 0;
                            updateCycle = false;
                        }

                        break;
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

    }

}

import javax.swing.*;
import java.awt.*;

public class Elemental extends JFrame{
    private String TITLE = "ELEMENTAL CHAMPIONS";
    private Board board;


    public Elemental(){
        board = new Board();
        add(board);
        setResizable(false);
        pack();
        setTitle(TITLE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void onClose(){
        board.saveHighScores();
    }

}

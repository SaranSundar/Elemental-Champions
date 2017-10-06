import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame jFrame = new Elemental();
                jFrame.addWindowListener(new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        ((Elemental)jFrame).onClose();
                        System.out.println("Closed");
                        e.getWindow().dispose();
                    }
                });
                jFrame.setVisible(true);
            }
        });
    }
}

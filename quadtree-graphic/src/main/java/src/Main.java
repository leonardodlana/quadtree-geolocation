package src;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 600;

    public void start() {
        Container c = getContentPane();
        c.setLayout( new BorderLayout() );

        resize(WIDTH, HEIGHT);

        CanvasPanel mDrawerPanel = new CanvasPanel();
        mDrawerPanel.setBounds(0, 0, WIDTH, HEIGHT);
        c.add(mDrawerPanel, "Center");

        mDrawerPanel.start();
    }

    public static void main(String args[]){
        Main frame = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH,HEIGHT);
        frame.setVisible(true);
        frame.start();
    }
}

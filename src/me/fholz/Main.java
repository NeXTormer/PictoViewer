package me.fholz;

import javax.swing.*;
import java.awt.*;


public class Main extends JFrame {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Frame frame = new Frame();
                    frame.setVisible(true);
                    frame.setBounds(100, 100, 512, 562);
                    frame.setTitle("PictoVIEW - by Felix Holz");
                    //frame.setLocationRelativeTo(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

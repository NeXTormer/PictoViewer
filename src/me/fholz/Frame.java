package me.fholz;

import com.idrsolutions.image.JDeli;
import com.sun.javafx.iio.ImageStorage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Frame extends JFrame implements KeyListener {

    private BufferedImage currentImage;
    private int currentImageIndex;
    private ArrayList<String> matches = new ArrayList<>();
    private boolean inverted = true;

    public ArrayList<String> imageNames = new ArrayList<>();
    public HashMap<String, String> index = new HashMap<>();

    public Frame()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());
        addKeyListener(this);



        JTextField input_tf = new JTextField("", 15);

        input_tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    findImages(input_tf.getText());
                }
            }
        });
        input_tf.addKeyListener(this);
        getContentPane().add(input_tf);

        pack();
        setVisible(true);

        loadIndex();
    }

    public void findImages(String input)
    {
        if(input.length() == 0) return;
        currentImageIndex = 0;
        matches.clear();
        for(String s : imageNames)
        {
            if(s.toLowerCase().contains(input.toLowerCase()))
            {
                matches.add(s);
            }
        }
        System.out.println(Arrays.toString(matches.toArray()));
        repaint();
    }

    public BufferedImage readImage(String id)
    {
        File file = new File("img/" + id + ".wmf");
        try {
            BufferedImage img = JDeli.read(file);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void paint(Graphics g)
    {
        try
        {
            System.out.println("CURRENT IMAGE: " + matches.get(currentImageIndex));
            BufferedImage image = readImage(index.get(matches.get(currentImageIndex)));
            currentImage = image;
            g.drawImage(image, 10, 60, 492, 492, this);

            setTitle("PictoVIEW - by Felix Holz | " + matches.get(currentImageIndex));

        }
        catch(IndexOutOfBoundsException e)
        {

        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int size = matches.size();
        if(e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            if(currentImageIndex > 0)
            {
                currentImageIndex--;
                repaint();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            if(currentImageIndex < size-1)
            {
                currentImageIndex++;
                repaint();
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            ColorModel cm = currentImage.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = currentImage.copyData(null);
            BufferedImage image = new BufferedImage(cm, raster, isAlphaPremultiplied, null);



            BufferedImage newImage = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);
            Graphics newImageGraphics = newImage.getGraphics();
            newImageGraphics.drawImage(image, 0, 0, 1024, 1024, null);

            if(inverted)
            {
                for (int x = 0; x < newImage.getWidth(); x++) {
                    for (int y = 0; y < newImage.getHeight(); y++) {
                        int rgba = newImage.getRGB(x, y);
                        Color col = new Color(rgba, true);
                        col = new Color(255 - col.getRed(),
                                255 - col.getGreen(),
                                255 - col.getBlue());
                        newImage.setRGB(x, y, col.getRGB());
                    }
                }
            }

            ImageSelection is = new ImageSelection((Image) newImage);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(is, null);


        }
    }


    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    private void loadIndex()
    {
        File indexFile = new File("pictograms.ini");
        String fileInput = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile), "UTF8")))
        {
            String temp;
            while ((temp = reader.readLine()) != null) {
                fileInput += temp + "\n";
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //System.out.println(fileInput);



        String[] lines = fileInput.split("\n");

        for(String s : lines)
        {
            String[] line = s.split("=");
            if(line.length == 2)
            {
                if(!line[0].equals("Antal") && !line[0].equals("Kod"))
                {
                    imageNames.add(line[1]);
                    index.put(line[1], line[0]);
                }
            }

        }
    }
}

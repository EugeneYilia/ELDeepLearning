package com.EL.DL.NN.CNN;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class _2DImageReader {
    public Point[][] readImage(String imageURL) {
        Point[][] points = null;
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(imageURL));
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            points = new Point[width][height];
            for (int y = bufferedImage.getMinY(); y < height; y++) {
                for (int x = bufferedImage.getMinX(); x < width; x++) {
                    int pixel = bufferedImage.getRGB(x,y);
                    int r = (pixel & 0xff0000) >> 16;//get red
                    int g = (pixel & 0xff00) >> 8;//get green
                    int b = (pixel & 0xff);//get blue
                    points[x][y] = new Point(r,g,b);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }
}

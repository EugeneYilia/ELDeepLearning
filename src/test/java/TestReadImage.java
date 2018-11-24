import com.EL.DL.NN.CNN.Point;
import com.EL.DL.NN.CNN._2DImageReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class TestReadImage {
    public static void main(String[] args) {
        final String base = "@#&$%*o!;.";//length is 10
        final int _x_zoom = 1;//横向缩放比例  每_x_zoom个点取一个
        final int _y_zoom = 1;//纵向缩放比例  每_y_zoom个点取一个

        //从磁盘将图像读取到内存中
        Point[][] points = new _2DImageReader().readImage("/home/eugeneliu/images/confidential.png");
        int _1_dimension_length = points.length;//array.length  数组第一维度的长度
        int _2_dimension_length = points[0].length;//array[0].length  数组第二维度的长度

        BufferedImage bufferedImage = new BufferedImage(_1_dimension_length, _2_dimension_length, BufferedImage.TYPE_INT_RGB);
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(new File("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/txt/test2.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("横向像素点数目：" + _1_dimension_length + "  " + "纵向像素点数目:" + _2_dimension_length);


        //灰度处理  图片字符化处理   图片缩放处理
        for (int y = 0; y < _2_dimension_length; y = y + _y_zoom) {//先横向再纵向对图片每一个像素点进行设置
            for (int x = 0; x < _1_dimension_length; x = x + _x_zoom) {
                int grayScale = (int) (0.299 * points[x][y].getRed() + 0.578 * points[x][y].getGreen() + points[x][y].getBlue() * 0.114);//r g b转换灰度的加权算法,得到的灰度范围是0-255
               bufferedImage.setRGB(x, y, (grayScale << 16) | (grayScale << 8) | grayScale);//图片灰度化
                final int index = Math.round((base.length() + 1) * grayScale / 255);// 11 * (0,255) / 255 = (0,11)  (0,9)代表着特殊字符,10代表着空白
                System.out.print(index == base.length() ? " " : String.valueOf(base.charAt(index)));
                printWriter.print(index == base.length() ? " " : String.valueOf(base.charAt(index)));
//                System.out.print(points[x][y].getPoint()+"  ");
            }
            System.out.println();
            printWriter.println();
        }


//        生成持续变换的图像
//        int a= 1;
//        while(a == 1){
//            int rand = new Random().nextInt(100);
//            System.out.println(rand);
//            for(int y = 0;y<_2_dimension_length;y++){
//                bufferedImage.setRGB(_1_dimension_length/2 + rand,y,0xffffff);
//            }
//            try {
//                ImageIO.write(bufferedImage, "JPEG", new File("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/img/test.jpg"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


//        //进行画圆的操作
//        //平移操作 左加右减 下加上减
//        int minLength = (_1_dimension_length < _2_dimension_length)?_1_dimension_length:_2_dimension_length;
//        System.out.println(minLength);
//        for(int x = 0;x<_1_dimension_length;x++){
//            for(int y=0;y<_2_dimension_length;y++){
//                if(Math.pow((Math.pow(x-150,2)+Math.pow(y-150,2)-1),3)-Math.pow(x-150,2)*Math.pow(y-150,3) <= Math.pow(10,10)){
//                    System.out.println("("+x+","+y+")");
//                    bufferedImage.setRGB(x,y,0xffffff);
//                }
//            }
//        }


        //将内存中的图像写入到磁盘中
        try {
            ImageIO.write(bufferedImage, "PNG", new File("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/img/confidential2.jpg"));
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

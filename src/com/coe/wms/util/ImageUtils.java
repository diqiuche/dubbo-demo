package com.coe.wms.util;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtils {

	/**
	 * 
	 * 对图片进行放大
	 * 
	 * @param originalImage
	 *            原始图片
	 * 
	 * @param times
	 *            放大倍数
	 * 
	 * @return
	 */
	public static BufferedImage zoomInImage(BufferedImage originalImage, Double times) {

		int width = Double.valueOf(originalImage.getWidth() * times).intValue();

		int height = Double.valueOf(originalImage.getHeight() * times).intValue();

		BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());

		Graphics g = newImage.getGraphics();

		g.drawImage(originalImage, 0, 0, width, height, null);

		g.dispose();

		return newImage;

	}

	/**
	 * 
	 * 对图片进行放大
	 * 
	 * @param srcPath
	 *            原始图片路径(绝对路径)
	 * 
	 * @param newPath
	 *            放大后图片路径（绝对路径）
	 * 
	 * @param times
	 *            放大倍数
	 * 
	 * @return 是否放大成功
	 */

	public static boolean zoomInImage(String srcPath, String newPath, Double times) {

		BufferedImage bufferedImage = null;

		try {

			File of = new File(srcPath);

			if (of.canRead()) {

				bufferedImage = ImageIO.read(of);
			}
		} catch (IOException e) {

			// TODO: 打印日志

			return false;

		}

		if (bufferedImage != null) {

			bufferedImage = zoomInImage(bufferedImage, times);

			try {

				// TODO: 这个保存路径需要配置下子好一点

				ImageIO.write(bufferedImage, "PNG", new File(newPath)); // 保存修改后的图像,全部保存为PNG格式

			} catch (IOException e) {

				// TODO 打印错误信息

				return false;

			}

		}

		return true;

	}

	/**
	 * 
	 * 对图片进行缩小
	 * 
	 * @param originalImage
	 *            原始图片
	 * 
	 * @param times
	 *            缩小倍数
	 * 
	 * @return 缩小后的Image
	 */

	public static BufferedImage zoomOutImage(BufferedImage originalImage, Double times) {
		int width = Double.valueOf(originalImage.getWidth() / times).intValue();
		int height = Double.valueOf(originalImage.getHeight() / times).intValue();
		BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());

		Graphics g = newImage.getGraphics();

		g.drawImage(originalImage, 0, 0, width, height, null);

		g.dispose();

		return newImage;

	}

	/**
	 * 
	 * 对图片进行缩小
	 * 
	 * @param srcPath
	 *            源图片路径（绝对路径）
	 * 
	 * @param newPath
	 *            新图片路径（绝对路径）
	 * 
	 * @param times
	 *            缩小倍数
	 * 
	 * @return 保存是否成功
	 */

	public static boolean zoomOutImage(String srcPath, String newPath, Double times) {

		BufferedImage bufferedImage = null;

		try {

			File of = new File(srcPath);

			if (of.canRead()) {

				bufferedImage = ImageIO.read(of);

			}

		} catch (IOException e) {

			// TODO: 打印日志

			return false;

		}

		if (bufferedImage != null) {

			bufferedImage = zoomOutImage(bufferedImage, times);

			try {

				// TODO: 这个保存路径需要配置下子好一点

				ImageIO.write(bufferedImage, "PNG", new File(newPath)); // 保存修改后的图像,全部保存为PNG格式

			} catch (IOException e) {

				// TODO 打印错误信息

				return false;

			}

		}
		return true;
	}

	  /* 
     * 图片裁剪通用接口 
     */  
    public static void cutImage(String src,String dest,int x,int y,int w,int h) throws IOException{   
           Iterator iterator = ImageIO.getImageReadersByFormatName("png");   
           ImageReader reader = (ImageReader)iterator.next();   
           InputStream in=new FileInputStream(src);  
           ImageInputStream iis = ImageIO.createImageInputStream(in);   
           reader.setInput(iis, true);   
           ImageReadParam param = reader.getDefaultReadParam();   
           Rectangle rect = new Rectangle(x, y, w,h);    
           param.setSourceRegion(rect);   
           BufferedImage bi = reader.read(0,param);     
           ImageIO.write(bi, "png", new File(dest));             
  
    }  
}
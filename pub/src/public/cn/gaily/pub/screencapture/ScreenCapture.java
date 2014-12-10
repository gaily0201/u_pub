package cn.gaily.pub.screencapture;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 一个简单的屏幕抓图
 * 
 **/

public class ScreenCapture {
	
	
	private JPanel mainPanel = null;
	
	private ScreenCapture() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			System.err.println("Internal Error: " + e);
			e.printStackTrace();
		}
		mainPanel = (JPanel) dialog.getContentPane();
		mainPanel.setLayout(new BorderLayout());
		labFullScreenImage.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evn) {
				isFirstPoint = true;
				pickedImage = fullScreenImage.getSubimage(recX, recY, recW, recH);
				pickedImage = zoomInImage(pickedImage, 2);
				dialog.setVisible(false);
			}
		});

		labFullScreenImage.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent evn) {
				if (isFirstPoint) {
					x1 = evn.getX();
					y1 = evn.getY();
					isFirstPoint = false;
				} else {
					x2 = evn.getX();
					y2 = evn.getY();
					int maxX = Math.max(x1, x2);
					int maxY = Math.max(y1, y2);
					int minX = Math.min(x1, x2);
					int minY = Math.min(y1, y2);
					recX = minX;
					recY = minY;
					recW = maxX - minX;
					recH = maxY - minY;
					labFullScreenImage.drawRectangle(recX, recY, recW, recH);
				}
			}

			public void mouseMoved(MouseEvent e) {
				labFullScreenImage.drawCross(e.getX(), e.getY());
			}
		});

		mainPanel.add(BorderLayout.CENTER, labFullScreenImage);
		dialog.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		dialog.setAlwaysOnTop(true);
		dialog.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
		dialog.setUndecorated(true);
		dialog.setSize(dialog.getMaximumSize());
		dialog.setModal(true);
	}

	
	//将图片放大 times:放大的倍数
	public BufferedImage zoomInImage(BufferedImage  originalImage, Integer times){

		Image image =  originalImage.getScaledInstance(originalImage.getWidth()*times, originalImage.getHeight()*times, Image.SCALE_SMOOTH);
		
		return toBufferedImage(image);
		
//		int width = originalImage.getWidth()*times;
//        int height = originalImage.getHeight()*times;
//        BufferedImage newImage = new BufferedImage(width,height,originalImage.getType());
//        Graphics2D g = (Graphics2D) newImage.getGraphics();
//        g.drawImage(originalImage, 0,0,width,height,null);
//        g.dispose();
//        return newImage;
        
    }
	
	
	// Singleton Pattern
	public static ScreenCapture getInstance() {
		return defaultCapturer;
	}

	/** 捕捉全屏慕 */
	public Icon captureFullScreen() {
		fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit
				.getDefaultToolkit().getScreenSize()));
		ImageIcon icon = new ImageIcon(fullScreenImage);
		return icon;
	}

	/** 捕捉屏幕的一个矫形区域 */
	public void captureImage() {
		fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIcon icon = new ImageIcon(fullScreenImage);
		labFullScreenImage.setIcon(icon);
		dialog.setVisible(true);
	}

	/** 得到捕捉后的BufferedImage */
	public BufferedImage getPickedImage() {
		return pickedImage;
	}

	/** 得到捕捉后的Icon */
	public ImageIcon getPickedIcon() {
		return new ImageIcon(getPickedImage());
	}

	/**
	 * 储存为一个文件,为PNG格式
	 * 
	 * @deprecated replaced by saveAsPNG(File file)
	 **/
	@Deprecated
	public void saveToFile(File file) throws IOException {
		ImageIO.write(getPickedImage(), defaultImageFormater, file);
	}

	/** 储存为一个文件,为PNG格式 */
	public void saveAsPNG(File file) throws IOException {
		ImageIO.write(getPickedImage(), "png", file);
	}

	/** 储存为一个JPEG格式图像文件 */
	public void saveAsJPEG(File file) throws IOException {
		ImageIO.write(getPickedImage(), "JPEG", file);
	}

	/** 写入一个OutputStream */
	public void write(OutputStream out) throws IOException {
		ImageIO.write(getPickedImage(), defaultImageFormater, out);
	}

	
	public void dispose(){
		dialog.dispose();
//		mainPanel.removeAll();
	}
	
	public BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	     }
	     image = new ImageIcon(image).getImage();
	     BufferedImage bimage = null;
	     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        int transparency = Transparency.OPAQUE;
	         GraphicsDevice gs = ge.getDefaultScreenDevice();
	         GraphicsConfiguration gc = gs.getDefaultConfiguration();
	         bimage = gc.createCompatibleImage(
	         image.getWidth(null), image.getHeight(null), transparency);
	     } catch (HeadlessException e) {
	     }
	    if (bimage == null) {
	        int type = BufferedImage.TYPE_INT_RGB;
	         bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	     }
	 
	     Graphics g = bimage.createGraphics();
	     g.drawImage(image, 0, 0, null);
	     g.dispose();
	     
	     return bimage;
	}
	  
	
	
	// singleton design pattern
	private static ScreenCapture defaultCapturer = new ScreenCapture();
	private int x1, y1, x2, y2;
	private int recX, recY, recH, recW; // 截取的图像
	private boolean isFirstPoint = true;
	private BackgroundImage labFullScreenImage = new BackgroundImage();
	private Robot robot;
	private BufferedImage fullScreenImage;
	private BufferedImage pickedImage;
	private String defaultImageFormater = "png";
	private JDialog dialog = new JDialog();
}


/** 显示图片的Label */
class BackgroundImage extends JLabel {
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRect(x, y, w, h);
		String area = Integer.toString(w) + " * " + Integer.toString(h);
		g.drawString(area, x + (int) w / 2 - 15, y + (int) h / 2);
		g.drawLine(lineX, 0, lineX, getHeight());

		g.drawLine(0, lineY, getWidth(), lineY);
	}

	public void drawRectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		h = height;
		w = width;
		repaint();
	}

	public void drawCross(int x, int y) {
		lineX = x;
		lineY = y;
		repaint();
	}

	int lineX, lineY;
	int x, y, h, w;
}
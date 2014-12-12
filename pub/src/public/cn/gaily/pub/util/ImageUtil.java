package cn.gaily.pub.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Blob;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import nc.bs.logging.Logger;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class ImageUtil {
	
	
	public static Image pdfToImage(File pdfFile) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(pdfFile, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
				channel.size());
		PDFFile pdffile = new PDFFile(buf);
		BufferedImage bufferedImage = null;
		Graphics2D g2d = null;
		Image img = null;
		int tHeight = 0;
		// 拼接Image
		for (int i = 1; i <= pdffile.getNumPages(); i++) {
			PDFPage page = pdffile.getPage(i);
			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
					.getWidth(), (int) page.getBBox().getHeight());
			img = page
					.getImage(rect.width, rect.height, rect, null, true, true);
			if (i == 1) {
				bufferedImage = new BufferedImage(rect.width, rect.height
						* pdffile.getNumPages(), BufferedImage.TYPE_INT_RGB);
				g2d=(Graphics2D) bufferedImage.getGraphics();
				// 设置渲染效果
				g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			}
			g2d.drawImage(img, 0, tHeight, rect.width,
					rect.height, null);
			tHeight += rect.height;
		}
		return bufferedImage;
	}

	public static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
		if (icon == null) {
			return null;
		}
		return new ImageIcon(icon.getImage().getScaledInstance(width, height,
				Image.SCALE_SMOOTH));
	}
	
	
	/**
	 * <p>方法名称：doCompressImage</p>
	 * <p>方法描述：创建缩略图</p>
	 * @param image   图片??
	 * @param width   长度
	 * @param height  高度
	 * @param out     输出??
	 * @return		    字节??
	 * @author xiaoh
	 * @since  2014-10-10
	 * <p> history 2014-10-10 xiaoh  创建   <p>
	 */
	public static ByteArrayInputStream doCompressImage(Blob blob, double width, double height, ByteArrayOutputStream out) {
		ByteArrayInputStream bin = null;
		try {
			InputStream in = blob.getBinaryStream();
			BufferedInputStream ins = new BufferedInputStream(in);
			BufferedImage image=ImageIO.read(ins); 
			
			if (image != null) {
				ImageIcon icon = new ImageIcon(image);
				double rateh = icon.getIconHeight() / height;
				double ratew = icon.getIconWidth() / width;
				double rate = (rateh > ratew) ? rateh : ratew;
				int new_w = (int) (icon.getIconWidth() / rate);
				int new_h = (int) (icon.getIconHeight() / rate);
				BufferedImage tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
				tag.getGraphics().drawImage(image, 0, 0, new_w, new_h, null);

				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
				/* 压缩质量 */
				jep.setQuality(1f, true);
				encoder.encode(tag, jep);
				
				bin = new ByteArrayInputStream(out.toByteArray());
				{
					return bin;
				}

			}
		} catch (Exception e) {
			Logger.error(e);
		} finally {

		}
		return null;
	}
	
}

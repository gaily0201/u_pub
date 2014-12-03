package cn.gaily.pub.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.swing.ImageIcon;
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
}

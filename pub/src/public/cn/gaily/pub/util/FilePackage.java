package cn.gaily.pub.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FilePackage {
	
	private void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
	}

	public static final boolean deleteFilePath(File filePath) throws IOException {
		if (filePath == null) {
			return false;
		}
		if (filePath.isDirectory()) {
			File[] files = filePath.listFiles();
			if ((files != null) && (files.length > 0)) {
				for (int i = 0; i < files.length; i++) {
					deleteFilePath(files[i]);
				}
			}
			return filePath.delete();
		}

		return filePath.delete();
	}

	public static void main(String[] args) {
		try {
			File file = new File("c:\\test");
			// AppDebug.debug(deleteFilePath(file));
		} catch (Exception e) {
		}
	}

	public final String[] unzipFile(File file, String sTargetPath) throws IOException {
		ZipFile zipFile = null;
		if (file == null) {
			// AppDebug.debug("parse file not export");
		}
		Vector vector = new Vector();
		try {
			zipFile = new ZipFile(file);
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				String sFilename = validFileName(entry.getName());
				if (sTargetPath != null) {
					sFilename = sTargetPath.concat(sFilename);
					vector.add(sFilename);
				}
				copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(sFilename)));
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (Exception e) {
			}
		}
		int size = vector.size();
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = ((String) vector.get(i));
		}
		return names;
	}

	public final String[] unzipFile(String sFile, String sTargetPath)
			throws IOException {
		return unzipFile(new File(sFile), sTargetPath);
	}

	public String validFileName(String name) {
		if (name != null) {
			char[] chars = name.toCharArray();
			int nMark = 0;
			for (int i = 0; i < chars.length; i++) {
				if ((chars[i] == '\\') || (chars[i] == '/')) {
					nMark = i;
				}
			}
			if (nMark > 0) {
				name = name.substring(nMark + 1);
			}
		}
		return name;
	}

	public static void zipFile(File[] files, String zipfile) throws FileNotFoundException, IOException {
		byte[] bytes = new byte[1024];
		OutputStream os = null;
		ZipOutputStream zout = null;
		try {
			os = new FileOutputStream(zipfile);
			zout = new ZipOutputStream(os);
			for (int i = 0; i < files.length; i++) {
				if (files[i].exists()) {
					InputStream in = null;
					try {
						in = new FileInputStream(files[i]);
						ZipEntry e = new ZipEntry(files[i].getName().replace(File.separatorChar, '/'));
						zout.putNextEntry(e);
						int len = 0;
						while ((len = in.read(bytes)) != -1) {
							zout.write(bytes, 0, len);
						}
						zout.closeEntry();
					} catch (Exception e) {
						// AppDebug.error(e);
					} finally {
						try {
							if (in != null)
								in.close();
						} catch (IOException e) {
						}
					}
				}
			}
		} catch (Exception e) {
			// AppDebug.error(e);
		} finally {
			try {
				if (zout != null) {
					zout.close();
				}
				if (os != null)
					os.close();
			} catch (IOException e) {
			}
		}
	}

	public final void zipFileWithDir(File[] files, String zipfile) throws FileNotFoundException, IOException {
		byte[] bytes = new byte[1024];
		OutputStream os = null;
		ZipOutputStream zout = null;
		try {
			os = new FileOutputStream(zipfile);
			zout = new ZipOutputStream(os);

			String strDirRoot = null;
			for (int i = 0; i < files.length; i++) {
				String strPath = files[i].getParent();
				if (strDirRoot == null)
					strDirRoot = strPath;
				else if (strDirRoot.length() > strPath.length()) {
					strDirRoot = strPath;
				}
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].exists()) {
					InputStream in = null;
					try {
						in = new FileInputStream(files[i]);
						String strPath = files[i].getAbsolutePath();
						if ((strPath.startsWith(strDirRoot))
								&& (strPath.length() > strDirRoot.length()))
							strPath = strPath.substring(strDirRoot.length());
						else {
							strPath = files[i].getName();
						}
						ZipEntry e = new ZipEntry(strPath.replace(File.separatorChar, '/'));
						zout.putNextEntry(e);
						int len = 0;
						while ((len = in.read(bytes)) != -1) {
							zout.write(bytes, 0, len);
						}
						zout.closeEntry();
					} catch (Exception e) {
					} finally {
						try {
							if (in != null)
								in.close();
						} catch (IOException e) {
						}
					}
				}
			}
		} catch (Exception e) {
			// AppDebug.error(e);
		} finally {
			try {
				if (zout != null) {
					zout.close();
				}
				if (os != null)
					os.close();
			} catch (IOException e) {
			}
		}
	}
}
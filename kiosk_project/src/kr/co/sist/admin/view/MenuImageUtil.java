package kr.co.sist.admin.view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import kr.co.sist.common.db.DbConn;

/* DB에 저장된 이미지 경로로 이미지를 찾아서 읽는다. */
public class MenuImageUtil {
	private MenuImageUtil() {
	}

	public static String getImageDir() {
		try {
			Properties prop = DbConn.getInstance().getProp(null);
			if (prop != null) {
				String dir = prop.getProperty("imageDir");
				if (dir != null && !dir.trim().isEmpty()) {
					return dir.trim();
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* 새 이미지를 저장할 images 폴더 */
	public static File getSaveFolder() {
		String dir = getImageDir();
		return dir == null ? new File("images") : new File(dir, "images");
	}

	/* 경로로 이미지를 읽어서 반환. 못 찾으면 null */
	public static BufferedImage read(String dbPath) {
		if (dbPath == null || dbPath.trim().isEmpty()) {
			return null;
		}
		String path = dbPath.trim().replace('\\', '/');
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		List<String> tried = new ArrayList<>();
		List<File> files = new ArrayList<>();
		String imageDir = getImageDir();
		if (imageDir != null) {
			files.add(new File(imageDir + path));
		}
		files.add(new File("." + path)); // 작업 폴더(프로젝트 폴더) 기준
		files.add(new File(path)); // 드라이브 루트 기준

		for (File f : files) {
			tried.add(f.getAbsolutePath());
			if (f.isFile()) {
				try {
					BufferedImage img = ImageIO.read(f);
					if (img != null) {
						return img;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		URL url = MenuImageUtil.class.getResource(path); // 클래스패스(src, bin) 기준
		tried.add("classpath:" + path);
		if (url != null) {
			try {
				BufferedImage img = ImageIO.read(url);
				if (img != null) {
					return img;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.err.println("[이미지] 파일을 찾지 못했습니다: " + dbPath);
		for (String t : tried) {
			System.err.println("        찾아본 위치: " + t);
		}
		return null;
	}
}

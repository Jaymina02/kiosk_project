package kr.co.sist.admin.evt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import kr.co.sist.admin.dto.MenuDTO;
import kr.co.sist.admin.service.MenuService;
import kr.co.sist.admin.view.MenuEditDialog;

public class MenuEditEvt implements ActionListener {
	private static final String IMAGE_DIR = "images"; // 실행 폴더 아래 images 폴더에 저장
	private static final int THUMB_WIDTH = 150;

	private MenuEditDialog editDialog;
	private MenuService menuService;

	public MenuEditEvt(MenuEditDialog editDialog) {
		this.editDialog = editDialog;
		this.menuService = new MenuService();
	}

	/** 카테고리/옵션 목록을 DB에서 읽어 화면에 채우고, 수정 모드면 기존 값도 채운다 */
	public void initData() {
		try {
			editDialog.setCategories(menuService.searchCategories());
			editDialog.setOptionChoices(menuService.searchAllOptions());

			MenuDTO target = editDialog.getMenuDTO();
			if (target != null) {
				editDialog.setMenuData(target);
				List<Long> ids = new ArrayList<>();
				menuService.searchMenuOptions(target.getMenuId()).forEach(o -> ids.add(o.getOptionId()));
				editDialog.setSelectedOptionIds(ids);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			editDialog.showMessage("데이터를 불러오지 못했습니다.\n" + e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == editDialog.getSaveButton()) {
			if (editDialog.isEditMode()) {
				modifyMenu();
			} else {
				addMenu();
			}
		} else if (e.getSource() == editDialog.getCancelButton()) {
			editDialog.dispose();
		} else if (e.getSource() == editDialog.getImageButton()) {
			chooseImage();
		}
	}

	/** 이미지 등록: 파일 선택창에서 이미지를 고른다 (실제 복사는 저장할 때) */
	public void chooseImage() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("상품 이미지 선택");
		chooser.setFileFilter(new FileNameExtensionFilter("이미지 파일 (jpg, png, gif, bmp)", "jpg", "jpeg", "png",
				"gif", "bmp"));
		if (chooser.showOpenDialog(editDialog) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		if (!editDialog.setPickedImage(chooser.getSelectedFile())) {
			editDialog.showMessage("이미지 파일을 읽을 수 없습니다.");
		}
	}

	public void addMenu() {
		if (!validateMenu() || !saveImageFiles()) {
			return;
		}
		try {
			if (menuService.addMenu(editDialog.getMenuData(), editDialog.getSelectedOptionIds())) {
				editDialog.showMessage("메뉴가 추가되었습니다.");
				editDialog.getOwnerForm().refreshMenuList();
				editDialog.dispose();
			} else {
				editDialog.showMessage("메뉴 추가에 실패했습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			editDialog.showMessage("메뉴 추가 중 DB 오류가 발생했습니다.\n" + e.getMessage());
		}
	}

	public void modifyMenu() {
		if (!validateMenu() || !saveImageFiles()) {
			return;
		}
		try {
			if (menuService.modifyMenu(editDialog.getMenuData(), editDialog.getSelectedOptionIds())) {
				editDialog.showMessage("메뉴가 수정되었습니다.");
				editDialog.getOwnerForm().refreshMenuList();
				editDialog.dispose();
			} else {
				editDialog.showMessage("수정할 메뉴를 찾을 수 없습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			editDialog.showMessage("메뉴 수정 중 DB 오류가 발생했습니다.\n" + e.getMessage());
		}
	}

	/** 입력값 검사 */
	public boolean validateMenu() {
		if (editDialog.getMenuNameField().getText().trim().isEmpty()) {
			editDialog.showMessage("메뉴명을 입력해주세요.");
			editDialog.getMenuNameField().requestFocus();
			return false;
		}
		String price = editDialog.getPriceField().getText().trim();
		try {
			if (Integer.parseInt(price) < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			editDialog.showMessage("가격은 0 이상의 숫자로 입력해주세요.");
			editDialog.getPriceField().requestFocus();
			return false;
		}
		if (!editDialog.isSizeSelected()) {
			editDialog.showMessage("사이즈를 한 개 이상 선택해주세요.");
			return false;
		}
		return true;
	}

	/**
	 * 고른 이미지를 images 폴더로 복사하고 썸네일을 만들어, DB에 저장할 경로(/images/파일명)를 화면에 채운다.
	 * 이미지를 새로 고르지 않았다면 기존 경로를 그대로 둔다.
	 */
	private boolean saveImageFiles() {
		File picked = editDialog.getPickedImage();
		if (picked == null) {
			return true;
		}
		try {
			BufferedImage src = ImageIO.read(picked);
			if (src == null) {
				editDialog.showMessage("이미지 파일을 읽을 수 없습니다.");
				return false;
			}
			File dir = new File(IMAGE_DIR);
			dir.mkdirs();

			String name = picked.getName();
			int dot = name.lastIndexOf('.');
			String base = dot > 0 ? name.substring(0, dot) : name;
			String ext = dot > 0 ? name.substring(dot) : "";

			File target = new File(dir, name);
			if (target.exists() && !Files.isSameFile(target.toPath(), picked.toPath())) {
				base = base + "_" + System.currentTimeMillis(); // 같은 이름의 다른 파일이 있으면 이름을 바꾼다
				target = new File(dir, base + ext);
			}
			if (!target.exists()) {
				Files.copy(picked.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			// 썸네일 (가로 150px): 상세 이미지가 menu1.png 이면 썸네일은 menu1_1.png 로 저장
			String lower = ext.toLowerCase();
			boolean supported = lower.equals(".jpg") || lower.equals(".jpeg") || lower.equals(".png")
					|| lower.equals(".gif") || lower.equals(".bmp");
			String format = !supported ? "png" : lower.equals(".jpeg") ? "jpg" : lower.substring(1);
			String thumbExt = supported ? ext : ".png"; // 저장할 수 없는 형식이면 png 로
			boolean png = format.equals("png");
			int w = Math.min(THUMB_WIDTH, src.getWidth());
			int h = Math.max(1, src.getHeight() * w / src.getWidth());
			BufferedImage thumb = new BufferedImage(w, h,
					png ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
			Graphics2D g = thumb.createGraphics();
			if (!png) {
				g.setColor(Color.WHITE); // 투명 배경을 지원하지 않는 형식은 흰 배경
				g.fillRect(0, 0, w, h);
			}
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(src, 0, 0, w, h, null);
			g.dispose();
			String thumbName = base + "_1" + thumbExt;
			if (!ImageIO.write(thumb, format, new File(dir, thumbName))) {
				throw new IOException("썸네일 저장에 실패했습니다. (" + format + ")");
			}

			editDialog.getImagePathField().setText("/" + IMAGE_DIR + "/" + target.getName());
			editDialog.getThumbnailPathField().setText("/" + IMAGE_DIR + "/" + thumbName);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			editDialog.showMessage("이미지를 저장하지 못했습니다.\n" + e.getMessage());
			return false;
		}
	}
}

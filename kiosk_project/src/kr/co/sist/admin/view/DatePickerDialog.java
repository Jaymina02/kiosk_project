package kr.co.sist.admin.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.time.YearMonth;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DatePickerDialog extends JDialog {

	private YearMonth current;
	private LocalDate selected;
	private final LocalDate today = LocalDate.now();
	private JLabel monthLabel;
	private JPanel dayPanel;

	private DatePickerDialog(Window owner, LocalDate init) {
		super(owner, "날짜 선택", ModalityType.APPLICATION_MODAL);
		current = YearMonth.from(init);

		JButton prevButton = new SalesForm.RoundButton("◀");
		JButton nextButton = new SalesForm.RoundButton("▶");
		monthLabel = new JLabel("", SwingConstants.CENTER);
		monthLabel.setFont(new Font(SalesForm.FONT_NAME, Font.BOLD, 17));

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);
		headerPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, SalesForm.DIVIDER),
				BorderFactory.createEmptyBorder(0, 0, 10, 0)));
		headerPanel.add(prevButton, BorderLayout.WEST);
		headerPanel.add(monthLabel, BorderLayout.CENTER);
		headerPanel.add(nextButton, BorderLayout.EAST);

		dayPanel = new JPanel(new GridLayout(0, 7, 4, 4)); 
		dayPanel.setOpaque(false);

		JPanel root = new JPanel(new BorderLayout(0, 10));
		root.setBackground(Color.WHITE);
		root.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(SalesForm.LINE_GRAY),
				BorderFactory.createEmptyBorder(14, 14, 14, 14)));
		root.add(headerPanel, BorderLayout.NORTH);
		root.add(dayPanel, BorderLayout.CENTER);
		setContentPane(root);

		prevButton.addActionListener(e -> {
			current = current.minusMonths(1);
			drawCalendar();
		});
		nextButton.addActionListener(e -> {
			current = current.plusMonths(1);
			drawCalendar();
		});

		drawCalendar();
		setSize(380, 400);
		setResizable(false);
		setLocationRelativeTo(owner);
	}

	private void drawCalendar() {
		monthLabel.setText(current.getYear() + "년 " + current.getMonthValue() + "월");
		dayPanel.removeAll();

		String[] dayNames = { "일", "월", "화", "수", "목", "금", "토" };
		for (int i = 0; i < dayNames.length; i++) {
			JLabel nameLabel = new JLabel(dayNames[i], SwingConstants.CENTER);
			nameLabel.setFont(new Font(SalesForm.FONT_NAME, Font.BOLD, 13));
			nameLabel.setOpaque(true);
			nameLabel.setBackground(SalesForm.BTN_GRAY); 
			dayPanel.add(nameLabel);
		}

		int blank = current.atDay(1).getDayOfWeek().getValue() % 7;
		for (int i = 0; i < blank; i++) {
			dayPanel.add(new JLabel(""));
		}

		for (int d = 1; d <= current.lengthOfMonth(); d++) {
			final int day = d;
			JButton dayButton = new SalesForm.RoundButton(String.valueOf(day));
			dayButton.setMargin(new Insets(2, 2, 2, 2));
			if (current.atDay(day).equals(today)) { 
				dayButton.setFont(new Font(SalesForm.FONT_NAME, Font.BOLD, 14));
			}
			dayButton.addActionListener(e -> {
				selected = current.atDay(day);
				dispose(); 
			});
			dayPanel.add(dayButton);
		}

		dayPanel.revalidate();
		dayPanel.repaint();
	}


	public static LocalDate showDialog(Component parent, LocalDate init) {
		DatePickerDialog dialog = new DatePickerDialog(SwingUtilities.getWindowAncestor(parent), init);
		dialog.setVisible(true);
		return dialog.selected;
	}

}

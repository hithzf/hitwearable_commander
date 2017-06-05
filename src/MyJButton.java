import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * 圆角按钮
 * @author hzf
 *
 */
public class MyJButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* 决定圆角的弧度 */
	public static int radius = 10;
	public static Color COLOR1, COLOR2;
	public static int pink = 3, ashen = 2, sky = 1, stone = 0;
	/* 粉红 */
	public static Color pink1 = new Color(240, 240, 240);
	public static Color pink2 = new Color(254, 173, 240);
	/* 灰白 */
	public static Color ashen1 = new Color(250, 250, 250);
	public static Color ashen2 = new Color(197, 197, 197);
	/* 淡天蓝色 */
	public static Color sky1 = new Color(240, 240, 240);
	public static Color sky2 = new Color(121, 230, 230);
	/* 深宝石蓝 */
	public static Color stone1 = new Color(79, 125, 184);
	public static Color stone2 = new Color(89, 200, 240);
	/* 光标进入按钮判断 */
	private boolean hover;

	public MyJButton() {
		this("", stone, 14);
	}

	public MyJButton(String name) {
		this(name, stone, 14);
	}
	
	public MyJButton(String name, int style) {
		super.setText(name);
		if (style == pink) {
			COLOR1 = pink1;
			COLOR2 = pink2;
		}
		if (style == ashen) {
			COLOR1 = ashen1;
			COLOR2 = ashen2;
		}
		if (style == stone) {
			COLOR1 = stone1;
			COLOR2 = stone2;
		}
		if (style == sky) {
			COLOR1 = sky1;
			COLOR2 = sky2;
		}
		paintcolor(COLOR1, COLOR2, 14);
	}

	/**
	 * 
	 * @param name 按钮名
	 * @param style 按钮颜色
	 * @param font_size 字体大小
	 */
	public MyJButton(String name, int style, int font_size) {
		super.setText(name);
		if (style == pink) {
			COLOR1 = pink1;
			COLOR2 = pink2;
		}
		if (style == ashen) {
			COLOR1 = ashen1;
			COLOR2 = ashen2;
		}
		if (style == stone) {
			COLOR1 = stone1;
			COLOR2 = stone2;
		}
		if (style == sky) {
			COLOR1 = sky1;
			COLOR2 = sky2;
		}
		paintcolor(COLOR1, COLOR2, font_size);
	}

	private void paintcolor(Color COLOR1, Color COLOR2, int FONTSIZE) {
		setMargin(new Insets(0, 0, 0, 0));
		setFont(new Font("微软雅黑", Font.PLAIN, FONTSIZE));
		setBorderPainted(false);
		setForeground(Color.black);
		setFocusPainted(false);
		setContentAreaFilled(false);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hover = true;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hover = false;
				repaint();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		int height = getHeight();
		int with = getWidth();
		float tran = 1F;
		if (!hover) {/* 鼠标离开/进入时的透明度改变量 */
			tran = 0.6F;
		}
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		/* GradientPaint是颜色渐变类 */
		GradientPaint p1;
		GradientPaint p2;
		if (getModel().isPressed()) {
			p1 = new GradientPaint(0, 0, new Color(0, 0, 0), 0, height, new Color(100, 100, 100), true);
			p2 = new GradientPaint(0, 1, new Color(0, 0, 0, 50), 0, height, new Color(255, 255, 255, 100), true);
		} else {
			p1 = new GradientPaint(0, 0, new Color(100, 100, 100), 0, height, new Color(0, 0, 0), true);
			p2 = new GradientPaint(0, 1, new Color(255, 255, 255, 100), 0, height, new Color(0, 0, 0, 50), true);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tran));
		RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0, with - 1, height - 1, radius, radius);
		// 最后两个参数数值越大，按钮越圆，以下同理
		Shape clip = g2d.getClip();
		g2d.clip(r2d);
		GradientPaint gp = new GradientPaint(0.0F, 0.0F, COLOR1, 0.0F, height / 2, COLOR2, true);
		g2d.setPaint(gp);
		g2d.fillRect(0, 0, with, height);
		g2d.setClip(clip);
		g2d.setPaint(p1);
		g2d.drawRoundRect(0, 0, with - 3, height - 3, radius, radius);
		g2d.setPaint(p2);
		g2d.drawRoundRect(1, 1, with - 3, height - 3, radius, radius);
		g2d.dispose();
		super.paintComponent(g);
	}

	public static void main(String args[]) {
//		JFrame frm = new JFrame();
//		MyJButton but = new MyJButton("圆角JButton", 2, 14);
//		frm.setLayout(null);
//		frm.setBounds(800, 400, 200, 200);
//		but.setBounds(30, 30, 100, 50);
//		frm.add(but);
//		frm.setDefaultCloseOperation(3);
//		frm.setVisible(true);
	}
}
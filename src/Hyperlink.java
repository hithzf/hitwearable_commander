import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * ³¬Á´½Ó
 * http://www.xuebuyuan.com/1750000.html
 * https://www.baidu.com/s?ie=utf-8&f=3&rsv_bp=1&rsv_idx=1&tn=93555097_hao_pg&wd=java%E5%A6%82%E4%BD%95%E6%92%AD%E6%94%BE%E8%AF%AD%E9%9F%B3&oq=java%25E5%25A6%2582%25E4%25BD%2595%25E8%25B0%2583%25E7%2594%25A8%2520amr%2520%25E6%2592%25AD%25E6%2594%25BE&rsv_pq=d07bf4650002ad4c&rsv_t=44d3UwR%2Bc%2FkHoQd6VPSrm%2B5xJeBT5dGDj0z2Kr%2FYB9km4U2ZdL03L2bIH8FuvoOHRfhVMi6n&rqlang=cn&rsv_enter=0&inputT=4476&rsv_sug3=28&rsv_sug1=12&rsv_sug7=100&rsv_sug2=1&prefixsug=java%25E5%25A6%2582%25E4%25BD%2595%25E6%2592%25AD%25E6%2594%25BE&rsp=1&rsv_sug4=4695&rsv_sug=2
 * @author hzf
 *
 */
public class Hyperlink extends JFrame {
	public Hyperlink() {
		JEditorPane jEditorPane = new JEditorPane();
		jEditorPane.setEditable(false);
		jEditorPane.setContentType("text/html");
		jEditorPane.setText("<html><body><a href=http://www.baidu.com>baidu</a></body></html>");
		jEditorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						String command = "explorer.exe " + e.getURL().toString();
						System.out.println(command);
						Runtime.getRuntime().exec(command);
					} catch (Exception ex) {
						ex.printStackTrace();
						System.err.println("connection error");
					}
				}
			}
		});
		this.getContentPane().add(jEditorPane);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		Hyperlink temp = new Hyperlink();
		temp.setSize(200, 200);
		temp.setVisible(true);
	}
}
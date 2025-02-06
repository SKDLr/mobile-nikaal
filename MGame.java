import javax.swing.JFrame;

public class MGame extends JFrame{

	public MGame() {
		add(new Model());
	}
	



	public static void main(String[] args) {

		MGame game = new MGame();
		game.setVisible(true);
		game.setTitle("Mobile Nikaal (Seraiki Edition)");
		game.setSize(380,420);
		game.setDefaultCloseOperation(EXIT_ON_CLOSE);
		game.setLocationRelativeTo(null);
		
	}

}

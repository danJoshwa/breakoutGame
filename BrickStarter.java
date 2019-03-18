   import javax.swing.JFrame;

public class BrickStarter extends JFrame {

    public BrickStarter()
    {
        add(new BrickBoard());
        setTitle("BrickBoard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) {
        new BrickStarter();
    }
}
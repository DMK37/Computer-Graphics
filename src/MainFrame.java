import javax.swing.*;

public class MainFrame extends JFrame {
    private final MainPanel mainPanel;

    public MainFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000,1000);

        mainPanel = new MainPanel();
        this.add(mainPanel);
        this.setVisible(true);
    }
}

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private final DrawPanel drawPanel;
    private final JPanel optionPanel;
    private static final JCheckBox drawBox = new JCheckBox();

    static JMenuBar mb;

    static JTextField textField = new JTextField(16);

    static JMenu x;

    static JMenuItem saveItem, loadItem;


    public static final JCheckBox sliderBox = new JCheckBox("Offset");
    public static final JSlider slider = new JSlider(0, 100);

    public static final JCheckBox bresenhamBox = new JCheckBox("Bresenham");

    public static final JCheckBox wuBox = new JCheckBox("Wu");

    public static boolean isDrawBox() {
        return drawBox.isSelected();
    }

    public static void setDrawBox(boolean value) {
        drawBox.setSelected(value);
    }

    public MainPanel() {
        this.setLayout(new BorderLayout());


        mb = new JMenuBar();
        x = new JMenu("Menu");
        saveItem = new JMenuItem("Save");
        loadItem = new JMenuItem("Load");
        x.add(saveItem);
        x.add(loadItem);
        mb.add(x);

        ImageIcon imageIcon = new ImageIcon("./icons/createShape.png");
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
        drawBox.setSelectedIcon(new ImageIcon(newimg));
        imageIcon = new ImageIcon("./icons/createShapeOff.png");
        image = imageIcon.getImage();
        newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
        drawBox.setIcon(new ImageIcon(newimg));
        bresenhamBox.setSelected(false);
        wuBox.setSelected(false);


        optionPanel = new JPanel();
        drawPanel = new DrawPanel();
        slider.setMajorTickSpacing(20);
        bresenhamBox.addActionListener(drawPanel);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(drawPanel);
        drawBox.addActionListener(drawPanel);
        sliderBox.addActionListener(drawPanel);
        slider.setEnabled(false);
        optionPanel.add(drawBox);
        optionPanel.add(sliderBox);
        optionPanel.add(slider);
        optionPanel.add(bresenhamBox);
        wuBox.addActionListener(drawPanel);
        optionPanel.add(wuBox);

        saveItem.addActionListener(drawPanel);
        loadItem.addActionListener(drawPanel);
        optionPanel.add(mb);
        optionPanel.add(textField);

        this.add(optionPanel, BorderLayout.NORTH);
        this.add(drawPanel);
    }

}

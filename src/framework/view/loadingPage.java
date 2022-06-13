package framework.view;

import javax.swing.*;

import java.awt.*;

public class loadingPage {
    private static final String loadingImagePath = "images/loading.gif";

    public JPanel loadingPanel;
    private JLabel imageLabel;
    private JLabel loadingLabel;

    public loadingPage() {
        initialize();
    }

    private void initialize() {
        // initialize loading panel
        loadingPanel = new JPanel();
        loadingPanel.setBackground(Color.WHITE);
        loadingPanel.setLayout(null);
        loadingPanel.setSize(400,500);
        loadingPanel.setLocation(400,0);

        // loading pabel initialize
        loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setBounds(0, 330, 400,30);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        
        // load gif
        ImageIcon icon = new ImageIcon(loadingImagePath);
        imageLabel = new JLabel(icon);
        icon.setImageObserver(imageLabel);
        imageLabel.setBounds(100, 100, 200, 200);

        // add component to panel
        loadingPanel.add(imageLabel);
        loadingPanel.add(loadingLabel);
    }
}

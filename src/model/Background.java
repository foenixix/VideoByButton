
package model;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.EventQueue;
import javax.swing.JFrame;

public class Background
{
    private JFrame frame;
    
    public static void show() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final Background window = new Background();
                    window.frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public Background() {
        this.initialize();
    }
    
    private void initialize() {
        (this.frame = new JFrame()).setTitle("Gui");
        this.frame.setDefaultCloseOperation(3);
        this.frame.setExtendedState(6);
        this.frame.setUndecorated(true);
        this.frame.getContentPane().setLayout(null);
        final JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon(Background.class.getResource("/resources/background.jpg")));
        lblNewLabel.setBounds(0, 0, 1920, 1080);
        this.frame.getContentPane().add(lblNewLabel);
        this.frame.setVisible(true);
    }
}

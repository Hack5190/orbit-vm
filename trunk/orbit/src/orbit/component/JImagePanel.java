package orbit.component;

/**
 * Imports
 */
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * JPanel with background image
 * @author sjorge
 */
public class JImagePanel extends JPanel {

    private Image img;
    private boolean autoSizeBool = false;

    public JImagePanel() {
        super();
    }

    public JImagePanel(String img) {
        super();
        this.setImage(new ImageIcon(img).getImage());
    }

    public JImagePanel(Image img) {
        super();
        this.setImage(img);
    }

    public void setImage(Image img) {
        this.img = img;
    }

    public Image getImage() {
        return this.img;
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSizeBool = autoSize;
    }

    public boolean getAutoSize() {
        return this.autoSizeBool;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            if (autoSizeBool) {
                Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
                this.setPreferredSize(size);
                this.setMinimumSize(size);
                this.setMaximumSize(size);
                this.setSize(size);
            }
            g.drawImage(img, 0, 0, null);
        }
    }
}

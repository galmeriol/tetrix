package capture;

import java.awt.Component;
import java.awt.Font;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class ListRenderer extends DefaultListCellRenderer{
    
    private Map<String, ImageIcon> imageMap = null;
    
    public Map<String, ImageIcon> getImageMap() {
        return imageMap;
    }

    public void setImageMap(Map<String, ImageIcon> imageMap){
	this.imageMap = imageMap;
    }
    Font font = new Font("helvitica", Font.BOLD, 24);

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        label.setIcon(imageMap.get((String) value));
        label.setHorizontalTextPosition(JLabel.RIGHT);
        label.setFont(font);
        return label;
    }

}

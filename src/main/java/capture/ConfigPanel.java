package capture;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.opencv.core.Mat;

@SuppressWarnings("serial")
public class ConfigPanel extends JPanel implements ActionListener, ChangeListener {
    Context ctx;

    public Context getCtx() {
	return ctx;
    }

    public void setCtx(Context ctx) {
	this.ctx = ctx;
    }

    int FPS_MIN = 0;
    int FPS_MAX = 255;
    int FPS_INIT = 80;

    @SuppressWarnings("unchecked")

    public ConfigPanel(Dimension size) {
	this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

	this.setLayout(new BorderLayout());
	/*this.setLayout(layout);
	layout.setAutoCreateGaps(true);
	layout.setAutoCreateContainerGaps(true);*/

	setPreferredSize(size);
	setMinimumSize(size);
	setMaximumSize(size);
	setSize(size);

	JButton btnSample = new JButton("DETECT MODE");
	btnSample.setSize(new Dimension(100, 100));
	btnSample.addActionListener(this);
	/*End of mode selection*/

	add(btnSample, BorderLayout.NORTH);
    }

    JList list = null;

    void listThumbs(){

	Object data[][] = new Object[ctx.getActions().size()][];
	String columns[] = new String[ctx.vars.helperConstants.getSAMPLE_SIZE()];

	int c = 0;
	while(c < ctx.vars.helperConstants.getSAMPLE_SIZE())
	    columns[c++] = String.valueOf(c);

	for (String actionDir : ctx.getActionDirNames()) {
	    
	    list = new JList(columns);
	    ListRenderer renderer = new ListRenderer();
	    renderer.setImageMap(createImageMap(columns, actionDir));

	    list.setCellRenderer(renderer);
	 
	}
    }

    private Map<String, ImageIcon> createImageMap(String[] list, String dir) {
	Map<String, ImageIcon> map = new HashMap<String, ImageIcon>();
	for (String s : list) {
	    map.put(s, new ImageIcon(dir + "frame_" + s + ".jpg"));
	}
	return map;
    }


    public static Object[][] fill(Object[] a) {
	Object[][] result = new Object[a.length][2];

	for(int i = 0;i < a.length;i++){
	    ImageIcon icon = new ImageIcon(Frame.toBufferedImage((Mat)a[i]));
	    result[i][0] = icon;
	    result[i][1] = "frame_"+(i+1);
	}

	return result;
    }

    public void stateChanged(ChangeEvent arg0) {
	int val = ((JSlider)arg0.getSource()).getValue();
	if(ctx != null)
	    ctx.vars.diffModelConstants.setTHRESHOLD(val);
    }

    public void actionPerformed(ActionEvent arg0) {

	JButton b = ((JButton)arg0.getSource());
	listThumbs();
	b.setText("detecting...");
	if(ctx.getMODE() == Mode.DETECT){
	    b.setText("Sampling...");

	    ctx.setMODE((Mode.SAMPLE));
	}
	else if(ctx.getMODE() == Mode.SAMPLE){
	    b.setText("backsampling...");
	    ctx.setMODE(Mode.BACKGROUND);
	}
	else if(ctx.getMODE() == Mode.BACKGROUND){
	    b.setText("detecting...");
	    ctx.setMODE(Mode.TEST);
	}
	else if(ctx.getMODE() == Mode.TEST){
	    b.setText("detecting...");
	    ctx.setMODE(Mode.DETECT);
	}
    }




}
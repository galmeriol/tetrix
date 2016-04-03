package capture;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigPanel extends JPanel implements ActionListener, ChangeListener {
    Frame frame;
    int FPS_MIN = 0;
    int FPS_MAX = 255;
    int FPS_INIT = 80;

    @SuppressWarnings("unchecked")
    public ConfigPanel(Dimension size) {
	this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

	GroupLayout layout = new GroupLayout(this);

	this.setLayout(layout);
	layout.setAutoCreateGaps(true);
	layout.setAutoCreateContainerGaps(true);


	setPreferredSize(size);
	setMinimumSize(size);
	setMaximumSize(size);
	setSize(size);

	/* Thresholding */
	JLabel thresholdLabel = new JLabel("Threshold: ");
	//thresholdLabel.setBorder(border);
	JSlider thresholdSlider = new JSlider(JSlider.VERTICAL,
	                                      FPS_MIN, FPS_MAX, FPS_INIT);

	thresholdSlider.addChangeListener(this);
	thresholdSlider.setMajorTickSpacing(3);
	thresholdSlider.setPaintTicks(true);

	//Create the label table
	@SuppressWarnings("rawtypes")
	Hashtable labelTable = new Hashtable();
	labelTable.put( new Integer( 0 ), new JLabel("") );
	labelTable.put( new Integer( FPS_MAX/5 ), new JLabel("Light") );
	labelTable.put( new Integer( FPS_MAX/2 ), new JLabel("Dark") );
	thresholdSlider.setLabelTable( labelTable );
	thresholdSlider.setSize(200, 200);
	thresholdSlider.setPaintLabels(true);

	/* End of Thresholding*/


	/* Mode Selection*/

	JButton btnSample = new JButton("DETECT MODE");
	btnSample.addActionListener(this);
	/*End of mode selection*/

	layout.setVerticalGroup(
	                        layout.createSequentialGroup()
	                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                                  .addComponent(thresholdSlider)                    
	                                  .addComponent(thresholdLabel)
	                        	)
	                        	.addComponent(btnSample)

		);

	layout.setHorizontalGroup(
	                          layout.createSequentialGroup()
	                          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
	                                    .addComponent(thresholdSlider)                    
	                                    .addComponent(thresholdLabel)
	                                    .addComponent(btnSample)
	                        	  )
		);
    }
    public void setFrame(Frame f){
	this.frame = f;
    }

    public void stateChanged(ChangeEvent arg0) {
	int val = ((JSlider)arg0.getSource()).getValue();
	if(frame != null)
	    frame.setParam("threshold", val);
    }

    public void actionPerformed(ActionEvent arg0) {
	
	JButton b = ((JButton)arg0.getSource());
	b.setText("detecting...");
	if(frame.getMODE() == Mode.DETECT){
	    b.setText("Sampling...");
	    frame.setMODE(Mode.SAMPLE);
	}
	else if(frame.getMODE() == Mode.SAMPLE){
	    b.setText("backsampling...");
	    frame.setMODE(Mode.BACKGROUND);
	}
	else if(frame.getMODE() == Mode.BACKGROUND){
	    b.setText("detecting...");
	    frame.setMODE(Mode.TEST);
	}
	else if(frame.getMODE() == Mode.TEST){
	    b.setText("detecting...");
	    frame.setMODE(Mode.DETECT);
	}
    }


}
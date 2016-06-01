package capture;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
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

import libsvm.svm_model;
import models.OpticalFlowModel;
import models.OpticalFlowModelFarne;
import models.OpticalFlowMonocular;

import org.opencv.core.Mat;

import classification.CSVHelper;
import classification.SVMClassifier;

import com.opencsv.CSVReader;

import javax.swing.GroupLayout.Alignment;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.DefaultComboBoxModel;

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

    public ConfigPanel() {
	setSize(new Dimension(236, 480));
	this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	/*this.setLayout(layout);
	layout.setAutoCreateGaps(true);
	layout.setAutoCreateContainerGaps(true);*/


	JButton btnSample = new JButton("Sampling Hand...");
	btnSample.setSize(new Dimension(100, 100));
	btnSample.setActionCommand(Actions.MODE.name());
	btnSample.addActionListener(this);
	/*End of mode selection*/


	String[] models = new String[]{"Optical Flow (FARNE)", "Sparse Optical Flow (SOP)"};
	JComboBox modelList = new JComboBox(models);
	modelList.setActionCommand(Actions.MODEL.name());
	modelList.setModel(new DefaultComboBoxModel(new String[] {"seçiniz...", "Optical Flow (FARNE)", "Sparse Optical Flow (SOP)", "Monocular Optical Flow (MON)"}));
	modelList.setSelectedIndex(0);
	modelList.setPreferredSize(new Dimension(200, 75));
	modelList.addActionListener(this);

	JLabel lblNewLabel = new JLabel("Model:");
	lblNewLabel.setAlignmentX(2.0f);

	JLabel lblNewLabel_1 = new JLabel("Match Method:");

	JComboBox cmbMatchMethod = new JComboBox();
	cmbMatchMethod.setActionCommand(Actions.MATCH_METHOD.name());
	cmbMatchMethod.addActionListener(this);

	cmbMatchMethod.setModel(new DefaultComboBoxModel(new String[] {"SQDIFF", "CCOEFF", "CCORR"}));

	JButton btnTrain = new JButton("TRAIN");
	btnTrain.setActionCommand(Actions.TRAIN.name());
	btnTrain.addActionListener(this);

	JButton btnDonate = new JButton("DONATE");
	btnDonate.setActionCommand(Actions.DONATE.name());
	btnDonate.addActionListener(this);

	JButton btnCollect = new JButton("COLLECT");
	btnCollect.setActionCommand(Actions.COLLECT.name());
	btnCollect.addActionListener(this);

	GroupLayout groupLayout = new GroupLayout(this);
	groupLayout.setHorizontalGroup(
	                               groupLayout.createParallelGroup(Alignment.TRAILING)
	                               .addGroup(groupLayout.createSequentialGroup()
	                                         .addContainerGap()
	                                         .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
	                                                   .addGroup(groupLayout.createSequentialGroup()
	                                                             .addComponent(lblNewLabel)
	                                                             .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                                             .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
	                                                                       .addComponent(btnSample, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                                                       .addComponent(modelList, Alignment.TRAILING, 0, 132, Short.MAX_VALUE))
	                                                                       .addGap(50))
	                                                                       .addGroup(groupLayout.createSequentialGroup()
	                                                                                 .addComponent(cmbMatchMethod, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
	                                                                                 .addContainerGap(35, Short.MAX_VALUE))
	                                                                                 .addGroup(groupLayout.createSequentialGroup()
	                                                                                           .addComponent(lblNewLabel_1)
	                                                                                           .addContainerGap(80, Short.MAX_VALUE))
	                                                                                           .addGroup(groupLayout.createSequentialGroup()
	                                                                                                     .addComponent(btnTrain)
	                                                                                                     .addContainerGap(112, Short.MAX_VALUE))
	                                                                                                     .addGroup(groupLayout.createSequentialGroup()
	                                                                                                               .addComponent(btnDonate)
	                                                                                                               .addContainerGap(96, Short.MAX_VALUE))
	                                                                                                               .addGroup(groupLayout.createSequentialGroup()
	                                                                                                                         .addComponent(btnCollect)
	                                                                                                                         .addContainerGap(98, Short.MAX_VALUE))))
		);
	groupLayout.setVerticalGroup(
	                             groupLayout.createParallelGroup(Alignment.TRAILING)
	                             .addGroup(groupLayout.createSequentialGroup()
	                                       .addGap(6)
	                                       .addComponent(btnSample, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
	                                       .addPreferredGap(ComponentPlacement.RELATED)
	                                       .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
	                                                 .addComponent(modelList, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
	                                                 .addComponent(lblNewLabel))
	                                                 .addGap(18)
	                                                 .addComponent(lblNewLabel_1)
	                                                 .addPreferredGap(ComponentPlacement.RELATED)
	                                                 .addComponent(cmbMatchMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                                                 .addGap(8)
	                                                 .addComponent(btnCollect)
	                                                 .addPreferredGap(ComponentPlacement.RELATED)
	                                                 .addComponent(btnTrain)
	                                                 .addPreferredGap(ComponentPlacement.RELATED)
	                                                 .addComponent(btnDonate)
	                                                 .addGap(251))
		);
	setLayout(groupLayout);
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

	if(arg0.getActionCommand().equals(Actions.MODE.name())){
	    JButton b = ((JButton)arg0.getSource());
	    b.setText("Sampling Hand...");
	    if(ctx.getMODE() == Mode.DETECT){
		b.setText("Sampling Hand...");
		ctx.setMODE((Mode.SAMPLE));
	    }
	    else if(ctx.getMODE() == Mode.SAMPLE){
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(this, "Yen el kesit görüntüsünü onaylıyor musunuz?","Warning", dialogButton);

		if(dialogResult == 0)
		    ctx.setHandResampled(true);
		else
		    ctx.setHandResampled(false);

		b.setText("Detectng...");
		ctx.setMODE(Mode.DETECT);
	    }
	}
	else if(arg0.getActionCommand().equals(Actions.MODEL.name())){
	    System.out.println("combo");
	    JComboBox c = ((JComboBox)arg0.getSource());
	    String model = (String)c.getSelectedItem();

	    System.out.println(model);
	    if(model.endsWith("(SOP)")){
		ctx.setModel(new OpticalFlowModel());
	    }
	    else if(model.endsWith("(FARNE)")){
		ctx.setModel((new OpticalFlowModelFarne()));
	    }
	    else if(model.endsWith("(MON)")){
		ctx.setModel((new OpticalFlowMonocular()));
	    }
	}
	else if(arg0.getActionCommand().equals(Actions.MATCH_METHOD.name())){
	    JComboBox c = ((JComboBox)arg0.getSource());
	    String method = (String)c.getSelectedItem();

	    if(method.startsWith("SQDIFF")){
		ctx.setMatch_method(0);
	    }
	    else if(method.startsWith("CCOEFF")){
		ctx.setMatch_method(4);
	    }
	    else if(method.startsWith("CCORR")){
		ctx.setMatch_method(2);
	    }
	}
	else if(arg0.getActionCommand().equals(Actions.COLLECT.name())){
	    String prefix = "/home/zafer/Desktop/workspace/motion_data/";
	    String[] labels = new String[]{"SOL", "SAG", "ASAGI", "YUKARI", "CEVIR"};
	    List<double[]> descriptors = new ArrayList<double[]>();
	    int y = 0;
	    try {
		File traincsv = new File(prefix + "training_data/");
		if(!traincsv.exists())
		    traincsv.mkdir();
		traincsv = new File(prefix + "training_data/" + "train.csv");

		CSVHelper csvHelper = null;
		//List<String[]> data = csvHelper.read();
		for(String label:labels){
		    File trainingDataDir = new File(prefix + label);
		    System.out.println(label + " etiketi için data toplanıyor..");
		    for(String descriptorFolder:trainingDataDir.list()){

			//if(descriptorFile.startsWith("descriptor")){
			csvHelper = new CSVHelper();
			csvHelper.setReader(trainingDataDir + "/" + descriptorFolder + "/descriptor.csv");
			List<String[]> temp = csvHelper.read();

			String[] descriptorString = temp.get(0);
			double[] labeledDescriptor = new double[descriptorString.length + 1];
			labeledDescriptor[0] = Double.valueOf(y);
			for(int i = 1;i < descriptorString.length;i++){
			    labeledDescriptor[i] = Double.valueOf(descriptorString[i - 1]);
			}
			descriptors.add(labeledDescriptor);
			//}
		    }
		    System.out.println(label + " etiketi için data toplama işlemi bitti.");
		    y++;
		}

		int file_counter = 1;

		while((traincsv = new File(traincsv.getParent() + "/train_" + file_counter++ + ".csv")).exists());
		csvHelper = new CSVHelper(traincsv.getAbsolutePath());

		csvHelper.writeToCSV(descriptors);

		int dialogButton = JOptionPane.INFORMATION_MESSAGE;
		JOptionPane.showMessageDialog(this, "Collection işlemi başarıyla tamamlandı.", "Info", dialogButton);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	}
	else if(arg0.getActionCommand().equals(Actions.TRAIN.name())){
	    String prefix = "/home/zafer/Desktop/workspace/motion_data/";
	    File traincsv = new File(prefix + "training_data/");
	    String[] train_files = traincsv.list();
	    
	    CSVHelper csvHelper = null;
	    Arrays.sort(train_files, new Comparator<String>() {

		public int compare(String o1, String o2) {
		    int motion_indis = Integer.valueOf(o1.replace("train_", "").replace(".csv", ""));
		    int motion_indis2 = Integer.valueOf(o2.replace("train_", "").replace(".csv", ""));

		    if(motion_indis < motion_indis2)
			return 1;
		    else if(motion_indis > motion_indis2)
			return -1;

		    return 0;
		}
	    });
	    csvHelper = new CSVHelper();
	    csvHelper.setReader(prefix + "training_data/" + train_files[0]);
	    try {
		List<String[]> temp = csvHelper.read();
		double[][] dataformodel = csvHelper.convert(temp);
		
		SVMClassifier classifier = new SVMClassifier();
		classifier.setTrain(dataformodel);
		
		File svmmodel = new File(prefix + "models/model");
		int file_counter = 1;
		while((svmmodel = new File(svmmodel.getParent() + "/model" + file_counter++)).exists());
		
		svm_model model = classifier.train();
		classifier.saveSvmModel(model, svmmodel.getAbsolutePath());
		int dialogButton = JOptionPane.INFORMATION_MESSAGE;
		JOptionPane.showMessageDialog(this, "Model oluşturma işlemi başarıyla tamamlandı.", "Info", dialogButton);
		ctx.setSvmmodel(model);
		
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    
	}
	else if(arg0.getActionCommand().equals(Actions.DONATE.name())){
	    ElectionFrame election = new ElectionFrame("Hareket Verisi Eleme Ekranı", null);
	}

    }
    
    
    
}
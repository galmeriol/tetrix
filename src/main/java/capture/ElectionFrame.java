package capture;

import static java.nio.file.StandardCopyOption.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ReplicateScaleFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.Component;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class ElectionFrame extends JFrame{

    Context ctx = null;

    public Context getCtx() {
	return ctx;
    }

    public void setCtx(Context ctx) {
	this.ctx = ctx;
    }

    Comparator<String> action_comparator = new Comparator<String>() {

	public int compare(String o1, String o2) {
	    int motion_indis = Integer.valueOf(o1.replace("action_", ""));
	    int motion_indis2 = Integer.valueOf(o2.replace("action_", ""));

	    if(motion_indis < motion_indis2)
		return 1;
	    else if(motion_indis > motion_indis2)
		return -1;

	    return 0;
	}
    };

    private JLabel photographLabel = new JLabel();
    private JPanel buttonBar = new JPanel();
    private JComboBox cmbBar = new JComboBox();
    Dimension panelSize = new Dimension(200,200);
    private String prefix = "/home/zafer/Desktop/workspace/motion_data/";
    private String imagedir = prefix + "action_";

    private ImageIcon placeholderIcon = new ImageIcon("");

    /**
     * List of all the descriptions of the image files. These correspond one to
     * one with the image file names
     */
    /**
     * List of all the image files to load.
     */
    JPanel images = new JPanel();
    JScrollPane scrollPane = new JScrollPane(images,   ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    public ElectionFrame(String title, Context ctx) {



	super(title);


	getContentPane().setLayout(new BorderLayout());
	setCtx(ctx);
	setTitle(title);


	images.setPreferredSize(new Dimension(500, 300));
	images.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	BoxLayout layout = new BoxLayout(images, BoxLayout.PAGE_AXIS);


	images.setLayout(layout);

	images_load();

	getContentPane().add(images, BorderLayout.WEST);

	// A label for displaying the pictures
	photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
	photographLabel.setHorizontalTextPosition(JLabel.CENTER);
	photographLabel.setHorizontalAlignment(JLabel.CENTER);
	photographLabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

	// We add two glue components. Later in process() we will add thumbnail buttons
	// to the toolbar inbetween thease glue compoents. This will center the
	// buttons in the toolbar.

	setSize(900, 675);

	getContentPane().add(photographLabel, BorderLayout.CENTER);
	// this centers the frame on the screen
	setLocationRelativeTo(null);

	// start the image loading SwingWorker in a background thread
	loadimages.execute();
	
	setVisible(true);

    }

    private void images_load(){
	buttonBar.setName("bar1");
	//buttonBar.setFloatable(false);
	buttonBar.setToolTipText("motion1");
	buttonBar.setLayout(new FlowLayout(FlowLayout.LEADING, 3, 3));
	buttonBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	buttonBar.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
	buttonBar.setPreferredSize(panelSize);
	//images.add(Box.createRigidArea(new Dimension(10, 10)));
	//buttonBar.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));

	images.add(buttonBar);
    }

    public static void main(String args[]) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		ElectionFrame app = new ElectionFrame("Hareket Verisi Eleme Ekranı", null);
		app.setVisible(true);
	    }
	});
    }

    /**
     * SwingWorker class that loads the images a background thread and calls publish
     * when a new one is ready to be displayed.
     *
     * We use Void as the first SwingWroker param as we do not need to return
     * anything from doInBackground().
     */
    private SwingWorker<Void, ThumbnailAction> loadimages = new SwingWorker<Void, ThumbnailAction>() {

	/**
	 * Creates full size and thumbnail versions of the target image files.
	 */
	@Override
	protected Void doInBackground() throws Exception {

	    File motion_dir = new File(prefix);

	    String motions[] = motion_dir.list(new FilenameFilter() {

		public boolean accept(File dir, String name) {
		    if(name.startsWith("action"))
			return true;

		    return false;
		}
	    });

	    Arrays.sort(motions, action_comparator);
	    //int motion_count = motion_dir.list().length;

	    for (int i = 1; i <= motions.length; i++) {
		ImageIcon icon;
		for(int j = 1;j <= 4;j++){
		    imagedir = prefix + motions[i];
		    icon = createImageIcon(imagedir + "/frame_" + String.valueOf(j) + ".jpg", j + ".image");

		    ThumbnailAction thumbAction;
		    if(icon != null){
			ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(icon.getImage(), 32, 32));

			thumbAction = new ThumbnailAction(icon, thumbnailIcon, j + ".image");

		    }else{
			// the image failed to load for some reason
			// so load a placeholder instead
			thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, j + ".image");
		    }
		    publish(thumbAction);
		}
	    }
	    // unfortunately we must return something, and only null is valid to
	    // return when the return type is void.
	    return null;
	}

	/**
	 * Process all loaded images.
	 */
	@Override
	protected void process(List<ThumbnailAction> chunks) {
	    for (ThumbnailAction thumbAction : chunks) {
		JButton thumbButton = new JButton(thumbAction);
		//buttonBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		// add the new button BEFORE the last glue
		// this centers the buttons in the toolbar
		buttonBar.add(thumbButton);


		if(buttonBar.getComponentCount() == 4){
		    String i = buttonBar.getToolTipText().substring(buttonBar.getToolTipText().length() - 1);
		    int indis = Integer.valueOf(i);

		    cmbBar = new JComboBox();
		    cmbBar.setModel(new DefaultComboBoxModel(new String[] {"NO_ACTION", "SOL", "SAG", "ASAGI", "YUKARI", "CEVIR"}));

		    cmbBar.setName("cmb" + String.valueOf(indis));

		    buttonBar.add(cmbBar);

		    JButton addToFeatures = new JButton();
		    addToFeatures.setText("Onayla");
		    addToFeatures.setName("onay" + String.valueOf(indis));
		    addToFeatures.addActionListener(new ApproveAction(cmbBar));

		    buttonBar.add(addToFeatures);

		    buttonBar = new JPanel();
		    buttonBar.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		    buttonBar.setName("bar" + String.valueOf(indis + 1));
		    buttonBar.setPreferredSize(panelSize);
		    //buttonBar.setEnabled(false);
		    //buttonBar.setFloatable(false);
		    buttonBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		    buttonBar.setToolTipText("motion" + String.valueOf(indis + 1));
		    buttonBar.setLayout(new FlowLayout(FlowLayout.LEADING, 3, 3));
		    //buttonBar.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
		    images.add(buttonBar);
		}
	    }
	}

	class ApproveAction extends AbstractAction
	{
	    JComboBox combo = new JComboBox();
	    
	    public ApproveAction(JComboBox motion)
	    {
		combo = motion;
	    }
	    public void actionPerformed( ActionEvent e )
	    {
		JButton sender = ((JButton)e.getSource());
		File motion_dir = new File(prefix);
		String[] files = motion_dir.list(new FilenameFilter() {

		    public boolean accept(File dir, String name) {
			if(name.startsWith("action"))
			    return true;

			return false;
		    }
		});
		int motion_indis = Integer.valueOf(combo.getName().replace("cmb", "")) - 1;
		String label = combo.getSelectedItem().toString();
		Arrays.sort(files, action_comparator);
		System.out.println(motion_indis);
		File tobemoved = new File(prefix + files[motion_indis]);
		if(label.startsWith("NO")){
		    FileUtils.deleteDirectory(tobemoved);
		    int dialogButton = JOptionPane.INFORMATION_MESSAGE;
		    JOptionPane.showMessageDialog(images, tobemoved.getAbsolutePath() + " isimli hareket dosyası silinmiştir.", "Info", dialogButton);
		    sender.setEnabled(false);
		    combo.setEnabled(false);
		    sender.getParent().setBackground(Color.green);
		}
		else {
		    File dest = new File(tobemoved.getParent() + "/" + label);
		    //System.out.println(tobemoved.getAbsolutePath() + " isimli hareket dosyası '"+ label + "' etiketiyle " + dest + " klasörüne taşınıyor...");
		    try {
			FileUtils.copyDirectory(tobemoved, dest, true);
			FileUtils.deleteDirectory(tobemoved);
			int dialogButton = JOptionPane.INFORMATION_MESSAGE;
			JOptionPane.showMessageDialog(images, tobemoved.getAbsolutePath() + " isimli hareket dosyası '"+ label + "' etiketiyle " + dest + " klasörüne taşınmıştır.", "Info", dialogButton);
			sender.setEnabled(false);
			combo.setEnabled(false);
			sender.getParent().setBackground(Color.green);
		    } catch (IOException e1) {
			e1.printStackTrace();
		    }
		}
		//System.out.println("Taşıma işlemi tamamlandı...");
	    }
	}
    };

    /**
     * Creates an ImageIcon if the path is valid.
     * @param String - resource path
     * @param String - description of the file
     */
    protected ImageIcon createImageIcon(String path,
                                        String description) {
	//java.net.URL imgURL = getClass().getResource(path);

	return new ImageIcon(path);

    }



    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h){
	BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2 = resizedImg.createGraphics();
	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	g2.drawImage(srcImg, 0, 0, w, h, null);
	g2.dispose();
	return resizedImg;
    }


    /**
     * Action class that shows the image specified in it's constructor.
     */
    private class ThumbnailAction extends AbstractAction{

	/**
	 *The icon if the full image we want to display.
	 */
	private Icon displayPhoto;

	/**
	 * @param Icon - The full size photo to show in the button.
	 * @param Icon - The thumbnail to show in the button.
	 * @param String - The descriptioon of the icon.
	 */
	public ThumbnailAction(Icon photo, Icon thumb, String desc){
	    displayPhoto = photo;

	    // The short description becomes the tooltip of a button.
	    putValue(SHORT_DESCRIPTION, desc);

	    // The LARGE_ICON_KEY is the key for setting the
	    // icon when an Action is applied to a button.
	    putValue(LARGE_ICON_KEY, thumb);
	}

	/**
	 * Shows the full image in the main area and sets the application title.
	 */
	public void actionPerformed(ActionEvent e) {
	    photographLabel.setIcon(displayPhoto);
	    setTitle("Hareket Verisi Eleme Ekranı " + getValue(SHORT_DESCRIPTION).toString());
	}
    }

}

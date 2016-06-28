package main;
import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.imgscalr.Scalr;

import filters.DarkerFilter;
import filters.EdgeFilter;
import filters.Filter;
import filters.FishEyeFilter;
import filters.GrayScaleFilter;
import filters.InvertFilter;
import filters.LighterFilter;
import filters.PixelizeFilter;
import filters.SmoothFilter;
import filters.SolarizeFilter;
import filters.ThresholdFilter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * ImageViewer is the main class of the image viewer application. It builds and
 * displays the application GUI and initialises all other components.
 * 
 * To start the application, create an object of this class.
 * 
 * @author Michael KÃ¶lling and David J. Barnes.
 * @version 3.1
 */
public class ImageViewer implements ListSelectionListener
{
    // static fields:
    private static final String VERSION = "Version 3.1";
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

    // fields:
    private JFrame frame;
    private ImagePanel imagePanel;
    private JLabel filenameLabel;
    private JLabel statusLabel;
    private OFImage currentImage;
    
    private List<Filter> filters;
	private JScrollPane scrollPane;
	private JScrollPane previewPane;
	private ImageAlbum album;
	private ArrayList<String> filepaths;
	private JButton rotateLeftButton;
	private JButton rotateRightButton;
	private JButton prevButton;
	private JButton nextButton;
	private JButton slideshowButton;
	private JSlider zoomSlider;
	private Vector<ImageIcon> thumbnails;
	private JSplitPane splitPane;
	private JList list;
	
    
	
    /**
     * Create an ImageViewer and display its GUI on screen.
     */
	
	
	
	
	
	
	
	
    public ImageViewer()
    {
        currentImage = null;
        //new
        album = new ImageAlbum();
        thumbnails = new Vector<ImageIcon>();
        filepaths = new ArrayList<String>();
        filters = createFilters();
        makeFrame();
    }
  
 
  
    // ---- implementation of menu functions ----
    
    /**
     * Open function: open a file chooser to select a new image file,
     * and then display the chosen image.
     */
    private void openFile()
    {
    	
        int returnVal = fileChooser.showOpenDialog(frame);

        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;  // cancelled
        }
        File selectedFile = fileChooser.getSelectedFile();
        currentImage = ImageFileManager.loadImage(selectedFile);
       
        if(currentImage == null) {   // image file was not a valid image
            JOptionPane.showMessageDialog(frame,
                    "The file was not in a recognized image file format.",
                    "Image Load Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        filepaths.add(selectedFile.getPath());
        thumbnails.add(album.createThumbnail(currentImage));
        scrollPane.getViewport().add(imagePanel);
        previewPane.revalidate();
        splitPane.setDividerLocation(160);
        splitPane.setEnabled(false);	//lock the position of the divider
        album.addImage(currentImage);
        imagePanel.setImage(currentImage);
        setButtonsEnabled(true);
        setMenuItemsEnabled(true);
        showFilename(selectedFile.getPath());
        showStatus("File loaded.");
        
    }

    /**
     * Close function: close the current image.
     */
    private void close()
    {
        
        thumbnails.remove(album.getImageIndex());
        filepaths.remove(album.getImageIndex());
        album.deleteImage(album.getImageIndex());
        
        
        if (album.getImageIndex() != -1){
        	imagePanel.setImage(album.getImage());
        	setUndoRedoEnabled();
			showFilename(filepaths.get(album.getImageIndex()));
        	
        }else{
        	currentImage = null;
        	scrollPane.getViewport().remove(0);
        	splitPane.setDividerLocation(0);
        	showFilename(null);
        	showStatus(VERSION);
            setButtonsEnabled(false);
            setMenuItemsEnabled(false);
            list.setSelectedIndex(0);
        }
        
        list.revalidate();
        
        
    }

    /**
     * Save As function: save the current image to a file.
     */
    private void saveAs()
    {
        if(currentImage != null) {
            int returnVal = fileChooser.showSaveDialog(frame);
    
            if(returnVal != JFileChooser.APPROVE_OPTION) {
                return;  // cancelled
            }
            File selectedFile = fileChooser.getSelectedFile();
            ImageFileManager.saveImage(currentImage, selectedFile);
            
            showFilename(selectedFile.getPath());
        }
    }

    /**
     * Quit function: quit the application.
     */
    private void quit()
    {
        System.exit(0);
    }

    /**
     * Apply a given filter to the current image.
     * 
     * @param filter   The filter object to be applied.
     */
    private void applyFilter(Filter filter)
    {
        if(currentImage != null) {
            
        	OFImage newImage = new OFImage(currentImage);
        	
        	filter.apply(newImage);
        	currentImage = newImage;
            //new
            album.addEntry(currentImage);
            imagePanel.setImage(currentImage);
            frame.repaint();
            showStatus("Applied: " + filter.getName());
        }
        else {
            showStatus("No image loaded.");
        }
        setUndoRedoEnabled();
    }

    /**
     * 'About' function: show the 'about' box.
     */
    private void showAbout()
    {
        JOptionPane.showMessageDialog(frame, 
                    "ImageViewer\n" + VERSION,
                    "About ImageViewer", 
                    JOptionPane.INFORMATION_MESSAGE);
        
    }

    
    private void undo(){
    	
		if (album != null &&album.isUndoable()){
		currentImage = album.undo();
		
		imagePanel.setImage(currentImage);
		setUndoRedoEnabled();
		frame.repaint();
		}
		
	
	
}
    private void redo(){
	
    	if (album != null && album.isRedoable()){
		currentImage = album.redo();
		
		imagePanel.setImage(currentImage);
		setUndoRedoEnabled();
		frame.repaint();
    	}
	
	
}
    private void zoom(){
    		
    		int value = zoomSlider.getValue();
    		double scale = value/100.00;
    		imagePanel.setImage(currentImage, scale);
    		Rectangle viewportSize = scrollPane.getViewport().getViewRect();
    		Dimension viewSize = scrollPane.getViewport().getViewSize();
    		int x = (viewSize.width - viewportSize.width) / 2;
    		int y = (viewSize.height - viewportSize.height) / 2;
    		scrollPane.getViewport().setViewPosition(new Point(x, y));
    		scrollPane.revalidate();
    		frame.repaint();
    	
    }
    private void resize() {
		  
    	  JTextField widthField = new JTextField(4);
	      JTextField heightField = new JTextField(4);

	      JPanel resizePanel = new JPanel();
	      resizePanel.add(new JLabel("width:"));
	      resizePanel.add(widthField);
	      resizePanel.add(new JLabel("height:"));
	      resizePanel.add(heightField);
	      int result = JOptionPane.showConfirmDialog(null, resizePanel, 
	               "Resize", JOptionPane.OK_CANCEL_OPTION);
	      if(result != JFileChooser.APPROVE_OPTION) {
	            return;  // cancelled
	        }
	      String width = widthField.getText();
	      String height = heightField.getText();
	      if (width.matches("[0-9]+")){
	      int newWidth = Integer.parseInt(widthField.getText());
	      currentImage = new OFImage(Scalr.resize(currentImage, newWidth));
	      }
	      if (height.matches("[0-9]+")){
	      int newHeight = Integer.parseInt(heightField.getText());
	      currentImage = new OFImage(Scalr.resize(currentImage, newHeight));
	      }
	        
	      imagePanel.setImage(currentImage);
	      frame.repaint();
		
	}
    private void flipHorizontal(){
    	
    	int height = currentImage.getHeight();
        int width = currentImage.getWidth();
        OFImage newImage = new OFImage(width, height);
        for(int y = 0; y < height/2; y++) {
            for(int x = 0; x < width ; x++) {
                Color left = currentImage.getPixel(x, y);
                newImage.setPixel(x, y, currentImage.getPixel(x, height -1 -y));
                newImage.setPixel(x, height -1 -y, left);
            }
        }
        currentImage = newImage;
        album.addEntry(currentImage);
        setUndoRedoEnabled();
        imagePanel.setImage(currentImage);
	    frame.repaint();
    }
    private void flipVertical(){
    	int height = currentImage.getHeight();
        int width = currentImage.getWidth();
        OFImage newImage = new OFImage(width, height);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width / 2; x++) {
                Color left = currentImage.getPixel(x, y);
                newImage.setPixel(x, y, currentImage.getPixel(width - 1 - x, y));
                newImage.setPixel(width - 1 - x, y, left);
            }
        }
        currentImage = newImage;
        album.addEntry(currentImage);
        setUndoRedoEnabled();
        imagePanel.setImage(currentImage);
	    frame.repaint();
    }
    private void rotateRight(){
    	OFImage newImage = new OFImage(currentImage.getHeight(), currentImage.getWidth());
	    for (int x = 0; x<currentImage.getWidth(); x++){
			for(int y = 0; y < currentImage.getHeight(); y++){
				int newX = currentImage.getHeight() - y - 1;
				int newY = x;
				Color left = currentImage.getPixel(x, y);
	           
	            newImage.setPixel(newX, newY, left);
	            
	           
	            
			}
		}
	    currentImage = newImage;
	    imagePanel.setImage(currentImage);
	    album.addEntry(currentImage);
	    setUndoRedoEnabled();
	    frame.repaint();
    }
    private void rotateLeft(){
    	OFImage newImage = new OFImage(currentImage.getHeight(), currentImage.getWidth());
	    for (int x = 0; x<currentImage.getWidth(); x++){
			for(int y = 0; y < currentImage.getHeight(); y++){
		    	int newX = y;
				int newY = currentImage.getWidth() - x - 1;
				Color left = currentImage.getPixel(x, y);
		        
		        newImage.setPixel(newX, newY, left);
		        
       
		        
			}
		}
		currentImage = newImage;
		imagePanel.setImage(currentImage);
		album.addEntry(currentImage);
		setUndoRedoEnabled();
		frame.repaint();
    }
    
    
    
    // ---- support methods ----

    /**
     * Show the file name of the current image in the fils display label.
     * 'null' may be used as a parameter if no file is currently loaded.
     * 
     * @param filename  The file name to be displayed, or null for 'no file'.
     */
    private void showFilename(String filename)
    {
        if(filename == null) {
            filenameLabel.setText("No file displayed.");
        }
        else {
            filenameLabel.setText("File: " + filename);
        }
    }
    
    
    /**
     * Show a message in the status bar at the bottom of the screen.
     * @param text The status message.
     */
    private void showStatus(String text)
    {
        statusLabel.setText(text);
    }
    
    
    /**
     * Enable or disable all toolbar buttons.
     * 
     * @param status  'true' to enable the buttons, 'false' to disable.
     */
    private void setButtonsEnabled(boolean status)
    {
        zoomSlider.setEnabled(status);
    	rotateLeftButton.setEnabled(status);
    	rotateRightButton.setEnabled(status);
    	prevButton.setEnabled(status);
    	nextButton.setEnabled(status);
    	slideshowButton.setEnabled(status);
    }
    private void setMenuItemsEnabled(boolean status){
    	frame.getJMenuBar().getMenu(0).getItem(1).setEnabled(status);
    	frame.getJMenuBar().getMenu(0).getItem(2).setEnabled(status);
    	
    	frame.getJMenuBar().getMenu(1).getItem(2).setEnabled(status);
    	
    	int sizeF = frame.getJMenuBar().getMenu(2).getItemCount();
    	for (int i = 0; i < sizeF; i++){
    		frame.getJMenuBar().getMenu(2).getItem(i).setEnabled(status);
    	}
    	
    	int sizeI = frame.getJMenuBar().getMenu(3).getItemCount();
    	for (int i = 0; i < sizeI; i++){
    		frame.getJMenuBar().getMenu(3).getItem(i).setEnabled(status);
    	}
    }
    private void setUndoRedoEnabled(){
    	if (album.isUndoable()){ 
    		
    		frame.getJMenuBar().getMenu(1).getItem(0).setEnabled(true);
    	
    	}else{
    		frame.getJMenuBar().getMenu(1).getItem(0).setEnabled(false);
    	}
    	if (album.isRedoable()){ 
    		
    		frame.getJMenuBar().getMenu(1).getItem(1).setEnabled(true);
    	
    	}else{
    		frame.getJMenuBar().getMenu(1).getItem(1).setEnabled(false);
    	}
    	
    }
       
    
    /**
     * Create a list with all the known filters.
     * @return The list of filters.
     */
    private List<Filter> createFilters()
    {
        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(new DarkerFilter("Darker"));
        filterList.add(new LighterFilter("Lighter"));
        filterList.add(new ThresholdFilter("Threshold"));
        filterList.add(new InvertFilter("Invert"));
        filterList.add(new SolarizeFilter("Solarize"));
        filterList.add(new SmoothFilter("Smooth"));
        filterList.add(new PixelizeFilter("Pixelize"));
        filterList.add(new GrayScaleFilter("Grayscale"));
        filterList.add(new EdgeFilter("Edge Detection"));
        filterList.add(new FishEyeFilter("Fish Eye"));
       
        return filterList;
    }
    
    // ---- Swing stuff to build the frame and all its components and menus ----
    
    private void makeSlideshowFrame(){
    	JFrame slideshowFrame = new JFrame ("ImageView Slideshow");
    	JPanel slideshowContentPane = (JPanel)slideshowFrame.getContentPane();
    	
    	slideshowContentPane.setLayout(new GridLayout());
    	ImagePanel ip = new ImagePanel();
    	slideshowContentPane.add(ip);
    	slideshowFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
    	slideshowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	slideshowFrame.setVisible(true);
    	//int x =0;
    	Timer timer = new Timer(1200, new ActionListener(){

			private int x=0;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				File file = new File(filepaths.get(x));
				OFImage slideshowImage = ImageFileManager.loadImage(file);
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				double scale = d.getHeight()/slideshowImage.getHeight();
				ip.setImage(slideshowImage, scale);
				slideshowFrame.repaint();
				x += 1;
				if(x >= filepaths.size()) x = 0; 
				
				}
				
				
			});
			timer.start();	
			
			
			
    	
    }
    /**
     * Create the Swing frame and its content.
     */
    private void makeFrame()
    {
        frame = new JFrame("ImageViewer");
        JPanel contentPane = (JPanel)frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));

        makeMenuBar(frame);
        
        // Specify the layout manager with nice spacing
        contentPane.setLayout(new BorderLayout(6, 6));
        
        // Create the image pane in the center
        imagePanel = new ImagePanel();
        scrollPane = new JScrollPane(imagePanel);
        scrollPane.setVisible(true);
        scrollPane.setAutoscrolls(true);
        
        
        
        list = new JList<ImageIcon>(thumbnails);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();  
        renderer.setHorizontalAlignment(JLabel.CENTER);
        renderer.setVerticalAlignment(JLabel.CENTER);
        list.setFixedCellHeight(150);
        list.addListSelectionListener(this);
        previewPane = new JScrollPane();  
        previewPane.setViewportView(list);
       
        
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, previewPane, scrollPane);
        splitPane.setDividerLocation(0);
        splitPane.setEnabled(false);
        contentPane.add(splitPane, BorderLayout.CENTER);
        // Create two labels at top and bottom for the file name and status messages
        filenameLabel = new JLabel();
        contentPane.add(filenameLabel, BorderLayout.NORTH);

        
        
        // Create the toolbar with the buttons
        statusLabel = new JLabel(VERSION);
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        
        
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,0));
        

        
        ImageIcon iconLeft = new ImageIcon("icons/rotateLeft.png");
        rotateLeftButton = new JButton(iconLeft);
        rotateLeftButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				rotateLeft();
				setUndoRedoEnabled();
				
				
				
			}});
        buttons.add(rotateLeftButton);
        
        ImageIcon iconPrev = new ImageIcon("icons/prev.png");
        prevButton = new JButton(iconPrev);
        prevButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (album.getImageIndex() > 0){
				currentImage = album.prevImage();
				imagePanel.setImage(currentImage);
				setUndoRedoEnabled();
				showFilename(filepaths.get(album.getImageIndex()));
				}
				
				
			}});
        
        buttons.add(prevButton);
        
        ImageIcon iconPlay = new ImageIcon("icons/play.png");
        slideshowButton = new JButton(iconPlay);
        slideshowButton.addActionListener(new ActionListener(){

			

			@Override
			public void actionPerformed(ActionEvent e) {
				makeSlideshowFrame();
				
			}});
        buttons.add(slideshowButton);
        
        ImageIcon iconNext = new ImageIcon("icons/next.png");
        nextButton = new JButton(iconNext);
        nextButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (album.getImageIndex() <album.size()-1){
				currentImage = album.nextImage();
				imagePanel.setImage(currentImage);
				setUndoRedoEnabled();
				showFilename(filepaths.get(album.getImageIndex()));
				}
				
				
				
			}});
        buttons.add(nextButton);
        
        ImageIcon iconRight = new ImageIcon("icons/rotateRight.png");
        rotateRightButton = new JButton(iconRight);
        rotateRightButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				rotateRight();
				setUndoRedoEnabled();
				
				
			}});
        buttons.add(rotateRightButton);
        
        zoomSlider = new JSlider(JSlider.HORIZONTAL, 25, 200, 100);
        Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
        table.put(25, new JLabel("25%"));
        table.put(100, new JLabel("100%"));
        table.put(200, new JLabel("200%"));
        zoomSlider.setLabelTable(table);
        zoomSlider.setPaintLabels(true);
        zoomSlider.setMajorTickSpacing(100);
        zoomSlider.setMinorTickSpacing(1);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setPaintTicks(true);
        zoomSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				
				zoom();
				
				
				
				
				
				
			}});
        JPanel zoomSliderPanel = new JPanel();
        zoomSliderPanel.setLayout(new BorderLayout());
        zoomSliderPanel.add(zoomSlider, BorderLayout.EAST);
        
        
        
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout());  
        toolbar.add(statusLabel);
        toolbar.add(buttons);
        toolbar.add(zoomSliderPanel);
        
        

        // Add toolbar into panel with flow layout for spacing
        JPanel flow = new JPanel();
        flow.add(toolbar);
        
        contentPane.add(flow, BorderLayout.SOUTH);
        
        // building is done - arrange the components      
        showFilename(null);
        setButtonsEnabled(false);
        frame.setPreferredSize(new Dimension(1200, 600));
        frame.pack();
        
        // place the frame at the center of the screen and show
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width/2 - frame.getWidth()/2, d.height/2 - frame.getHeight()/2);
        frame.setVisible(true);
        
        
    }


	/**
     * Create the main frame's menu bar.
     * 
     * @param frame   The frame that the menu bar should be added to.
     */
    private void makeMenuBar(JFrame frame)
    {
        final int SHORTCUT_MASK =
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        
        JMenu menu;
        JMenuItem item;
        
        // create the File menu
        menu = new JMenu("File");
        menubar.add(menu);
        
        item = new JMenuItem("Open...");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
            item.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) { openFile(); }
                           });
        menu.add(item);

        item = new JMenuItem("Close");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_MASK));
            item.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) { close(); }
                           });
        item.setEnabled(false);
        menu.add(item);
       

        item = new JMenuItem("Save As...");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
            item.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) { saveAs(); }
                           });
        item.setEnabled(false);
        menu.add(item);
        
       
        
        item = new JMenuItem("Quit");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
            item.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) { quit(); }
                           });
        menu.add(item);

        
        menu = new JMenu("Edit");
        menubar.add(menu);
        
        
        item = new JMenuItem("Undo"); 
        item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				undo();
				
			}});
        item.setEnabled(false);
        menu.add(item);
        
        
        
        item = new JMenuItem("Redo");
        item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				redo();
				
			}});
        item.setEnabled(false);
        menu.add(item);
       
        
        item = new JMenuItem("Reload");
        item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(filepaths.get(album.getImageIndex()));
	            currentImage = ImageFileManager.loadImage(file);
	          
	            album.addEntry(currentImage);
	            imagePanel.setImage(currentImage);
	            setUndoRedoEnabled();
	            frame.repaint();
			}});
        item.setEnabled(false);
        menu.add(item);
        
        // create the Filter menu
        menu = new JMenu("Filter");
        menubar.add(menu);
        
        for(final Filter filter : filters) {
            item = new JMenuItem(filter.getName());
            item.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) { 
                                    applyFilter(filter);
                                    
                                }
                           });
            item.setEnabled(false);
            menu.add(item);
            
         }
        menu = new JMenu("Image");
        menubar.add(menu);
        
        item = new JMenuItem("Resize");
        item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				  resize();
			      
				
			}});
        item.setEnabled(false);
        menu.add(item);
        

        
        item = new JMenuItem("Rotate 90°");
        item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rotateRight();
				
			}});
        item.setEnabled(false);
        menu.add(item);
        
        item = new JMenuItem("Rotate 270°");
        item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rotateLeft();
				
			}});
        item.setEnabled(false);
        menu.add(item);

        item = new JMenuItem("Flip Horizontal");
        item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				flipHorizontal();
				
			}});
        item.setEnabled(false);
        menu.add(item);
        
        item = new JMenuItem("Flip Vertical");
        item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				flipVertical();
				
			}});
        item.setEnabled(false);
        menu.add(item);
        // create the Help menu
        menu = new JMenu("Help");
        menubar.add(menu);
        
        item = new JMenuItem("About ImageViewer...");
            item.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) { showAbout(); }
                           });
        menu.add(item);

    }
    

	

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		int index = list.getSelectedIndex();
		album.setImageIndex(index);
		currentImage = album.getImage();
		showFilename(filepaths.get(album.getImageIndex()));
		imagePanel.setImage(currentImage);
		frame.repaint();
		
	}

}	
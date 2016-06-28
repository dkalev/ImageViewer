package main;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

import org.imgscalr.Scalr;

import java.awt.image.*;

/**
 * An ImagePanel is a Swing component that can display an OFImage.
 * It is constructed as a subclass of JComponent with the added functionality
 * of setting an OFImage that will be displayed on the surface of this
 * component.
 * 
 * @author Michael Kölling and David J. Barnes.
 * @version 1.0
 */
public class ImagePanel extends JLabel 
{
    // The current width and height of this panel
    private int width, height;

    // An internal image buffer that is used for painting. For
    // actual display, this image buffer is then copied to screen.
    private OFImage panelImage;

	
    /**
     * Create a new, empty ImagePanel.
     */
    public ImagePanel()
    {
        width = 360;    // arbitrary size for empty panel
        height = 240;
        panelImage = null;
        
        
        
    }

    /**
     * Set the image that this panel should show.
     * 
     * @param image  The image to be displayed.
     */
    public void setImage(OFImage image)
    {
        if(image != null) {
            panelImage = new OFImage(Scalr.resize(image, width, height));
            repaint();
        }
    }
    
    
    public void setImage(OFImage image, double scale){
    	
    	if(image != null) {
    		int newHeight = (int) (image.getHeight()*scale);
            panelImage = new OFImage(Scalr.resize(image, newHeight));
            width = panelImage.getWidth();
            height = panelImage.getHeight();
            repaint();
    	}
    }
    
  

	
    
    // The following methods are redefinitions of methods
    // inherited from superclasses.
    
    /**
     * Tell the layout manager how big we would like to be.
     * (This method gets called by layout managers for placing
     * the components.)
     * 
     * @return The preferred dimension for this component.
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(width, height);
    }
    
    /**
     * This component needs to be redisplayed. Copy the internal image 
     * to screen. (This method gets called by the Swing screen painter 
     * every time it want this component displayed.)
     * 
     * @param g The graphics context that can be used to draw on this component.
     */
    public void paintComponent(Graphics g)
    {
    	if(panelImage != null) {
        Dimension size = getSize();
        int centeredWidth = size.width/2 - panelImage.getWidth()/2;
        int centeredHeight = size.height/2 - panelImage.getHeight()/2;
        
        g.drawImage(panelImage, centeredWidth, centeredHeight, null);
            
        }
    }
}

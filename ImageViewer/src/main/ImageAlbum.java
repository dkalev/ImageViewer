package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.imgscalr.Scalr;

public class ImageAlbum {

	
	private ArrayList<History> album;
	private int imageIndex;
	
	public ImageAlbum(){
		album = new ArrayList<History>();
		imageIndex = -1;
	}
	
	public void addImage(OFImage image){
		
		if (album.size() <= 20){
		History history = new History();
		history.addOFImage(image);
		album.add(history);
		imageIndex++;
		}
		
	}
	public void addEntry(OFImage image){
		
		album.get(imageIndex).addOFImage(image);
	}
	
	public void deleteImage(int index){
		
		if (imageIndex == album.size()-1){
		album.remove(index);
		imageIndex--;
		}else if (album.size() == 1){
			album.remove(index);
			imageIndex = -1;
			
		}else{
			album.remove(index);
		}
		
	}
	
	
	public int getImageIndex() {
		return imageIndex;
	}
	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}
	
	
	public OFImage getImage(){
		
		return album.get(imageIndex).getCurrent();
		
	}
	public OFImage nextImage(){
		
		if (imageIndex < album.size()-1){
		imageIndex++;
		return album.get(imageIndex).getFirst();
		}
		return null;
	}
	public OFImage prevImage(){
		if (imageIndex > 0){
			imageIndex--;
			return album.get(imageIndex).getFirst();
		}
		return null;
		
	}
	public int size(){
		return album.size();
	}
	public boolean isUndoable(){
		return album.get(imageIndex).isUndoable();
	}
	
	public boolean isRedoable(){
		return album.get(imageIndex).isRedoable();
	}
	
	public OFImage undo(){
		return album.get(imageIndex).undo();
	}
	
	public OFImage redo(){
		return album.get(imageIndex).redo();
	}
	
	public ImageIcon createThumbnail(OFImage image){
		BufferedImage newImage = Scalr.resize(image, 150);
		ImageIcon icon = new ImageIcon(newImage);
		
		
		return icon;
	}
	
		
}

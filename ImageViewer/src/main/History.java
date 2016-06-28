package main;

import java.util.LinkedList;

public class History {
	
	private LinkedList<OFImage> undoHistory;
	private LinkedList<OFImage> redoHistory;
	private int undoIndex;
	private int redoIndex;
	
	public History(){
		undoHistory = new LinkedList<OFImage>();
		redoHistory = new LinkedList<OFImage>();
		undoIndex = 0;
		redoIndex = 0;
	}
	public boolean isUndoable(){
		if (undoIndex > 1) return true;
		return false;
	}
	public boolean isRedoable(){
		if (redoIndex > 0) return true;
		return false;
	}
	
	public void addOFImage(OFImage image){
		
		if (undoHistory.size() <=20){
			undoHistory.addFirst(image);
			undoIndex++;
		}
	}
	
	public OFImage getFirst(){
		return undoHistory.getLast();
		
	}
	public OFImage getCurrent(){
		return undoHistory.getFirst();
	}
	
	public OFImage undo(){
		
		
			redoHistory.addFirst(undoHistory.removeFirst());
			undoIndex--;
			redoIndex++;
			return undoHistory.getFirst();
			
	}
	
	public OFImage redo(){
		
		
			undoHistory.addFirst(redoHistory.removeFirst());
			redoIndex--;
			undoIndex++;
			return undoHistory.getFirst();
		
	}
}

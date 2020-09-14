package editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

// key event listener class
public class Keyboard extends KeyAdapter {
	// when the key gets pressed
	@Override
	public void keyPressed(KeyEvent e) {
		// the key that was pressed
		int keyCode = e.getKeyCode();
		
		switch(keyCode) {
		// f1: creates new project
		case 112:
			if(Util.showOpenDialog()) {
				// prompts the user for tile size
				// displays input window
				String prompt = JOptionPane.showInputDialog(null, "Please enter the tile size.", "Enter tile size", JOptionPane.INFORMATION_MESSAGE);
				
				// user must press "OK" on input window for input to be processed
				// user's input cannot be blank
				// regex command for numbers only
				if(prompt != null && !prompt.isBlank() && prompt.matches("[0-9]+"))
					MapEditor.ui.tileSize = Integer.parseInt(prompt);
				
				// this gets ignored if user presses "CANCEL" on empty input
				else if(prompt != null)
					JOptionPane.showMessageDialog(null, "Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
				
				// cuts tileset up into individual tiles
				MapEditor.ui.tileSelector.cutUpTileset();
				MapEditor.ui.tileSelector.repaint();
			}
			
			break;
		
		// f2: saves between placement and solid mode
		case 113:
			MapEditor.ui.map.solidMode = !MapEditor.ui.map.solidMode;
			MapEditor.ui.map.repaint();
			break;
		
		// f3: saves map to a file
		case 114:
			Util.save(MapEditor.ui.map);
			break;
		}
	}
}

package editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

//tile map utility class
public class Util {
	// file chooser
	public static JFileChooser fileChooser;
	
	public static boolean showOpenDialog() {
		// open once
		if(MapEditor.ui.tileset != null) {
			JOptionPane.showMessageDialog(null, "Tileset has already been loaded", "Whoops!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		fileChooser = new JFileChooser();
		
		// show open dialog and store button information
		int result = Util.fileChooser.showOpenDialog(null);
		
		try {
			// loads image if valid option
			if(result == JFileChooser.APPROVE_OPTION) {
				MapEditor.ui.tileset = ImageIO.read(Util.fileChooser.getSelectedFile());
				return true;
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		return false;
	}
	
	// saves tile map data to file
	public static void save(Map map) {
		if(fileChooser == null)
			fileChooser = new JFileChooser();
		
		int result = fileChooser.showSaveDialog(null);
		
		if(result != JFileChooser.APPROVE_OPTION)
			return;
		
		File file = fileChooser.getSelectedFile();
		
		if(file == null)
			return;
		
		if(!file.getName().toLowerCase().endsWith(".txt"))
			file = new File(file.getPath() + ".txt");
		
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		writer.write(String.valueOf((int) MapEditor.ui.map.getTileMapWidth()));
		writer.println();
		
		writer.write(String.valueOf((int) MapEditor.ui.map.getTileMapHeight()));
		writer.println();
		
		ArrayList<Tile> temp = new ArrayList<Tile>();
		
		for(int i = map.smallestY; i < map.biggestY; i += MapEditor.ui.tileSize)
			for(int j = map.smallestX; j < map.biggestX; j += MapEditor.ui.tileSize)
				a: for(Tile tile : map.tiles)
					if(tile.tileMapX == j && tile.tileMapY == i) {
						temp.add(tile);
						break a;
					}
		
		for(Tile tile : temp) {
			writer.write(String.valueOf(tile.index));
			writer.println();
			
			writer.write(String.valueOf(tile.solid));
			writer.println();
		}
		
		writer.close();
	}
	
	public static int convertToTileSize(int number) {
		if(MapEditor.ui.tileSize == 0)
			return number;
		
		return number / MapEditor.ui.tileSize * MapEditor.ui.tileSize;
	}
}

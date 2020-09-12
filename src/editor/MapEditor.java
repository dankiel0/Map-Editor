package editor;

// imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// driver class
public class MapEditor {
	
	// for external use
	public static UI ui;
	
	public static void main(String[] args) {
		// create ui
		ui = new UI();
	}
}

// ui class
class UI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// the window
	public JFrame window;
	
	// stores tile map information
	public Map map;
	
	// stores tileset information
	public TileSelector tileSelector;
	
	// stores helpful information
	public Help help;
	
	// for map and tile selector classes to use
	public BufferedImage tileset;
	public Tile selectedTile;
	public int tileSize;
	
	public ArrayList<BufferedImage> individualTiles;
	
	// ui object constructor
	public UI() {
		// create window
		window = new JFrame();
		
		// adds the panels to the window
		window.getContentPane().add(map = new Map());
		window.getContentPane().add(tileSelector = new TileSelector());
		window.getContentPane().add(help = new Help());
		
		// adds key listener to the window
		window.addKeyListener(new Keyboard());
		
		// defaults
		window.setLayout(new FlowLayout());
		window.setTitle("Tile Map Editor");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		// initialize lists
		individualTiles = new ArrayList<BufferedImage>();
	}
	
	// key event listener class
	class Keyboard extends KeyAdapter {
		// when the key gets pressed
		@Override
		public void keyPressed(KeyEvent e) {
			// the key that was pressed
			int keyCode = e.getKeyCode();
			
			switch(keyCode) {
			// f1: creates new project
			case 112:
				if(FileUtil.showOpenDialog()) {
					// prompts the user for tile size
					// displays input window
					String prompt = JOptionPane.showInputDialog(null, "Please enter a number.", "Enter tile size", JOptionPane.INFORMATION_MESSAGE);
					
					// user must press "OK" on input window for input to be processed
					// user's input cannot be blank
					// regex command for numbers only
					if(prompt != null && !prompt.isBlank() && prompt.matches("[0-9]+"))
						MapEditor.ui.tileSize = Integer.parseInt(prompt);
					
					// this gets ignored if user presses "CANCEL" on empty input
					else if(prompt != null)
						JOptionPane.showMessageDialog(null, "Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
					
					MapEditor.ui.tileSelector.cutUpTileset();
					MapEditor.ui.tileSelector.repaint();
				}
				
				break;
			// f2: displays or hides the help panel
			case 113:
				// resizes panels based on boolean value
				if(help.visible) {
					window.getContentPane().remove(help);
					tileSelector.setPreferredSize(new Dimension(555, 720));
				}
				
				else {
					window.getContentPane().add(help);
					tileSelector.setPreferredSize(new Dimension(350, 720));
				}
				
				// switch boolean value
				help.visible = !help.visible;
				
				window.pack();
				break;
			
			// f4: switches between delete/add "modes"
			case 115:
				// delete mode stuff goes here
				break;
			
			// f5: saves map to a file
			case 116:
				FileUtil.save(map);
				break;
			
			// f6: loads map from a file
			case 117:
				FileUtil.load();
				break;
			}
		}
	}
}

class Map extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Tile> tiles;
	
	public int tileMapOffsetX, tileMapOffsetY, tileMapX, tileMapY;
	
	public Map() {
		// defaults
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setPreferredSize(new Dimension(730, 720));
		setBackground(Color.BLACK);
		
		// add mouse event listeners
		Mouse mouse = new Mouse();
		
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		// add description
		add(new JLabel("Map")).setForeground(Color.WHITE);
		
		// create list
		tiles = new ArrayList<Tile>();
	}
	
	// where stuff gets drawn
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(tiles.isEmpty())
			return;
		
		for(Tile tile : tiles)
			g.drawImage(MapEditor.ui.individualTiles.get(tile.index), tile.tileMapX + tileMapX, tile.tileMapY + tileMapY, null);
		
		g.setColor(Color.RED);
	}
	
	private class Mouse extends MouseAdapter {
		private boolean mouseExited;
		
		@Override
		public void mousePressed(MouseEvent e) {
			if(mouseExited)
				return;
			
			if(SwingUtilities.isRightMouseButton(e)) {
				tileMapOffsetX = e.getX() - tileMapX;
				tileMapOffsetY = e.getY() - tileMapY;
			}
			
			if(SwingUtilities.isLeftMouseButton(e)) {
				MapEditor.ui.selectedTile.setTileMapPosition((e.getX() - tileMapX) / MapEditor.ui.tileSize * MapEditor.ui.tileSize, (e.getY() - tileMapY) / MapEditor.ui.tileSize * MapEditor.ui.tileSize);
				
				for(int i = 0; i < tiles.size(); i++)
					if(tiles.get(i).tileMapX == MapEditor.ui.selectedTile.tileMapX && tiles.get(i).tileMapY == MapEditor.ui.selectedTile.tileMapY) {
						tiles.remove(tiles.get(i));
						break;
					}
				
				tiles.add((Tile) MapEditor.ui.selectedTile.clone());
			}
			
			repaint();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if(mouseExited)
				return;
			
			if(SwingUtilities.isRightMouseButton(e)) {
				tileMapX = (e.getX() - tileMapOffsetX) / MapEditor.ui.tileSize * MapEditor.ui.tileSize;
				tileMapY = (e.getY() - tileMapOffsetY) / MapEditor.ui.tileSize * MapEditor.ui.tileSize;
			}
			
			if(SwingUtilities.isLeftMouseButton(e)) {
				MapEditor.ui.selectedTile.setTileMapPosition((e.getX() - tileMapX) / MapEditor.ui.tileSize * MapEditor.ui.tileSize, (e.getY() - tileMapY) / MapEditor.ui.tileSize * MapEditor.ui.tileSize);
				
				for(int i = 0; i < tiles.size(); i++)
					if(tiles.get(i).tileMapX == MapEditor.ui.selectedTile.tileMapX && tiles.get(i).tileMapY == MapEditor.ui.selectedTile.tileMapY) {
						tiles.remove(tiles.get(i));
						break;
					}
				
				tiles.add((Tile) MapEditor.ui.selectedTile.clone());
			}
			
			repaint();
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			mouseExited = false;
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			mouseExited = true;
		}
	}
}

class TileSelector extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// tileset transform
	public int tilesetOffsetX, tilesetOffsetY, tilesetX, tilesetY;
	
	public TileSelector() {
		// defaults
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setPreferredSize(new Dimension(350, 720));
		setBackground(Color.BLACK);
		
		// add description
		add(new JLabel("Tileset")).setForeground(Color.WHITE);;
		
		
		Mouse mouse = new Mouse();
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
	}
	
	public void cutUpTileset() {
		int width = MapEditor.ui.tileset.getWidth() / MapEditor.ui.tileSize;
		int height = MapEditor.ui.tileset.getHeight() / MapEditor.ui.tileSize;
		
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++)
				MapEditor.ui.individualTiles.add(MapEditor.ui.tileset.getSubimage(j * MapEditor.ui.tileSize, i * MapEditor.ui.tileSize, MapEditor.ui.tileSize, MapEditor.ui.tileSize));
		
		MapEditor.ui.selectedTile = new Tile(0);
	}
	
	// where stuff gets drawn
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(MapEditor.ui.tileset == null)
			return;
		
		g.drawImage(MapEditor.ui.tileset, tilesetX, tilesetY, null);
		// draws box around tileset
		g.setColor(Color.WHITE);
		g.drawRect(tilesetX, tilesetY, MapEditor.ui.tileset.getWidth(), MapEditor.ui.tileset.getHeight());
		
		// draw selected tile from tileset
		g.setColor(Color.RED);
		g.drawRect(MapEditor.ui.selectedTile.selectorX, MapEditor.ui.selectedTile.selectorY, MapEditor.ui.tileSize, MapEditor.ui.tileSize);
	}
	
	// mouse event listener
	private class Mouse extends MouseAdapter {
		public boolean mouseExited;
		
		// when the mouse is dragged (aka pressed and moving)
		@Override
		public void mouseDragged(MouseEvent e) {
			if(mouseExited || MapEditor.ui.tileset == null || MapEditor.ui.tileSize == 0)
				return;
			
			if(SwingUtilities.isRightMouseButton(e)) {
				tilesetX = (e.getX() - tilesetOffsetX) / MapEditor.ui.tileSize * MapEditor.ui.tileSize;
				tilesetY = (e.getY() - tilesetOffsetY) / MapEditor.ui.tileSize * MapEditor.ui.tileSize;
			}
			
			if(SwingUtilities.isLeftMouseButton(e)) {
				MapEditor.ui.selectedTile.setSelectorPosition(e.getX() / MapEditor.ui.tileSize * MapEditor.ui.tileSize, e.getY() / MapEditor.ui.tileSize * MapEditor.ui.tileSize);
				
				int temp = ((MapEditor.ui.selectedTile.selectorY - tilesetY) / MapEditor.ui.tileSize) * (MapEditor.ui.tileset.getWidth() / MapEditor.ui.tileSize) + ((MapEditor.ui.selectedTile.selectorX - tilesetX) / MapEditor.ui.tileSize);
				
				if(temp < MapEditor.ui.individualTiles.size())
					MapEditor.ui.selectedTile.index = temp;
			}
			
			repaint();
		}
		
		// when any mouse key is pressed
		@Override
		public void mousePressed(MouseEvent e) {
			if(mouseExited || MapEditor.ui.tileset == null || MapEditor.ui.tileSize == 0)
				return;
			
			if(SwingUtilities.isRightMouseButton(e)) {
				tilesetOffsetX = e.getX() - tilesetX;
				tilesetOffsetY = e.getY() - tilesetY;
			}
			
			if(SwingUtilities.isLeftMouseButton(e)) {
				MapEditor.ui.selectedTile.setSelectorPosition(e.getX() / MapEditor.ui.tileSize * MapEditor.ui.tileSize, e.getY() / MapEditor.ui.tileSize * MapEditor.ui.tileSize);
				
				int temp = ((MapEditor.ui.selectedTile.selectorY - tilesetY) / MapEditor.ui.tileSize) * (MapEditor.ui.tileset.getWidth() / MapEditor.ui.tileSize) + ((MapEditor.ui.selectedTile.selectorX - tilesetX) / MapEditor.ui.tileSize);
				
				if(temp < MapEditor.ui.individualTiles.size())
					MapEditor.ui.selectedTile.index = temp;
			}
			
			repaint();
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			mouseExited = false;
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			mouseExited = true;
		}
	}
}

// help panel class
class Help extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// should the help panel be visible
	public boolean visible;
	
	// help panel object constructor
	public Help() {
		// defaults
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setPreferredSize(new Dimension(200, 720));
		setBackground(Color.WHITE);
		
		// helpful information
		add(new JLabel("Keyboard Help"));
		add(new JLabel("F1: New Project."));
		add(new JLabel("F2: Show/hide \"Keyboard Help.\""));
		add(new JLabel("F3: Delete/add tiles."));
		
		visible = true;
	}
}

// tile class
class Tile implements Cloneable {
	// index of tile on tile map
	public int index;
	
	// the position of the tile in the tile selector
	public int selectorX, selectorY;
	
	// the position of the tile in the tile map
	public int tileMapX, tileMapY;
	
	// is the tile solid
	// 0: not solid
	// 1: solid
	public int solid;
	
	// tile object constructor
	public Tile(int index) {
		this.index = index;
	}
	
	public void setSelectorPosition(int x, int y) {
		selectorX = x;
		selectorY = y;
	}
	
	public void setTileMapPosition(int x, int y) {
		tileMapX = x;
		tileMapY = y;
	}
	
	public Object clone() {
		try {
			return super.clone();
		}
		
		catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

// tile map utility class
class FileUtil {
	// file chooser
	public static JFileChooser fileChooser;
	
	public static boolean showOpenDialog() {
		// open once
		if(MapEditor.ui.tileset != null)
			return false;
		
		fileChooser = new JFileChooser();
		
		// show open dialog and store button information
		int result = FileUtil.fileChooser.showOpenDialog(null);
		
		try {
			// loads image if valid option
			if(result == JFileChooser.APPROVE_OPTION) {
				MapEditor.ui.tileset = ImageIO.read(FileUtil.fileChooser.getSelectedFile());
				return true;
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		return false;
	}
	
	// loads tile map data from file
	public static void load() {
		
	}
	
	// saves tile map data to file
	public static void save(Map map) {
		for(Tile tile : map.tiles)
			System.out.print(tile.index + " ");
	}
}

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
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// driver class
public class MapEditor {
	public static void main(String[] args) {
		// create ui
		new UI();
	}
}

// ui class
class UI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// the window
	private JFrame window;
	
	// the panels
	public JPanel paintSurface, tileSet, help;
	
	// should the help panel be visible
	private boolean hideHelp;
	
	public UI() {
		// create window
		window = new JFrame();
		
		// adds the panels
		window.getContentPane().add(paintSurface = new Map());
		window.getContentPane().add(tileSet = new Tileset());
		window.getContentPane().add(help = new Help());
		
		// adds key listener
		window.addKeyListener(new Keyboard());
		
		// defaults
		window.setLayout(new FlowLayout());
		window.setTitle("Tile Map Editor");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	// key event listener
	private class Keyboard extends KeyAdapter {
		// when the key gets pressed
		@Override
		public void keyPressed(KeyEvent e) {
			// key code
			int keyCode = e.getKeyCode();
			
			switch(keyCode) {
			
			// F1 key
			case 112:
				// opens file chooser
				((Tileset) tileSet).openTileset();
				break;
			
			// F2 key
			case 113:
				// shows or hides help panel based on boolean value
				// resizes the tileset panel to account for the lost window size
				hideHelp = !hideHelp;
				if(hideHelp) {
					window.getContentPane().remove(help);
					tileSet.setPreferredSize(new Dimension(550 + 5, 720));
				} else {
					window.getContentPane().add(help);
					tileSet.setPreferredSize(new Dimension(350, 720));
				}
				window.pack();
				break;
			
			// F3 key
			case 114:
				// locks or unlocks tile set movement
				((Tileset) tileSet).switchTileSetMobility();
				break;
			
			// F4 key
			case 115:
				// user input for tile size
				String input = JOptionPane.showInputDialog(null, "Please enter a number.", "Enter tile size", JOptionPane.INFORMATION_MESSAGE);
				
				// regex for numbers only
				if(input != null && !input.isBlank() && input.matches("[0-9]+"))
					((Tileset) tileSet).setTileSize(Integer.parseInt(input));
				// if user presses cancel
				else if(input != null)
					JOptionPane.showMessageDialog(null, "Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
				break;
			}
		}
	}
}

// map class
class Map extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Tile> tiles;
	
	public Map() {
		// defaults
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(730, 720));
		setBackground(Color.WHITE);
		
		// add mouse event listeners
		Mouse mouse = new Mouse();
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		// add description
		add(new JLabel("Map"));
		
		// create list
		tiles = new ArrayList<Tile>();
	}
	
	// where stuff gets drawn
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(Tile tile : tiles)
			if(tile != null)
				g.drawImage(tile.getSprite(), tile.getX(), tile.getY(), null);
	}
	
	private class Mouse extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				Tile tile = Tileset.getSelectedTile(e.getX(), e.getY());
				
				if(tile != null) {
					tiles.add(tile);
					repaint();
					System.out.println(tiles.size());
				}
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)) {
				Tile tile = Tileset.getSelectedTile(e.getX(), e.getY());
				
				if(tile != null) {
					tiles.add(tile);
					repaint();
					System.out.println(tiles.size());
				}
			}
		}
	}
}

// tileset class
class Tileset extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// for file selection
	private JFileChooser fileChooser;
	
	// the tileset
	private static BufferedImage tileSet;
	
	// tileset transform
	private static int mouseOffsetX, mouseOffsetY, imageX, imageY;
	
	// is the user allowed to move the tileset
	private boolean isTileSetLocked;
	
	// the selected tile from the tileset
	private static int tileSize, selectedX, selectedY;
	
	public Tileset() {
		// defaults
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(350, 720));
		setBackground(Color.WHITE);
		
		// add description
		add(new JLabel("Tileset"));
		
		Mouse mouse = new Mouse();
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		tileSize = 1;
		
		fileChooser = new JFileChooser();
	}
	
	// opens file chooser
	public void openTileset() {
		// which button the user presses
		int result = fileChooser.showOpenDialog(null);
		
		try {
			// loads image if valid option
			if(result == JFileChooser.APPROVE_OPTION)
				tileSet = ImageIO.read(fileChooser.getSelectedFile());
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		// resets values if valid option
		if(result == JFileChooser.APPROVE_OPTION) {
			imageX = 0;
			imageY = 0;
			selectedX = 0;
			selectedY = 0;
			isTileSetLocked = false;
		}
		
		repaint();
	}
	
	// sets the tile size
	// also resets values
	public void setTileSize(int size) {
		tileSize = size;
		
		imageX = 0;
		imageY = 0;
		selectedX = 0;
		selectedY = 0;
		
		repaint();
	}
	
	// switches tile set movement flag
	public void switchTileSetMobility() {
		isTileSetLocked = !isTileSetLocked;
	}
	
	// returns selected tile
	public static Tile getSelectedTile(int x, int y) {
		if(tileSet == null || selectedX - imageX > tileSet.getWidth() || selectedX - imageX < 0 || selectedY - imageY > tileSet.getHeight() || selectedY - imageY < 0)
			return null;
		return new Tile(tileSet.getSubimage(selectedX - imageX, selectedY - imageY, tileSize, tileSize), -1, x / tileSize * tileSize, y / tileSize * tileSize);
	}
	
	// where stuff gets drawn
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// draw tileset only if it exists
		if(tileSet != null) {
			g.drawImage(tileSet, imageX, imageY, null);
			// draws box around tileset
			g.drawRect(imageX, imageY, tileSet.getWidth(), tileSet.getHeight());
		}
		
		// draw selected tile from tileset
		g.setColor(Color.RED);
		g.drawRect(selectedX, selectedY, tileSize, tileSize);
	}
	
	// mouse event listener
	private class Mouse extends MouseAdapter {
		// when the mouse is dragged (aka pressed and moving)
		@Override
		public void mouseDragged(MouseEvent e) {
			// if the tileset exists, move it around
			if(tileSet != null) {
				// if the tileset is allowed to move, move it around
				if(!isTileSetLocked) {
					imageX = (e.getX() - mouseOffsetX) / tileSize * tileSize;
					imageY = (e.getY() - mouseOffsetY) / tileSize * tileSize;
				}
				
				// move the selected tile marker
				selectedX = e.getX() / tileSize * tileSize;
				selectedY = e.getY() / tileSize * tileSize;
				
				repaint();
			}
		}
		
		// when any mouse key is pressed
		@Override
		public void mousePressed(MouseEvent e) {
			// if the tileset exists, initialize the offsets and move the selected tile marker
			if(tileSet != null) {
				mouseOffsetX = e.getX() - imageX;
				mouseOffsetY = e.getY() - imageY;
				
				selectedX = e.getX() / tileSize * tileSize;
				selectedY = e.getY() / tileSize * tileSize;
				
				repaint();
			}
		}
	}
}

// help class
class Help extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public Help() {
		// defaults
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(200, 720));
		setBackground(new Color(0xFCFCFC));
		
		// helpful information
		add(new JLabel("Keyboard Help"));
		add(new JLabel("F1: Open tileset image."));
		add(new JLabel("F2: Hide/show \"Keyboard Help.\""));
		add(new JLabel("F3: Lock/unlock tileset movement."));
		add(new JLabel("F4: Set tile size."));
	}
}

class Tile {
	private BufferedImage sprite;
	private int index, x, y, isSolid;
	
	public Tile(BufferedImage tileSprite, int spriteIndex, int xPos, int yPos) {
		sprite = tileSprite;
		index = spriteIndex;
		x = xPos;
		y = yPos;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getIndex() {
		return index;
	}
	
	public BufferedImage getSprite() {
		return sprite;
	}
	
	public void setIsSolid(boolean val) {
		isSolid = val ? 1 : 0;
	}
}

// for loading and saving map files
class MapFileUtil {
	public static void openMapFile() {
		
	}
	
	public static void saveMapFile() {
		
	}
}

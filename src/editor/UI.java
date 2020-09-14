package editor;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

//ui class
public class UI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// the window
	public JFrame window;
	
	// stores tile map information
	public Map map;
	
	// stores tileset information
	public TilesetSelection tileSelector;
	
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
		window.getContentPane().add(tileSelector = new TilesetSelection());
		
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
}

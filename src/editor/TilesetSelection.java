package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TilesetSelection extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	
	// tileset transform
	public int offsetX, offsetY, tilesetX, tilesetY;
	
	public TilesetSelection() {
		// defaults
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setPreferredSize(new Dimension(640, 640));
		setBackground(Color.BLACK);
		
		// add description
		add(new JLabel("Tileset")).setForeground(Color.WHITE);;
		
		// add listeners
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	// cuts up the tileset into individual tiles
	public void cutUpTileset() {
		// tile size
		int tileSize = MapEditor.ui.tileSize;
		
		// width and height of tileset
		int width = MapEditor.ui.tileset.getWidth() / MapEditor.ui.tileSize;
		int height = MapEditor.ui.tileset.getHeight() / MapEditor.ui.tileSize;
		
		// cut up tileset
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++)
				MapEditor.ui.individualTiles.add(MapEditor.ui.tileset.getSubimage(j * tileSize, i * tileSize, tileSize, tileSize));
		
		// default tile
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
	
	// for mouse events
	private double scale;
	private boolean entered;
	
	// for zoom
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(!entered)
			return;
		
		scale += e.getWheelRotation() * 0.01;
		
		if(scale < 0.25)
			scale = 0.25;
		
		if(scale > 10)
			scale = 10;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		onMouseUpdate(e);
		
		// tileset will move around panel
		if(SwingUtilities.isMiddleMouseButton(e)) {
			tilesetX = Util.convertToTileSize(e.getX() - offsetX);
			tilesetY = Util.convertToTileSize(e.getY() - offsetY);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		onMouseUpdate(e);
		
		// tileset will move around panel
		if(SwingUtilities.isMiddleMouseButton(e)) {
			offsetX = e.getX() - tilesetX;
			offsetY = e.getY() - tilesetY;
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		entered = true;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		entered = false;
	}
	
	private void onMouseUpdate(MouseEvent e) {
		if(!entered || MapEditor.ui.selectedTile == null)
			return;
		
		// a new tile will be selected
		if(SwingUtilities.isLeftMouseButton(e)) {
			// the selected tile
			Tile selectedTile = MapEditor.ui.selectedTile;
			
			// set selected tile position
			selectedTile.setSelectorPosition(Util.convertToTileSize(e.getX()), Util.convertToTileSize(e.getY()));
			
			// index of tile on tileset
			int index = ((selectedTile.selectorY - tilesetY) / MapEditor.ui.tileSize) * (MapEditor.ui.tileset.getWidth() / MapEditor.ui.tileSize) + ((selectedTile.selectorX - tilesetX) / MapEditor.ui.tileSize);
			
			// index must fit into size
			if(index < MapEditor.ui.individualTiles.size())
				selectedTile.index = index;
		}
		
		repaint();
	}
	
	// not needed
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}

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
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Map extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Tile> tiles;
	
	public int offsetX, offsetY, tileMapX, tileMapY;
	
	public boolean solidMode;
	
	public int smallestX, biggestX, smallestY, biggestY;
	
	public Map() {
		// defaults
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setPreferredSize(new Dimension(640, 640));
		setBackground(Color.BLACK);
		
		// add mouse event listeners
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		
		// add description
		add(new JLabel("Map")).setForeground(Color.WHITE);
		
		// create list
		tiles = new ArrayList<Tile>();
	}
	
	// returns map width
	public double getTileMapWidth() {
		double mapSmallestX = Integer.MAX_VALUE;
		double mapBiggestX = Integer.MIN_VALUE;
		
		for(Tile tile : tiles) {
			if(tile.tileMapX < mapSmallestX)
				mapSmallestX = tile.tileMapX;
			if(tile.tileMapX > mapBiggestX)
				mapBiggestX = tile.tileMapX;
		}
		
		smallestX = (int) mapSmallestX;
		biggestX = (int) mapBiggestX + 1;
		
		return (mapBiggestX - mapSmallestX) / MapEditor.ui.tileSize + 1;
	}
	
	// returns map height
	public double getTileMapHeight() {
		double mapSmallestY = Integer.MAX_VALUE;
		double mapBiggestY = Integer.MIN_VALUE;
		
		for(Tile tile : tiles) {
			if(tile.tileMapY < mapSmallestY)
				mapSmallestY = tile.tileMapY;
			if(tile.tileMapY > mapBiggestY)
				mapBiggestY = tile.tileMapY;
		}
		
		smallestY = (int) mapSmallestY;
		biggestY = (int) mapBiggestY + 1;
		
		return (mapBiggestY - mapSmallestY) / MapEditor.ui.tileSize + 1;
	}
	
	// where stuff gets drawn
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(Tile tile : tiles) {
			g.drawImage(MapEditor.ui.individualTiles.get(tile.index), (int) (tile.tileMapX + tileMapX), (int) (tile.tileMapY + tileMapY), null);
			if(tile.solid == 1 && solidMode) {
				g.setColor(Color.RED);
				g.fillRect(tile.tileMapX + tileMapX, tile.tileMapY + tileMapY, MapEditor.ui.tileSize / 2, MapEditor.ui.tileSize / 2);
			}
		}
		
		g.setColor(Color.WHITE);
		g.drawString("<" + -tileMapX + ", " + -tileMapY + ">", 5, 40);
		g.drawString(solidMode ? "Solid Mode" : "Placement Mode", 5, 62);
	}
	
	public double scale;
	
	public boolean entered;
	
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
		
		// tiles will move around panel
		if(SwingUtilities.isMiddleMouseButton(e)) {
			tileMapX = e.getX() - offsetX;
			tileMapY = e.getY() - offsetY;
			
			if(-tileMapX <= 0)
				tileMapX = 0;
			if(-tileMapY <= 0)
				tileMapY = 0;
		}
		
		repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		onMouseUpdate(e);
		
		// tiles will move around panel
		if(SwingUtilities.isMiddleMouseButton(e)) {
			offsetX = e.getX() - tileMapX;
			offsetY = e.getY() - tileMapY;
		}
	}
	
	private void onMouseUpdate(MouseEvent e) {
		if(!entered || MapEditor.ui.tileSize == 0)
			return;
		
		if(!solidMode) {
			// add tile and remove existing tile
			if(SwingUtilities.isLeftMouseButton(e)) {
				// set tile position on map
				MapEditor.ui.selectedTile.setTileMapPosition(Util.convertToTileSize(e.getX() - tileMapX), Util.convertToTileSize(e.getY() - tileMapY));
				
				for(int i = tiles.size() - 1; i >= 0; i--)
					if(tiles.get(i).tileMapX == Util.convertToTileSize(e.getX() - tileMapX) && tiles.get(i).tileMapY == Util.convertToTileSize(e.getY() - tileMapY)) {
						if(tiles.get(i).index == MapEditor.ui.selectedTile.index)
							tiles.remove(i);
						break;
					}
				
				// add the selected tile to the tiles array
				tiles.add((Tile) MapEditor.ui.selectedTile.clone());
			}
			
			// remove hovered over tile
			if(SwingUtilities.isRightMouseButton(e))
				removeHoveredOverTile(e);
		} else {
			if(SwingUtilities.isLeftMouseButton(e)) {
				for(int i = tiles.size() - 1; i >= 0; i--) {
					if(tiles.get(i).tileMapX == Util.convertToTileSize(e.getX() - tileMapX) && tiles.get(i).tileMapY == Util.convertToTileSize(e.getY() - tileMapY)) {
						tiles.get(i).solid = 1;
						break;
					}
				}
			}
			
			if(SwingUtilities.isRightMouseButton(e)) {
				for(int i = tiles.size() - 1; i >= 0; i--) {
					if(tiles.get(i).tileMapX == Util.convertToTileSize(e.getX() - tileMapX) && tiles.get(i).tileMapY == Util.convertToTileSize(e.getY() - tileMapY)) {
						tiles.get(i).solid = 0;
						break;
					}
				}
			}
		}
		
		repaint();
	}
	
	// removes tile that is hovered over by mouse
	private void removeHoveredOverTile(MouseEvent e) {
		// loops through every tile on the map and compares coordinates to mouse coordinates
		for(int i = tiles.size() - 1; i >= 0; i--)
			if(tiles.get(i).tileMapX == Util.convertToTileSize(e.getX() - tileMapX) && tiles.get(i).tileMapY == Util.convertToTileSize(e.getY() - tileMapY)) {
				tiles.remove(i);
				break;
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
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}

package editor;

//tile class
public class Tile implements Cloneable {
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

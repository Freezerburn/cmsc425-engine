package texture;

/**
 * Author: FreezerburnVinny
 * Date: 2/11/12
 * Time: $(TIME}
 */
public interface TileMapLoader extends Runnable {
    public Texture get(int x, int y);
    public Texture[] getAll();
    public int getRows();
    public int getColumns();
}

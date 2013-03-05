package game.entimpl;

import game.Entity;
import scene.Bounds;
import scene.Vector3;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/21/13
 * Time: 5:45 PM
 */
public class Test extends Entity {
    public Test(float x, float y, float width, float height) {
        bounds = Bounds.get(Vector3.get(x + width / 2.0f, y + height / 2.0f), width, height);
    }

    @Override
    protected void onDestroy() {
        Bounds.recycle(bounds);
    }
}

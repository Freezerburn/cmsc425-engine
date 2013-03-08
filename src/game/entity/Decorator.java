package game.entity;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 3/7/13
 * Time: 12:05 PM
 */
public abstract class Decorator {
    protected Decorator child;

    protected Decorator() {
        this.child = null;
    }

    protected Decorator(Decorator decorator) {
        this.child = decorator;
    }

    public void tick(float dt) {
        onTick(dt);
        if(child != null) {
            child.tick(dt);
        }
    }

    public void draw(float dt) {
        onDraw(dt);
        if(child != null) {
            child.draw(dt);
        }
    }

    protected abstract void onTick(float dt);
    protected abstract void onDraw(float dt);
}

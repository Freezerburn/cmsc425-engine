package texture;

/**
 * Author: FreezerburnVinny
 * Date: 1/10/12
 * Time: $(TIME}
 */
public abstract class Texture {
    protected static int lastBound = -1;

    public abstract void alloc();
    public abstract void destroy();

    public abstract float getTexBottomRightX();
    public abstract float getTexTopRightX();
    public abstract float getTexTopLeftX();
    public abstract float getTexBottomLeftX();

    public abstract float getTexBottomRightY();
    public abstract float getTexTopRightY();
    public abstract float getTexTopLeftY();
    public abstract float getTexBottomLeftY();

    public abstract float getWidth();
    public abstract float getHeight();

    public abstract int getName();
    public abstract int getTarget();
    public abstract String getManagerHandle();

    public abstract void setWidth(double width);
    public abstract void setHeight(double height);

    public abstract void bind(float dt);

    public abstract boolean isValidTexture();

    public abstract void restart();
    public abstract void pause();
    public abstract void resume();
    public abstract void stopAt(int frame);
    public abstract void stopAfterFullCycle();

    public abstract int numFrames();
}

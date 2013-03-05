package texture;

import org.lwjgl.opengl.GL11;

/**
 * Author: FreezerburnVinny
 * Date: 1/5/12
 * Time: $(TIME}
 */
public class SimpleTexture extends Texture {
    private int mTarget, mName;
    private float mWidth, mHeight;
    private float mx1, mx2, my1, my2;

    private int refCount = 1;
    private String managerHandle;

    public SimpleTexture(int target, int name, String managerHandle) {
        this.mTarget = target;
        this.mName = name;
        this.mWidth = 0.0f;
        this.mHeight = 0.0f;
        this.mx1 = 0.0f;
        this.mx2 = 1.0f;
        this.my1 = 0.0f;
        this.my2 = 1.0f;
        this.managerHandle = managerHandle;
    }

    public SimpleTexture(int target, int name, String managerHandle, double width, double height) {
        this.mTarget = target;
        this.mName = name;
        this.mWidth = (float)width;
        this.mHeight = (float)height;
        this.managerHandle = managerHandle;
    }

    public SimpleTexture(int target, int name, String managerHandle, double width, double height,
                         double x1, double x2, double y1, double y2) {
        this.mTarget = target;
        this.mName = name;
        this.mWidth = (float)width;
        this.mHeight = (float)height;
        this.mx1 = (float)x1;
        this.mx2 = (float)x2;
        this.my1 = (float)y1;
        this.my2 = (float)y2;
        this.managerHandle = managerHandle;
    }

    @Override
    public void alloc() {
        refCount += 1;
//        System.out.println("Texture " + getName() + " at " + refCount + " refs");
    }

    @Override
    public void destroy() {
        refCount -=1;
//        System.out.println("Texture " + getName() + " at " + refCount + " refs");
        if(refCount == 0) {
//            System.out.println("Destroying texture: " + this.mName);
            TextureManager.removeTexture(managerHandle);
            GL11.glDeleteTextures(this.mName);
        }
    }

    public float getTexBottomRightX() {
        return mx2;
    }

    public float getTexBottomRightY() {
        return my1;
    }

    public float getTexTopRightX() {
        return mx2;
    }

    public float getTexTopRightY() {
        return my2;
    }

    public float getTexTopLeftX() {
        return mx1;
    }

    public float getTexTopLeftY() {
        return my2;
    }

    public float getTexBottomLeftX() {
        return mx1;
    }

    public float getTexBottomLeftY() {
        return my1;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public int getName() {
        return mName;
    }

    public int getTarget() {
        return mTarget;
    }

    @Override
    public String getManagerHandle() {
        return managerHandle;
    }

    public void setWidth(double width) {
        this.mWidth = (float)width;
    }

    public void setHeight(double height) {
        this.mHeight = (float)height;
    }

    public void bind(float dt) {
        if (Texture.lastBound != mName) {
//            System.out.println("Binding " + mName);
            GL11.glBindTexture(mTarget, mName);
            Texture.lastBound = mName;
        }
    }

    public boolean isValidTexture() {
        if (mName == -1) return false;
        return GL11.glIsTexture(mName);
    }

    @Override
    public void restart() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void stopAt(int frame) {
    }

    @Override
    public void stopAfterFullCycle() {
    }

    @Override
    public int numFrames() {
        return 1;
    }
}

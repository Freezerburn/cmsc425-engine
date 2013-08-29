package main;

import function.IntBinaryConsumer;

import java.util.function.IntBinaryOperator;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/19/13
 * Time: 8:31 PM
 */
public interface GameApplicationRunnable {
    public void initGL();
    public void initData();
    public void run(float dt);
    public void cleanup();
    public void setResizable(boolean resizable);
    public void setTickRate(float tickRate);
}

package game.entimpl;

import game.Entity;
import game.Project1;
import main.GameApplicationDisplay;
import org.lwjgl.input.Keyboard;
import scene.Bounds;
import structure.control.MouseEvent;
import structure.geometries.Cube;
import structure.geometries.SceneNode;
import structure.opengl.Matrix4;
import structure.opengl.Vector3;
import stuff.TempVars;
import texture.TextureManager;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 3/27/13
 * Time: 1:47 PM
 */
public class Player extends Entity {
    public static final float GRAVITY = 9.8f;
    public static final float MOVE_SPEED = 10.0f;

    public static final Vector3 CAMERA_OFFEST = new Vector3(0, 4, 4);

    protected SceneNode shell, flipperbl, flipperbr, flipperfl, flipperfr, head;
    protected Vector3 shellOff, flipperblOff, flipperbrOff, flipperflOff, flipperfrOff, headOff;

    protected float rotAroundCam = 0, flipperRotate = 0;
    protected Vector3 vel = new Vector3(), terpLoc = new Vector3();
    protected Vector3 rotbl = new Vector3(),
                        rotbr = new Vector3(),
                        rotfl = new Vector3(),
                        rotfr = new Vector3();
    protected boolean lastFrameMoved = false;
//    protected float rotateFullTerp = 0;
    protected boolean thirdPerson = false;
    protected boolean falling = true;

    public Player() {
        bounds = new Bounds(Project1.camera.getPosition(), new Vector3(1.5f, 4, 1.5f));
        GameApplicationDisplay.mouseManager.listenForMovement(this::onMouseMove);
//        Project1.instance.addMouseMovementListener(this::onMouseMove);

        // Magic numbers. Magic numbers EVERYWHERE.
        // DO NOT TOUCH THE NUMBERS
        shell = new Cube(Project1.colorProg, "model", "vertNormal");
        shellOff = new Vector3(0, 1.5f, 0);
        shell
                .move(0, 1.5f, 0)
                .setScale(2, 1, 2);
        ((Cube)shell).addTexture(TextureManager.loadTexture("res/shell.png", "shell", true, true));

        flipperbl = new Cube(Project1.colorProg, "model", "vertNormal");
        rotbl.set(-0.1f, 44.44f, 0.1f);
        flipperblOff = new Vector3(-1.55f, 0.5f, 3.6f);
        flipperbl
                .move(-1.55f, 0.5f, 3.6f)
                .setScale(0.8f, 0.3f, 2.3f)
                .rotate(-0.1f, 44.44f, 0.1f);

        flipperbr = new Cube(Project1.colorProg, "model", "vertNormal");
        rotbr.set(-0.1f, -44.44f, -0.1f);
        flipperbrOff = new Vector3(1.55f, 0.5f, 2.22f);
        flipperbr
                .move(1.55f, 0.5f, 2.22f)
                .setScale(0.8f, 0.3f, 2.3f)
                .rotate(-0.1f, -44.44f, -0.1f);

        flipperfl = new Cube(Project1.colorProg, "model", "vertNormal");
        rotfl.set(-0.1f, 84.44f, -0.1f);
        flipperflOff = new Vector3(-3.85f, 0.7f, 1.2f);
        flipperfl
                .move(-3.85f, 0.7f, 1.2f)
                .setScale(0.8f, 0.3f, 2.3f)
                .rotate(-0.1f, 84.44f, -0.1f);

        flipperfr = new Cube(Project1.colorProg, "model", "vertNormal");
        rotfr.set(-0.1f, -84.44f, -0.1f);
        flipperfrOff = new Vector3(1.85f, 0.5f, -0.8f);
        flipperfr
                .move(1.85f, 0.5f, -0.8f)
                .setScale(0.8f, 0.3f, 2.3f)
                .rotate(-0.1f, -84.44f, -0.1f);

        head = new Cube(Project1.colorProg, "model", "vertNormal");
        headOff = new Vector3(0, 1.3f, -2.77f);
        head
                .move(0, 1.3f, -2.77f)
                .setScale(0.8f, 0.8f, 0.8f);
    }

    public void setThirdPerson(boolean thirdPerson) {
        this.thirdPerson = thirdPerson;
    }

    @Override
    public void draw(float dt) {
        if(thirdPerson) {
            Vector3 cam = Project1.camera.getPosition();
            Matrix4 rot = new Matrix4();
            rot.translateLocal(cam.x, cam.y, cam.z);
            rot.translateLocal(terpLoc.x, terpLoc.y, terpLoc.z);
            rot = Matrix4.rotate(rot, 0, rotAroundCam, 0, new Matrix4());
            rot.translateLocal(0, -6, -4);
            rot.scaleLocal(0.7f, 0.7f, 0.7f);
//            rotateFullTerp += 100.0f * dt;
//            rot = Matrix4.rotate(rot, 0, rotateFullTerp, 0, new Matrix4());
            Project1.colorProg.use();
            Project1.colorProg.setUniform("preModel", rot);
            shell.draw();
            flipperbl.draw();
            flipperbr.draw();
            flipperfl.draw();
            flipperfr.draw();
            head.draw();
            Project1.colorProg.setUniform("preModel", new Matrix4());
            Project1.colorProg.stopUsing();
        }
    }

    @Override
    public void onKeyDown(int key) {
        if(key == Keyboard.KEY_W) {
            vel.z -= MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_S) {
            vel.z += MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_A) {
            vel.x -= MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_D) {
            vel.x += MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_SPACE) {
            falling = false;
            vel.y += MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_LSHIFT) {
            falling = false;
            vel.y -= MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_2) {
            falling = !falling;
        }
        else if(key == Keyboard.KEY_E) {
            Vector3 center = bounds.getCenter();
            thirdPerson = !thirdPerson;
            if(thirdPerson) {
                shell.setPosition(0, 0, 0);
                shell.move(shellOff);

                flipperbl.setPosition(0, 0, 0);
                flipperbl.move(flipperblOff);

                flipperbr.setPosition(0, 0, 0);
                flipperbr.move(flipperbrOff);

                flipperfl.setPosition(0, 0, 0);
                flipperfl.move(flipperflOff);

                flipperfr.setPosition(0, 0, 0);
                flipperfr.move(flipperfrOff);

                head.setPosition(0, 0, 0);
                head.move(headOff);

                Project1.camera.lookAt(center.x, center.y - 8, center.z + 8,
                        center.x, center.y - 3, center.z - 2,
                        Vector3.UP.x, Vector3.UP.y, Vector3.UP.z);
            }
            else {
                Project1.camera.lookAt(center.x, center.y, center.z,
                        center.x, center.y, center.z - 1,
                        Vector3.UP.x, Vector3.UP.y, Vector3.UP.z);
                terpLoc.set(0, 0, 0);
                rotAroundCam = 0;
//                Project1.camera.move(0, 0, 8);
            }
        }
    }

    @Override
    public void onKeyUp(int key) {
        if(key == Keyboard.KEY_W) {
            vel.z += MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_S) {
            vel.z -= MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_A) {
            vel.x += MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_D) {
            vel.x -= MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_SPACE) {
            falling = true;
            vel.y -= MOVE_SPEED;
        }
        else if(key == Keyboard.KEY_LSHIFT) {
            falling = true;
            vel.y += MOVE_SPEED;
        }
    }

    @Override
    public void onMouseMove(int dx, int dy) {
    }

    public void onMouseMove(MouseEvent me) {
        float dx = me.dx;
        float dy = me.dy;
        if(thirdPerson) {
            System.out.println("Move");
            float transform = 0.25f;
            float dxf = dx * transform;
            float dyf = dy * transform;
            rotAroundCam += dxf;
//            shell.rotate(0, dxf, 0);
//            flipperbl.rotate(0, dxf, 0);
//            flipperbr.rotate(0, dxf, 0);
        }
    }

    @Override
    protected void onTick(float dt) {
//        if(thirdPerson) {
//            shell.rotate(0, 100.0f * dt, 0);
//            flipperbl.rotate(0, 100.0f * dt, 0);
//        }
        if(thirdPerson) {
            if(vel.x != 0 || vel.y != 0 || vel.z != 0) {
                flipperRotate += dt;
                float amount = (float)Math.sin(Math.toRadians(flipperRotate * 130.0f + 100.0f)) / 5.0f;
                flipperbl.rotate(0, amount * 2.5f, 0);
                flipperbr.rotate(0, -amount * 2.5f, 0);
                flipperfl.rotate(0, amount * 2.5f, 0);
                flipperfr.rotate(0, -amount * 2.5f, 0);
                lastFrameMoved = true;
            }
            else if(lastFrameMoved) {
                flipperRotate = 0;
                Vector3 rotBackbl = rotbl.sub(flipperbl.getRotation());
                Vector3 rotBackbr = rotbr.sub(flipperbr.getRotation());
                Vector3 rotBackfl = rotfl.sub(flipperfl.getRotation());
                Vector3 rotBackfr = rotfr.sub(flipperfr.getRotation());
                flipperbl.rotate(rotBackbl.x, rotBackbl.y, rotBackbl.z);
                flipperbr.rotate(rotBackbr.x, rotBackbr.y, rotBackbr.z);
                flipperfl.rotate(rotBackfl.x, rotBackfl.y, rotBackfl.z);
                flipperfr.rotate(rotBackfr.x, rotBackfr.y, rotBackfr.z);
                lastFrameMoved = false;
            }
        }
//        float movement = -GRAVITY * dt;
        TempVars var = TempVars.get();
        Vector3 movement = vel.mult(dt);
//        System.out.println("VEL BEFORE: " + vel);
        Vector3 transformed = Vector3.transform(var.vect1.set(movement.x, 0, movement.z), Project1.camera.getOrientation());
//        System.out.println("VEL AFTER: " + transformed);
        transformed.y = movement.y;
//        System.out.println(movement);
        if(falling) {
            transformed.y += -GRAVITY * dt;
            movement.y += -GRAVITY * dt;
//            System.out.println("falling");
        }
        Project1.camera.moveRotated(movement);
        Vector3 before = bounds.getCenter();
//        System.out.println("CENTER BEFORE: " + bounds.getCenter());
//        System.out.println("VEL JUST BEFORE: " + transformed);
        bounds.moveLocal(transformed.x, transformed.y, transformed.z);
//        System.out.println("DELTA: " + bounds.getCenter().sub(before));
        for(SceneNode geom : Project1.meshes) {
            if(Bounds.getAABBIntersection(bounds, geom.getBounds(), var.vect1)) {
                Vector3.transform(var.vect3.set(var.vect1.x, 0, var.vect1.z), Project1.camera.getOrientation(), var.vect2);
                var.vect2.y = var.vect1.y;
//                bounds.moveLocal(var.vect2.x, var.vect2.y, var.vect2.z);
                bounds.moveLocal(var.vect1.x, var.vect1.y, var.vect1.z);
//                Project1.camera.moveRotated(var.vect1.x, var.vect1.y, var.vect1.z);
                Project1.camera.move(var.vect2);
            }
        }
        // Because (assumedly) floating point rounding errors suck, and the camera gets horribly, horribly
        // offset from where the collision bounds actually are.
        if(thirdPerson) {
//            Vector3 offset = new Vector3(bounds.getCenter()).sub(before);
            Vector3 offset = new Vector3(movement.x, bounds.getCenter().y - before.y, movement.z);
            terpLoc.add(offset);
//            shell.move(offset);
//            flipperbl.move(offset);
//            flipperbr.move(offset);
//            flipperfl.move(offset);
//            flipperfr.move(offset);
//            head.move(offset);
            Project1.camera.move(bounds.getCenter().sub(Project1.camera.getPosition()).add(CAMERA_OFFEST));
        }
        else {
            Project1.camera.move(bounds.getCenter().sub(Project1.camera.getPosition()));
        }
//        System.out.println("CENTER AFTER: " + bounds.getCenter());
//        System.out.println("CAMERA AFTER: " + Project1.camera.getPosition());
        var.release();
//        if(Bounds.getAABBIntersection(bounds, Project1.meshes.get(0).getBounds(), var.vect1)) {
//            bounds.moveLocal(var.vect1.x, var.vect1.y, var.vect1.z);
//            Project1.camera.moveRotated(var.vect1.x, var.vect1.y, var.vect1.z);
//        }
//        for(SceneNode geom : Project1.meshes) {
////            if(bounds.getSweepingAABB(geom.getBounds(), var.vect1)) {
//            if(Bounds.getAABBIntersection(bounds, geom.getBounds(), var.vect1)) {
////                bounds.moveLocal(0, -movement, 0);
////                Project1.camera.moveRotated(0, -movement, 0);
////                System.out.println("MY DELTA: " + bounds.getCenter().add(var.vect1).sub(bounds.getCenter()));
////                System.out.println("DELTA: " + var.vect1);
//                bounds.moveLocal(var.vect1.x, var.vect1.y, var.vect1.z);
//                Project1.camera.moveRotated(var.vect1.x, var.vect1.y, var.vect1.z);
//            }
//            if(var.vect1.x != 0 && var.vect1.y != 0 && var.vect1.z != 0) {
//                System.out.print("NON-ZERO ");
//            }
//            System.out.println();
//        }
    }

    @Override
    protected void onDestroy() {
    }
}

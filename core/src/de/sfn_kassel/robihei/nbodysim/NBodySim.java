package de.sfn_kassel.robihei.nbodysim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Random;

public class NBodySim extends ApplicationAdapter implements Runnable, InputProcessor, GestureDetector.GestureListener {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    Texture texture;
    Viewport vp;
    Camera c;

    Simulation sim;
    ArrayList<SObject> objs;

    boolean isTouched = false;
    Random r;
    double radius = 20;
    boolean zooming = false;
    boolean touching = false;
    int tCount = 0;
    int oID = 0;
    boolean removedObj = true;
    int fId = -1;
    float dx = 0;
    float dy = 0;
    boolean reset = false;
    float rotation = 0;


    @Override
    public void create() {
        c = new OrthographicCamera();
        vp = new ScreenViewport(c);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        texture = new Texture("reload.png");

        Gdx.graphics.setVSync(true);

        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(gd);
        im.addProcessor(this);

        Gdx.input.setInputProcessor(im);

        sim = new Simulation();

        r = new Random();

        new Thread(this).start();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        objs = (ArrayList<SObject>) sim.getObjects().clone();

        if (fId >= 0) {
            dx = (float) (0.5 * Gdx.graphics.getWidth() - objs.get(fId).getX());
            dy = (float) (0.5 * Gdx.graphics.getHeight() - objs.get(fId).getY());
        } else {
            dx = 0;
            dy = 0;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1.0f);
        shapeRenderer.ellipse((float) (50.0f - radius), (float) (50.0f - radius), (float) (2 * radius), (float) (2 * radius));

        for (SObject o : objs) {
            shapeRenderer.setColor(o.getC());
            shapeRenderer.ellipse((float) (o.getX() + dx - o.getRadius()), (float) (o.getY() + dy - o.getRadius()), (float) o.getRadius() * 2, (float) o.getRadius() * 2);
        }

        if (touching) {
            Vector3 t = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 1);

            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.line((float) objs.get(oID).getX() + dx, (float) objs.get(oID).getY() + dy, t.x, Gdx.graphics.getHeight() - t.y);
        }

        shapeRenderer.end();

        if (reset && rotation < 360) {
            rotation += 6f;
        } else {
            reset = false;
            rotation = 0;
        }

        batch.begin();
        batch.draw(texture, (float) 25, (float) Gdx.graphics.getHeight() - 75, (float) (texture.getWidth() * 0.5), (float) (texture.getHeight() * 0.5), texture.getWidth(), texture.getHeight(), 1, 1, 360.0f - rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height, true);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sim.evolve(0.04);
        }
    }

    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tCount++;

        if (tCount == 1) {
            sim.pause();

            removedObj = false;

            touching = true;

            Vector3 t = new Vector3(screenX, screenY, 0);

            c.project(t);

            Rectangle rec = new Rectangle(0, Gdx.graphics.getHeight() - 75, 75, 75);

            if (rec.contains(t.x, Gdx.graphics.getHeight() - t.y)) {
                sim.reset();
                fId = -1;
                oID = -1;
                touching = false;
                dx = 0;
                dy = 0;

                reset = true;
            } else {
                oID = sim.addObject(new SObject(t.x - dx, Gdx.graphics.getHeight() - t.y - dy, radius, new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1)));
            }

            Gdx.app.log("INFO", "touchDown at x: " + t.x + " y: " + (Gdx.graphics.getHeight() - t.y));
        } else if (tCount == 2) {
            if (!removedObj) {
                removedObj = true;
                touching = false;

                sim.removeObject(oID);
            }
        }

        return true;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        tCount--;

        if (touching) {

            Vector3 t = new Vector3(screenX, screenY, 0);

            c.project(t);

            sim.setVelocity(oID, t.x - dx, Gdx.graphics.getHeight() - t.y - dy);
        }

        touching = false;

        if (tCount < 1) {
            sim.unPause();
        }

        return true;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }

    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    public boolean longPress(float x, float y) {
        Vector3 t = new Vector3(x, y, 0);

        c.project(t);

        fId = sim.getHitObj(t.x - dx, Gdx.graphics.getHeight() - t.y - dy);

        Gdx.app.log("GestureDetectorTest", "long press at " + x + ", " + y + "hitID: " + fId);

        touching = false;

        return true;
    }

    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    public boolean zoom(float initialDistance, float distance) {
        zooming = true;

        radius -= 0.001 * (initialDistance - distance);
        if (radius < 2) radius = 2;

        Gdx.app.log("INFO", "new radius: " + radius);

        return true;
    }

    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}

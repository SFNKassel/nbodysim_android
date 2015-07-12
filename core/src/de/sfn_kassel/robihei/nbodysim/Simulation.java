package de.sfn_kassel.robihei.nbodysim;


import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by robin on 15.05.15.
 */
public class Simulation {
    private static double gamma = 1;
    private static double f1 = -1;
    private static double f2 = 1;
    private CopyOnWriteArrayList<SObject> objects = new CopyOnWriteArrayList<SObject>();
    private Random rand = new Random();
    private boolean paused;
    private boolean toReset = false;
    private boolean toRemove = false;
    private int toRemId = 0;

    public int addObject(SObject o) {
        boolean hitSomething = false;
        int hitNum = 0;

        for (int i = objects.size() - 1; i >= 0; --i) {
            if (objects.get(i).contains(o.getX(), o.getY())) {
                hitSomething = true;
                hitNum = i;
                break;
            }
        }

        if (!hitSomething) {
            o.setC(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1));
            objects.add(o);
            hitNum = objects.indexOf(o);
        }

        return hitNum;
    }

    public void reset() {
        toReset = true;
    }

    private void setupNew() {
        for (SObject o : objects) {
            o.setaX(0);
            o.setaY(0);
        }
    }

    private void sumForce() {
        double dx = 0, dy = 0, r = 0, t = 0;
        SObject a = null;
        SObject b = null;

        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                a = objects.get(i);
                b = objects.get(j);

                dx = a.getX() - b.getX();
                dy = a.getY() - b.getY();

                r = Math.sqrt(dx * dx + dy * dy);

                t = gamma / (r * r * r);

                a.addaX(f1 * t * dx * b.getMass());
                a.addaY(f1 * t * dy * b.getMass());
                b.addaX(f2 * t * dx * a.getMass());
                b.addaY(f2 * t * dy * a.getMass());
            }
        }
    }

    public void step(double dt) {
        double x, y;

        for (SObject o : objects) {
            x = 2 * o.getX();
            y = 2 * o.getY();

            x -= o.getOldX();
            y -= o.getOldY();

            x += o.getaX() * dt * dt;
            y += o.getaY() * dt * dt;

            o.setOldX(o.getX());
            o.setOldY(o.getY());

            o.setX(x);
            o.setY(y);
        }
    }

    public void evolve(double dt) {
        if (!paused) {
            setupNew();

            sumForce();

            step(dt);

            checkCollision(dt);
        }

        if (toRemove) {
            objects.remove(toRemId);
            toRemove = false;
        }

        if (toReset) {
            objects.clear();
            toReset = false;
        }
    }

    private void checkCollision(double dt) {
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                if (objects.get(i).contains(objects.get(j).getX(), objects.get(j).getY())) {
                    SObject o1 = objects.get(i);
                    SObject o2 = objects.get(j);

                    double px1 = ((o1.getX() - o1.getOldX()) / dt) * o1.getMass();
                    double py1 = ((o1.getY() - o1.getOldY()) / dt) * o1.getMass();

                    double px2 = ((o2.getX() - o2.getOldX()) / dt) * o2.getMass();
                    double py2 = ((o2.getY() - o2.getOldY()) / dt) * o2.getMass();

                    double vx = (px1 + px2) / (o1.getMass() + o2.getMass());
                    double vy = (py1 + py2) / (o1.getMass() + o2.getMass());

                    if (objects.get(i).getRadius() > objects.get(j).getRadius()) {
                        double ox = o1.getX() - (vx * dt);
                        double oy = o1.getY() - (vy * dt);

                        objects.get(i).addMass(objects.get(j).getMass());
                        objects.get(i).setOldX(ox);
                        objects.get(i).setOldY(oy);
                        objects.remove(j);
                    } else {
                        double ox = o2.getX() - (vx * dt);
                        double oy = o2.getY() - (vy * dt);

                        objects.get(j).addMass(objects.get(i).getMass());
                        objects.get(j).setOldX(ox);
                        objects.get(j).setOldY(oy);
                        objects.remove(i);
                    }
                }
            }
        }
    }

    public void pause() {
        paused = true;
    }

    public void unPause() {
        paused = false;
    }

    public void togglePause() {
        paused = !paused;
    }

    public ArrayList<SObject> getObjects() {
        return new ArrayList<SObject>(objects);
    }

    public void setVelocity(int oId, double vx, double vy) {
        vx -= objects.get(oId).getX();
        vy -= objects.get(oId).getY();

        objects.get(oId).addOldX(-vx * 0.001);
        objects.get(oId).addOldY(-vy * 0.001);
    }

    public int getHitObj(double x, double y) {
        int hitNum = -1;

        for (int i = objects.size() - 1; i >= 0; --i) {
            if (objects.get(i).contains(x, y)) {
                hitNum = i;
                break;
            }
        }

        return hitNum;
    }

    public void removeObject(int oID) {
        toRemove = true;
        toRemId = oID;
    }
}

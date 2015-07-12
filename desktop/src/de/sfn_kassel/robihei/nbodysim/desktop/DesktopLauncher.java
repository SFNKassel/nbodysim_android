package de.sfn_kassel.robihei.nbodysim.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.sfn_kassel.robihei.nbodysim.NBodySim;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.samples = 4;
        config.vSyncEnabled = true;

        new LwjglApplication(new NBodySim(), config);
    }
}

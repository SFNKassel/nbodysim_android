package de.sfn_kassel.robihei.nbodysim.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import de.sfn_kassel.robihei.nbodysim.NBodySim;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.numSamples = 2;
        initialize(new NBodySim(), config);
    }
}

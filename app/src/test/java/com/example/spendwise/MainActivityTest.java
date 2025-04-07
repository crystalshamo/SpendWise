package com.example.spendwise;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 31)
public class MainActivityTest {

    @Test
    public void logout_redirectsToLoginActivity() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();

        activity.logout(); // call the logout method

        Intent expectedIntent = new Intent(activity, LoginActivity.class);
        Intent actualIntent = Shadows.shadowOf(activity).getNextStartedActivity();

        // Check that the redirect intent is correct
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }
}

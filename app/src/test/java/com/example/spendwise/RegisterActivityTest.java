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
public class RegisterActivityTest {

    @Test
    public void clickingLoginText_opensLoginActivity() {
        RegisterActivity activity = Robolectric.buildActivity(RegisterActivity.class).create().start().resume().get();

        activity.textLogin.performClick();

        Intent expected = new Intent(activity, LoginActivity.class);
        Intent actual = Shadows.shadowOf(activity).getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
    }
}

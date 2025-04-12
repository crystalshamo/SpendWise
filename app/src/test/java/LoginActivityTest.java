import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

import com.example.spendwise.ForgotPassword;
import com.example.spendwise.LoginActivity;
import com.example.spendwise.RegisterActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 31)
public class LoginActivityTest {

    @Test
    public void clickingRegister_opensRegisterActivity() {
        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class).create().start().resume().get();

        activity.textRegister.performClick();

        Intent expected = new Intent(activity, RegisterActivity.class);
        Intent actual = Shadows.shadowOf(activity).getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
    }

    @Test
    public void clickingForgotPassword_opensForgotPasswordActivity() {
        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class).create().start().resume().get();

        activity.forgotPassword.performClick();

        Intent expected = new Intent(activity, ForgotPassword.class);
        Intent actual = Shadows.shadowOf(activity).getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
    }
}

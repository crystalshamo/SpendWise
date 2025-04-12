import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

import com.example.spendwise.ForgotPassword;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 31)
public class ForgotPasswordTest {

    @Test
    public void clickingBackFinishesActivity() {
        ForgotPassword activity = Robolectric.buildActivity(ForgotPassword.class).create().start().resume().get();

        activity.back.performClick();
        assertTrue(activity.isFinishing());
    }

    @Test
    public void restPassword_showsProgressBarOnClick() {
        ForgotPassword activity = Robolectric.buildActivity(ForgotPassword.class).create().start().resume().get();

        activity.emailEditText.setText("test@example.com");
        activity.restPasswordBtn.performClick();

        assertEquals(View.GONE, activity.progressBar.getVisibility());
    }
}

package com.example.spendwise.ui.notifications;

import android.os.Build;
import android.widget.EditText;

import androidx.fragment.app.testing.FragmentScenario;

import com.example.spendwise.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.S)
public class AccountFragmentTest {

    @Test
    public void updateUserProfile_allowsValidUpdates() {
        FragmentScenario<AccountFragment> scenario =
                FragmentScenario.launchInContainer(AccountFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            fragment.firstNameField.setText("Selena");
            fragment.lastNameField.setText("Shamo");

            // Simulate the success of a profile update (without real Firebase)
            fragment.simulateProfileUpdated();

            assertFalse(fragment.firstNameField.isEnabled());
            assertFalse(fragment.lastNameField.isEnabled());
        });
    }

    @Test
    public void updateUserProfile_doesNotUpdateIfFirstNameEmpty() {
        FragmentScenario<AccountFragment> scenario =
                FragmentScenario.launchInContainer(AccountFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            // Simulate tapping the edit icon
            fragment.firstNameField.setEnabled(true);
            fragment.lastNameField.setEnabled(true);

            fragment.firstNameField.setText(""); // Empty first name
            fragment.lastNameField.setText("Shamo");

            fragment.updateUserProfile();

            // Confirm update wasn't called
            assertFalse(fragment.wasUpdateCalled);

            // Fields should remain enabled since update didn't happen
            assertTrue(fragment.firstNameField.isEnabled());
            assertTrue(fragment.lastNameField.isEnabled());
        });
    }




    @Test
    public void updateUserProfile_doesNotCrashWithNullUserRef() {
        FragmentScenario<AccountFragment> scenario =
                FragmentScenario.launchInContainer(AccountFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            fragment.firstNameField.setText("Selena");
            fragment.lastNameField.setText("Shamo");

            fragment.userRef = null; // simulate missing reference

            fragment.updateUserProfile(); // should not crash
        });
    }

    // 🧪 Optional: confirm password field is set to asterisks on load
    @Test
    public void passwordField_isMaskedByDefault() {
        FragmentScenario<AccountFragment> scenario =
                FragmentScenario.launchInContainer(AccountFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            String actual = fragment.passwordField.getText().toString();
            assertEquals(8, actual.length());  // Optional: could also check for `!actual.isEmpty()`
        });
    }

    @Test
    public void updateUserProfile_doesNotDisableFieldsIfFirstNameEmpty() {
        FragmentScenario<AccountFragment> scenario =
                FragmentScenario.launchInContainer(AccountFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            // Simulate tapping the edit icon
            fragment.firstNameField.setEnabled(true);
            fragment.lastNameField.setEnabled(true);

            fragment.firstNameField.setText(""); // Empty first name
            fragment.lastNameField.setText("Shamo");

            fragment.updateUserProfile(); // Should not proceed

            // Fields should remain editable
            assertTrue(fragment.firstNameField.isEnabled());
            assertTrue(fragment.lastNameField.isEnabled());
        });
    }


}

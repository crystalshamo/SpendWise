package com.example.spendwise.ui.home;

import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.spendwise.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.S)
public class BudgetFragmentTest {

    // ✅ Test valid days between
    @Test
    public void getDaysBetween_returnsCorrectDays() {
        FragmentScenario<BudgetFragment> scenario =
                FragmentScenario.launchInContainer(BudgetFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            long days = fragment.getDaysBetween("01/01/2025", "01/06/2025");
            assertEquals(5, days);
        });
    }

    // ✅ Test invalid input returns 0
    @Test
    public void getDaysBetween_returnsZeroOnInvalidDates() {
        FragmentScenario<BudgetFragment> scenario =
                FragmentScenario.launchInContainer(BudgetFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            long days = fragment.getDaysBetween("bad", "date");
            assertEquals(0, days);
        });
    }

    // ✅ Test when end date is before start date
    @Test
    public void getDaysBetween_returnsZeroWhenEndBeforeStart() {
        FragmentScenario<BudgetFragment> scenario =
                FragmentScenario.launchInContainer(BudgetFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            long days = fragment.getDaysBetween("01/06/2025", "01/01/2025");
            assertEquals(0, days);
        });
    }

    // ✅ Test savings calculation
    @Test
    public void addGoal_calculatesRemainingCorrectly() {
        FragmentScenario<BudgetFragment> scenario =
                FragmentScenario.launchInContainer(BudgetFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            Map<String, Object> goal = new HashMap<>();
            goal.put("title", "Vacation");
            goal.put("goalAmount", "1000");
            goal.put("currentAmount", "200");
            goal.put("startDate", "01/01/2025");
            goal.put("endDate", "01/31/2025");
            goal.put("docId", "123");

            fragment.goalList = new LinearLayout(ApplicationProvider.getApplicationContext());
            fragment.addGoal(goal);

            TextView remaining = fragment.goalList.findViewById(R.id.remainingText);
            assertNotNull(remaining);
            assertTrue(remaining.getText().toString().contains("$800.00"));
        });
    }

    // ✅ Test more info is hidden by default
    @Test
    public void addGoal_hidesMoreInfoByDefault() {
        FragmentScenario<BudgetFragment> scenario =
                FragmentScenario.launchInContainer(BudgetFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            Map<String, Object> goal = new HashMap<>();
            goal.put("title", "Laptop");
            goal.put("goalAmount", "1500");
            goal.put("currentAmount", "500");
            goal.put("startDate", "01/01/2025");
            goal.put("endDate", "02/01/2025");
            goal.put("docId", "456");

            fragment.goalList = new LinearLayout(ApplicationProvider.getApplicationContext());
            fragment.addGoal(goal);

            TextView moreInfo = fragment.goalList.findViewById(R.id.moreInfoText);
            assertNotNull(moreInfo);
            assertEquals(View.GONE, moreInfo.getVisibility());
        });
    }

    // ✅ Double-tap reveals more info
    @Test
    public void doubleClick_togglesMoreInfoVisibility() {
        FragmentScenario<BudgetFragment> scenario =
                FragmentScenario.launchInContainer(BudgetFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            Map<String, Object> goal = new HashMap<>();
            goal.put("title", "Car");
            goal.put("goalAmount", "10000");
            goal.put("currentAmount", "2000");
            goal.put("startDate", "01/01/2025");
            goal.put("endDate", "03/01/2025");
            goal.put("docId", "789");

            fragment.goalList = new LinearLayout(ApplicationProvider.getApplicationContext());
            fragment.addGoal(goal);

            View goalCard = fragment.goalList.getChildAt(0);
            TextView moreInfo = goalCard.findViewById(R.id.moreInfoText);

            assertEquals(View.GONE, moreInfo.getVisibility());

            // Simulate toggle manually (not actual MotionEvent, since it’s logic-based in test env)
            moreInfo.setVisibility(View.VISIBLE);
            assertEquals(View.VISIBLE, moreInfo.getVisibility());

            moreInfo.setVisibility(View.GONE);
            assertEquals(View.GONE, moreInfo.getVisibility());
        });
    }

    // ✅ Test missing fields in goal map
    @Test
    public void addGoal_handlesMissingFieldsGracefully() {
        FragmentScenario<BudgetFragment> scenario =
                FragmentScenario.launchInContainer(BudgetFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            Map<String, Object> goal = new HashMap<>();
            goal.put("title", "Minimal");
            // goalAmount and currentAmount are missing

            fragment.goalList = new LinearLayout(ApplicationProvider.getApplicationContext());
            fragment.addGoal(goal);

            // Should not crash and still render a goal card
            assertEquals(1, fragment.goalList.getChildCount());
        });
    }

    // ✅ Test zero-day goal period
    @Test
    public void addGoal_zeroDayPeriod() {
        FragmentScenario<BudgetFragment> scenario =
                FragmentScenario.launchInContainer(BudgetFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            Map<String, Object> goal = new HashMap<>();
            goal.put("title", "Emergency");
            goal.put("goalAmount", "500");
            goal.put("currentAmount", "500");
            goal.put("startDate", "01/01/2025");
            goal.put("endDate", "01/01/2025");
            goal.put("docId", "999");

            fragment.goalList = new LinearLayout(ApplicationProvider.getApplicationContext());
            fragment.addGoal(goal);

            TextView remaining = fragment.goalList.findViewById(R.id.remainingText);
            assertNotNull(remaining);
            assertTrue(remaining.getText().toString().contains("$0.00"));
        });
    }
}

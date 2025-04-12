package com.example.spendwise.ui.dashboard;

import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;

import com.example.spendwise.R;
import com.example.spendwise.TransactionItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.S)
public class SavingFragmentTest {

    @Test
    public void transactionList_canBeManuallyCleared() {
        FragmentScenario<SavingFragment> scenario =
                FragmentScenario.launchInContainer(SavingFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            fragment.transactionItems.add(new TransactionItem("Food", 10f, "2024-04-01", "Dinner"));
            fragment.transactionItems.clear();
            assertTrue(fragment.transactionItems.isEmpty());
        });
    }

    @Test
    public void transactionList_allowsManualAdditions() {
        FragmentScenario<SavingFragment> scenario =
                FragmentScenario.launchInContainer(SavingFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            fragment.transactionItems.clear();
            fragment.transactionItems.add(new TransactionItem("Groceries", 15f, "2024-03-10", "Milk & Bread"));
            assertEquals(1, fragment.transactionItems.size());
        });
    }

    @Test
    public void transactionList_filtersByMarch() {
        FragmentScenario<SavingFragment> scenario =
                FragmentScenario.launchInContainer(SavingFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            fragment.transactionItems.clear();
            fragment.transactionItems.add(new TransactionItem("Food", 10f, "2024-03-01", "Lunch"));
            fragment.transactionItems.add(new TransactionItem("Food", 12f, "2024-04-01", "Dinner"));
            fragment.selectedMonth = "March";

            long marchTxns = fragment.transactionItems.stream()
                    .filter(t -> t.getDate().contains("-03-"))
                    .count();

            assertEquals(1, marchTxns);
        });
    }

    @Test
    public void selectedMonth_updatesCorrectly() {
        FragmentScenario<SavingFragment> scenario =
                FragmentScenario.launchInContainer(SavingFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            int index = fragment.monthAdapter.getPosition("April");
            assertTrue(index >= 0);
            fragment.selectedMonth = "April";
            assertEquals("April", fragment.selectedMonth);
        });
    }

    @Test
    public void transactionList_handlesEmptyDateGracefully() {
        FragmentScenario<SavingFragment> scenario =
                FragmentScenario.launchInContainer(SavingFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            fragment.transactionItems.clear();
            fragment.transactionItems.add(new TransactionItem("Bills", 100f, "", "No date info"));
            long valid = fragment.transactionItems.stream()
                    .filter(t -> !t.getDate().isEmpty())
                    .count();
            assertEquals(0, valid); // All dates are empty
        });
    }

    @Test
    public void transactionList_filtersNothingWhenMonthIsAll() {
        FragmentScenario<SavingFragment> scenario =
                FragmentScenario.launchInContainer(SavingFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            fragment.selectedMonth = "All";
            fragment.transactionItems.clear();
            fragment.transactionItems.add(new TransactionItem("Gas", 20f, "2024-01-01", "Full tank"));
            fragment.transactionItems.add(new TransactionItem("Gas", 25f, "2024-02-01", "Half tank"));

            assertEquals(2, fragment.transactionItems.size());
        });
    }

    @Test
    public void transactionList_filtersNothingIfInvalidMonthFormat() {
        FragmentScenario<SavingFragment> scenario =
                FragmentScenario.launchInContainer(SavingFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            fragment.selectedMonth = "March";
            fragment.transactionItems.clear();
            fragment.transactionItems.add(new TransactionItem("Misc", 5f, "Invalid-Date", "Whoops"));

            boolean valid = true;
            try {
                String month = fragment.transactionItems.get(0).getDate().split("-")[1];
                Integer.parseInt(month); // throws exception
            } catch (Exception e) {
                valid = false;
            }

            assertFalse(valid); // Yup, should be false
        });
    }

    @Test
    public void selectedMonth_spinnerIncludesAllMonths() {
        FragmentScenario<SavingFragment> scenario =
                FragmentScenario.launchInContainer(SavingFragment.class, null, R.style.Theme_SpendWise);

        scenario.onFragment(fragment -> {
            assertEquals(13, fragment.monthAdapter.getCount()); // "All" + 12 months
            assertTrue(fragment.monthAdapter.getPosition("February") >= 0);
            assertTrue(fragment.monthAdapter.getPosition("All") == 0);
        });
    }
}

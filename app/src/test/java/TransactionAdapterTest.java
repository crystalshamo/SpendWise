package com.example.spendwise;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 31)
public class TransactionAdapterTest {

    @Test
    public void adapter_returnsCorrectItemCount() {
        Map<String, List<TransactionItem>> data = new HashMap<>();
        data.put("Food", Arrays.asList(new TransactionItem("Food", 10f, "2024-01-01", "Lunch")));
        data.put("Transport", Arrays.asList(new TransactionItem("Transport", 20f, "2024-01-02", "Bus")));

        TransactionAdapter adapter = new TransactionAdapter(data);
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void adapter_togglesExpandedCategories() {
        Map<String, List<TransactionItem>> data = new HashMap<>();
        data.put("Food", new ArrayList<>());
        TransactionAdapter adapter = new TransactionAdapter(data);

        assertFalse(adapter.expanded.contains("Food"));
        adapter.expanded.add("Food");
        assertTrue(adapter.expanded.contains("Food"));
        adapter.expanded.remove("Food");
        assertFalse(adapter.expanded.contains("Food"));
    }
}

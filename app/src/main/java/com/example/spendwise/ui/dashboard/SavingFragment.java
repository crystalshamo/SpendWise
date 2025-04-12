package com.example.spendwise.ui.dashboard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.PieChartView;
import com.example.spendwise.R;
import com.example.spendwise.TransactionAdapter;
import com.example.spendwise.TransactionItem;
import com.example.spendwise.databinding.FragmentDashboardBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class SavingFragment extends Fragment {

    public List<TransactionItem> transactionItems = new ArrayList<>();
    public FragmentDashboardBinding binding;
    public FirebaseFirestore db;
    public FirebaseAuth auth;
    public String selectedMonth = "All";
    public ArrayAdapter<String> monthAdapter;
    public RecyclerView transactionList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ✅ Skip Firebase initialization during test runs
        if (isRunningInTest()) {
            auth = null;
            db = null;
        } else {
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        }

        monthFilter(root);

        transactionList = root.findViewById(R.id.transaction_list);
        transactionList.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(v -> addTransactionDialog());

        // ✅ Only load from Firebase if we're not testing
        if (auth != null && db != null) {
            loadTransactions();
        }

        return root;
    }

    public void monthFilter(View root) {
        Spinner monthFilter = root.findViewById(R.id.month_filter_spinner);
        List<String> months = Arrays.asList("All", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");

        monthAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_text, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthFilter.setAdapter(monthAdapter);

        monthFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = months.get(position);
                if (auth != null && db != null) {
                    loadTransactions();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void addTransactionDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_transaction, null);

        Spinner categorySpinner = dialogView.findViewById(R.id.input_category);
        EditText amountInput = dialogView.findViewById(R.id.input_amount);
        EditText dateInput = dialogView.findViewById(R.id.input_date);
        EditText descriptionInput = dialogView.findViewById(R.id.input_description);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.transaction_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        dateInput.setFocusable(false);
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        dateInput.setText(formatted);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Transaction Added")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    if (auth == null || db == null) return;

                    String category = categorySpinner.getSelectedItem().toString();
                    String amountStr = amountInput.getText().toString();
                    String date = dateInput.getText().toString();
                    String description = descriptionInput.getText().toString();

                    if (amountStr.isEmpty() || date.isEmpty()) return;

                    float amount = Float.parseFloat(amountStr);
                    String uid = auth.getCurrentUser().getUid();

                    Map<String, Object> transaction = new HashMap<>();
                    transaction.put("category", category);
                    transaction.put("amount", amount);
                    transaction.put("date", date);
                    transaction.put("description", description);

                    db.collection("users").document(uid)
                            .collection("transactions")
                            .add(transaction)
                            .addOnSuccessListener(doc -> loadTransactions());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void loadTransactions() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("transactions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Float> pieData = new HashMap<>();
                    Map<String, List<TransactionItem>> grouped = new HashMap<>();
                    transactionItems.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String category = doc.getString("category");
                        Double amount = doc.getDouble("amount");
                        String date = doc.getString("date");
                        String desc = doc.getString("description");

                        if (category == null || amount == null || date == null) continue;

                        boolean include = true;
                        if (!selectedMonth.equals("All")) {
                            try {
                                int txnMonth = Integer.parseInt(date.split("-")[1]);
                                int selectedIndex = monthAdapter.getPosition(selectedMonth);
                                include = (txnMonth == selectedIndex);
                            } catch (Exception e) {
                                include = false;
                            }
                        }

                        if (include) {
                            pieData.put(category, pieData.getOrDefault(category, 0f) + amount.floatValue());

                            TransactionItem item = new TransactionItem(category, amount.floatValue(), date, desc != null ? desc : "");
                            transactionItems.add(item);

                            if (!grouped.containsKey(category)) grouped.put(category, new ArrayList<>());
                            grouped.get(category).add(item);
                        }
                    }

                    transactionList.setAdapter(new TransactionAdapter(grouped));
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ✅ Used to detect Robolectric test mode
    private boolean isRunningInTest() {
        return "robolectric".equals(Build.FINGERPRINT) || System.getProperty("robolectric.running") != null;
    }
}

// package ...
package com.example.spendwise.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.spendwise.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BudgetFragment extends Fragment {

    public LinearLayout goalList;
    public FirebaseFirestore db;
    public String userId;
    public final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        goalList = root.findViewById(R.id.goalList);
        FloatingActionButton fab = root.findViewById(R.id.fabAddGoal);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        fab.setOnClickListener(v -> addGoalDialog());
        loadGoals();

        Button viewCompletedBtn = root.findViewById(R.id.viewCompletedBtn);
        viewCompletedBtn.setOnClickListener(v -> completedGoalsPopup());


        return root;
    }

    // pulls previous budget goals stored in database corresponding the userID
    public void loadGoals() {
        goalList.removeAllViews();

        db.collection("savings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean hasCompleted = false;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> goal = doc.getData();
                        goal.put("docId", doc.getId());

                        double current = Double.parseDouble((String) goal.get("currentAmount"));
                        double target = Double.parseDouble((String) goal.get("goalAmount"));

                        if (current >= target) {
                            hasCompleted = true;
                            completedGoal(doc.getId(), goal);
                        } else {
                            addGoal(goal);
                        }
                    }

                    if (hasCompleted) completedGoalsPopup();
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Failed to load goals", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // moves a goal to completed table in database
    public void completedGoal(String docId, Map<String, Object> goal) {
        db.collection("completedGoals").add(goal)
                .addOnSuccessListener(success -> {
                    db.collection("savings").document(docId).delete();
                });
    }

    // shows all specific users completed goals
    public void completedGoalsPopup() {
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_completed_goals, null);
        LinearLayout completedList = popupView.findViewById(R.id.completedGoalsList);

        db.collection("completedGoals")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(docs -> {
                    for (QueryDocumentSnapshot doc : docs) {
                        String title = (String) doc.get("title");
                        String amount = (String) doc.get("goalAmount");

                        View card = LayoutInflater.from(requireContext()).inflate(R.layout.item_completed_goal_card, completedList, false);
                        TextView goalTitle = card.findViewById(R.id.completedGoalTitle);
                        TextView goalAmount = card.findViewById(R.id.completedGoalAmount);

                        goalTitle.setText(title);
                        goalAmount.setText("$" + amount);

                        completedList.addView(card);
                    }

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Completed Goals")
                            .setView(popupView)
                            .setPositiveButton("OK", null)
                            .show();
                });
    }

    // adds new goals to the list view
    public void addGoal(Map<String, Object> goal) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View goalItem = inflater.inflate(R.layout.goal_item_with_edit, null);

        TextView savedText = goalItem.findViewById(R.id.savedText);
        TextView dateText = goalItem.findViewById(R.id.dateText);
        TextView remainingText = goalItem.findViewById(R.id.remainingText);
        TextView moreInfoText = goalItem.findViewById(R.id.moreInfoText);
        View editButton = goalItem.findViewById(R.id.editIcon);

        double goalAmount = Double.parseDouble((String) goal.get("goalAmount"));
        double currentAmount = Double.parseDouble((String) goal.get("currentAmount"));
        String start = (String) goal.get("startDate");
        String end = (String) goal.get("endDate");

        double amountLeft = goalAmount - currentAmount;
        long daysLeft = getDaysBetween(start, end);
        double perDay = daysLeft > 0 ? amountLeft / daysLeft : 0;
        double perWeek = perDay * 7;
        double perMonth = perDay * 30;

        savedText.setText("Saved: $" + String.format("%.2f", currentAmount) + " / $" + String.format("%.2f", goalAmount));
        dateText.setText("From " + start + " to " + end);
        remainingText.setText("Remaining: $" + String.format("%.2f", amountLeft));

        String extendedText = "Need To Save:\n"
                + "$" + String.format("%.2f", perDay) + " / day\n"
                + "$" + String.format("%.2f", perWeek) + " / week\n"
                + "$" + String.format("%.2f", perMonth) + " / month";
        moreInfoText.setText(extendedText);
        moreInfoText.setVisibility(View.GONE);

        // double click for more details
        goalItem.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if (now - lastClickTime < 300) {
                    moreInfoText.setVisibility(moreInfoText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                }
                lastClickTime = now;
            }
        });

        editButton.setOnClickListener(v ->
                editGoal(goal.get("docId").toString(), currentAmount));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);
        goalItem.setLayoutParams(params);
        goalList.addView(goalItem);
    }

    // pen button to edit goals
    public void editGoal(String docId, double currentAmount) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(currentAmount));

        new AlertDialog.Builder(requireContext())
                .setTitle("Update Current Amount")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newAmount = input.getText().toString();
                    db.collection("savings").document(docId)
                            .update("currentAmount", newAmount)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                loadGoals();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // calculates days in between start and end date
    public long getDaysBetween(String startDateStr, String endDateStr) {
        try {
            Date start = dateFormat.parse(startDateStr);
            Date end = dateFormat.parse(endDateStr);
            return (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            return 0;
        }
    }

    // Handles + button to add new goals
    public void addGoalDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_goal, null);
        EditText title = dialogView.findViewById(R.id.goalTitle);
        EditText goalAmount = dialogView.findViewById(R.id.goalAmount);
        EditText currentAmount = dialogView.findViewById(R.id.currentAmount);
        EditText startDate = dialogView.findViewById(R.id.startDate);
        EditText endDate = dialogView.findViewById(R.id.endDate);

        startDate.setOnClickListener(v -> datePickerDialog(startDate));
        endDate.setOnClickListener(v -> datePickerDialog(endDate));

        new AlertDialog.Builder(requireContext())
                .setTitle("New Saving Goal")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("title", title.getText().toString());
                    data.put("goalAmount", goalAmount.getText().toString());
                    data.put("currentAmount", currentAmount.getText().toString());
                    data.put("startDate", startDate.getText().toString());
                    data.put("endDate", endDate.getText().toString());
                    data.put("userId", userId);

                    db.collection("savings").add(data)
                            .addOnSuccessListener(doc -> {
                                Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show();
                                loadGoals();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Date picker for start and end dates
    public void datePickerDialog(EditText targetField) {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    String formatted = (month + 1) + "/" + day + "/" + year;
                    targetField.setText(formatted);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}

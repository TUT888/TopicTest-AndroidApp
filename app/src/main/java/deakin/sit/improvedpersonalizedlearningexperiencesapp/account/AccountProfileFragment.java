package deakin.sit.improvedpersonalizedlearningexperiencesapp.account;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.MainActivity;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTask;

public class AccountProfileFragment extends Fragment {
    TextView userNameText, userEmailText, totalQuestionText, totalCorrectAnsweredText, totalIncorrectAnsweredText, aiSummaryText;
    Button backButton, shareButton;

    List<StudentTask> finishedTasks;
    int totalQuestions = 0;
    int totalCorrectQuestion = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_profile, container, false);

        // Get data
        Student currentStudent = ((AccountViewActivity) getActivity()).currentStudent;
        String aiSummaryString = ((AccountViewActivity) getActivity()).aiSummaryString;

        // Setup views
        userNameText = view.findViewById(R.id.userNameText);
        userEmailText = view.findViewById(R.id.userEmailText);
        totalQuestionText = view.findViewById(R.id.totalQuestionText);
        totalCorrectAnsweredText = view.findViewById(R.id.totalCorrectAnsweredText);
        totalIncorrectAnsweredText = view.findViewById(R.id.totalIncorrectAnsweredText);
        aiSummaryText = view.findViewById(R.id.aiSummaryText);

        backButton = view.findViewById(R.id.backButton);
        shareButton = view.findViewById(R.id.shareButton);

        // Config views
        finishedTasks = ((AccountViewActivity) getActivity()).finishedTasks;
        userNameText.setText(currentStudent.getName());
        userEmailText.setText(currentStudent.getEmail());
        aiSummaryText.setText(aiSummaryString);
        displayTaskStatus();

        // Config button
        backButton.setOnClickListener(view1 -> {
            ((AccountViewActivity) getActivity()).getSupportFragmentManager().popBackStack();
        });
        shareButton.setOnClickListener(view1 -> {
            ClipboardManager clipboardManager = (ClipboardManager) ((AccountViewActivity) getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(
                    "User profile",
                    MainActivity.BACKEND_URL + "share_profile?name=" + currentStudent.getUsername() + "&completed=" + totalQuestions + "&correct=" + totalCorrectQuestion
            );
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(getContext(), "Shared URL copied to clipboard", Toast.LENGTH_SHORT);
        });
        return view;
    }

    public void displayTaskStatus() {
        for (StudentTask task : finishedTasks) {
            totalQuestions += task.getStudentTaskQuestions().size();
            totalCorrectQuestion += task.getScore();
        }
        int totalIncorrectQuestion = totalQuestions - totalCorrectQuestion;

        totalQuestionText.setText(String.valueOf(totalQuestions));
        totalCorrectAnsweredText.setText(String.valueOf(totalCorrectQuestion));
        totalIncorrectAnsweredText.setText(String.valueOf(totalIncorrectQuestion));
    }
}
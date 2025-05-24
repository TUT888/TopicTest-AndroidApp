package deakin.sit.improvedpersonalizedlearningexperiencesapp.account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTask;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTaskQuestion;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.task.QuestionAdapter;

public class AccountTaskHistoryFragment extends Fragment {
    StudentTask completedTask;
    List<StudentTaskQuestion> completedQuestionList;

    Button backButton;
    RecyclerView answerRecyclerView;
    QuestionAdapter questionAdapter;

    TextView scoreTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_task_history, container, false);

        // Get data
        completedTask = (StudentTask) getArguments().getSerializable("StudentTask");
        completedQuestionList = completedTask.getStudentTaskQuestions();

        // Setup views
        answerRecyclerView = view.findViewById(R.id.answerRecyclerView);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        scoreTextView.setText(completedTask.getScore() + "/" + completedQuestionList.size());

        backButton = view.findViewById(R.id.backButton);

        // Config button
        backButton.setOnClickListener(view1 -> {
            ((AccountViewActivity) getActivity()).getSupportFragmentManager().popBackStack();
        });

        // Config recycler view
        questionAdapter = new QuestionAdapter(completedQuestionList, this);
        answerRecyclerView.setAdapter(questionAdapter);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
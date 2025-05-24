package deakin.sit.improvedpersonalizedlearningexperiencesapp.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTask;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTaskQuestion;

public class TaskResultFragment extends Fragment {
    StudentTask completedTask;
    List<StudentTaskQuestion> completedQuestionList;

    Button doneButton;
    RecyclerView answerRecyclerView;
    QuestionAdapter questionAdapter;

    TextView scoreTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_result, container, false);

        // Get data
        completedTask = ((TaskActivity) getActivity()).currentTask;
        completedQuestionList = completedTask.getStudentTaskQuestions();

        // Setup views
        answerRecyclerView = view.findViewById(R.id.answerRecyclerView);
        doneButton = view.findViewById(R.id.doneButton);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        scoreTextView.setText(completedTask.getScore() + "/" + completedQuestionList.size());

        // Config button
        doneButton.setOnClickListener(this::handleDoneButton);

        // Config recycler view
        questionAdapter = new QuestionAdapter(completedQuestionList, this);
        answerRecyclerView.setAdapter(questionAdapter);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void handleDoneButton(View view) {
        ((TaskActivity) getActivity()).finishActivity();
    }
}
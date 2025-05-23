package deakin.sit.improvedpersonalizedlearningexperiencesapp.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentInterest;

public class SignupPersonalInterestFragment extends Fragment {
    RecyclerView interestRecyclerView;
    InterestAdapter interestAdapter;
    TextView totalSelectedTextView;
    Button clearButton, createAccountButton;

    List<StudentInterest> availableStudentInterests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_personal_interest, container, false);

        // Setup views
        totalSelectedTextView = view.findViewById(R.id.totalSelectedTextView);
        interestRecyclerView = view.findViewById(R.id.interestRecyclerView);
        clearButton = view.findViewById(R.id.clearButton);
        createAccountButton = view.findViewById(R.id.createAccountButton);

        // Initialize recycler views with interests
        availableStudentInterests = initInterest();
        interestAdapter = new InterestAdapter(getContext(), this, availableStudentInterests);
        interestRecyclerView.setAdapter(interestAdapter);
        interestRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

        // Config buttons
        clearButton.setOnClickListener(this::handleClearButton);
        createAccountButton.setOnClickListener(this::handleCreateAccountButton);

        return view;
    }

    public void handleClearButton(View view) {
        interestAdapter.clearSelection(availableStudentInterests);
    }

    public void handleCreateAccountButton(View view) {
        List<StudentInterest> selectedInterests = interestAdapter.getSelectedInterests();
        ((SignupActivity) getActivity()).registerNewStudent(selectedInterests);
    }

    public void updateSelectionProgress(int totalSelected) {
        totalSelectedTextView.setText(String.format("Selected %d/10", totalSelected));
    }

    public void showWarningMessage() {
        Toast.makeText(getContext(), "Can not add more interest (selected 10/10 topics)", Toast.LENGTH_SHORT).show();
    }

    public List<StudentInterest> initInterest() {
        List<StudentInterest> STUDENT_INTEREST_LIST = new ArrayList<StudentInterest>();
        STUDENT_INTEREST_LIST.add(new StudentInterest("Algorithms"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Data Structures"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Web Development"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Testing"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Object-Oriented Programming"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Functional Programming"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("System Design"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Design Patterns"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Relational Database & SQL"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Non-relational Database & Big Data"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Cloud Computing"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("DevOps & CI/CD"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("API Development"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Frontend Development"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Backend Development"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Security & Authentication"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Performance Optimization"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Mobile App Development"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Data Science Fundamentals"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Machine Learning Basics"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Software Architecture"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Version Control"));
        STUDENT_INTEREST_LIST.add(new StudentInterest("Coding Interview Prep"));
        return STUDENT_INTEREST_LIST;
    }
}
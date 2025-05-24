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

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.home.StudentTaskAdapter;

public class AccountMenuFragment extends Fragment {
    TextView studentNameTextView;
    Button backButton, profileButton, upgradeButton;

    RecyclerView taskListRecyclerView;

    Student currentStudent;
    StudentTaskAdapter finishedTaskAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_menu, container, false);

        // Get data
        currentStudent = ((AccountViewActivity) getActivity()).currentStudent;
        finishedTaskAdapter = ((AccountViewActivity) getActivity()).finishedTaskAdapter;

        // Setup views
        studentNameTextView = view.findViewById(R.id.studentNameTextView);
        backButton = view.findViewById(R.id.backButton);
        profileButton = view.findViewById(R.id.profileButton);
        upgradeButton = view.findViewById(R.id.upgradeButton);

        taskListRecyclerView = view.findViewById(R.id.taskListRecyclerView);

        // Config view
        String studentName = currentStudent.getName();
        studentNameTextView.setText(studentName);

        // Config button
        backButton.setOnClickListener(view1 -> {
            ((AccountViewActivity) getActivity()).finish();
        });
        profileButton.setOnClickListener(view1 -> {
            ((AccountViewActivity) getActivity()).replaceProfileFragment();
        });
        upgradeButton.setOnClickListener(view1 -> {
            ((AccountViewActivity) getActivity()).startCheckoutActivity();
        });

        // Config recycler views
        taskListRecyclerView.setAdapter(finishedTaskAdapter);
        taskListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
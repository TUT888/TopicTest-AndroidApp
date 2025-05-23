package deakin.sit.improvedpersonalizedlearningexperiencesapp.signup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentInterest;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {
    Context context;
    SignupPersonalInterestFragment fragment;

    List<StudentInterest> availableStudentInterests;
//    List<Interest> selectedInterests;
//    boolean allowSelection;
    int countSelection = 0;
    final int maxSelection = 10;

    public InterestAdapter(Context context, SignupPersonalInterestFragment fragment, List<StudentInterest> availableStudentInterests) {
        this.context = context;
        this.fragment = fragment;
        this.availableStudentInterests = availableStudentInterests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentInterest studentInterest = availableStudentInterests.get(position);
        AppCompatToggleButton toggleButton = holder.interestToggleButton;

        // Toggle button
        toggleButton.setText(studentInterest.getName());
        toggleButton.setTextOff(studentInterest.getName());
        toggleButton.setTextOn(studentInterest.getName());

        toggleButton.setBackgroundTintList(context.getColorStateList(R.color.interest_selector));

        // On check change
        toggleButton.setOnCheckedChangeListener(((compoundButton, isChecked) -> {
            if (isChecked && countSelection >= maxSelection) {
                compoundButton.setChecked(false);
                fragment.showWarningMessage();
                return;
            }
            if (isChecked) {
                studentInterest.setSelected(true);
                countSelection += 1;
            } else {
                studentInterest.setSelected(false);
                countSelection -= 1;
            }
        }));

        fragment.updateSelectionProgress(countSelection);
    }

    public List<StudentInterest> getSelectedInterests() {
        List<StudentInterest> selectedInterestList = new ArrayList<StudentInterest>();
        for (StudentInterest studentInterest : availableStudentInterests) {
            if (studentInterest.isSelected()) {
                selectedInterestList.add(studentInterest);
            }
        }
        return selectedInterestList;
    }

    @Override
    public int getItemCount() {
        return availableStudentInterests.size();
    }

    public void clearSelection(List<StudentInterest> availableStudentInterests) {
        for (StudentInterest studentInterest : availableStudentInterests) {
            studentInterest.setSelected(false);
        }
        countSelection = 0;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatToggleButton interestToggleButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            interestToggleButton = itemView.findViewById(R.id.interestToggleButton);
        }
    }
}

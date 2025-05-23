package deakin.sit.improvedpersonalizedlearningexperiencesapp.task;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.account.AccountTaskHistoryFragment;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTaskQuestion;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    List<StudentTaskQuestion> currentQuestionList;
    Fragment fragment;

    public QuestionAdapter(List<StudentTaskQuestion> currentQuestionList, TaskQuestionFragment fragment) {
        this.currentQuestionList = currentQuestionList;
        this.fragment = fragment;
    }

    public QuestionAdapter(List<StudentTaskQuestion> currentQuestionList, TaskResultFragment fragment) {
        this.currentQuestionList = currentQuestionList;
        this.fragment = fragment;
    }

    public QuestionAdapter(List<StudentTaskQuestion> currentQuestionList, AccountTaskHistoryFragment fragment) {
        this.currentQuestionList = currentQuestionList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentTaskQuestion question = currentQuestionList.get(position);

        holder.questionTitleTextView.setText(question.getTitle());
        holder.questionDescriptionTextView.setText(question.getDescription());
        holder.questionTitleTextView.setText(question.getTitle());
        // Mapping: choices[i] <--> radioButtonIDs[i]
        String[] choices = question.getChoices();
        Log.d("QUESTION-ADAPTER:choices", Arrays.toString(choices));
        Log.d("QUESTION-ADAPTER:radios", Arrays.toString(holder.radioButtonIDs));
        for (int i=0; i<holder.radioButtonIDs.length; i++) {
            int radioID = holder.radioButtonIDs[i];
            String choice = choices[i];
            RadioButton radio = holder.itemView.findViewById(radioID);
            radio.setText(choice);
            if ((fragment.getClass() == TaskResultFragment.class) || (fragment.getClass() == AccountTaskHistoryFragment.class)) {
                int selected = question.getSelectedAnswer();
                int correct = question.getCorrectAnswer();
                int textColor = selected==i ? (correct==i ? Color.GREEN : Color.RED) : (correct==i ? Color.GREEN : Color.BLACK);
                radio.setTextColor(textColor);
                radio.setChecked(selected==i);
                radio.setEnabled(false);
            }
        }

        if (fragment.getClass() == TaskQuestionFragment.class) {
            holder.optionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    for (int i=0; i<holder.radioButtonIDs.length; i++) {
                        int radioID = holder.radioButtonIDs[i];
                        if (checkedId == radioID) {
                            question.setSelectedAnswer(i);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return currentQuestionList.size();
    }

    public List<StudentTaskQuestion> getResult() {
        return currentQuestionList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTitleTextView, questionDescriptionTextView;
        RadioGroup optionRadioGroup;
        int[] radioButtonIDs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            questionTitleTextView = itemView.findViewById(R.id.questionTitleTextView);
            questionDescriptionTextView = itemView.findViewById(R.id.questionDescriptionTextView);

            optionRadioGroup = itemView.findViewById(R.id.optionRadioGroup);
            radioButtonIDs = new int[]{R.id.choice1, R.id.choice2, R.id.choice3};
        }
    }
}

package deakin.sit.improvedpersonalizedlearningexperiencesapp.task;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTask;

public class TaskActivity extends AppCompatActivity {
    FragmentContainerView fragmentContainerView;
    FragmentManager fragmentManager;
    TaskQuestionFragment taskQuestionFragment;
    TaskResultFragment taskResultFragment;

    StudentTask currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data
        Intent intent = getIntent();
        currentTask = (StudentTask) intent.getSerializableExtra("StudentTask");

        // Setup views
        fragmentContainerView = findViewById(R.id.fragmentContainerView);

        // Config fragments
        taskQuestionFragment = new TaskQuestionFragment();
        taskResultFragment = new TaskResultFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, taskQuestionFragment).commit();
    }

    public void finishTask(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, taskResultFragment)
                .commit();
    }

    public void finishActivity() {
        Intent responseIntent = new Intent();
        responseIntent.putExtra("Message", "Task completed");
        setResult(RESULT_OK, responseIntent);
        finish();
    }
}
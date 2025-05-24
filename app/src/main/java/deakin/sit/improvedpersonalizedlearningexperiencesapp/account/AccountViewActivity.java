package deakin.sit.improvedpersonalizedlearningexperiencesapp.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.MainActivity;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTask;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTaskQuestion;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.home.StudentTaskAdapter;

public class AccountViewActivity extends AppCompatActivity {
    private static final String TAG = "INFO:AccountViewActivity";
    FragmentContainerView fragmentContainerView;
    FragmentManager fragmentManager;
    AccountMenuFragment accountMenuFragment;
    AccountProfileFragment accountProfileFragment;
    AccountTaskHistoryFragment accountTaskHistoryFragment;

    Student currentStudent;
    List<StudentTask> finishedTasks;
    StudentTaskAdapter finishedTaskAdapter;
    String aiSummaryString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data
        Intent intent = getIntent();
        currentStudent = (Student) intent.getSerializableExtra("Student");
        finishedTasks = new ArrayList<StudentTask>();
        finishedTaskAdapter = new StudentTaskAdapter(finishedTasks, this);
        fetchFinishedTask();

        // Setup views
        fragmentContainerView = findViewById(R.id.fragmentContainerView);

        // Config fragments
        accountMenuFragment = new AccountMenuFragment();
        accountProfileFragment = new AccountProfileFragment();
        accountTaskHistoryFragment = new AccountTaskHistoryFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, accountMenuFragment).commit();
    }

    public void replaceProfileFragment() {
        // Fetch profile summary and begin transaction when it is successful
        fetchAIProfileSummary();
    }

    public void startCheckoutActivity() {
        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivity(intent);
    }

    public void viewTask(StudentTask task) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("StudentTask", task);

        accountTaskHistoryFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, accountTaskHistoryFragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    // Backend database interaction
    public void fetchFinishedTask() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = MainActivity.BACKEND_URL + "tasks?student_id=" + currentStudent.getId() + "&finish=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, "Response: " + response.toString());

                            JSONArray tasksArray = response.getJSONArray("tasks");

                            List<StudentTask> newStudentTaskList = new ArrayList<StudentTask>();
                            for (int i = 0; i < tasksArray.length(); i++) {
                                JSONObject taskJSON = tasksArray.getJSONObject(i);

                                JSONArray taskQuestionArray = taskJSON.getJSONArray("listQuestions");
                                List<StudentTaskQuestion> taskQuestions = new ArrayList<StudentTaskQuestion>();
                                for (int k = 0; k < taskQuestionArray.length(); k++) {
                                    JSONObject jsonQuestion = taskQuestionArray.getJSONObject(k);
                                    taskQuestions.add(new StudentTaskQuestion(
                                            jsonQuestion.getString("title"),
                                            jsonQuestion.getString("description"),
                                            jsonQuestion.getJSONArray("choices").join("\t").split("\t"),
                                            jsonQuestion.getInt("correctAnswer"),
                                            jsonQuestion.getInt("selectedAnswer")
                                    ));

                                }
                                StudentTask task = new StudentTask(
                                        taskJSON.getString("_id"),
                                        taskJSON.getString("student_id"),
                                        taskJSON.getString("title"),
                                        taskJSON.getString("description"),
                                        taskJSON.getBoolean("finish"),
                                        taskJSON.getInt("score"),
                                        taskQuestions
                                );

                                newStudentTaskList.add(task);
                            }
                            finishedTasks = newStudentTaskList;
                            finishedTaskAdapter.updateTaskList(finishedTasks);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                            Toast.makeText(AccountViewActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.networkResponse != null ? "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data) : error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e(TAG, "Error saving note: " + errorMsg, error);
                        Toast.makeText(AccountViewActivity.this, "Error saving note: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void fetchAIProfileSummary() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = MainActivity.BACKEND_URL + "students/review?student_id=" + currentStudent.getId();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, "Response: " + response.toString());

                            aiSummaryString = response.getString("review");
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView, accountProfileFragment)
                                    .setReorderingAllowed(true)
                                    .addToBackStack(null)
                                    .commit();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                            Toast.makeText(AccountViewActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.networkResponse != null ? "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data) : error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e(TAG, "Error saving note: " + errorMsg, error);
                        Toast.makeText(AccountViewActivity.this, "Error saving note: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
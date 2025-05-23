package deakin.sit.improvedpersonalizedlearningexperiencesapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.account.AccountViewActivity;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.AppDatabase;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentInterest;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentInterestDao;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTask;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTaskDao;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTaskQuestion;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTaskQuestionDao;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.task.TaskActivity;

public class HomeActivity extends AppCompatActivity {
    static final String IP_ADDRESS = "http://192.168.4.28:5000/";
    static final String TAG = "HomeActivity";

    StudentTaskDao studentTaskDao;
    StudentTaskQuestionDao studentTaskQuestionDao;
    StudentInterestDao studentInterestDao;

    Student currentStudent;
    List<StudentTask> availableTasks;

    TextView studentNameTextView, notificationTextView;
    RecyclerView taskListRecyclerView;
    LinearLayout loadingContainer;
    Button generateButton, viewAccountButton;

    StudentTaskAdapter studentTaskAdapter;

    ActivityResultLauncher<Intent> taskActivityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Database
        studentTaskDao = AppDatabase.getInstance(this).studentTaskDao();
        studentTaskQuestionDao = AppDatabase.getInstance(this).studentTaskQuestionDao();
        studentInterestDao = AppDatabase.getInstance(this).studentInterestDao();

        // Get data
        Intent intent = getIntent();
        currentStudent = (Student) intent.getSerializableExtra("Student");
        availableTasks = studentTaskDao.getAllByStudentID(currentStudent.getId(), false);

        // Setup views
        studentNameTextView = findViewById(R.id.studentNameTextView);
        notificationTextView = findViewById(R.id.notificationTextView);
        taskListRecyclerView = findViewById(R.id.taskListRecyclerView);
        loadingContainer = findViewById(R.id.loadingContainer);
        generateButton = findViewById(R.id.generateButton);
        viewAccountButton = findViewById(R.id.viewAccountButton);

        // Config button
        generateButton.setOnClickListener(this::generateTaskWithAI);
        viewAccountButton.setOnClickListener(this::startViewAccountActivity);

        // Config views
        studentNameTextView.setText(currentStudent.getName());
        notificationTextView.setText(String.format("You have %d task to do", availableTasks.size()));

        // Config recycler views
        studentTaskAdapter = new StudentTaskAdapter(availableTasks, this);
        taskListRecyclerView.setAdapter(studentTaskAdapter);
        taskListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Config activity result launcher
        taskActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResultFromTaskActivity
        );
    }

    public void startViewAccountActivity(View view) {
        Intent intent = new Intent(this, AccountViewActivity.class);
        intent.putExtra("Student", currentStudent);
        startActivity(intent);
    }

    public void loadTaskAndRefresh() {
        availableTasks = studentTaskDao.getAllByStudentID(currentStudent.getId(), false);
        notificationTextView.setText(String.format("You have %d task to do", availableTasks.size()));
        studentTaskAdapter.updateTaskList(availableTasks);
    }

    public void beginTask(int taskID) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("TaskID", taskID);
        taskActivityResultLauncher.launch(intent);
    }

    public void generateTaskWithAI(View view) {
        int studentID = currentStudent.getId();
        // Get random interested topic, for example: Mobile Application Development
        List<StudentInterest> allTopic = studentInterestDao.getAllStudentInterest(studentID);
        Random rand = new Random();
        String selectedTopic = allTopic.get(rand.nextInt(allTopic.size())).getName();

        // Create new task
        studentTaskDao.insert(new StudentTask(studentID, selectedTopic, "Test your knowledge for " + selectedTopic));

        List<StudentTask> taskList = studentTaskDao.getAllByStudentID(studentID, false);
        int taskID = taskList.get(taskList.size()-1).getId();

        // URL for the Flask server
        String url = IP_ADDRESS + "getQuiz?topic=" + selectedTopic.replaceAll("\\s", "");
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the JSON Object Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Hide loading container when response is received
                    loadingContainer.setVisibility(View.GONE);

                    try {
                        Log.i(TAG, "Response received: " + response.toString());
                        JSONArray quizArray = response.getJSONArray("quiz");

                        // Process each quiz question
                        for (int i = 0; i < quizArray.length(); i++) {
                            JSONObject quizQuestion = quizArray.getJSONObject(i);

                            String question = quizQuestion.getString("question");
                            JSONArray optionsArray = quizQuestion.getJSONArray("options");
                            int correctAnswer = Integer.parseInt(quizQuestion.getString("correct_answer")) - 1;

                            String questionTitle = "Question " + (i+1);
                            String[] choiceList = new String[optionsArray.length()];
                            for(int k = 0; k < optionsArray.length(); k++){
                                choiceList[k] = optionsArray.getString(k);
                            }
                            Log.d("INFO-QUESTION", question);
                            Log.d("INFO-CHOICES", Arrays.toString(choiceList));
                            Log.d("INFO-ANSWER", String.valueOf(correctAnswer));

                            // Add to the list
                            studentTaskQuestionDao.insert(new StudentTaskQuestion(
                                    taskID, questionTitle, question, choiceList, correctAnswer
                            ));
                        }
                        loadTaskAndRefresh();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage(), e);
                        Toast.makeText(HomeActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Hide loading container on error
                    loadingContainer.setVisibility(View.GONE);

                    String errorMsg = "Unknown error";
                    if (error.networkResponse != null) {
                        errorMsg = "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data);
                    } else if (error.getMessage() != null) {
                        errorMsg = error.getMessage();
                    }
                    Log.e(TAG, "Error fetching quiz: " + errorMsg, error);
                    Toast.makeText(HomeActivity.this, "Error fetching quiz: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });

        // Set a custom retry policy with a longer timeout
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10-second timeout (adjust as needed)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 1 retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Default backoff multiplier
        ));

        // Show loading container before sending the request
        loadingContainer.setVisibility(View.VISIBLE);

        // Add the request to the queue
        Log.i(TAG, "Sending request to: " + url);
        queue.add(jsonObjectRequest);
    }

    public void handleResultFromTaskActivity(ActivityResult result) {
        if(result.getResultCode() == RESULT_OK){
            if (result.getData() !=null) {
                loadTaskAndRefresh();
                studentTaskAdapter.notifyDataSetChanged();
                String message = result.getData().getStringExtra("Message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task ended without message", Toast.LENGTH_SHORT).show();
            }
        } else if (result.getResultCode() == RESULT_CANCELED) {
            Toast.makeText(this, "Task cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }
}
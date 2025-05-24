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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.MainActivity;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.account.AccountViewActivity;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTask;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentTaskQuestion;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.task.TaskActivity;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "INFO:HomeActivity";

    Student currentStudent;
    ArrayList<String> currentStudentInterest;
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

        // Get data
        currentStudent = (Student) getIntent().getSerializableExtra("Student");
        currentStudentInterest = (ArrayList<String>) getIntent().getSerializableExtra("StudentInterest");
        fetchAvailableTask();

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
        notificationTextView.setText(String.format("You have 0 task to do"));

        // Config recycler views
        availableTasks = new ArrayList<StudentTask>();
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

    public void loadTaskAndRefresh(List<StudentTask> updatedTaskList) {
        availableTasks = updatedTaskList;
        notificationTextView.setText(String.format("You have %d task to do", availableTasks.size()));
        studentTaskAdapter.updateTaskList(availableTasks);
    }

    public void beginTask(StudentTask task) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("StudentTask", task);
        taskActivityResultLauncher.launch(intent);
    }

    public void generateTaskWithAI(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Get random interested topic, for example: Mobile Application Development
        Random rand = new Random();
        String selectedTopic = currentStudentInterest.get(rand.nextInt(currentStudentInterest.size()));

        // Create new task
        String studentID = currentStudent.getId();
        StudentTask newTask = new StudentTask(studentID, selectedTopic, "Test your knowledge for " + selectedTopic);

        // URL for the Flask server
        String url = MainActivity.BACKEND_URL + "getQuiz?topic=" + selectedTopic.replaceAll("\\s", "");

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
                            newTask.addQuestion(new StudentTaskQuestion(
                                    questionTitle, question, choiceList, correctAnswer
                            ));
                        }
                        addNewTaskToServer(newTask);
                        fetchAvailableTask();
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
                fetchAvailableTask();
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

    // Backend database interaction
    private void addNewTaskToServer(StudentTask task) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONArray questionJSONArray = new JSONArray();
        for (StudentTaskQuestion question : task.getStudentTaskQuestions()) {
            JSONObject jsonQuestion = new JSONObject();
            try {
                jsonQuestion.put("title", question.getTitle());
                jsonQuestion.put("description", question.getDescription());
                jsonQuestion.put("choices", new JSONArray(question.getChoices()));
                jsonQuestion.put("correctAnswer", question.getCorrectAnswer());
                jsonQuestion.put("selectedAnswer", question.getSelectedAnswer());
            } catch (Exception e) {
                Log.e(TAG, "Error creating task question JSON: " + e.getMessage(), e);
                return;
            }
            questionJSONArray.put(jsonQuestion);
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("student_id", task.getStudentID());
            jsonBody.put("title", task.getTitle());
            jsonBody.put("description", task.getDescription());
            jsonBody.put("listQuestion", questionJSONArray);
        } catch (Exception e) {
            Log.e(TAG, "Error creating request body JSON: " + e.getMessage(), e);
            return;
        }

        String url = MainActivity.BACKEND_URL + "tasks";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(HomeActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                            Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.networkResponse != null ? "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data) : error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e(TAG, "Error saving note: " + errorMsg, error);
                        Toast.makeText(HomeActivity.this, "Error saving note: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void fetchAvailableTask() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = MainActivity.BACKEND_URL + "tasks?student_id=" + currentStudent.getId() + "&finish=0";
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

                                    JSONArray choicesJSONArray = jsonQuestion.getJSONArray("choices");
                                    String[] choicesArray = new String[choicesJSONArray.length()];
                                    for (int c = 0; c < choicesJSONArray.length(); c++) {
                                        choicesArray[c] = choicesJSONArray.getString(i);
                                    }
                                    taskQuestions.add(new StudentTaskQuestion(
                                        jsonQuestion.getString("title"),
                                        jsonQuestion.getString("description"),
//                                        jsonQuestion.getJSONArray("choices").join("\t").split("\t"),
                                        choicesArray,
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
                            loadTaskAndRefresh(newStudentTaskList);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                            Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.networkResponse != null ? "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data) : error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e(TAG, "Error saving note: " + errorMsg, error);
                        Toast.makeText(HomeActivity.this, "Error saving note: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
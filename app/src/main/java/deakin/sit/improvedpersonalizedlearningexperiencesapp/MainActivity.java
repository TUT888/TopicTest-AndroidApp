package deakin.sit.improvedpersonalizedlearningexperiencesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.home.HomeActivity;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.signup.SignupActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "INFO:MainActivity";
    public static final String BACKEND_URL = "http://192.168.4.28:5000/";
    private RequestQueue queue;
    Student currentStudent;

    EditText inputUsername, inputPassword;
    Button loginButton;
    TextView signUpText;

    ActivityResultLauncher<Intent> signupActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup views
        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);

        // Config buttons
        loginButton.setOnClickListener(this::handleLoginButton);
        signUpText.setOnClickListener(this::handleSignupButton);

        // Config signup launcher
        signupActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResultFromSignupActivity
        );
    }

    public void handleLoginButton(View view) {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }
        getStudentFromServer(username, password);
    }

    public void handleSignupButton(View view) {
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        signupActivityResultLauncher.launch(intent);
    }

    public void handleResultFromSignupActivity(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Signup failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Backend database interaction
    private void getStudentFromServer(String username, String password) {
        queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON: " + e.getMessage(), e);
            return;
        }

        String url = MainActivity.BACKEND_URL + "students/login";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, "Response: " + response.toString());

                            JSONObject studentJSON = response.getJSONObject("student");
                            currentStudent = new Student(
                                    studentJSON.getString("_id"),
                                    studentJSON.getString("name"),
                                    studentJSON.getString("username"),
                                    studentJSON.getString("email"),
                                    studentJSON.getString("password"),
                                    studentJSON.getString("phone")
                            );

                            JSONArray interestArray = studentJSON.getJSONArray("interest");
                            ArrayList<String> studentInterest = new ArrayList<String>();
                            for (int i = 0; i < interestArray.length(); i++) {
                                String interest = interestArray.getString(i);
                                studentInterest.add(interest);
                            };

                            inputUsername.setText("");
                            inputPassword.setText("");

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("Student", currentStudent);
                            intent.putExtra("StudentInterest", studentInterest);
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.networkResponse != null ? "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data) : error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e(TAG, "Error saving note: " + errorMsg, error);
                        Toast.makeText(MainActivity.this, "Error saving note: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
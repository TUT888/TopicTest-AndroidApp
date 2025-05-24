package deakin.sit.improvedpersonalizedlearningexperiencesapp.signup;

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

import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.MainActivity;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentInterest;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "INFO:SignupActivity";
    private RequestQueue queue;

    FragmentContainerView fragmentContainerView;
    FragmentManager fragmentManager;

    SignupPersonalDetailFragment signupPersonalDetailFragment;
    SignupPersonalInterestFragment signupPersonalInterestFragment;

    Student newStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup fragments
        signupPersonalDetailFragment = new SignupPersonalDetailFragment();
        signupPersonalInterestFragment = new SignupPersonalInterestFragment();

        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, signupPersonalDetailFragment).commit();
    }

    public void completeRegisterPersonalDetail(Student student) {
        newStudent = student;
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, signupPersonalInterestFragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    public void registerNewStudent(List<StudentInterest> selectedInterests) {
        // Prepare interest array
        JSONArray interestJSONArray = new JSONArray();
        for (StudentInterest interest : selectedInterests) {
            interestJSONArray.put(interest.getName());
        }

        addNewStudentToServer(interestJSONArray);
    }

    // Backend database interaction
    private void addNewStudentToServer(JSONArray interestJSONArray) {
        queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", newStudent.getName());
            jsonBody.put("username", newStudent.getUsername());
            jsonBody.put("email", newStudent.getEmail());
            jsonBody.put("password", newStudent.getPassword());
            jsonBody.put("phone", newStudent.getPhone());
            jsonBody.put("interest", interestJSONArray);
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON: " + e.getMessage(), e);
            return;
        }

        String url = MainActivity.BACKEND_URL + "students";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(SignupActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                            Toast.makeText(SignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.networkResponse != null ? "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data) : error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e(TAG, "Error saving note: " + errorMsg, error);
                        Toast.makeText(SignupActivity.this, "Error saving note: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
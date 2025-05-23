package deakin.sit.improvedpersonalizedlearningexperiencesapp;

import android.content.Intent;
import android.os.Bundle;
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

import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.AppDatabase;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentDao;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentInterestDao;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.home.HomeActivity;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.signup.SignupActivity;

public class MainActivity extends AppCompatActivity {
    StudentDao studentDao;
    StudentInterestDao studentInterestDao;

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

        // Database
        studentDao = AppDatabase.getInstance(this).studentDao();
        studentInterestDao = AppDatabase.getInstance(this).studentInterestDao();

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

        Student existingStudent = studentDao.login(username, password);
        if (existingStudent == null) {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return;
        }

        inputUsername.setText("");
        inputPassword.setText("");

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("Student", existingStudent);
        startActivity(intent);
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
}
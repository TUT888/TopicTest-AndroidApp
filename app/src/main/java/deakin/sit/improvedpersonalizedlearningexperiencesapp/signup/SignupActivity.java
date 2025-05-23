package deakin.sit.improvedpersonalizedlearningexperiencesapp.signup;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.AppDatabase;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentDao;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentInterest;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.StudentInterestDao;

public class SignupActivity extends AppCompatActivity {
    StudentDao studentDao;
    StudentInterestDao studentInterestDao;

    FragmentContainerView fragmentContainerView;
    FragmentManager fragmentManager;

    SignupPersonalDetailFragment signupPersonalDetailFragment;
    SignupPersonalInterestFragment signupPersonalInterestFragment;

    Student newStudent;
    StudentInterest newStudentInterest;

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

        // Database
        studentDao = AppDatabase.getInstance(this).studentDao();
        studentInterestDao = AppDatabase.getInstance(this).studentInterestDao();

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
        // Add new student
        studentDao.register(newStudent);

        // Add interests
        int studentID = studentDao.getStudentByUsername(newStudent.getUsername()).getId();
        Log.d("NEW_STUDENT_ID", String.valueOf(studentID));
        for (StudentInterest interest : selectedInterests) {
            studentInterestDao.insert(new StudentInterest(studentID, interest.getName()));
        }

        setResult(RESULT_OK);
        finish();
    }
}
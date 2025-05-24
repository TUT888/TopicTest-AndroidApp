package deakin.sit.improvedpersonalizedlearningexperiencesapp.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import deakin.sit.improvedpersonalizedlearningexperiencesapp.R;
import deakin.sit.improvedpersonalizedlearningexperiencesapp.database.Student;

public class SignupPersonalDetailFragment extends Fragment {
    EditText inputName, inputUsername, inputEmail, inputConfirmEmail, inputPassword, inputConfirmPassword, inputPhoneNumber;
    Button nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_personal_detail, container, false);

        // Setup views
        inputName = view.findViewById(R.id.inputName);
        inputUsername = view.findViewById(R.id.inputUsername);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputConfirmEmail = view.findViewById(R.id.inputConfirmEmail);
        inputPassword = view.findViewById(R.id.inputPassword);
        inputConfirmPassword = view.findViewById(R.id.inputConfirmPassword);
        inputPhoneNumber = view.findViewById(R.id.inputPhoneNumber);

        nextButton = view.findViewById(R.id.nextButton);

        // Config button
        nextButton.setOnClickListener(this::handleNextButton);

        return view;
    }

    public void handleNextButton(View view) {
        String name = inputName.getText().toString();
        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString();
        String confirmEmail = inputConfirmEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();
        String phone = inputPhoneNumber.getText().toString();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || confirmEmail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!confirmEmail.equals(email)) {
            Toast.makeText(getContext(), "Email does not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirmPassword.equals(password)) {
            Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ((SignupActivity) getActivity()).completeRegisterPersonalDetail(new Student(name, username, email, password, phone));
    }
}
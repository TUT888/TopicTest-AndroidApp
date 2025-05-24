
# Mobile Application - Improved Personalized Learning Experiences App
Android Studio project source code for Task 10.1D, extended from [Task 6.1D](https://github.com/alice-tat/Task6_1D_PersonalizedLearningExperiencesApp)

## How to run
1. Clone and run the backend API at [alice-tat/Task10_1D_BackendAPI](https://github.com/alice-tat/Task10_1D_BackendAPI)
2. Clone this repo
3. Update the IP address
    - `MainActivity.java`: change the value of `BACKEND_URL` variable (**app/src/main/java/deakin/sit/improvedpersonalizedlearningexperiencesapp/MainActivity.java**)
    - `network_security_config.xml`: change the IP value inside the `domain` element (**app/src/main/res/xml/network_security_config.xml**)
4. Run the app

## Changes in this project
After cloning previous project (**Task 6.1D**), important changes were made to existing files and new files were added to the project. 

### Changes include
#### Added 3 new screens: History, Sharing and Purchasing
- History: please refer to
  - `AccountMenuFragment.java` to display completed task
  - `AccountTaskHistoryFragment.java` to display questions of selected completed task
- Sharing: please refer to `AccountProfileFragment.java`
- Purchasing: please refer to
  - `Constants.java`
  - `CheckoutActivity.java`
  - `viewmodel/CheckoutViewModel.java`
  - `util/PaymentsUtil.java`

#### Replaced ROOM database with MongoDB
The application now will fetch the data from backend server (with MongoDB) instead of storing locally (with ROOM database).
- ROOM database files (AppDatabase, DAOs, etc) were removed.
- Updated code of several files to send request to server instead of interact with ROOM database through DAOs.
  - `MainActivity.java`:
    - Login with `getStudentFromServer()`
  - `home/HomeActivity.java`:
    - Add newly generated task with `addNewTaskToServer()`
    - Get available tasks (incompleted) with `fetchAvailableTask()`
  - `signup/SignupActivity.java`:
    - Add new student with `addNewStudentToServer()`
  - `task/TaskQuestionFragment.java`:
    - Update task status after completing the quizzes with `saveTaskDetail()`
  - `account/AccountViewActivity.java`:
    - Get fininished tasks (completed) with `fetchFinishedTask()`

#### AI-related functionalities
The application extended the AI-generated quizzes with AI profile summary feature from backedn server.
- `home/HomeActivity.java`: Get AI generated quizzes with `generateTaskWithAI()`
- `account/AccountViewActivity.java`: Get profile summarised by AI with `fetchAIProfileSummary()`

For more information about the backend side, please refer to Backend API at [alice-tat/Task10_1D_BackendAPI](https://github.com/alice-tat/Task10_1D_BackendAPI)

### Java source code
```bash
├── account                                   # Newly added for 3 new screens
│   ├── util
│   │   └── PaymentsUtil.java
│   ├── viewmodel
│   │   └── CheckoutViewModel.java
│   └── AccountMenuFragment.java
│   └── AccountProfileFragment.java
│   └── AccountTaskHistoryFragment.java
│   └── AccountViewActivity.java
│   └── CheckoutActivity.java
│   └── Constants.java
├── database                                  # Updated (removed AppDatabase and DAOs)
│   └── Student.java
│   └── StudentInterest.java
│   └── StudentTask.java
│   └── StudentTaskQuestion.java
├── home                                      # Updated (replaced ROOM with backend API)
│   ├── HomeActivity.java
│   └── StudentTaskAdapter.java
├── signup                                    # Updated (replaced ROOM with backend API)
│   ├── InterestAdapter.java
│   ├── SignupActivity.java
│   ├── SignupPersonalDetailFragment.java
│   └── SignupPersonalInterestFragment.java
├── task                                      # Updated (replaced ROOM with backend API)
│   ├── QuestionAdapter.java
│   ├── TaskActivity.java
│   ├── TaskQuestionFragment.java
│   └── TaskResultFragment.java
└── MainActivity.java
```

### Resources
```bash
├── color
│   └── interest_selector.xml
├── drawable
│   ├── loading_background.xml
│   ├── progress_background.xml
│   ├── notification_background.
│   ├── task_block_background.xml
│   └── user.png
├── layout
│   ├── activity_account_view.xml              # Newly added for 3 new screens 
│   ├── activity_checkout.xml                  # Newly added for 3 new screens 
│   ├── activity_home.xml
│   ├── activity_main.xml
│   ├── activity_signup.xml
│   ├── activity_task.xml
│   ├── fragment_account_menu.xml              # Newly added for 3 new screens 
│   ├── fragment_account_profile.xml           # Newly added for 3 new screens 
│   ├── fragment_account_task_history.xml      # Newly added for 3 new screens 
│   ├── fragment_signup_personal_detail.xml
│   ├── fragment_signup_personal_interest.xml
│   ├── fragment_task_question.xml
│   ├── fragment_task_result.xml
│   ├── interest_row.xml
│   ├── question_row.xml
│   └── task_row.xml
├── values
│   ├── colors.xml
│   └── ...
└── values
    ├── network_security_config.xml
    └── ...
```

## Reference
Model and inference used in project
- LLM model: [google/gemma-2-2b-it](https://huggingface.co/google/gemma-2-2b-it)
- Nebius provider: [Nebius AI Studio Documentation](https://docs.nebius.com/studio/api/examples)
- Color reference [Scheme Color - Shiny Light Blue Color Scheme](https://www.schemecolor.com/shiny-light-blue.php)

Other:
- About inference provider: [Inference Provider](https://huggingface.co/docs/inference-providers/en/index)
- Search model by inference provider: [Hugging Face - Inference Providers](https://huggingface.co/models?other=conversational&sort=likes)

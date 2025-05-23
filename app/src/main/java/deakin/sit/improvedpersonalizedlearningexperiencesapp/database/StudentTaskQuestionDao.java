package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentTaskQuestionDao {

    @Query("SELECT * FROM studenttaskquestion")
    List<StudentTaskQuestion> getAll();

    @Query("SELECT * FROM studenttaskquestion WHERE taskID = :taskID")
    List<StudentTaskQuestion> getAllByTaskID(int taskID);

    @Update
    void update(StudentTaskQuestion studentTaskQuestion);

    @Insert
    void insert(StudentTaskQuestion studentTaskQuestion);
}

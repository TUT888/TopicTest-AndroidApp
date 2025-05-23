package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentTaskDao {
    @Query("SELECT * FROM studenttask")
    List<StudentTask> getAll();

//    @Query("SELECT * FROM studenttask WHERE studentID = :studentID")
//    List<StudentTask> getAllByStudentID(int studentID);

    @Query("SELECT * FROM studenttask WHERE studentID = :studentID AND finish = :finish")
    List<StudentTask> getAllByStudentID(int studentID, boolean finish);

    @Query("SELECT * FROM studenttask WHERE id = :id")
    StudentTask getByTaskID(int id);

    @Update
    void update(StudentTask studentTask);

    @Insert
    void insert(StudentTask studentTask);
}

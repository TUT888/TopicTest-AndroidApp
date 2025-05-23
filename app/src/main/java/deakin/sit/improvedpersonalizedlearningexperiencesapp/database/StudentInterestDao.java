package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentInterestDao {
    @Query("SELECT * FROM studentinterest")
    List<StudentInterest> getAll();

    @Query("SELECT * FROM studentinterest WHERE studentID = :studentID")
    List<StudentInterest> getAllStudentInterest(int studentID);

    @Insert
    void insert(StudentInterest studentInterest);

    @Update
    void update(StudentInterest studentInterest);
}

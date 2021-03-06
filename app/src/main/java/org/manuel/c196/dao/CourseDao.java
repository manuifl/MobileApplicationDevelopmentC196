package org.manuel.c196.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.manuel.c196.entities.CourseEntity;
import org.manuel.c196.generics.GenericDao;

import java.util.List;

@Dao
public interface CourseDao extends GenericDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CourseEntity courseEntity);

    @Update
    void update(CourseEntity courseEntity);

    @Delete
    void delete(CourseEntity courseEntity);

    @Query("DELETE FROM courses")
    void deleteAllCourses();

    @Query("SELECT * FROM courses WHERE termID= :termID ORDER BY id ASC")
    LiveData<List<CourseEntity>> getLiveTermCourses(int termID);

    @Query("SELECT * FROM courses WHERE termID= :termID ORDER BY id ASC")
    List<CourseEntity> getTermCourses(int termID);
}

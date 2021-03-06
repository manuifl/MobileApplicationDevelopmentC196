package org.manuel.c196.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.manuel.c196.R;
import org.manuel.c196.adapters.AssessmentAdapter;
import org.manuel.c196.entities.AssessmentEntity;
import org.manuel.c196.viewmodel.AssessmentViewModel;

public class CourseAssessmentsListActivity extends AppCompatActivity {
    public static final int ADD_ASSESSMENT_REQUEST = 1;

    public static final String EXTRA_COURSE_ID = "org.manuel.c196.activities.COURSE_ID";
    public static final String EXTRA_COURSE_TITLE = "org.manuel.c196.activities.COURSE_TITLE";

    private int courseID;
    private String courseTitle;

    private AssessmentViewModel assessmentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_assessments_list);

        FloatingActionButton buttonAddAssessment = findViewById(R.id.btn_add_assessment);
        buttonAddAssessment.setOnClickListener(v -> {
            Intent addAssessmentIntent = new Intent(CourseAssessmentsListActivity.this, AddEditAssessmentActivity.class);
            startActivityForResult(addAssessmentIntent, ADD_ASSESSMENT_REQUEST);
        });

        Intent loadAssessmentsListIntent = getIntent();
        courseID = loadAssessmentsListIntent.getIntExtra(EXTRA_COURSE_ID, -1);
        courseTitle = loadAssessmentsListIntent.getStringExtra(EXTRA_COURSE_TITLE);

        setTitle(courseTitle + " Assessments");

        RecyclerView recyclerView = findViewById(R.id.assessmentListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        AssessmentAdapter adapter = new AssessmentAdapter();
        recyclerView.setAdapter(adapter);

        assessmentViewModel = new ViewModelProvider(this).get(AssessmentViewModel.class);
        assessmentViewModel.getCourseAssessments(courseID).observe(this, assessmentEntities -> adapter.setAssessments(assessmentEntities));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AssessmentEntity deletedAssessment = adapter.getAssessmentAt(viewHolder.getAdapterPosition());
                assessmentViewModel.delete(deletedAssessment);
                Toast.makeText(CourseAssessmentsListActivity.this, "Assessment deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(assessmentEntity -> {
            Intent loadAssessmentIntent = new Intent(CourseAssessmentsListActivity.this, AssessmentActivity.class);
            loadAssessmentIntent.putExtra(AssessmentActivity.EXTRA_ASSESSMENT_COURSE_ID, courseID);
            loadAssessmentIntent.putExtra(AssessmentActivity.EXTRA_ASSESSMENT_COURSE_TITLE, courseTitle);
            loadAssessmentIntent.putExtra(AssessmentActivity.EXTRA_ASSESSMENT_ID, assessmentEntity.getId());
            loadAssessmentIntent.putExtra(AssessmentActivity.EXTRA_ASSESSMENT_TITLE, assessmentEntity.getName());
            loadAssessmentIntent.putExtra(AssessmentActivity.EXTRA_ASSESSMENT_TYPE, assessmentEntity.getType());
            loadAssessmentIntent.putExtra(AssessmentActivity.EXTRA_ASSESSMENT_DUE_DATE, assessmentEntity.getGoalDate());
            loadAssessmentIntent.putExtra(AssessmentActivity.EXTRA_ASSESSMENT_ALARM, assessmentEntity.isAlert());
            startActivity(loadAssessmentIntent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_ASSESSMENT_REQUEST && resultCode == RESULT_OK) {
            int courseID = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
            String assessmentName = data.getStringExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_TITLE);
            int assessmentType = data.getIntExtra(AddEditAssessmentActivity.EXTRA_ASSESSMENT_TYPE, -1);
            String assessmentGoalDate = data.getStringExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_GOAL_DATE);
            boolean assessmentAlertEnabled = data.getBooleanExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_ALERT, false);

            AssessmentEntity assessmentEntity = new AssessmentEntity(courseID,
                    assessmentName, assessmentType, assessmentGoalDate, assessmentAlertEnabled);

            assessmentViewModel.insert(assessmentEntity);
        }
    }
}

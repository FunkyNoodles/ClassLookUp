package io.github.funkynoodles.classlookup.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.models.Section;

public class SectionResultActivity extends AppCompatActivity {

    private Section section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_result);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            section = (Section) args.getSerializable("section");
        } else {
            throw new AssertionError("Arguments to SectionResultActivity cannot be null");
        }

        TextView subjectNameText = (TextView) findViewById(R.id.subjectNameText);
        subjectNameText.setText(section.getSubject());

        TextView courseNameText = (TextView) findViewById(R.id.courseNameText);
        courseNameText.setText(section.getCourse());

        TextView courseIdText = (TextView) findViewById(R.id.courseIdText);
        String courseId = section.getSubjectId() + " " + section.getCourseId();
        courseIdText.setText(courseId);
    }
}

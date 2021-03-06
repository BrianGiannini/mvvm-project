package io.train.mvvmtuto.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.mvvmtuto.R
import kotlinx.android.synthetic.main.activity_add_note.*

class AddEditNoteActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "com.example.mvvmtuto.EXTRA_ID"
        const val EXTRA_TITLE = "com.example.mvvmtuto.EXTRA_TITLE"
        const val EXTRA_DESCRIPTION = "com.example.mvvmtuto.EXTRA_DESCRIPTION"
        const val EXTRA_PRIORITY = "com.example.mvvmtuto.EXTRA_PRIORITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        number_picker_priority.minValue = 1
        number_picker_priority.maxValue = 10

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        if (intent.hasExtra(EXTRA_ID)) {
            title = "Edit Note"
            edit_text_title.setText(intent.getStringExtra(EXTRA_TITLE))
            edit_text_description.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            number_picker_priority.value = intent.getIntExtra(EXTRA_PRIORITY, 1)
        } else {
            title = "Add Note"
        }
    }

    private fun saveNote() {
        val titleText: String = edit_text_title.text.toString()
        val description: String = edit_text_description.text.toString()
        val priority: Int = number_picker_priority.value

        if (titleText.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent()
        intent.putExtra(EXTRA_TITLE, titleText)
        intent.putExtra(EXTRA_DESCRIPTION, description)
        intent.putExtra(EXTRA_PRIORITY, priority)

        val id: Int = getIntent().getIntExtra(EXTRA_ID, -1)

        if (id != -1)
            intent.putExtra(EXTRA_ID, id)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save_note -> {
                saveNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

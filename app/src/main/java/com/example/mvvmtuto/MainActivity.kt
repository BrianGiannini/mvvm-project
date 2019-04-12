package com.example.mvvmtuto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmtuto.AddNoteActivity.Companion.EXTRA_DESCRIPTION
import com.example.mvvmtuto.AddNoteActivity.Companion.EXTRA_ID
import com.example.mvvmtuto.AddNoteActivity.Companion.EXTRA_PRIORITY
import com.example.mvvmtuto.AddNoteActivity.Companion.EXTRA_TITLE
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LifecycleOwner {

    companion object {
        const val ADD_NOTE_REQUEST = 1
        const val EDIT_NOTE_REQUEST = 2
    }

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_add_note.setOnClickListener {
            startActivityForResult(
                Intent(this, AddNoteActivity::class.java),
                ADD_NOTE_REQUEST
            )
        }
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = NoteAdapter()
        recyclerView.adapter = adapter

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        noteViewModel.getAllNotes().observe(this, Observer<List<Note>> {
            adapter.submitList(it)
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.adapterPosition))
                Toast.makeText(applicationContext, "Note deleted", Toast.LENGTH_SHORT).show()
            }

        }).attachToRecyclerView(recycler_view)

        adapter.setOnItemClickListener(object: NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                var intent = Intent(baseContext, AddNoteActivity::class.java)
                intent.putExtra(AddNoteActivity.EXTRA_ID, note.id)
                intent.putExtra(AddNoteActivity.EXTRA_TITLE, note.title)
                intent.putExtra(AddNoteActivity.EXTRA_DESCRIPTION, note.description)
                intent.putExtra(AddNoteActivity.EXTRA_PRIORITY, note.priority)

                startActivityForResult(intent, EDIT_NOTE_REQUEST)
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val titleData = data.getStringExtra(AddNoteActivity.EXTRA_TITLE) ?: "no title"
                val descriptionData = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION) ?: "no description"
                val priorityData =  data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1)

                val note = Note(titleData, descriptionData, priorityData)

                noteViewModel.insert(note)
            }

        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val id = data?.getIntExtra(EXTRA_ID, -1)

            if (id == -1) {
                Toast.makeText(this, "Could not update! Error!", Toast.LENGTH_SHORT).show()
            }

            val updateNote = Note(
                data!!.getStringExtra(EXTRA_TITLE),
                data.getStringExtra(EXTRA_DESCRIPTION),
                data.getIntExtra(EXTRA_PRIORITY, 1)
            )
            updateNote.id = data.getIntExtra(EXTRA_ID, -1)
            noteViewModel.update(updateNote)

        } else {
            Toast.makeText(this, "Note not saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.maint_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.delete_all_notes -> {
                noteViewModel.deleteAllNotes()
                Toast.makeText(this, "All notes deleted !", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

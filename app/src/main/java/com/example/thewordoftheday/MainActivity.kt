package com.example.thewordoftheday

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thewordoftheday.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var quizModelList: MutableList<QuizModel>
    private lateinit var adapter: QuizListAdapter
    private lateinit var database: DatabaseReference
    private lateinit var wordModel: WordModel
    private var todayWord: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        getTodayWord()

        getWordFromFirebase()

        quizModelList = mutableListOf()
        getQuestionsFromFirebase()
    }

    private fun setDayCounter(dayCounter: Int) {
        database.child("2").child("dayCounter").setValue(dayCounter.toString())
    }

    private fun getTodayWord() {
        database
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.child("2").exists()) {
                    todayWord = dataSnapshot.child("2").child("dayCounter").getValue<String>().toString()
                }
            }
    }


    private fun setupRecyclerView() {
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getQuestionsFromFirebase() {
        database
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.child("0").exists()) {
                    for(snapshot in dataSnapshot.child("0").children) {
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            quizModelList.add(quizModel)
                        }
                    }
                }
                setupRecyclerView()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setupWordOfTheDay() {
        binding.apply {
            wordOfTheDay.text = wordModel.word + "-" + wordModel.translation
        }
    }

    private fun getWordFromFirebase() {
        database
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.child("1").exists()) {
                    val words = dataSnapshot.child("1").child("words")
                    wordModel = words.child(todayWord).getValue(WordModel::class.java)!!
                }
                setupWordOfTheDay()
            }
    }
}
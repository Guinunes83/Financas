package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.FinancialRepository
import com.example.ui.FinancialScreen
import com.example.ui.FinancialViewModel
import com.example.ui.FinancialViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val database = AppDatabase.getDatabase(this)
    val repository = FinancialRepository(database.financialEntryDao(), database.categoryDao(), database.planDao())
    val factory = FinancialViewModelFactory(repository)
    val viewModel = ViewModelProvider(this, factory)[FinancialViewModel::class.java]

    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            FinancialScreen(viewModel = viewModel)
        }
      }
    }
  }
}

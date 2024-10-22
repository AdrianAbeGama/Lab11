package com.example.makeitso.screens.tasks

import androidx.compose.runtime.mutableStateOf
import com.example.makeitso.EDIT_TASK_SCREEN
import com.example.makeitso.SETTINGS_SCREEN
import com.example.makeitso.TASK_ID
import com.example.makeitso.model.Task
import com.example.makeitso.model.service.ConfigurationService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope

@HiltViewModel
class TasksViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : MakeItSoViewModel(logService) {

  val options = mutableStateOf<List<String>>(listOf())

  // Cambiamos el flujo vacío por el flujo de tareas de Firestore
  val tasks: Flow<List<Task>> = storageService.tasks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun loadTaskOptions() {
    //TODO: Implementar lógica para cargar opciones
  }

  fun onTaskCheckChange(task: Task) {
    launchCatching { storageService.update(task.copy(completed = !task.completed)) }
  }

  fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_TASK_SCREEN)

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  fun onTaskActionClick(openScreen: (String) -> Unit, task: Task, action: String) {
    when (TaskActionOption.getByTitle(action)) {
      TaskActionOption.EditTask -> openScreen("$EDIT_TASK_SCREEN?$TASK_ID={${task.id}}")
      TaskActionOption.ToggleFlag -> onFlagTaskClick(task)
      TaskActionOption.DeleteTask -> onDeleteTaskClick(task)
    }
  }

  private fun onFlagTaskClick(task: Task) {
    launchCatching { storageService.update(task.copy(flag = !task.flag)) }
  }

  private fun onDeleteTaskClick(task: Task) {
    launchCatching { storageService.delete(task.id) }
  }
}

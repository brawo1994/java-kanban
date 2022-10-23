package ru.yandex.kanban.service.managers.taskManagers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.kanban.clients.KVTaskClient;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final Gson gson;
    private final KVTaskClient kvTaskClient;

    public HTTPTaskManager() {
        gson = new Gson();
        kvTaskClient = new KVTaskClient();
    }

    @Override
    public void save() {
        kvTaskClient.put("history", gson.toJson(getHistory()));
        kvTaskClient.put("tasks", gson.toJson(tasks));
        kvTaskClient.put("epics", gson.toJson(epics));
        kvTaskClient.put("subTasks", gson.toJson(subTasks));
    }

    public void load() {
        String jsonTasks = kvTaskClient.load("tasks");
        Type tasksType = new TypeToken<HashMap<Integer, Task>>(){}.getType();
        HashMap<Integer, Task> loadedTasks = gson.fromJson(jsonTasks, tasksType);
        for (Task task : loadedTasks.values()){
            super.restoreTask(task);
        }

        String jsonEpics = kvTaskClient.load("epics");
        Type epicsType = new TypeToken<HashMap<Integer, Epic>>(){}.getType();
        HashMap<Integer, Epic> loadedEpics = gson.fromJson(jsonEpics, epicsType);
        for (Epic epic : loadedEpics.values()){
            epic.clearSubTaskList();
            super.restoreEpic(epic);
        }

        String jsonSubTasks = kvTaskClient.load("subTasks");
        Type subTaskType = new TypeToken<HashMap<Integer, SubTask>>(){}.getType();
        HashMap<Integer, SubTask> loadedSubTasks = gson.fromJson(jsonSubTasks, subTaskType);
        for (SubTask subTask : loadedSubTasks.values()){
            super.restoreSubTask(subTask);
        }

        String jsonHistory = kvTaskClient.load("history");
        Type historyType = new TypeToken<List<Task>>(){}.getType();
        List<Task> loadHistory = gson.fromJson(jsonHistory, historyType);
        for (Task task : loadHistory){
            historyManager.add(findTask(task.getId()));
        }
    }
}

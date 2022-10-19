package ru.yandex.kanban.service;

import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerCSVFormatter {

    protected static final String CSV_HEADER = "id,type,name,status,description,duration,startTime,epic";
    public static String toString(Task task){
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription()+ "," + task.getDuration()+ "," + task.getStartTime() + "," + task.getEpicId();
    }

    public static String toString(HistoryManager historyManager){
        List<String> historyTasksId = new ArrayList<>();
        for (Task task : historyManager.getHistory()){
            historyTasksId.add(String.valueOf(task.getId()));
        }
        return String.join(",",historyTasksId);
    }

    public static Task taskFromString(String string){
        List<String> taskFields = new ArrayList<>(List.of(string.split(",")));
        final int id = Integer.parseInt(taskFields.get(0));
        final String name = taskFields.get(2);
        final String status = taskFields.get(3);
        final String description = taskFields.get(4);
        final long duration = Long.parseLong(taskFields.get(5));
        final Instant startTime = Instant.parse(taskFields.get(6));
        switch (taskFields.get(1)){
            case ("TASK"):
                return new Task(id, name, description, status, duration, startTime);
            case("EPIC"):
                return new Epic(id, name, description, status, duration, startTime);
            default:
                final int epicId = Integer.parseInt(taskFields.get(7));
                return new SubTask(id, name, description, status, epicId, duration, startTime);
        }
    }

    public static List<Integer> historyFromString(String string){
        List<String> taskIdList = new ArrayList<>(List.of(string.split(",")));
        List<Integer> taskIdListResult = new ArrayList<>();
        for (String taskIdInString : taskIdList){
            taskIdListResult.add(Integer.parseInt(taskIdInString));
        }
        return taskIdListResult;
    }
}

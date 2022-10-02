package ru.yandex.kanban.service;

import ru.yandex.kanban.exception.ManagerSaveException;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager {

    private final File file;
    static final String filePath = "src/ru/yandex/kanban/history.csv";

    public FileBackedTasksManager() {
        this.file = new File(filePath);
    }

    public static FileBackedTasksManager loadFromFile (File file) {
        final FileBackedTasksManager tasksManager = new FileBackedTasksManager();
        try {
            final String csv = Files.readString(Path.of(file.toURI()));
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            List<Integer> history = Collections.emptyList();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    history = TaskManagerCSVFormatter.historyFromString(lines[i + 1]);
                    break;
                }
                final Task task = TaskManagerCSVFormatter.taskFromString(line);
                final int id = task.getId();
                if (id > generatorId){
                    generatorId = id;
                }
                tasksManager.addTask(task);
            }
            for (SubTask subTask : tasksManager.subTasks.values()){
                final int id = subTask.getId();
                tasksManager.getEpic(subTask.getEpicId()).addSubTask(id);
            }
            for (Integer taskId : history){
                tasksManager.historyManager.add(tasksManager.findTask(taskId));
            }
            tasksManager.lastTaskId = generatorId;
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось прочитать из файла" + file.getName());
        }
        return tasksManager;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))){
            fileWriter.write(TaskManagerCSVFormatter.CSV_HEADER);
            fileWriter.newLine();

            for (Task task : tasks.values()){
                fileWriter.write(TaskManagerCSVFormatter.toString(task));
                fileWriter.newLine();
            }

            for (Task task : epics.values()){
                fileWriter.write(TaskManagerCSVFormatter.toString(task));
                fileWriter.newLine();
            }

            for (Task task : subTasks.values()){
                fileWriter.write(TaskManagerCSVFormatter.toString(task));
                fileWriter.newLine();
            }

            fileWriter.newLine();
            fileWriter.write(TaskManagerCSVFormatter.toString(historyManager));
            fileWriter.newLine();

        } catch (IOException e){
            throw new ManagerSaveException("Не удалось записать в файл" + file.getName());
        }
    }

    protected void addTask(Task task){
        final int id = task.getId();
        switch (task.getType()){
            case TASK :
                tasks.put(id, task);
                break;
            case SUBTASK:
                subTasks.put(id, (SubTask) task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
        }
    }

    protected Task findTask(Integer taskId){
        if (tasks.containsKey(taskId)){
            return tasks.get(taskId);
        }

        if (subTasks.containsKey(taskId)){
            return subTasks.get(taskId);
        }

        return epics.get(taskId);
    }

    // Методы для TASK
    @Override
    public int addNewTask(String name, String description) {
        final int id = super.addNewTask(name, description);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    // Методы EPIC
    @Override
    public int addNewEpic(String name, String description) {
        final int id = super.addNewEpic(name, description);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    // Методы SUBTASK
    @Override
    public int addNewSubTask(String name, String description, int epicId) {
        final int id = super.addNewSubTask(name, description, epicId);
        save();
        return id;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        super.deleteSubTask(subTaskId);
        save();
    }

    @Override
    public Task getTask(int taskId) {
        final Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        final List<Task> tasks = super.getAllTasks();
        save();
        return tasks;
    }

    @Override
    public Epic getEpic(int epicId) {
        final Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        final List<Epic> epics = super.getAllEpics();
        save();
        return epics;
    }

    @Override
    public List<SubTask> getAllSubTasksByEpic(int epicId) {
        final List<SubTask> subTasks = super.getAllSubTasksByEpic(epicId);
        save();
        return subTasks;
    }

    @Override
    public SubTask getSubTask(int subTaskId) {
        final SubTask subTask = super.getSubTask(subTaskId);
        save();
        return subTask;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        final List<SubTask> subTasks = super.getAllSubTasks();
        save();
        return subTasks;
    }
}

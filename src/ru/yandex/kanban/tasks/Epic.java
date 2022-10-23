package ru.yandex.kanban.tasks;

import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.model.enums.TaskType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> subTasksList;
    protected Instant endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, 0, Instant.ofEpochMilli(0));
        super.type = TaskType.EPIC;
        this.subTasksList = new ArrayList<>();
        this.endTime = super.getEndTime();
    }

    public Epic(int id, String name, String description, String status, long duration, Instant startTime) {
        super(id, name, description, duration, startTime);
        super.type = TaskType.EPIC;
        super.status = TaskStatus.valueOf(status);
        this.subTasksList = new ArrayList<>();
        this.endTime = super.getEndTime();
    }

    public void addSubTask(Integer subTaskId) {
        subTasksList.add(subTaskId);
    }

    public void deleteSubTask(Integer subTaskId) {
        subTasksList.remove(subTaskId);
    }

    public void clearSubTaskList() {
        subTasksList.clear();
    }

    public ArrayList<Integer> getSubTasksList() {
        return subTasksList;
    }

    @Override
    public Integer getEpicId() {
        return null;
    }

    public void calculateEpicStatusAndDuration(Map<Integer, SubTask> subTasks) {
        HashMap<TaskStatus, Integer> subTasksStatus = new HashMap<>();
        subTasksStatus.put(TaskStatus.NEW, 0);
        subTasksStatus.put(TaskStatus.IN_PROGRESS, 0);
        subTasksStatus.put(TaskStatus.DONE, 0);
        Instant startTime = Instant.MAX;
        Instant endTime = Instant.MIN;
        int subTasksCountInEpic = this.subTasksList.size();
        for (Integer subTaskId : this.subTasksList) {
            SubTask subTask = subTasks.get(subTaskId);
            int tempCount = 0;
            if (subTasksStatus.containsKey(subTask.getStatus())) {
                tempCount = subTasksStatus.get(subTask.getStatus());
            }
            subTasksStatus.put(subTask.getStatus(), ++tempCount);
            if (subTask.getStartTime().isBefore(startTime))
                startTime = subTask.getStartTime();
            if (subTask.getEndTime().isAfter(endTime))
                endTime = subTask.getEndTime();
        }
        this.startTime = startTime;
        this.endTime = endTime;
        final long MILLISECONDS_IN_MINUTE = 60000L;
        if (subTasksCountInEpic == 0){
            this.duration = 0;
        } else {
            this.duration = (startTime.toEpochMilli() - endTime.toEpochMilli()) / MILLISECONDS_IN_MINUTE;
        }
        if (subTasksCountInEpic == 0 || subTasksCountInEpic == subTasksStatus.get(TaskStatus.NEW)) {
            this.status = TaskStatus.NEW;
            return;
        }
        if (subTasksCountInEpic == subTasksStatus.get(TaskStatus.DONE)) {
            this.status = TaskStatus.DONE;
            return;
        }
        if (this.status != TaskStatus.IN_PROGRESS) {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return "\nEpic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", startTime=" + startTime.toEpochMilli() +
                ", duration=" + duration + "}, " +
                "subTasksList=" + subTasksList;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic otherEpic = (Epic) obj;
        return (id == otherEpic.id) &&
                Objects.equals(name, otherEpic.name) &&
                Objects.equals(description, otherEpic.description) &&
                (type == otherEpic.type) &&
                (status == otherEpic.status) &&
                Objects.equals(subTasksList, otherEpic.subTasksList) &&
                Objects.equals(startTime, otherEpic.startTime) &&
                (duration == otherEpic.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, status, subTasksList, duration, startTime);
    }
}

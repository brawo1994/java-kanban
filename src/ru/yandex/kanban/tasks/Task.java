package ru.yandex.kanban.tasks;

import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.model.enums.TaskType;

import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskType.taskType type;
    protected TaskStatus.taskStatus status;

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = TaskType.taskType.TASK;
        this.status = TaskStatus.taskStatus.NEW;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus.taskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus.taskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "\nTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return (id == otherTask.id) &&
                Objects.equals(name, otherTask.name) &&
                Objects.equals(description, otherTask.description) &&
                (type == otherTask.type) &&
                (status == otherTask.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, status);
    }
}

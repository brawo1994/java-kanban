package ru.yandex.kanban.tasks;

import ru.yandex.kanban.model.enums.TaskType;

import java.util.Objects;

public class SubTask extends Task{
    protected int epicId;

    public SubTask(int id, String name, String description, int epicId) {
        super(id, name, description);
        super.type = TaskType.taskType.SUBTASK;
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "\nSubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        SubTask otherSubTask = (SubTask) obj;
        return (id == otherSubTask.id) &&
                Objects.equals(name, otherSubTask.name) &&
                Objects.equals(description, otherSubTask.description) &&
                (type == otherSubTask.type) &&
                (status == otherSubTask.status) &&
                (epicId == otherSubTask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, status, epicId);
    }
}

package ru.yandex.kanban.tasks;

import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.model.enums.TaskType;

import java.time.Instant;
import java.util.Objects;

public class SubTask extends Task{
    protected int epicId;

    public SubTask(int id, String name, String description, int epicId, long duration, Instant startTime) {
        super(id, name, description, duration, startTime);
        super.type = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, String status, int epicId, long duration, Instant startTime) {
        super(id, name, description, duration, startTime);
        super.type = TaskType.SUBTASK;
        super.status = TaskStatus.valueOf(status);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
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
                ", startTime=" + startTime.toEpochMilli() +
                ", duration=" + duration +
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
                (epicId == otherSubTask.epicId) &&
                Objects.equals(startTime, otherSubTask.startTime) &&
                (duration == otherSubTask.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, status, epicId, duration, startTime);
    }
}

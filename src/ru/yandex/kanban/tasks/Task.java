package ru.yandex.kanban.tasks;

import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.model.enums.TaskType;

import java.time.Instant;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskType type;
    protected TaskStatus status;
    protected long duration;
    protected Instant startTime;

    public Task(int id, String name, String description, long duration, Instant startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = TaskType.TASK;
        this.status = TaskStatus.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, String status, long duration, Instant startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = TaskType.TASK;
        this.status = TaskStatus.valueOf(status);
        this.duration = duration;
        this.startTime = startTime;
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

    public TaskType getType() {
        return type;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getEpicId() {
        return null;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public Instant getEndTime() {
        final long SECONDS_IN_MINUTE = 60L;
        return startTime.plusSeconds(duration * SECONDS_IN_MINUTE);
    }

    @Override
    public String toString() {
        return "\nTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", startTime=" + startTime.toEpochMilli() +
                ", duration=" + duration +
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
                (status == otherTask.status) &&
                Objects.equals(startTime, otherTask.startTime) &&
                (duration == otherTask.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, status, duration, startTime);
    }
}

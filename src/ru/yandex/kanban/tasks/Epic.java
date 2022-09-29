package ru.yandex.kanban.tasks;

import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.model.enums.TaskType;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> subTasksList;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        super.type = TaskType.EPIC;
        this.subTasksList = new ArrayList<>();
    }

    public Epic(int id, String name, String description, String status) {
        super(id, name, description);
        super.type = TaskType.EPIC;
        super.status = TaskStatus.valueOf(status);
        this.subTasksList = new ArrayList<>();
    }

    public void addSubTask(Integer subTaskId) {
        subTasksList.add(subTaskId);
    }

    public void deleteSubTask(Integer subTaskId) {
        subTasksList.remove(subTaskId);
    }

    public ArrayList<Integer> getSubTasksList() {
        return subTasksList;
    }

    @Override
    public Integer getEpicId() {
        return null;
    }

    @Override
    public String toString() {
        return "\nEpic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status + "}, " +
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
                Objects.equals(subTasksList, otherEpic.subTasksList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, status, subTasksList);
    }
}

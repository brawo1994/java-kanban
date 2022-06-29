public class SubTask extends Task{
    protected int epicId;

    SubTask(String name, String description, int epicId) {
        super(name, description);
        super.type = TaskType.taskType.SUBTASK;
        this.epicId = epicId;
    }

    //Конструктор для дебага
    SubTask(int id, String name, String description, int epicId, TaskStatus.taskStatus status) {
        super(id, name, description, status);
        this.epicId = epicId;
        this.type = TaskType.taskType.TASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskType.taskType type;
    protected TaskStatus.taskStatus status;

    Task(String name, String description) {
        this.id = Manager.getNewTaskId();
        this.name = name;
        this.description = description;
        this.type = TaskType.taskType.TASK;
        this.status = TaskStatus.taskStatus.NEW;

    }

    //Конструктор для дебага
    Task(int id, String name, String description, TaskStatus.taskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = TaskType.taskType.TASK;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public TaskStatus.taskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus.taskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}

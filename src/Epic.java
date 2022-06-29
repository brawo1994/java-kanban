import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subTasksList;

    Epic(String name, String description) {
        super(name, description);
        super.type = TaskType.taskType.EPIC;
        subTasksList = new ArrayList<>();
    }

    //Конструктор для дебага
    Epic(int id, String name, String description, TaskStatus.taskStatus status, ArrayList<Integer> subTasksList) {
        super(id, name, description, status);
        this.type = TaskType.taskType.TASK;
        this.subTasksList = subTasksList;
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
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status + "}, " +
                "subTasksList=" + subTasksList;
    }
}

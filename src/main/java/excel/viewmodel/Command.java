package excel.viewmodel;

public interface Command {
    void execute();
    void undo();
}

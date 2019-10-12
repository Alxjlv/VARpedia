package main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

public class ProcessRunner extends Task<Void> {

    private int exitVal;
    private int processStatus;
    private String _command;
    private Process process;

    public ProcessRunner(String command){
        _command=command;
    }

    @Override
    protected Void call() throws Exception {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("bash","-c",_command);
            process = builder.start();
//            stateProperty().addListener((observable, oldValue, newValue) -> {
//                if (newValue == State.CANCELLED) {
//                    process.destroy();
//                }
//            });
            processStatus = process.waitFor();
            exitVal = process.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void cancelled() {
        process.destroy();
    }

    public int getStatus(){
        return processStatus;
    }

    public int getExitVal(){
        return exitVal;
    }
}

package main;

import javafx.concurrent.Task;

public class ProcessRunner extends Task<Void> {

    private int exitVal;
    private int processStatus;
    private String _command;

    public ProcessRunner(String command){
        _command=command;
    }

    @Override
    protected Void call() throws Exception {
        try{
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("bash","-c",_command);
            Process process = builder.start();
            processStatus = process.waitFor();
            exitVal = process.exitValue();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }

    public int getStatus(){
        return processStatus;
    }

    public int getExitVal(){
        return exitVal;
    }
}

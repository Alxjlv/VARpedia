package main;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
            if (process.waitFor() != 0) {
                BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorString = errorStream.readLine();
                throw new Exception(errorString);
            }
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

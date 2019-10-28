package main;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * ProcessRunner is responsible for running bash commands and reporting their exit value back to the rest of the program
 * @author Tait & Alex
 */
public class ProcessRunner extends Task<Void> {

    private int exitValue;
    private String _command; // The bash command
    private Process process;

    public ProcessRunner(String command){
        _command=command;
    }

    @Override
    protected Void call() throws Exception {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("bash","-c",_command);
        process = builder.start();
        exitValue = process.waitFor(); // Waiting for the process to finish executing
        if (process.waitFor() != 0) { // Prints out the command & error stream if the bash command fails
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorString = errorStream.readLine();
            throw new Exception("Failed command: "+_command+"\n"+errorString);
        }
        return null;
    }

    @Override
    protected void cancelled() {
        process.destroyForcibly(); // Killing the process if we want to stop it
    }

    public int getExitValue() {
        return exitValue;
    }
}

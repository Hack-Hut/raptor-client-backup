package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class Exec implements Runnable {
    private String stdout;
    private String stderr;
    private String[] command;
    private Process process;
    private boolean showOutput = true;
    private long pid;
    private int exitCode;

    public Exec(String[] command){
        this.command = command;
    }

    public static ArrayList<String> executeCommandGetOutput(String[] command){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        ArrayList<String> output = new ArrayList<>();
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) output.add(line);
        } catch (IOException e) {
            Log.debug("Failed executing " + Arrays.toString(command));
            Log.debug(e.toString());
        }
        return output;
    }

    public void execute() {
        ProcessBuilder pb = new ProcessBuilder(this.command);
        Log.debug("Executing " + Arrays.toString(this.command));
        try {
            process = pb.start();
            pid = process.pid();
        } catch (IOException e){
            Log.error("The command " + Arrays.toString(command) + " was interrupted.");
            Log.error(e.toString());
        }
    }

    public Process getProcess(){
        return process;
    }

    public boolean isRunning(){
        return process.isAlive();
    }

    public void getOutput() {
        try { Thread.sleep(20); } catch (InterruptedException ignored){}
        BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        StringBuilder inBuilder = new StringBuilder();
        StringBuilder errBuilder = new StringBuilder();

        String inputStream = null, errorStream = null;
        while (true) {
            try {
                if ((inputStream = inReader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            inBuilder.append(inputStream);
            inBuilder.append(System.getProperty("line.separator"));
            if (showOutput) {
                System.out.println(inputStream);
            }
        }
        while (true) {
            try {
                if ((errorStream = errReader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            errBuilder.append(errorStream);
            errBuilder.append(System.getProperty("line.separator"));
            if (showOutput) {
                System.out.println(errorStream);
            }
        }
        stdout = inBuilder.toString();
        stderr = errBuilder.toString();

        try {
            inReader.close();
            errReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStderr(){
        return stderr;
    }

    public String getStdout(){
        return stdout;
    }

    public long getPid(){
        return pid;
    }

    public int getReturnCode(){
        return exitCode;
    }

    public void run(){
        Thread.currentThread().setName("Build-Script-Executor");
        Log.debug("Starting the execution thread.");
        execute();
    }

}


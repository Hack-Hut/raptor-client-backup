package monitors.bepStep;

import org.junit.jupiter.api.Test;
import utils.Exec;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ProcessTreeTest {
    private static String cwd = utils.SystemOps.getCWD();
    private String eightcc = cwd + "/src/test/resources/testCompilationProject/8cc";
    private String os = utils.SystemOps.getOsType().toLowerCase();

    @Test
    void compile8cc(){
        if (!os.equals("linux")) return;
        String[] testCleanCommand = {"make", "-C", eightcc, "clean"};
        String[] testCompileCommand = {"make", "-C", eightcc};

        Exec cleanProject = new Exec(testCleanCommand);
        Exec testProject = new Exec(testCompileCommand);

        Thread clean = new Thread(cleanProject);

        clean.start();
        cleanProject.getOutput();

        Thread execThread = new Thread(testProject);
        execThread.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long pid = testProject.getPid();
        ProcessTree bepStep = new ProcessTree(pid);
        bepStep.setTestMode();
        Thread bepStepThread = new Thread(bepStep);
        bepStepThread.start();
        try {
            bepStepThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<String> expected = new ArrayList<>();
        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o main.o main.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o cpp.o cpp.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o debug.o debug.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o dict.o dict.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o gen.o gen.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o lex.o lex.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o vector.o vector.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o parse.o parse.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o buffer.o buffer.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR='\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\'" +
                " -c -o map.o map.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o error.o error.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                "   -c -o path.o path.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o file.o file.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o set.o set.c ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        expected.add("cc -Wall -Wno-strict-aliasing -std=gnu11 -g -I. -O0 " +
                "-DBUILD_DIR=\"/home/luke/documents/java/RaptorClient/src/test/resources/testCompilationProject/8cc\"" +
                " -c -o encoding.o encoding.c. ".replace("/home/luke/documents/java/RaptorClient/", cwd));

        ArrayList<String> actual = bepStep.getCommands();
        System.out.println(Arrays.toString(actual.toArray()));
        assertTrue(actual.contains(expected.get(3)));
        assertTrue(actual.contains(expected.get(4)));

    }
}
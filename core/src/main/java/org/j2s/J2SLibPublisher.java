package org.j2s;

import java.io.*;
import java.util.List;

public class J2SLibPublisher {

    public static void publish(String libraryName, String version, String description, J2SCompilationType compilationType, List<J2SCompilationResult> compilationResults) throws IOException {
        // src
        String libPath = "build\\j2s\\" + libraryName;
        File dir = new File(libPath);
        dir.mkdirs();
        for (J2SCompilationResult compilationResult : compilationResults) {
            File file = new File(libPath + "\\src", compilationResult.getFile().getName());
            file.getParentFile().mkdirs();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append(compilationResult.getContent());
            fileWriter.flush();
            fileWriter.close();
        }

        if (compilationType == J2SCompilationType.TYPESCRIPT) {
            // cfgs
            File file = new File(libPath, "tsconfig.json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append(
                    "{\n" +
                    "  \"compilerOptions\": {\n" +
                    "    \"module\": \"commonjs\",\n" +
                    "    \"target\": \"es6\",\n" +
                    "    \"declaration\": true,\n" +
                    "    \"outDir\": \"./dist\"\n" +
                    "  },\n" +
                    "  \"include\": [\n" +
                    "    \"src/**/*\"\n" +
                    "  ]\n" +
                    "}"
            );
            fileWriter.flush();
            fileWriter.close();
            file = new File(libPath, "package.json");
            fileWriter = new FileWriter(file);
            fileWriter.append(
                    "{\n" +
                    "  \"name\": \"" + libraryName + "\",\n" +
                    "  \"version\": \"" + version + "\",\n" +
                    "  \"description\": \"" + description + "\"\n" +
                    "}"
            );
            fileWriter.flush();
            fileWriter.close();
            file = new File(libPath, ".npmignore");
            fileWriter = new FileWriter(file);
            fileWriter.append("tsconfig.json").append("\n");
            fileWriter.append("src");
            fileWriter.flush();
            fileWriter.close();

            // publishing
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "cmd.exe",
                    "/c",
                    "cd " + dir.getAbsolutePath() + " && npx tsc && npm publish"
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while (true) {
                line = bufferedReader.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
        }
    }
}

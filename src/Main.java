import util.ArrayUtils;
import util.CliUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static util.CliUtils.getModuleDirectory;
import static util.CliUtils.parseCmdArgs;

public class Main {

    public static List<Integer> createNewList(List<Integer> list1, List<Integer> list2){
        // Создаем новый список, который будет объединять элементы двух списков
        List<Integer> result = new ArrayList<>(list1.size() + list2.size());

        // Копируем элементы из list1 и list2 в result
        copy(list1, 0, result, 0, list1.size());
        copy(list2, 0, result, result.size(), list2.size());

        // Сортируем объединенный список
        sort(result);

        return result;
    }

    public static int copy(List<Integer> src, int srcIndex, List<Integer> dst, int dstIndex, int count) {
        // Проверяем границы
        if (srcIndex < 0 || srcIndex + count > src.size()) {
            throw new IllegalArgumentException("srcIndex or count is out of bounds");
        }
        if (dstIndex < 0 || dstIndex > dst.size()) {
            throw new IllegalArgumentException("dstIndex is out of bounds");
        }

        // Копируем элементы
        for (int i = 0; i < count; i++) {
            if (dstIndex + i < dst.size()) {
                dst.set(dstIndex + i, src.get(srcIndex + i));
            } else {
                dst.add(src.get(srcIndex + i));
            }
        }
        return count;
    }

    public static void sort(List<Integer> list){
        int n = list.size();
        boolean swapped;

        // Внешний цикл: количество проходов
        for (int i = 0; i < n - 1; i++) {
            swapped = false;

            // Внутренний цикл: сравнение и обмен соседних элементов
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    // Меняем местами элементы
                    int temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                }
            }

            // Если не было обменов за проход, список уже отсортирован
            if (!swapped) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        CliUtils.CmdParams params = parseCmdArgs(args);

        try {
            if (params.test) {
                runTests();
            } else if (params.window) {
                Locale.setDefault(Locale.ROOT);

                java.awt.EventQueue.invokeLater(() -> new FrameMain().setVisible(true));
            } else {
                processFiles(params.inputFile, params.outputFile);
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Подготовка к прогону через createNewList
    public static void processFiles(String inputFile, String outputFile) throws FileNotFoundException {
        List<List<Integer>> list = ArrayUtils.readIntList2FromFile(inputFile);

        if (list == null || list.size() < 2) {
            System.err.printf("Error: Unable to read valid array from file \"%s\"%n", inputFile);
            System.exit(70);
        }

        List<Integer> list1 = list.get(0), list2 = list.get(1);

        List<Integer> result = createNewList(list1, list2);
        ArrayUtils.writeListToFile(outputFile, result);

        System.out.printf("Processed file: Input1 = %s, Output = %s%n", inputFile, outputFile);
    }

    // Запуск тестов
    public static void runTests() throws Exception {
        String moduleDir = getModuleDirectory();
        String testsDir = moduleDir + File.separator + "src" + File.separator + "tests";

        File inputDir = new File(testsDir, "input");
        File outputDir = new File(testsDir, "output");
        File expectedDir = new File(testsDir, "expected");

        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.err.printf("Input directory \"%s\" does not exist or is not a directory%n", inputDir.getAbsolutePath());
            System.exit(70);
        }
        if (!expectedDir.exists() || !expectedDir.isDirectory()) {
            System.err.printf("Expected directory \"%s\" does not exist or is not a directory%n", expectedDir.getAbsolutePath());
            System.exit(70);
        }
        if (!outputDir.exists()) {
            if (!outputDir.mkdir()) {
                System.err.printf("Failed to create output directory \"%s\"%n", outputDir.getAbsolutePath());
                System.exit(70);
            }
        }

        File[] inputFiles = inputDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (inputFiles == null || inputFiles.length == 0) {
            System.err.printf("No input files found in directory \"%s\"%n", inputDir.getAbsolutePath());
            System.exit(70);
        }

        for (File inputFile : inputFiles) {
            String baseName = inputFile.getName().replace("input", "").replace(".txt", "");
            File expectedFile = new File(expectedDir, "expected" + baseName + ".txt");
            File outputFile = new File(outputDir, "output" + baseName + ".txt");

            if (!expectedFile.exists()) {
                System.err.printf("Expected file \"%s\" not found%n", expectedFile.getAbsolutePath());
                continue;
            }

            System.out.printf("Processing test: Input = %s, Expected = %s, Output = %s%n",
                    inputFile.getAbsolutePath(), expectedFile.getAbsolutePath(), outputFile.getAbsolutePath());

            processFiles(inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

            if (compareFiles(outputFile, expectedFile)) {
                System.out.printf("Test %s passed%n", baseName);
            } else {
                System.err.printf("Test %s failed%n", baseName);
            }
        }
    }

    private static boolean compareFiles(File outputFile, File expectedFile) throws IOException {
        try (BufferedReader outputReader = new BufferedReader(new FileReader(outputFile));
             BufferedReader expectedReader = new BufferedReader(new FileReader(expectedFile))) {

            String outputLine;
            String expectedLine;

            while ((outputLine = outputReader.readLine()) != null) {
                expectedLine = expectedReader.readLine();

                if (!outputLine.equals(expectedLine)) {
                    return false;
                }
            }

            return expectedReader.readLine() == null;
        }
    }
}
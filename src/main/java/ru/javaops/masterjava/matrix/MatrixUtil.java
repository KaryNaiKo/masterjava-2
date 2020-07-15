package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final CompletionService<Result> completionService = new ExecutorCompletionService<>(executor);

        for (int rowIndex = 0; rowIndex < matrixSize; rowIndex++) {
            final int i = rowIndex;

            completionService.submit(() -> {
                int[] matrixCRow = new int[matrixSize];

                for (int columnIndex = 0; columnIndex < matrixSize; columnIndex++) {
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += matrixA[i][k] * matrixB[k][columnIndex];
                    }
                    matrixCRow[columnIndex] = sum;
                }

                return new Result(i, matrixCRow);
            });
        }

        for (int i = 0; i < matrixSize; i++) {
            Result result = completionService.take().get();

            int[] matrixCRow = result.getMatrixCRow();
            for (int j = 0; j < matrixCRow.length; j++) {
                matrixC[result.index][j] = matrixCRow[j];
            }
        }

        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        //return withoutOptimization(matrixA, matrixB);
        return optimized(matrixA, matrixB);
    }

    private static int[][] withoutOptimization(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    private static int[][] optimized(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int thatColumn[] = new int[matrixSize];

        try {
            for (int j = 0; ; j++) {
                for (int k = 0; k < matrixSize; k++) {
                    thatColumn[k] = matrixB[k][j];
                }

                for (int i = 0; i < matrixSize; i++) {
                    int thisRow[] = matrixA[i];
                    int summand = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        summand += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][j] = summand;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    static class Result {
        private int index;
        private int[] matrixCRow;

        public Result(int index, int[] matrixCRow) {
            this.index = index;
            this.matrixCRow = matrixCRow;
        }

        public int getIndex() {
            return index;
        }

        public int[] getMatrixCRow() {
            return matrixCRow;
        }
    }
}

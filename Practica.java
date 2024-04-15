package practica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

public class Practica extends JFrame {
    private JButton btnGenerar;
    private JButton btnBorrar;
    private JButton btnSecuencial;
    private JButton btnForkJoin;
    private JButton btnExecutorService;
    private JTextArea txtArregloOriginal;
    private JTextArea txtArregloOrdenado;
    private JTextArea txtSecuencialTiempo;
    private JTextArea txtForkJoinTiempo;
    private JTextArea txtExecutorServiceTiempo;
    private int[] arregloOriginal;
    private int[] arregloOrdenado;

    public Practica() {
        setTitle("Comparacion de Algoritmos");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia la ventana en modo de pantalla completa
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inicialización de componentes GUI
        btnGenerar = new JButton("Generar Arreglo");
        btnBorrar = new JButton("Borrar Arreglo");
        btnSecuencial = new JButton("Secuencial");
        btnForkJoin = new JButton("Fork/Join");
        btnExecutorService = new JButton("Executor Service");
        txtArregloOriginal = new JTextArea();
        txtArregloOrdenado = new JTextArea();
        txtSecuencialTiempo = new JTextArea();
        txtForkJoinTiempo = new JTextArea();
        txtExecutorServiceTiempo = new JTextArea();

        // Panel para botones
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnGenerar);
        btnPanel.add(btnBorrar);
        btnPanel.add(btnSecuencial);
        btnPanel.add(btnForkJoin);
        btnPanel.add(btnExecutorService);

        // Panel para áreas de texto
        JPanel textAreasPanel = new JPanel(new GridLayout(1, 5));
        textAreasPanel.add(new JScrollPane(txtArregloOriginal));
        textAreasPanel.add(new JScrollPane(txtArregloOrdenado));
        textAreasPanel.add(new JScrollPane(txtSecuencialTiempo));
        textAreasPanel.add(new JScrollPane(txtForkJoinTiempo));
        textAreasPanel.add(new JScrollPane(txtExecutorServiceTiempo));

        // Agregar paneles al marco
        add(btnPanel, BorderLayout.NORTH);
        add(textAreasPanel, BorderLayout.CENTER);

        // Acción de botón para generar un nuevo arreglo
        btnGenerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Solicitar al usuario el número de elementos para el arreglo
                String input = JOptionPane.showInputDialog(Practica.this, "Ingrese el número de elementos:");
                if (input == null || input.isEmpty()) {
                    JOptionPane.showMessageDialog(Practica.this, "Debe ingresar un número de elementos válido.");
                    return;
                }
                try {
                    int size = Integer.parseInt(input);
                    arregloOriginal = generateRandomArray(size);
                    arregloOrdenado = Arrays.copyOf(arregloOriginal, arregloOriginal.length);
                    txtArregloOriginal.setText("Arreglo original:\n" + Arrays.toString(arregloOriginal) + "\n");
                    txtArregloOrdenado.setText("");
                    txtSecuencialTiempo.setText("");
                    txtForkJoinTiempo.setText("");
                    txtExecutorServiceTiempo.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Practica.this, "Debe ingresar un número válido.");
                }
            }
        });

        // Acción de botón para borrar el arreglo y limpiar las cajas de texto
        btnBorrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                arregloOriginal = null;
                arregloOrdenado = null;
                txtArregloOriginal.setText("");
                txtArregloOrdenado.setText("");
                txtSecuencialTiempo.setText("");
                txtForkJoinTiempo.setText("");
                txtExecutorServiceTiempo.setText("");
            }
        });

        // Acción de botón para ordenar de forma secuencial
        btnSecuencial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verificar si se ha generado un arreglo antes de ordenar
                if (arregloOriginal == null) {
                    JOptionPane.showMessageDialog(Practica.this, "Genera un arreglo primero.");
                    return;
                }
                long startTime = System.nanoTime();
                mergeSort(arregloOrdenado);
                long endTime = System.nanoTime();
                double durationMicroseconds = (endTime - startTime) / 1e3; // Convertir nanosegundos a microsegundos
                txtSecuencialTiempo.setText("Secuencial Tiempo: " + durationMicroseconds + " microsegundos\n");
                txtArregloOrdenado.setText("Arreglo ordenado:\n" + Arrays.toString(arregloOrdenado) + "\n");
            }
        });

        // Acción de botón para ordenar utilizando Fork/Join
        btnForkJoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verificar si se ha generado un arreglo antes de ordenar
                if (arregloOriginal == null) {
                    JOptionPane.showMessageDialog(Practica.this, "Genera un arreglo primero.");
                    return;
                }
                long startTime = System.nanoTime();
                ForkJoinPool pool = new ForkJoinPool();
                pool.invoke(new MergeSortTask(arregloOrdenado, 0, arregloOrdenado.length - 1));
                long endTime = System.nanoTime();
                double durationMicroseconds = (endTime - startTime) / 1e3; // Convertir nanosegundos a microsegundos
                txtForkJoinTiempo.setText("Fork/Join Sort Tiempo: " + durationMicroseconds + " microsegundos\n");
                txtArregloOrdenado.setText("Arreglo ordenado:\n" + Arrays.toString(arregloOrdenado) + "\n");
            }
        });

        // Acción de botón para ordenar utilizando ExecutorService
        btnExecutorService.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verificar si se ha generado un arreglo antes de ordenar
                if (arregloOriginal == null) {
                    JOptionPane.showMessageDialog(Practica.this, "Genera un arreglo primero.");
                    return;
                }
                long startTime = System.nanoTime();
                ExecutorService executor = Executors.newFixedThreadPool(1);
                Future<int[]> future = executor.submit(() -> {
                    Arrays.sort(arregloOrdenado); // Usar Arrays.sort() en lugar de Merge Sort (mejora el tiempo)
                    return arregloOrdenado;
                });
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
                executor.shutdown();
                long endTime = System.nanoTime();
                double durationMicroseconds = (endTime - startTime) / 1e3; // Convertir nanosegundos a microsegundos
                txtExecutorServiceTiempo.setText("Executor Service Sort Time: " + durationMicroseconds + " microsegundos\n");
                txtArregloOrdenado.setText("Arreglo ordenado:\n" + Arrays.toString(arregloOrdenado) + "\n");
            }
        });

        // Configura JTextAreas para ajustar automáticamente su tamaño al contenido
        txtArregloOriginal.setLineWrap(true);
        txtArregloOriginal.setWrapStyleWord(true);
        txtArregloOrdenado.setLineWrap(true);
        txtArregloOrdenado.setWrapStyleWord(true);
    }

    // Función para generar un arreglo aleatorio de tamaño dado
    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(100); // Genera números aleatorios entre 0 y 99
        }
        return array;
    }

    // Función para ordenar un arreglo utilizando el algoritmo Merge Sort de manera secuencial
    public static void mergeSort(int[] array) {
        if (array.length <= 1) {
            return;
        }
        int mid = array.length / 2;
        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);
        mergeSort(left);
        mergeSort(right);
        merge(left, right, array);
    }

    // Función auxiliar para fusionar dos arreglos ordenados en uno solo
    public static void merge(int[] left, int[] right, int[] result) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] < right[j]) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }
        while (i < left.length) {
            result[k++] = left[i++];
        }
        while (j < right.length) {
            result[k++] = right[j++];
        }
    }

    // Clase interna que representa una tarea de Merge Sort para Fork/Join
    static class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int start;
        private final int end;

        public MergeSortTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (start < end) {
                int mid = (start + end) / 2;
                MergeSortTask leftTask = new MergeSortTask(array, start, mid);
                MergeSortTask rightTask = new MergeSortTask(array, mid + 1, end);
                invokeAll(leftTask, rightTask);
                merge(array, start, mid, end);
            }
        }

        private void merge(int[] array, int start, int mid, int end) {
            int[] temp = new int[end - start + 1];
            int i = start, j = mid + 1, k = 0;

            while (i <= mid && j <= end) {
                if (array[i] < array[j]) {
                    temp[k++] = array[i++];
                } else {
                    temp[k++] = array[j++];
                }
            }

            while (i <= mid) {
                temp[k++] = array[i++];
            }

            while (j <= end) {
                temp[k++] = array[j++];
            }

            for (i = start; i <= end; i++) {
                array[i] = temp[i - start];
            }
        }
    }

    // Método principal para iniciar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Practica().setVisible(true);
            }
        });
    }
}

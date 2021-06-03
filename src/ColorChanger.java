import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ColorChanger {
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Integer> list;
    private ArrayList<Integer> newList = new ArrayList<>();
    private final int NUMBER_THREADS = 3;
    private int blue;
    private int green;
    private int red;
    Path pic = Paths.get(".\\src\\pic.bmp");
    Path newPic = Paths.get(".\\src\\newPic.bmp");

    public void multiThreadingChangeColor() {
        ArrayList<Thread> threadArrayList = new ArrayList<>();
        getInfo();
        int[] rgb = new int[]{blue, green, red};
        long start = System.currentTimeMillis();
        System.out.println("Start file's size: " + pic.toFile().length());
        //массив из потоков загрузки
        LoadPicThread[] loadPicThreads = new LoadPicThread[NUMBER_THREADS];
        for (int i = 0; i < NUMBER_THREADS; i++) {
            loadPicThreads[i] = new LoadPicThread(pic, String.valueOf(NUMBER_THREADS) + (i + 1));
        }
        // массив из потоков измения картинки
        ChangePicThread[] changePicThreads = new ChangePicThread[NUMBER_THREADS];
        for (int i = 0; i < NUMBER_THREADS; i++) {
            changePicThreads[i] = new ChangePicThread(rgb[i], i + 1);
        }
        //запуск потоков загрузки
        for (LoadPicThread picThread : loadPicThreads) {
            Thread thread = new Thread(picThread);
            threadArrayList.add(thread);
            thread.start();
        }
        for (Thread thread : threadArrayList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        //добавление файла в коллекцию
        list = getList(loadPicThreads);
        threadArrayList.clear();
        //запуск потоков изменений
        for (ChangePicThread changePicThread : changePicThreads) {
            changePicThread.setList(list);
            Thread thread = new Thread(changePicThread);
            threadArrayList.add(thread);
            thread.start();
        }
        for (Thread thread : threadArrayList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 54; i++) {
            newList.add(list.get(i));
        }
        //объединение массивов из потоков изменений
        newList.addAll(unitedThreadsArrays(changePicThreads));

        long finish = System.currentTimeMillis();
        System.out.println("Time - " + (finish - start) + " ms");
        //запись в файл
        writeToFile();
        System.out.println("New file's size: " + newPic.toFile().length());
        System.out.println("Colors changed successful");
    }

    private ArrayList<Integer> getList(LoadPicThread[] loadPicThread) {
        ArrayList<Integer> result = new ArrayList<>();
        for (LoadPicThread picThread : loadPicThread) {
            result.addAll(picThread.getList());
        }
        return result;
    }

    public ArrayList<Integer> unitedThreadsArrays(ChangePicThread[] changePicThreads){
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < changePicThreads[0].getList().size(); i++) {
            result.add(changePicThreads[0].getList().get(i));
            if (i > changePicThreads[1].getList().size() - 1)
                continue;
            result.add(changePicThreads[1].getList().get(i));
            if (i > changePicThreads[2].getList().size() - 1)
                continue;
            result.add(changePicThreads[2].getList().get(i));
        }
        return result;
    }

    public void writeToFile() {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newPic.toString()))) {
            System.out.println("write to file...");
            byte[] bytes = toByteArray(newList);
            outputStream.write(bytes, 0, bytes.length);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] toByteArray(ArrayList<Integer> list){
        byte[] bytes = new byte[list.size()];
        int[] tempArray = list.stream().mapToInt(i->i).toArray();
        for (int i = 0; i < tempArray.length; i++) {
            bytes[i] = (byte) tempArray[i];
        }
        return bytes;
    }

    public void getInfo() {
        System.out.println("Enter limit BLUE color (until 255):");
        while (true) {
            System.out.print("Input: ");
            blue = scanner.nextInt();
            if (0 < blue && blue < 256) {
                break;
            }
        }
        System.out.println("Enter limit GREEN color (until 255):");
        while (true) {
            System.out.print("Input: ");
            green = scanner.nextInt();
            if (0 < green && green < 256) {
                break;
            }
        }
        System.out.println("Enter limit RED color (until 255):");
        while (true) {
            System.out.print("Input: ");
            red = scanner.nextInt();
            if (0 < red && red < 256) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        ColorChanger colorChanger = new ColorChanger();
        colorChanger.multiThreadingChangeColor();
    }
}

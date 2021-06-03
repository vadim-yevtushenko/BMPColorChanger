import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;

public class LoadPicThread implements Runnable{

    private Path pic;
    private ArrayList<Integer> list;
    private String info;

    public LoadPicThread(Path pic, String info) {
        this.pic = pic;
        list = new ArrayList<>();
        this.info = info;
    }

    @Override
    public void run() {
        System.out.println(info+" - load thread start");
        ArrayList<Integer> twoValue = takePartByteOfFile();
        int seek = twoValue.get(0);
        int length = twoValue.get(1);
        try (RandomAccessFile raf = new RandomAccessFile(pic.toString(), "r")){
            raf.seek(seek);
            byte[] arr = new byte[length];
            raf.read(arr, 0, length);

            list = transformToList(arr);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(info+" - load thread finish");
    }

    private ArrayList<Integer> transformToList(byte[] arr) {
        ArrayList<Integer> result = new ArrayList<>();
        for (byte b : arr) {
            int i = Byte.toUnsignedInt(b);
            result.add(i);
        }
        return result;
    }

    private ArrayList<Integer> takePartByteOfFile(){

        ArrayList<Integer> twoValue = new ArrayList<>();
        String[] strings = info.split("");
        int parts = Integer.parseInt(strings[0]);
        int number = Integer.parseInt(strings[1]);
        int quantity = (int) pic.toFile().length() / parts;
        int seek = 0;
        int length = 0;
        for (int i = 0; i < number; i++) {
            seek = i * quantity;
            if (i == parts - 1){
                length = (int) (pic.toFile().length() - seek);
            }else
                length = quantity;
        }
        twoValue.add(seek);
        twoValue.add(length);
        return twoValue;
    }

    public ArrayList<Integer> getList() {
        return list;
    }
}

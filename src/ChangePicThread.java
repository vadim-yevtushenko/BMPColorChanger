import java.util.ArrayList;

public class ChangePicThread implements Runnable {
    private int limit;
    private ArrayList<Integer> list;
    private int info;

    public ChangePicThread(int limit, int info) {
        this.limit = limit;
        this.info = info;
    }

    @Override
    public void run() {
        ArrayList<Integer> result = takePartByteOfArray();
        System.out.println(info + " - change thread start ");
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) > limit) {
                result.set(i, limit);
            }
        }
        list = result;
        System.out.println(info + " - change thread finish ");
    }

    private ArrayList<Integer> takePartByteOfArray() {
        ArrayList<Integer> result = new ArrayList<>();
        int startIndex = 0;
        startIndex = 54 + (info - 1);
        for (int i = startIndex; i < list.size(); i += 3) {
            result.add(list.get(i));
        }
        return result;
    }

    public ArrayList<Integer> getList() {
        return list;
    }

    public void setList(ArrayList<Integer> list) {
        this.list = list;
    }
}
package ariafx.core.url.type;

import ariafx.opt.R;
import ariafx.opt.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.time.LocalDate;

public class Queue extends Type {

    public static final Queue Default = new Queue("Default");
    public static final Queue Main = new Queue("Main");
    public static final Queue Second = new Queue("Second");

    private static final ObservableList<Queue> queues = FXCollections
            .observableArrayList(Default, Main, Second);

    public static ObservableList<Queue> newQueues = FXCollections.observableArrayList();

    private int parallel;
    private LocalDate start;
    private LocalDate stop;
    private boolean closeAPP;
    private boolean shutDownOS;

    public Queue() {
        id = 2;
    }

    public Queue(String queue) {
        this();
        name = queue;
    }

    public Queue(String queue, int parallel, boolean closeAPP, boolean shutDownOS) {
        this();
        this.name = queue;
        this.parallel = parallel;
        this.closeAPP = closeAPP;
        this.shutDownOS = shutDownOS;
    }

    public static boolean add(Queue e) {
        return newQueues.add(e);
    }

    public static ObservableList<Queue> getQueues() {
        ObservableList<Queue> q = FXCollections.observableArrayList();
        for (Queue queue : queues) {
            q.add(queue);
        }
        for (Queue queue : newQueues) {
            q.add(queue);
        }
        return q;
    }

    public static Queue getQueue(String readUTF) {
        for (Queue q : getQueues()) {
            if (readUTF.equals(q.name)) {
                return q;
            }
        }
        return Main;
    }

    public static Queue[] getQueuesArray() {
        Queue[] qu = new Queue[queues.size()];
        for (int i = 0; i < qu.length; i++) {
            qu[i] = queues.get(i);
        }
        return qu;
    }

    public static Queue[] getNewQueues() {
        Queue[] list = new Queue[newQueues.size()];
        return newQueues.toArray(list);
    }

    public static Queue get(int index) {
        return queues.get(index);
    }

    public static ObservableList<Queue> getQueuesTree() {
        ObservableList<Queue> ll = FXCollections.observableArrayList();
        for (Queue queue : queues) {
            if (!queue.equals(Queue.Default))
                ll.add(queue);
        }
        for (Queue queue : newQueues) {
            ll.add(queue);
        }
        return ll;
    }

    /**
     * save only the new categories
     *
     * @return void
     */

    public static void saveNewQueues() {
        for (Queue queue : newQueues) {
            queue.toJson();
        }
    }

    public static Queue fromJson(String file) {
        return Utils.fromJson(file, Queue.class);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String str) {
        name = str;
    }

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int p) {
        parallel = p;
    }

    public LocalDate getStartDate() {
        return start;
    }

    public void setStartDate(LocalDate date) {
        start = date;
    }

    public LocalDate getStopDate() {
        return stop;
    }

    public void setStopDate(LocalDate date) {
        stop = date;
    }

    public boolean isCloseAPPAfterDownload() {
        return closeAPP;
    }

    public void setCloseAPPA(boolean bool) {
        closeAPP = bool;
    }

/**------------------------------SAVE OPT-----------------------------------**/

    public boolean isShutDownOSAfterDownload() {
        return shutDownOS;
    }

    public void setShutDownOS(boolean bool) {
        shutDownOS = bool;
    }

    public void toJson() {
        toJson(R.OptQueuesDir + File.separator + name + ".json");
    }

    public void toJson(String file) {
        Utils.toJsonFile(file, this);
    }


}

package concurrent;

public class NotifyTest {
    //wait(),notify(),notifyall()操作必须要获取该对象的控制权
    //线程获得对象控制权的方式为使用同步的方式
    private String[] flag = {"true"};

    class NotifyThread extends Thread {
        public NotifyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (flag) {
//                flag.notify();
                flag.notifyAll();
            }

//            try {
//                sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            synchronized (flag){
//                flag.notify();
//            }
//
//            try{
//                sleep(3000);
//            } catch (InterruptedException e){
//                e.printStackTrace();
//            }
//
//            synchronized (flag){
//                flag.notify();
//            }
        }
    }

    class WaitThread extends Thread {
        public WaitThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            synchronized (flag) {
                System.out.println(getName() + " begin waiting");
                long startTime = System.currentTimeMillis();
                try {
                    flag.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long waitTime = System.currentTimeMillis() - startTime;
                System.out.println(getName() + " has waited " + waitTime);

                System.out.println(getName() + " end waiting");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Main Thread run");
        NotifyTest notifyTest = new NotifyTest();
        NotifyThread notifyThread = notifyTest.new NotifyThread("Notify01");
        WaitThread waitThread1 = notifyTest.new WaitThread("Wait01");
        WaitThread waitThread2 = notifyTest.new WaitThread("Wait02");
        WaitThread waitThread3 = notifyTest.new WaitThread("Wait03");
        notifyThread.start();
        waitThread1.start();
        waitThread2.start();
        waitThread3.start();
//        try {
//            notifyThread.join();
//            waitThread1.join();
//            waitThread2.join();
//            waitThread3.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}

package controller;

/**
 * Created by Administrator on 2018/5/11.
 */
public class Stations extends Thread{

    public static void main(String[] args) {
        //线程使用示例一：
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        System.out.println("线程输出");

                        //休眠两秒
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }



}

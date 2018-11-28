package concurrent;

import java.awt.*;

public class TestConcurrent {

    public TestConcurrent(EventSource eventSource){//this引用在构造函数中溢出，会造成对当前对象的不安全访问
        eventSource.registerListener(new EventListener() {
            public void onEvent(Event e) {
                //do something
                testMethod();//this引用溢出到别的对象中
            }
        });
    }

    public void escapeMethod(EventSource eventSource){//this引用在实例方法中溢出，不会造成对当前对象的不安全访问
        eventSource.registerListener(new EventListener() {
            public void onEvent(Event e) {
                //do something
                testMethod();//this引用溢出到别的对象中
            }
        });
    }

    public void testMethod(){
        System.out.println("test");
    }

    public static void main(String[] args) {
        
    }
}

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ThreadTest {

    @Test
    @DisplayName("스레드 인터럽트가 발생하더라도 주어진 작업을 완료한다. ")
    void ThreadInterruptedTest() throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("인터럽트 요청 발생");
                }
                System.out.println("스레드 시작 -> " + i + "번째");
            }
        });
        thread.start();
        for (int i=0; i<5; i++) {
            Thread.sleep(100);
            thread.interrupt();
        }
        thread.join();
    }
}

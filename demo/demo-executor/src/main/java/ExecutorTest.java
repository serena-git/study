import java.util.concurrent.*;

public class ExecutorTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Double> future = executorService.submit(new Callable<Double>() {
            public Double call() throws InterruptedException {
                Thread.sleep(2000);
                return 1d;
            }
        });
        System.out.println("do Something");
        Double result = 0.0;
        try {
            result = future.get(1, TimeUnit.SECONDS); // <--- 블록 방지
            System.out.println(result);
        } catch (InterruptedException e) {
            System.out.println("interrupte");
            //현재 스레드에서 대기중 인터럽트 발생
        } catch (ExecutionException e) {
            System.out.println("execution");
            //계산중 예외
        } catch (TimeoutException e) {
            System.out.println("timeout");
            System.out.println(result); //시간이 지나면 기본값 출력
            //완료되기전 타임아웃
        }
    }
}

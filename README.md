## Java의 Interrupt

보통 Interrupt 개념과 다르게, 자바에서 특정 쓰레드에 Interrupt를 요청을 할 경우,

해당 스레드의 작업을 지금 당장 중단하는 것을 보장하진 않는다. 즉 쓰레드의 작업을 강제할 수 없다는 말이기도 하다.

만약 쓰레드를 강제로 중지하게 된다면, 이는 쓰레드가 작업중인 것들을 마무리 하는 시간을 갖지 못한다는 뜻이기도 하다.

데이터베이스 락을 획득한 상태인데 Interrupt되었다고 락을 반환하지 않고 작업이 중단된다면?

동시성에 필요한 로직에 다른 스레드들이 접근하지 못하게 됨에 따라 큰 장애로 이어질 수 있다.

<br/>

## isInterrupted()와 Interrupted() 메서드의 차이
```text
- isInterrupted()는 interrupt 발생 여부 flag만 확인하는 용도

- Interrupted() 메서드는 interrupt 발생 여부를 확인하고 인터럽트 상태를 false로 reset 까지 해준다.
```


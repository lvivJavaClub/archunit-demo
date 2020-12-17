package ua.lvivjavaclub.archunitdemo.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class HelloRepository {
  final AtomicInteger atomicInt = new AtomicInteger(0);

  public int incrementAndGet() {
    return atomicInt.incrementAndGet();
  }

  public int getCount() {
    return atomicInt.get();
  }
}

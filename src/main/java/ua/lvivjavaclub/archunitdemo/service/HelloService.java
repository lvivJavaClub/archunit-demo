package ua.lvivjavaclub.archunitdemo.service;

import lombok.RequiredArgsConstructor;
import ua.lvivjavaclub.archunitdemo.CustomAnnotation;
import ua.lvivjavaclub.archunitdemo.repository.HelloRepository;

import org.springframework.stereotype.Service;

@Service
@CustomAnnotation(value = "test")
@RequiredArgsConstructor
public class HelloService {

  private final HelloRepository helloRepository;

  public String hello() {
    System.out.println("Hello! ");
    return "hello #" + helloRepository.incrementAndGet();
  }

  public int getCount() {
    return helloRepository.getCount();
  }
}

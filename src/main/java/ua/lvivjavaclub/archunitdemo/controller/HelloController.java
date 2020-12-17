package ua.lvivjavaclub.archunitdemo.controller;

import lombok.RequiredArgsConstructor;
import ua.lvivjavaclub.archunitdemo.repository.HelloRepository;
import ua.lvivjavaclub.archunitdemo.service.HelloService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HelloController {
  private final HelloService helloService;
  
  private final HelloRepository helloRepository;
  
  @GetMapping
  @ResponseBody
  public String hello() {
    return helloService.hello();
  }

  @GetMapping("/count")
  @ResponseBody
  public int count() {
    return helloRepository.getCount();
  }
}

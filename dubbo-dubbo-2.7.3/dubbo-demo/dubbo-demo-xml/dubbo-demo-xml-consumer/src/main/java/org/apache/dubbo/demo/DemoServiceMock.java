package org.apache.dubbo.demo;

public class DemoServiceMock implements DemoService {
    @Override
    public String sayHello(String name) {
        return "mock result ============== " + name;
    }
}

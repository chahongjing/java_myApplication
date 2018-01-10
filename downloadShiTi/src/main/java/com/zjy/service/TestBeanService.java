package com.zjy.service;

import com.zjy.entity.Ibm_Major;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestBeanService {
    public void getBeans() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "beans.xml");

        Ibm_Major major = (Ibm_Major) context.getBean("major");
        System.out.println("majorcode:" + major.getMajorCode());
    }
}

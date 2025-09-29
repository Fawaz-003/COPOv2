package com.copo.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ListAllBeans implements CommandLineRunner {

	@Autowired
	private ApplicationContext appContext;

	@Override
	public void run(String... args) throws Exception {
		
		for(String beanName : appContext.getBeanDefinitionNames())
		{
			System.out.println(beanName);
		}
		
	}
	
	

}

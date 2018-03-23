package com.nm.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class SampleService implements JavaDelegate {

	  public void execute(DelegateExecution execution) {
		 System.out.println("delegate task called");
	  }

	}
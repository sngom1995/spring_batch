package com.samba.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Befor Job:"+stepExecution);
		System.out.println("Job parameters:"+stepExecution);
		System.out.println("Job Execution Ctx:"+stepExecution);
		
		stepExecution.getExecutionContext().put("ctx", "context value");

		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

}

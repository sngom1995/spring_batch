package com.samba.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Befor Step :"+stepExecution.getStepName());
		System.out.println("Step name :"+stepExecution.getStepName());
		System.out.println("Step Execution Ctx:"+stepExecution.getJobExecution().getStepExecutions());
		
		stepExecution.getExecutionContext().put("ctx", "context value");

		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("After Step:"+stepExecution.getStepName());
		System.out.println("Step name :"+stepExecution.getStepName());
		System.out.println("Job Execution Ctx:"+stepExecution.getJobExecution().getStepExecutions());
		return ExitStatus.COMPLETED;
	}

}

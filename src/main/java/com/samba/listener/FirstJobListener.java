package com.samba.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobListener implements JobExecutionListener{

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Befor Job:"+jobExecution.getJobInstance().getJobName());
		System.out.println("Job parameters:"+jobExecution.getJobParameters());
		System.out.println("Job Execution Ctx:"+jobExecution.getExecutionContext());
		
		jobExecution.getExecutionContext().put("ctx", "context value");

	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("After Job:"+jobExecution.getJobInstance().getJobName());
		System.out.println("Job parameters:"+jobExecution.getJobParameters());
		System.out.println("Job Execution Ctx:"+jobExecution.getExecutionContext());

		
	}

}

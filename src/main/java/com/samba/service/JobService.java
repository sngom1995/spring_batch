package com.samba.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.samba.request.JobParamRequest;

@Service
public class JobService {
	@Autowired
	JobLauncher jobLauncher;
	
	
	@Qualifier("firstJob")
	@Autowired
	Job firstJob;
	
	@Qualifier("secondJob")
	@Autowired
	Job secondJob;
	
	
	
	@Async
	public void startJob(String jobName, List<JobParamRequest> jobParamRequestList) {
		Map<String, JobParameter> params = new HashMap<>();
		params.put("Current time", new JobParameter(System.currentTimeMillis()));
		
		jobParamRequestList.stream()
			.forEach(jobParam -> {
				params.put(jobParam.getParamKey(), new JobParameter(jobParam.getParamValue()));
			});
		JobParameters jobParameters = new JobParameters(params);

		try {
			JobExecution jobExecution = null;

			if(jobName.equals("First Job")) {
				jobExecution = jobLauncher.run(firstJob,  jobParameters);	
				}
			else if(jobName.equals("Second Job")) {
				jobExecution  =	jobLauncher.run(secondJob, (JobParameters) jobParameters);	
				}
			System.out.println("Job Execution ID: "+jobExecution.getId());
		}
		catch(Exception e) {
			System.out.println("Exception while starting the job"+e.getMessage());
		}
		
	}

}

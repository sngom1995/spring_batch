package com.samba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samba.request.JobParamRequest;
import com.samba.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
	
	@Autowired
	public JobService jobService;
	
	@GetMapping("/start/{jobName}")
	public String startJob(
			@PathVariable String jobName,
			@RequestBody List<JobParamRequest> jobParamRequestList
			) throws Exception {
		jobService.startJob(jobName,jobParamRequestList) ;
		
		return "Job started!";
	}

	@GetMapping("/stop")
	public String stopJob() {
		return "Job stopped...";
	}
}

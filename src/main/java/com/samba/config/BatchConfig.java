package com.samba.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.samba.listener.FirstJobListener;
import com.samba.service.SecondTasklet;

@Configuration
public class BatchConfig {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private SecondTasklet secondTask;
	
	@Autowired
	private FirstJobListener firstJobListener;
	
	@Bean 
	public Job firstJob() {
		return jobBuilderFactory.get("First Job")
				.incrementer(new RunIdIncrementer())
				.start(firstStep())
				.next(secondStep())
				.listener(firstJobListener)
				.build();
	}

	private Step firstStep() {
	return	stepBuilderFactory.get("first Step")
		.tasklet(firstTaslet())
		.build();
	}
	
	private Tasklet firstTaslet() {
		return new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Run first step");
				return RepeatStatus.FINISHED;
			}
			
		};
	}
	private Step secondStep() {
		return	stepBuilderFactory.get("Second Step")
			.tasklet(secondTask)
			.build();
		}
	
	/*private Tasklet secondTaslet() {
		return new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Run fsecond step");
				return RepeatStatus.FINISHED;
			}
			
		};
	}*/
}

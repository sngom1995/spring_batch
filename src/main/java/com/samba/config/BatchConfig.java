package com.samba.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;

import com.samba.listener.FirstJobListener;
import com.samba.listener.FirstStepListener;
import com.samba.model.StudentCSV;
import com.samba.processor.FirstItemProcessor;
import com.samba.reader.FirstItemReader;
import com.samba.service.SecondTasklet;
import com.samba.writer.FirstItemWriter;

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
	
	@Autowired
	private FirstStepListener firstStepListener;
	
	@Autowired
	private FirstItemReader firstItemReader;
	
	@Autowired
	private FirstItemProcessor firstItemProcessor;
	
	@Autowired
	private FirstItemWriter firstItemWriter;
	
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
		.listener(firstStepListener)
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
	
	@Bean 
	public Job secondJob() {
		return jobBuilderFactory.get("Second Job")
				.incrementer(new RunIdIncrementer())
				.start(firstChunkStep())
				.next(secondStep())
				.build();
				
	}
	
	private Step firstChunkStep() {
		return	stepBuilderFactory.get("first chunk Step")
			.<StudentCSV, StudentCSV>chunk(3)
			.reader(flatFileItemReader())
			//.processor(firstItemProcessor)
			.writer(firstItemWriter)
			.build();

	}
	
	public FlatFileItemReader<StudentCSV> flatFileItemReader(){
		FlatFileItemReader<StudentCSV> flatFileItemReader = new FlatFileItemReader<StudentCSV>();
		flatFileItemReader.setResource(
				new FileSystemResource(
						new File("/Users/badou/Documents/springSTS/Spring-batch-app/inputFiles/students.csv")
						)
				);
		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCSV>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {
				{
					setNames("ID","First Name", "Last Name","Email");
					setStrict(false);
					setDelimiter("|");
				}
			});
			
			setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCSV>() {
				{
					setTargetType(StudentCSV.class);
				}
			});
		}} );
		
		flatFileItemReader.setLinesToSkip(1);
		return flatFileItemReader;
	}
}

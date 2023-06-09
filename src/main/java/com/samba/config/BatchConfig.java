package com.samba.config;

import java.io.File;
import java.util.Date;
import java.io.IOException;
import java.io.Writer;

import com.samba.model.StudentJdbc;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;

import com.samba.listener.FirstJobListener;
import com.samba.listener.FirstStepListener;
import com.samba.model.StudentCSV;
import com.samba.model.StudentJSON;
import com.samba.processor.FirstItemProcessor;
import com.samba.reader.FirstItemReader;
import com.samba.service.SecondTasklet;
import com.samba.writer.FirstItemWriter;

import javax.sql.DataSource;

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

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource(){
		return DataSourceBuilder.create().build();
	};
	@Bean
	@ConfigurationProperties(prefix ="spring.universitydatasource")
	public DataSource dataSourceUniversity(){
		return DataSourceBuilder.create().build();
	};
	
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
			.<StudentJdbc, StudentJdbc>chunk(3)
			//.reader(jsonItemreader(null))
				//.reader(staxEventItemReader(null))
			//.reader(flatFileItemReader(null))
			//.processor(firstItemProcessor)
				.reader(jdbcCursorItemReader())
			//.writer(studentJdbcFileWriter(null))
				.writer(jsonFlatFileItemWriter(null))
			.build();

	}
	
	@StepScope
	@Bean
	public FlatFileItemReader<StudentCSV> flatFileItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource  fileName
			){
		FlatFileItemReader<StudentCSV> flatFileItemReader = new FlatFileItemReader<StudentCSV>();
		flatFileItemReader.setResource(fileName);
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
	
	@StepScope
	@Bean
	public JsonItemReader<StudentJSON> jsonItemreader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource  fileName
			){
		JsonItemReader<StudentJSON> jsonItemReader = new JsonItemReader<StudentJSON>();
		jsonItemReader.setResource(fileName);
		jsonItemReader.setJsonObjectReader(new JacksonJsonObjectReader<StudentJSON>(StudentJSON.class));
		return jsonItemReader;
	}
	

	@Bean
	public JdbcCursorItemReader<StudentJdbc>	 jdbcCursorItemReader(){
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader
				= new JdbcCursorItemReader<StudentJdbc>();
		jdbcCursorItemReader.setDataSource(dataSourceUniversity());
		jdbcCursorItemReader.setSql("select * from student");
		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>(){
			{
				setMappedClass(StudentJdbc.class);
			}
		});
		return jdbcCursorItemReader;
	}

	@StepScope
	@Bean
	public FlatFileItemWriter<StudentJdbc>	studentJdbcFileWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource  fileName){
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter = new FlatFileItemWriter<StudentJdbc>();
		flatFileItemWriter.setResource(fileName);
		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {

			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("Id,First Name, Last Name, Email");
			}
			
		});
		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {
			{
				setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
					{
						setNames(new String[] {"id","firstName","lastName","email"});
					}
				});
			}
		});
		flatFileItemWriter.setFooterCallback(
				new FlatFileFooterCallback() {

					@Override
					public void writeFooter(Writer writer) throws IOException {
						writer.write("Created @"+new Date());
					}
					
				}
				);
		return flatFileItemWriter;
	}
	
	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJdbc> jsonFlatFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource  fileName){
		
		JsonFileItemWriter<StudentJdbc> jsonItemWriter = 
				new JsonFileItemWriter<StudentJdbc>(
						fileName,
						new JacksonJsonObjectMarshaller<StudentJdbc>());
		return jsonItemWriter;
	}
}

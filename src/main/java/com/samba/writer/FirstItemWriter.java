package com.samba.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.samba.model.StudentJSON;

@Component
public class FirstItemWriter  implements ItemWriter<StudentJSON>{

	@Override
	public void write(List<? extends StudentJSON> items) throws Exception {
		System.out.println("Inside Flat File Reader");
		items.stream().forEach(student ->{
			if(student.getId()!=null) {
				System.out.println(student);
			}
		});
	}

	

}

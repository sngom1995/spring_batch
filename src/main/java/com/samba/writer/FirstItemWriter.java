package com.samba.writer;

import java.util.List;

import com.samba.model.StudentJdbc;
import com.samba.model.StudentXML;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.samba.model.StudentJSON;

@Component
public class FirstItemWriter  implements ItemWriter<StudentJdbc>{

	@Override
	public void write(List<? extends StudentJdbc> items) throws Exception {
		System.out.println("Inside Flat File Reader");
		items.stream().forEach(student ->{
			if(student.getId()!=null) {
				System.out.println(student);
			}
		});
	}

	

}

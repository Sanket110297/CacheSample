package com.test.domain;

import lombok.Data;

@Data
public class Student {

	private String id;
	private String name;
	private String clz;

	public Student(String id, String name, String clz) {
		super();
		this.id = id;
		this.name = name;
		this.clz = clz;
	}
}
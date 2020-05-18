package io.cronox.delta.models;

import java.util.List;

import lombok.Data;

@Data
public class TestSuit {
	
	private String id;
	
	private List<TestCase> tests;
}

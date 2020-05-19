package io.cronox.delta.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "connections")
@XmlAccessorType (XmlAccessType.FIELD)
public class Connections<T> {
	
	@XmlElement(name = "connection")
	List<T> connections = new ArrayList<T>();
}

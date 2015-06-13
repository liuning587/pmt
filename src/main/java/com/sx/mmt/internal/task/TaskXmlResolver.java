package com.sx.mmt.internal.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.util.ErrorTool;


@Component
public final class TaskXmlResolver {
	private Map<String,TaskConfig> tasks;
	private XMLOutputter out = null;
	private SAXBuilder builder=null;
	private final String path=System.getProperty("user.dir")+"/config/task.xml";
	private static Logger logger = LoggerFactory.getLogger(TaskXmlResolver.class); 
	
	public TaskXmlResolver(){
		out=new XMLOutputter();
		builder=new SAXBuilder();
	}
	
	@PostConstruct
	public void load(){
		tasks=new LinkedHashMap<String,TaskConfig>();
		Document doc=getDocument();
		Element rootEl = doc.getRootElement();
		List<Element> tasklist = rootEl.getChildren();
		for (Element el : tasklist){
			TaskConfig tc=new TaskConfig();
			tc.setName(el.getAttributeValue(ConfigConstants.Name));
			tc.setProtocolArea(el.getChildText(ConfigConstants.ProtocolArea));
			tc.setProtocolType(el.getChildText(ConfigConstants.ProtocolType));
			Element commands=el.getChild(ConfigConstants.Commands);
			if(commands!=null){
				List<String> com=new ArrayList<String>();
				for(Element ele:commands.getChildren()){
					com.add(ele.getText());
				}
				tc.setCommands(com);
			}
			tasks.put(el.getAttributeValue(ConfigConstants.Name), tc);
		}
	}

	public synchronized void add(String name,TaskConfig task){
		tasks.put(name, task);
		Document doc=getDocument();
		Element rootEl = doc.getRootElement();
		Element taskEl=new Element(ConfigConstants.Task);
		taskEl.setAttribute(ConfigConstants.Name,name);
		taskEl.addContent(new Element(ConfigConstants.ProtocolArea)
					.setText(task.getProtocolArea()));
		taskEl.addContent(new Element(ConfigConstants.ProtocolType)
					.setText(task.getProtocolType()));
		Element commands=new Element(ConfigConstants.Commands);
		if(task.getCommands()!=null){
			for(String s:task.getCommands()){
				Element command=new Element(ConfigConstants.Command);
				command.setText(s);
				commands.addContent(command);
			}
			taskEl.addContent(commands);
		}	
		rootEl.addContent(taskEl);
		print(doc);
	}
	
	public synchronized void delete(String name){
		tasks.remove(name);
		Document doc=getDocument();
		List<Element> commandlist = doc.getRootElement().getChildren();
		Element deleteElement=null;
		for (Element el : commandlist){
			String commandName=el.getAttribute(ConfigConstants.Name).getValue();
			if(commandName.equals(name)){
				deleteElement=el;
			}
		}
		if(deleteElement!=null){
			deleteElement.getParentElement().removeContent(deleteElement);
		}
		print(doc);
	}
	
	public synchronized void modify(String name,TaskConfig task){
		delete(name);
		add(name,task);
	}
	
	private Document getDocument(){
		Document doc=null;
		try {
			doc = builder.build(new File(path));
		} catch (JDOMException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return doc;
	}
	
	private void print(Document doc){
		try {
			out.output(doc,new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		}
		out.clone();
	}
	
	
	public Map<String, TaskConfig> getTasks() {
		return tasks;
	}
}

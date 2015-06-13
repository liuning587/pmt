package com.sx.mmt.internal.task.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public final class CommandXmlResolver {
	private Map<String,CommandConfig> commands;
	private XMLOutputter out = null;
	private SAXBuilder builder=null;
	private final String path=System.getProperty("user.dir")+"/config/command.xml";
	private static Logger logger = LoggerFactory.getLogger(CommandXmlResolver.class); 
	public CommandXmlResolver(){
		out=new XMLOutputter();
		builder=new SAXBuilder();
	}
	
	@PostConstruct
	public void load(){
		commands=new LinkedHashMap<String,CommandConfig>();
		Document doc=getDocument();
		Element rootEl = doc.getRootElement();
		List<Element> commandlist = rootEl.getChildren();
		for (Element el : commandlist){
			CommandConfig cc=new CommandConfig();
			cc.setName(el.getAttributeValue(ConfigConstants.Name));
			cc.setClazz(el.getChildText(ConfigConstants.Clazz));
			cc.setUse(Boolean.parseBoolean(el.getChildText(ConfigConstants.IsUse)));
			Map<String,String> ccmap=new HashMap<String,String>();
			Element attr=el.getChild(ConfigConstants.Attr);
			if(attr!=null){
				for(Element ele:attr.getChildren()){
					ccmap.put(ele.getAttributeValue(ConfigConstants.Name), ele.getText());
				}
				cc.setAttr(ccmap);
			}
			commands.put(cc.getName(), cc);
		}		
	}
	
	public synchronized void add(CommandConfig command){
		commands.put(command.getName(), command);
		Document doc=getDocument();
		Element rootEl = doc.getRootElement();
		Element commandEl=new Element(ConfigConstants.Command);
		commandEl.setAttribute(ConfigConstants.Name,command.getName());
		commandEl.addContent(new Element(ConfigConstants.Clazz).setText(command.getClazz()));
		commandEl.addContent(new Element(ConfigConstants.IsUse).setText(Boolean.toString(command.isUse())));
		Element attrEl=new Element(ConfigConstants.Attr);
		if(command.getAttr()!=null){
			for(Entry<String,String> entry:command.getAttr().entrySet()){
				Element key=new Element(ConfigConstants.Key);
				key.setAttribute(ConfigConstants.Name,entry.getKey());
				key.setText(entry.getValue());
				attrEl.addContent(key);
			}
			commandEl.addContent(attrEl);
		}
		rootEl.addContent(commandEl);
		print(doc);
		
	}
	
	public synchronized void delete(CommandConfig command){
		commands.remove(command.getName());
		Document doc=getDocument();
		List<Element> commandlist = doc.getRootElement().getChildren();
		Element deleteElement=null;
		for (Element el : commandlist){
			String commandName=el.getAttribute(ConfigConstants.Name).getValue();
			if(commandName.equals(command.getName())){
				deleteElement=el;
			}
		}
		if(deleteElement!=null){
			deleteElement.getParentElement().removeContent(deleteElement);
		}
		print(doc);
	}
	
	public synchronized void modify(CommandConfig command){
		delete(command);
		add(command);
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
	
	public CommandConfig get(String name){
		return commands.get(name);
	}

	public Map<String, CommandConfig> getCommands() {
		return commands;
	}
	
}

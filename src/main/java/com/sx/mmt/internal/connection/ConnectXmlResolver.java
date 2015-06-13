package com.sx.mmt.internal.connection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
public final class ConnectXmlResolver {
	private Map<String,ConnectConfig> connects;
	private XMLOutputter out = null;
	private SAXBuilder builder=null;
	private final String path=System.getProperty("user.dir")+"/config/connect.xml";
	private static Logger logger = LoggerFactory.getLogger(ConnectXmlResolver.class);
	public ConnectXmlResolver(){
		out=new XMLOutputter();
		builder=new SAXBuilder();
	}
	
	@PostConstruct
	public void load(){
		connects=new ConcurrentHashMap<String, ConnectConfig>();
		Document doc=getDocument();
		Element rootEl = doc.getRootElement();
		List<Element> connectlist = rootEl.getChildren();
		for (Element el : connectlist){
			ConnectConfig cc=new ConnectConfig();
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
			connects.put(cc.getName(), cc);
		}		
	}
	
	public ConnectConfig getInUseConfig(){
		for(ConnectConfig config:connects.values()){
			if(config.isUse()){
				return config;
			}
		}
		return null;
	}
	
	public synchronized void add(ConnectConfig connect){
		connects.put(connect.getName(), connect);
		Document doc=getDocument();
		Element rootEl = doc.getRootElement();
		Element connectEl=new Element(ConfigConstants.Connect);
		connectEl.setAttribute(ConfigConstants.Name,connect.getName());
		connectEl.addContent(new Element(ConfigConstants.Clazz).setText(connect.getClazz()));
		connectEl.addContent(new Element(ConfigConstants.IsUse).setText(Boolean.toString(connect.isUse())));
		Element attrEl=new Element(ConfigConstants.Attr);
		if(connect.getAttr()!=null){
			for(Entry<String,String> entry:connect.getAttr().entrySet()){
				Element key=new Element(ConfigConstants.Key);
				key.setAttribute(ConfigConstants.Name,entry.getKey());
				key.setText(entry.getValue());
				attrEl.addContent(key);
			}
			connectEl.addContent(attrEl);
		}
		rootEl.addContent(connectEl);
		print(doc);
		
	}
	
	public synchronized void delete(ConnectConfig connect){
		connects.remove(connect.getName());
		Document doc=getDocument();
		List<Element> connectlist = doc.getRootElement().getChildren();
		Element deleteElement=null;
		for (Element el : connectlist){
			String connectName=el.getAttribute(ConfigConstants.Name).getValue();
			if(connectName.equals(connect.getName())){
				deleteElement=el;
			}
		}
		if(deleteElement!=null){
			deleteElement.getParentElement().removeContent(deleteElement);
		}
		print(doc);
	}
	
	public synchronized void modify(ConnectConfig connect){
		delete(connect);
		add(connect);
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
	
	public ConnectConfig get(String name){
		return connects.get(name);
	}

	public Map<String, ConnectConfig> getconnects() {
		return connects;
	}
}

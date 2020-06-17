package com.lsy.myorm.util;

import java.io.File;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * dom4j工具类
 *
 * @author lsy
 */
public class Dom4jUtil {

    private Dom4jUtil() {

    }

    /**
     * 通过文件的路径获取xml的document对象
     *
     * @param path 文件的路径
     * @return 返回文档对象
     */
    public static Document getXMLByFilePath(String path) {
        if (null == path) {
            return null;
        }
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 获得某文档中某元素内某属性的值和元素的文本信息
     *
     * @param document    xml文档对象
     * @param elementName 元素名
     * @param attrName    属性名
     * @return 返回一个Map集合
     */
    public static Map<String, String> Elements2Map(Document document, String elementName, String attrName) {
        List<Element> propList = document.getRootElement().elements(elementName);
        Map<String, String> propConfig = new HashMap<>();
        for (Element element : propList) {
            String key = element.attribute(attrName).getValue();
            String value = element.getTextTrim();
            propConfig.put(key, value);
        }
        return propConfig;
    }

    /**
     * 针对mapper.xml文件，获得映射信息并存到Map集合中
     *
     * @param document xml文档对象
     * @return 返回一个Map集合
     */
    public static Map<String, String> Elements2Map(Document document) {
        Element classElement = document.getRootElement().element("class");
        Map<String, String> mapping = new HashMap<>();

        Element idElement = classElement.element("id");
        String idKey = idElement.attribute("name").getValue();
        String idValue = idElement.attribute("column").getValue();
        mapping.put(idKey, idValue);

        List<Element> propElements = classElement.elements("property");
        for (Element element : propElements) {
            String propKey = element.attribute("name").getValue();
            String propValue = element.attribute("column").getValue();
            mapping.put(propKey, propValue);
        }
        return mapping;
    }

    /**
     * 针对mapper.xml文件，获得主键的映射信息并存到Map集合中
     *
     * @param document xml文档对象
     * @return 返回一个Map集合
     */
    public static Map<String, String> ElementsID2Map(Document document) {
        Element classElement = document.getRootElement().element("class");
        Map<String, String> mapping = new HashMap<>();

        Element idElement = classElement.element("id");
        String idKey = idElement.attribute("name").getValue();
        String idValue = idElement.attribute("column").getValue();
        mapping.put(idKey, idValue);

        return mapping;
    }

    /**
     * 获得某文档中某元素内某属性的值
     *
     * @param document    xml文档对象
     * @param elementName 元素名
     * @param attrName    属性名
     * @return 返回一个Set集合
     */
    public static Set<String> Elements2Set(Document document, String elementName, String attrName) {
        List<Element> mappingList = document.getRootElement().elements(elementName);
        Set<String> mappingSet = new HashSet<>();
        for (Element element : mappingList) {
            String value = element.attribute(attrName).getValue();
            mappingSet.add(value);
        }
        return mappingSet;
    }

    /**
     * 获得某文档中某元素内某属性的值
     *
     * @param document    xml文档对象
     * @param elementName 元素名
     * @param attrName    属性名
     * @return 返回一个Set集合
     */
    public static String getPropValue(Document document, String elementName, String attrName) {
        Element element = (Element) document.getRootElement().elements(elementName).get(0);
        return element.attribute(attrName).getValue();
    }
}

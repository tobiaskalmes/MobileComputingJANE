/*****************************************************************************
 * 
 * EventDB.java
 * 
 * $Id: EventDBImplementationDepth.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.service.operatingSystem.manager; 

import java.lang.reflect.Field;
import java.util.*;



import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.service.event.ServiceEvent;

import de.uni_trier.jane.util.HashMapSet;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class EventDBImplementationDepth implements EventDB {
    
    
   /**
     * @author goergen
     *
     * TODO comment class
     */
    private static class FieldElement {

        
        
        private Field field;
        private Set nullSet;
        private HashMapSet valueMap;
        private Set allIDs;
        private TreeElement treeElement;
        private int depth;
        private int MAX_DEPTH;
        
        /**
         * 
         * Constructor for class <code>FieldElement</code>
         * @param field
         * @param depth
         * @param maxDepth
         */
        public FieldElement(Field field,int depth,int maxDepth) {
            this.field=field;
            this.depth=depth;
            MAX_DEPTH=maxDepth;
            field.setAccessible(true);
            nullSet=new LinkedHashSet();
            valueMap=new HashMapSet();
            allIDs=new LinkedHashSet();
        }
    
        /**
         * TODO Comment method
         * @param object
         * @return
         */
        public Collection getTemplateIDs(Object object) {
            if (allIDs.isEmpty()) return allIDs;
            Object value=null;
            try {
                value = field.get(object);
            } catch (IllegalArgumentException exeception) {
                // TODO Auto-generated catch block
                exeception.printStackTrace();
            } catch (IllegalAccessException exeception) {
                // TODO Auto-generated catch block
                exeception.printStackTrace();
            }
            if (isNullValue(value)){
                return nullSet;//allIDs;
            }
            
            Set set=new LinkedHashSet(nullSet);
            if (value instanceof Class){
                getTemplatesForClass((Class)value,set);
                return set;
            }
            Set valueSet=valueMap.get(value);
            if (valueSet!=null&&!valueSet.isEmpty()){
                set.addAll(valueSet);
            }
            if (treeElement!=null){
                TreeElement element=treeElement.getNearestChild(value.getClass());
                set.addAll(element.getTemplatIDs(value));
            }
            //set.addAll(nullSet);
            return set;
        }
    
        /**
         * 
         * TODO Comment method
         * @param className
         * @param set
         */
        private void getTemplatesForClass(Class className, Set set) {
            if (className==null) return;
            Set valueSet=valueMap.get(className);
            if (valueSet!=null){
                set.addAll(valueSet);
            }
            getTemplatesForClass(className.getSuperclass(),set);
            Class[] classes=className.getInterfaces();
            for (int i=0;i<classes.length;i++){
                getTemplatesForClass(classes[i],set);
            }
            
        }

        /**
         * 
         * TODO Comment method
         * @param template
         * @param listenerID
         */
        public void addTemplate(Object template, ListenerID listenerID) {
            Object value=null;
            try {
                value = field.get(template);
            } catch (IllegalArgumentException exeception) {
                // TODO Auto-generated catch block
                exeception.printStackTrace();
            } catch (IllegalAccessException exeception) {
                // TODO Auto-generated catch block
                exeception.printStackTrace();
            }
            if (isNullValue(value)){
                nullSet.add(listenerID);
            }else{
                valueMap.put(value,listenerID);
                if (treeElement==null){
                    createTree();
                }
                if (treeElement!=null){
                    TreeElement element = treeElement.createChild(value.getClass());
                    element.addTemplate(value,listenerID);
                }
            }
            allIDs.add(listenerID);
            
        }
    
        /**
         * TODO Comment method
         */
        private void createTree() {
            if (!field.getType().isPrimitive()&&
                    !field.getType().equals(Class.class)&&
                    depth<MAX_DEPTH){
                  treeElement=new TreeElement(field.getType(),depth++,MAX_DEPTH);
              }
            
        }

        /**
         * 
         * TODO Comment method
         * @param template
         * @param listenerID
         */
        public void removeTemplate(Object template, ListenerID listenerID) {
            Object value=null;
            try {
                value = field.get(template);
            } catch (IllegalArgumentException exeception) {
                // TODO Auto-generated catch block
                exeception.printStackTrace();
            } catch (IllegalAccessException exeception) {
                // TODO Auto-generated catch block
                exeception.printStackTrace();
            }
            if (isNullValue(value)){
                nullSet.remove(listenerID);
            }else{
                Set set=valueMap.get(value);
                if (set!=null){
                    set.remove(listenerID);
                    if (set.isEmpty()){
                        valueMap.remove(value);
                    }
                }
                if (treeElement!=null){
                    TreeElement element = treeElement.getNearestChild(value.getClass());
                    element.removeTemplate(value,listenerID);
                }
                if (valueMap.isEmpty()){
                    treeElement=null;
                }
            }
            allIDs.remove(listenerID);
        }
        
        public String toString() {
         
            return field.toString();
        }
    }
    
    
    
    /**
     * @author goergen
     *
     * TODO comment class
     */
    private static class FieldEntries {

        private ArrayList fieldElements;
        private Set allIDs;
        
        /**
         * Constructor for class <code>FieldEntries</code>
         * @param lastSuper
         * @param depth 
         */
        public FieldEntries(Class lastSuper, int depth,int maxDepth) {
            fieldElements=new ArrayList();
            while (lastSuper!=null){
                Field[] fields=lastSuper.getDeclaredFields();
                for (int i=0;i<fields.length;i++){
                    fieldElements.add(new FieldElement(fields[i],depth,maxDepth));
                }
                
                lastSuper=lastSuper.getSuperclass();
            }
            allIDs=new LinkedHashSet();
        }
        
        /**
         * 
         * TODO Comment method
         * @param object
         * @return
         */
        public Set getTemplateIDs(Object object){
            if (allIDs.isEmpty()) return new LinkedHashSet();
            Iterator iterator=fieldElements.iterator();
            Set returnSet=null;
            if (iterator.hasNext()){
                FieldElement element = (FieldElement) iterator.next();
                returnSet=new LinkedHashSet(element.getTemplateIDs(object));    
            }
            while (iterator.hasNext()) {
                FieldElement element = (FieldElement) iterator.next();
                returnSet.retainAll(element.getTemplateIDs(object));
                
            }
             if (returnSet!=null){
                 return returnSet;
             }
             return allIDs;// no fields in this class...
        }
        
        /**
         * 
         * TODO Comment method
         * @param object
         * @param listenerID
         */
        public void addTemplate(Object object, ListenerID listenerID){
            Iterator iterator=fieldElements.iterator();
            while (iterator.hasNext()) {
                FieldElement element = (FieldElement) iterator.next();
                element.addTemplate(object,listenerID);
            } 
            allIDs.add(listenerID);
        }
        
        /**
         * 
         * TODO Comment method
         * @param object
         * @param listenerID
         */
        public void removeTemplate(Object object, ListenerID listenerID){
            Iterator iterator=fieldElements.iterator();
            while (iterator.hasNext()) {
                FieldElement element = (FieldElement) iterator.next();
                element.removeTemplate(object,listenerID);
            } 
            allIDs.remove(listenerID);
        }
        
        /**
         * 
         * TODO Comment method
         * @return
         */
        public boolean isEmpty(){
            return allIDs.isEmpty();
        }



    }
    // private ObjectContainer container;
    

    private static class TreeElement{
        private TreeElement parent;
        private Class className;
        private Map childs;
        
        
        private FieldEntries fieldMap;
        private int depth;
        private int maxReflectionDepth;
        /**
         * 
         * Constructor for class <code>TreeElement</code>
         * @param rootClass
         * @param depth
         * @param maxReflectionDepth
         */
        public TreeElement(Class rootClass, int depth, int maxReflectionDepth) {
            this(rootClass,null,depth,maxReflectionDepth);  
        }
        
        private TreeElement(Class lastSuper,TreeElement parent,int depth, int maxReflectionDepth) {
            fieldMap=new FieldEntries(lastSuper,depth,maxReflectionDepth);
            this.maxReflectionDepth=maxReflectionDepth;
            this.depth=depth;
            className=lastSuper;
            childs=new HashMap(); 
            this.parent=parent;
        }

        /**
         * 
         * TODO Comment method
         * @param className
         * @return
         */
        public boolean hasChild(Class className){
            if (this.className.equals(className)){
                return true;
            }
            
            Iterator iterator=childs.keySet().iterator();
            while (iterator.hasNext()) {
                Class element = (Class) iterator.next();
                if (element.isAssignableFrom(className)){
                    return ((TreeElement)childs.get(element)).hasChild(className);
                }
            }
            return false;
        }
        
        /**
         * 
         * TODO Comment method
         * @param className
         * @return
         */
        public TreeElement createChild(Class className){
            if (this.className.equals(className)){
                return this;
            }
            Iterator iterator=childs.keySet().iterator();
            while (iterator.hasNext()) {
                Class element = (Class) iterator.next();
                if (element.isAssignableFrom(className)){
                    return ((TreeElement)childs.get(element)).createChild(className);
                }
                
            }
            
            Class lastSuper=className;
            if (this.className.isInterface()){
                
                Class superClass=className;
                while (!this.className.equals(superClass)){
                    lastSuper=superClass;
                    superClass=superClass.getSuperclass();
                    if (superClass==null||!this.className.isAssignableFrom(superClass)||this.className.equals(superClass)){
                        Class[] superIfs=lastSuper.getInterfaces();        
                        for (int i=0;i<superIfs.length;i++){
                            superClass=superIfs[i];
                            if (this.className.isAssignableFrom(superClass)||this.className.equals(superClass)) break;
                        }
                       //if (lastSuper.equals(superClass)throw new IllegalStateException();
                    }
                }
            }else{
                Class superClass=className.getSuperclass();
                while (!this.className.equals(superClass)){
                    lastSuper=superClass;
                    superClass=superClass.getSuperclass();
                }
            }
            
            TreeElement newChild=new TreeElement(lastSuper,this,depth,maxReflectionDepth);
            childs.put(lastSuper,newChild);
            return newChild.createChild(className);
            
        }
        
        /**
         * 
         * TODO Comment method
         * @param className
         * @return
         */
        public TreeElement getChild(Class className){
            if (this.className.equals(className)){
                return this;
            }
            Iterator iterator=childs.keySet().iterator();
            while (iterator.hasNext()) {
                Class element = (Class) iterator.next();
                if (element.isAssignableFrom(className)){
                    return ((TreeElement)childs.get(element)).getChild(className);
                }
                
            }
            return null;
        }
        
        /**
         * 
         * TODO Comment method
         * @param className
         * @return
         */
        public TreeElement getNearestChild(Class className){
            if (this.className.equals(className)){
                return this;
            }
            Iterator iterator=childs.keySet().iterator();
            while (iterator.hasNext()) {
                Class element = (Class) iterator.next();
                if (element.isAssignableFrom(className)){
                    return ((TreeElement)childs.get(element)).getNearestChild(className);
                }
                
            }
            return this;
        }
        
        /**
         * 
         * TODO Comment method
         * @param object
         * @param listenerID
         */
        public void addTemplate(Object object,ListenerID listenerID){
            
            Class objectClass=object.getClass();
            //if (!objectClass.equals(className)) throw new IllegalStateException("Wrong TreeElement");
            if (!className.isAssignableFrom(objectClass)) throw new IllegalStateException("Wrong TreeElement");
            fieldMap.addTemplate(object,listenerID);
            
        }
        
        /**
         * 
         * TODO Comment method
         * @param object
         * @param listenerID
         */
        public void removeTemplate(Object object,ListenerID listenerID){
            
            Class objectClass=object.getClass();
            if (!objectClass.equals(className)) return;// throw new IllegalStateException("Wrong TreeElement");
            fieldMap.removeTemplate(object,listenerID);
            checkRemove();
            
            
        }
        /**
         * TODO Comment method
         */
        private void checkRemove() {
            if (fieldMap.isEmpty()&&childs.isEmpty()&&parent!=null){
                parent.removeChild(className);
                
            }
            
        }

        /**
         * TODO Comment method
         * @param child 
         */
        private void removeChild(Class child) {
            childs.remove(child);
            checkRemove();
            
            
        }

        /**
         * 
         * TODO Comment method
         * @param object
         * @return
         */
        public Set getTemplatIDs(Object object){
            Set set=new LinkedHashSet();
            set.addAll(fieldMap.getTemplateIDs(object));
            if (parent!=null){
                set.addAll(parent.getTemplatIDs(object));
            }
            return set;
        }
                
        
    }
    
    
    private static HashMap primitiveNulls;
    static {
        
        primitiveNulls = new HashMap();
        primitiveNulls.put(Short.class,new Short((short)0));
        primitiveNulls.put(Integer.class,new Integer(0));
        primitiveNulls.put(Long.class,new Long(0));
        primitiveNulls.put(Float.class,new Float(0));
        primitiveNulls.put(Double.class,new Double(0));
        
        primitiveNulls.put(Byte.class,new Byte((byte)0));
        primitiveNulls.put(Character.class,new Character((char)0));
        
        primitiveNulls.put(Boolean.class,new Boolean(false));
    }
    
    private TreeElement tree;
    //private HashMapSet listenerMap;
    private int firstFreeID;
    private HashMap internalToListenerMap;
    private HashMapSet listenerToInternalMap;
    
    /**
     * 
     * Constructor for class <code>EventDBImplementationDepth</code>
     * @param maxReflectionDepth
     */
    public EventDBImplementationDepth(int maxReflectionDepth) {
        tree=new TreeElement(ServiceEvent.class,0,maxReflectionDepth);
        
        internalToListenerMap=new HashMap();
        listenerToInternalMap=new HashMapSet();
        
        //listenerMap=new HashMapSet();

        
    }
    
    

    public Set getListeners(ServiceEvent event){
        LinkedHashSet set=new LinkedHashSet();
        TreeElement element=tree.getNearestChild(event.getClass());
        Iterator iterator=element.getTemplatIDs(event).iterator();
        while (iterator.hasNext()) {
            EventDBListenerID eventDBListenerID=(EventDBListenerID) iterator.next();
            EvenDBEntry entry   = (EvenDBEntry) internalToListenerMap.get(eventDBListenerID);
            set.add(entry.getListenerID());
        }
        return set;
        
        
    }

   
    public void registerEventListener(ServiceEvent eventByExample, ListenerID listenerID){
        ListenerID internalID=new EventDBListenerID(firstFreeID++); 
        internalToListenerMap.put(internalID,new EvenDBEntry(eventByExample,listenerID));
        listenerToInternalMap.put(listenerID,internalID);
        
        TreeElement element=tree.createChild(eventByExample.getClass());
        element.addTemplate(eventByExample,internalID);
        
        
        
        //listenerMap.put(internalID,eventByExample);
    }
    
    public void removeEventListener(ListenerID listenerID){
        
        Set set=listenerToInternalMap.remove(listenerID);
        if (set!=null){
            Iterator iterator=set.iterator();
            while(iterator.hasNext()){
                EventDBListenerID eventDBListenerID=(EventDBListenerID)iterator.next();
                EvenDBEntry entry=(EvenDBEntry)internalToListenerMap.remove(eventDBListenerID);
                ServiceEvent object = entry.getEventByExample();
                TreeElement element=tree.getNearestChild(object.getClass());
                element.removeTemplate(object,eventDBListenerID);
            }
        }
    }
    





    /**
     * 
     * Returns the null value of the given object class
     * @param className
     * @return
     */
    private  static Object getNullValue(Class className) {
        
        return primitiveNulls.get(className);
        
        
    }
    
    /**
     * TODO Comment method
     * @param value
     * @return
     */
    protected static boolean isNullValue(Object value) {
        if (value==null){
            return true;
        }
        return value.equals(getNullValue(value.getClass()));
    }

    

}

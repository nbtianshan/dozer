/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.classmap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.util.MappingUtils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal class that determines the appropriate class mapping to be used for
 * the source and destination object being mapped. Only intended for internal
 * use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class ClassMappings {
  
  private static final Log log = LogFactory.getLog(ClassMappings.class);

  // Cache key --> Mapping Structure
  private Map<String, ClassMap> classMappings = new ConcurrentHashMap<String, ClassMap>();
  private ClassMapKeyFactory keyFactory;

  public ClassMappings() {
    keyFactory = new ClassMapKeyFactory();
  }

  public void add(Class<?> srcClass, Class<?> destClass, ClassMap classMap) {
    classMappings.put(keyFactory.createKey(srcClass, destClass), classMap);
  }

  public void add(Class<?> srcClass, Class<?> destClass, String mapId, ClassMap classMap) {
    classMappings.put(keyFactory.createKey(srcClass, destClass, mapId), classMap);
  }

  public void addAll(ClassMappings classMappings) {
    this.classMappings.putAll(classMappings.getAll());
  }

  // TODO: don't expose the internal datastore
  public Map<String, ClassMap> getAll() {
    return classMappings;
  }

  public long size() {
    return classMappings.size();
  }

  public ClassMap find(Class<?> srcClass, Class<?> destClass) {
    return classMappings.get(keyFactory.createKey(srcClass, destClass));
  }

  public boolean contains(Class<?> srcClass, Class<?> destClass, String mapId) {
    String key = keyFactory.createKey(srcClass, destClass, mapId);
    return classMappings.containsKey(key);
  }

  public ClassMap find(Class<?> srcClass, Class<?> destClass, String mapId) {
    final String key = keyFactory.createKey(srcClass, destClass, mapId);
    ClassMap mapping = classMappings.get(key);

    if (mapping == null) {
      mapping = findInterfaceMapping(destClass, srcClass, mapId);
    }

    // one more try...
    // if the mapId is not null looking up a map is easy
    if (!MappingUtils.isBlankOrNull(mapId) && mapping == null) {
      // probably a more efficient way to do this...
      for (Entry<String, ClassMap> entry : classMappings.entrySet()) {
        ClassMap classMap = entry.getValue();
        if (StringUtils.equals(classMap.getMapId(), mapId)
            && classMap.getSrcClassToMap().isAssignableFrom(srcClass)
            && classMap.getDestClassToMap().isAssignableFrom(destClass)) {
          return classMap;
        } else if (StringUtils.equals(classMap.getMapId(), mapId) && srcClass.equals(destClass)) {
          return classMap;
        }
      }

      // If map-id was specified and mapping was not found, then fail
      MappingUtils.throwMappingException("Class mapping not found by map-id: " + key);
    }

    return mapping;
  }

  // Look for an interface mapping
  private ClassMap findInterfaceMapping(Class<?> destClass, Class<?> srcClass, String mapId) {
    // Use object array for keys to avoid any rare thread synchronization issues
    // while iterating over the custom mappings.
    // See bug #1550275.
    Object[] keys = classMappings.keySet().toArray();
    for (Object key : keys) {
      ClassMap map = classMappings.get(key);
      Class<?> mappingDestClass = map.getDestClassToMap();
      Class<?> mappingSrcClass = map.getSrcClassToMap();

      if ((mapId == null && map.getMapId() != null)
          || (mapId != null && !mapId.equals(map.getMapId()))) {
        continue;
      }

      if (mappingSrcClass.isInterface() && mappingSrcClass.isAssignableFrom(srcClass)) {
        if (mappingDestClass.isInterface() && mappingDestClass.isAssignableFrom(destClass)) {
          return map;
        } else if (destClass.equals(mappingDestClass)) {
          return map;
        }
      }

      if (destClass.isAssignableFrom(mappingDestClass) ||
          (mappingDestClass.isInterface() && mappingDestClass.isAssignableFrom(destClass))) {
        if (srcClass.equals(mappingSrcClass)) {
          return map;
        }
      }

    }
    return null;
  }

}
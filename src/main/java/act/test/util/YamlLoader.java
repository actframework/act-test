package act.test.util;

import act.app.DaoLocator;
import act.db.Dao;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.osgl._;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.IO;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The utility class to support loading data from YAML file
 */
public class YamlLoader {

    static Pattern keyPattern = Pattern.compile("([^(]+)\\(([^)]+)\\)");

    public Map<Class, List> load(File yamlFile, String modelPackage, DaoLocator daoLocator) {
        return load(IO.readContentAsString(yamlFile), modelPackage, daoLocator);
    }

    public Map<Class, List> load(URL yamlFile, String modelPackage, DaoLocator daoLocator) {
        try {
            return load(IO.readContentAsString(yamlFile.openStream()), modelPackage, daoLocator);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Read the data YAML file and returns List of model objects mapped to their class names
     * @param yamlFile the file with data records in YAML format
     * @param modelPackage the predefined model package to be prepend to type if no package specified
     * @return the data object list mapped to class
     */
    public Map<Class, List> load(String yamlFile, String modelPackage, DaoLocator daoLocator) {
        Map<Class, List> repo = C.newMap();
        Map<Object, Map<?, ?>> objects = _.cast(new Yaml().load(yamlFile));
        Map<String, JSONObject> jsonCache = C.newMap();
        Map<String, Object> entityCache = C.newMap();
        Map<String, Class> classCache = C.newMap();
        for (Object key : objects.keySet()) {
            Matcher matcher = keyPattern.matcher(key.toString().trim());
            if (matcher.matches()) {
                String type = matcher.group(1);
                String id = matcher.group(2);
                if (!type.contains(".")) {
                    type = modelPackage + "." + type;
                }

                Class<?> modelType = classCache.get(type);
                if (null == modelType) {
                    modelType = _.classForName(type);
                    classCache.put(type, modelType);
                }
                List list = repo.get(modelType);
                if (null == list) {
                    list = C.newList();
                    repo.put(modelType, list);
                }

                if (jsonCache.containsKey(id)) {
                    throw E.unexpected("Duplicate id '" + id + "' for type " + type);
                }

                Map entityValues =  objects.get(key);
                JSONObject json = new JSONObject(entityValues);
                Dao dao = null == daoLocator ? null : daoLocator.dao(modelType);
                resolveDependencies(json, jsonCache, entityCache, dao);
                jsonCache.put(id, json);
                Object entity = JSON.toJavaObject(json, modelType);
                if (null != dao) {
                    dao.save(entity);
                }
                entityCache.put(id, entity);
                list.add(entity);
            }
        }
        return repo;
    }

    private void resolveDependencies(JSONObject objects, Map<String, JSONObject> jsonCache, Map<String, Object> entityCache, Dao dao) {
        for (String k: objects.keySet()) {
            Object v = objects.get(k);
            if (v instanceof JSONObject) {
                resolveDependencies((JSONObject) v, jsonCache, entityCache, dao);
            } else if (v instanceof String) {
                String s = (String) v;
                if (s.startsWith("$")) {
                    String id = s.substring(1);
                    JSONObject embedded = jsonCache.get(id);
                    if (null == embedded) {
                        throw E.unexpected("Cannot find embedded object by ID: %s", id);
                    }
                    objects.put(k, embedded);
                } else if (s.startsWith("ref:")) {
                    String id = s.substring(4);
                    Object reference = entityCache.get(id);
                    if (null == reference) {
                        throw E.unexpected("Cannot find reference object by ID: %s", id);
                    } else if (null == dao) {
                        throw E.unexpected("Cannot resolve reference when Dao is missing");
                    }
                    objects.put(k, dao.getId(reference));
                }
            } else if (v instanceof JSONArray) {
                JSONArray array = (JSONArray) v;
                int len = array.size();
                for (int i = 0; i < len; i++) {
                    Object e = array.get(i);
                    if (e instanceof JSONObject) {
                        resolveDependencies((JSONObject) e, jsonCache, entityCache, dao);
                    } else if (e instanceof String) {
                        String s = (String) e;
                        if (s.startsWith("[") && s.endsWith("]")) {
                            String id = s.substring(1, s.length() - 1);
                            JSONObject embedded = jsonCache.get(id);
                            if (null == embedded) {
                                throw E.unexpected("Cannot find embedded object by ID: %s", id);
                            }
                            array.set(i, embedded);
                        } else if (s.startsWith("ref:")) {
                            String id = s.substring(4);
                            Object reference = entityCache.get(id);
                            if (null == reference) {
                                throw E.unexpected("Cannot find reference object by ID: %s", id);
                            } else if (null == dao) {
                                throw E.unexpected("Cannot resolve reference when Dao is missing");
                            }
                            array.set(i, dao.getId(reference));
                        }
                    }
                }
            }
        }
    }

}

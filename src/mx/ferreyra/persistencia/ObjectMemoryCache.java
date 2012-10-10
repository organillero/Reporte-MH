package mx.ferreyra.persistencia;

import java.lang.ref.SoftReference;
import java.util.HashMap;

public class ObjectMemoryCache {
    private HashMap<String, SoftReference<Object>> cache=new HashMap<String, SoftReference<Object>>();
    
    public Object get(String id){
        if(!this.cache.containsKey(id))
            return null;
        SoftReference<Object> ref=this.cache.get(id);
        return ref.get();
    }
    
    public void put(String id, Object obj){
        this.cache.put(id, new SoftReference<Object>(obj));
    }

    public void clear() {
        this.cache.clear();
    }
}
package com.a1stream.web.app.ifs.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import com.a1stream.common.ifs.Interf;
import com.a1stream.common.ifs.InterfProcessor;

@Component
public final class InterfManager {

    private Map<String, InterfProcessor<? , ?>> mapping = new HashMap<>();

    /**
     *  Load all beans which implements {@link InterfProcessor} and build a mapping
     *  interface code with processor bean
     *
     * @param ipProvider
     */
    public InterfManager(ObjectProvider<List<InterfProcessor<?, ?>>> ipProvider) {
        ipProvider.ifAvailable(ps -> {
            ps.stream()
            .forEach(processor -> {
                Interf[] types =  processor.getClass().getAnnotationsByType(Interf.class);

                if(types != null && types.length > 0 && types[0].code().length > 0) {
                    String[] codes = types[0].code();
                    for(String code:codes){
                        mapping.put(code, processor);
                    }
                }
            });
        });
    }

    public InterfProcessor<?, ?> findByCode(String code) {
        return mapping.get(code);
    }

    public InterfProcessor<?, ?> findByCode(String consumerType, String code) {
        if(mapping.containsKey(consumerType + code)){
            return mapping.get(consumerType + code);
        }else if(mapping.containsKey(code)){
            return mapping.get(code);
        }
        return null;
    }

    public boolean constainKey(String consumerType, String code) {
        boolean flag = false;
        if(mapping.containsKey(consumerType + code)){
            flag = true;
        }else if(mapping.containsKey(code)){
            flag = true;
        }
        return flag;
    }
}

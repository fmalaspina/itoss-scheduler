package com.frsi.itoss.model.ct;

import com.frsi.itoss.model.repository.CtTypeRepo;
import com.frsi.itoss.shared.InstrumentationParameter;
import com.frsi.itoss.shared.TypeAttribute;
import com.frsi.itoss.shared.Utils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Component
public class CtKeyConstructor {

    @Autowired
    CtTypeRepo ctTypeRepo;

    private String key;


    public String getKeyDefault(CtType type, Ct entity) throws Exception {

        Optional<CtType> optionalType = ctTypeRepo.findById(type.getId());
        if (optionalType.isPresent()) {
            CtType realType = optionalType.get();
            Set<TypeAttribute> setTypeAttTotal = new HashSet<>();

            Set<InstrumentationParameter> setInstParams;// = new HashSet<>();
            setInstParams = realType.getInstrumentations().stream().flatMap(i -> i.getInstrumentationParameters().stream()).filter(Utils.distinctByKey(p -> p.getName())).collect(Collectors.toSet());
            setTypeAttTotal.addAll(realType.getTypeAttributes());
            setTypeAttTotal.addAll(setInstParams.stream().map(i -> {
                TypeAttribute temp = new TypeAttribute();
                temp.setName(i.getName());
                temp.setOrder(i.getOrder());
                temp.setIdPart(i.isIdPart());
                return temp;
            }).collect(Collectors.toList()));


            StringBuilder sbKey = new StringBuilder();

            setTypeAttTotal.stream().filter(a -> a.isIdPart()).sorted(Comparator.comparing(a -> a.getOrder())).forEach(a -> {

                if (a.isIdPart()) {
                    if (!sbKey.toString().isEmpty()) {
                        sbKey.append("/");
                    }
                    String tempKey = entity.getJoinedAtt(a.getName().toString()).toString();
                    if (tempKey != null) {
                        sbKey.append(tempKey);
                    }
                }

            });

            this.key = sbKey.toString();
        } else {

            this.key = "/";
        }
        return key;
    }
}

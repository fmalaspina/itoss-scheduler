package com.frsi.itoss.model.baseclasses;

import lombok.Data;

@Data
public class KeyConstructor {

    private String key;
    private TypeEntityBase type;
    private EntityBase entity;

    public KeyConstructor(TypeEntityBase type, EntityBase entity) throws Exception {
        this.type = type;
        this.entity = entity;
        this.setKeyDefault();
    }


    private void setKeyDefault() throws Exception {
        if (this.type != null) {


            StringBuilder sbKey = new StringBuilder();

            this.type.getTypeAttributes().stream().filter(a -> a.isIdPart()).forEachOrdered(a -> {

                if (a.isIdPart()) {
                    if (!sbKey.toString().isEmpty()) {
                        sbKey.append("/");
                    }
                    String tempKey = this.entity.getObject(a.getName().toString()).toString();
                    if (tempKey != null) {
                        sbKey.append(tempKey);
                    }
                }

            });

            this.key = sbKey.toString();
        } else {

            this.key = "/";
        }
    }
}

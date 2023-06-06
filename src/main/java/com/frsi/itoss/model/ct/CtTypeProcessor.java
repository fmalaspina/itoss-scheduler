package com.frsi.itoss.model.ct;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;

public class CtTypeProcessor implements RepresentationModelProcessor<EntityModel<CtType>> {

    @Override
    public EntityModel<CtType> process(EntityModel<CtType> model) {

        model.removeLinks();
        return model;
    }
}
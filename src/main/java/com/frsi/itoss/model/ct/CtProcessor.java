package com.frsi.itoss.model.ct;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;

public class CtProcessor implements RepresentationModelProcessor<EntityModel<Ct>> {

    @Override
    public EntityModel<Ct> process(EntityModel<Ct> model) {

        model.removeLinks();
        return model;
    }
}
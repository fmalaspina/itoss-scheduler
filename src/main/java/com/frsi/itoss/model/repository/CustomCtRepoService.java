package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.ct.Ct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomCtRepoService {

    @Autowired
    CtRepo ctRepo;


    public List<Ct> findByAttribute(String name, String value, Integer pageNo, Integer pageSize, String sortBy) {


        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<Ct> pagedResult = ctRepo.findAll(paging);


        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Ct>().stream().filter(ct -> String.valueOf(ct.getObject(name)).equals(value)).collect(Collectors.toList());
        }
    }
}

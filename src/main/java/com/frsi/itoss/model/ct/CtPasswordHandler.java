package com.frsi.itoss.model.ct;

import com.frsi.itoss.model.baseclasses.CustomValidationException;
import com.frsi.itoss.shared.Attribute;
import com.frsi.itoss.shared.CryptoService;
import com.frsi.itoss.shared.DataType;
import com.frsi.itoss.shared.InstrumentationParameterValue;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RepositoryEventHandler(Ct.class)

public class CtPasswordHandler {
    Map<String, Object> oldCryp = new HashMap<>();

//    @Autowired
//    CtRepo ctRepo;

    @HandleBeforeCreate
    public Ct handleCtCreate(Ct ct) {

        List<Attribute> attributes = ct.getAttributes();
        List<InstrumentationParameterValue> instrumParameterValues = ct.getInstrumentationParameterValues();


        instrumParameterValues.forEach(a -> {
            if (ct.getType().getDataType(a.getName()).equals(DataType.PASSWORD)) {
                try {
                    if (ct.getOldCryptedPropertyValues() == null) {

                        ct.setOldCryptedPropertyValues(oldCryp);
                    }

                    a.setValue(CryptoService.encrypt((String) a.getValue()));
                    ct.getOldCryptedPropertyValues().put(a.getName(), (String) a.getValue());
                } catch (Exception e) {
                    throw new CustomValidationException("Unable to encrypt password or null password.");
                }

            }

        });
        ct.setInstrumentationParameterValues(instrumParameterValues);

        attributes.forEach(a -> {
            if (ct.getType().getDataType(a.getName()).equals(DataType.PASSWORD)) {
                try {
                    if (ct.getOldCryptedPropertyValues() == null) {

                        ct.setOldCryptedPropertyValues(oldCryp);
                    }

                    a.setValue(CryptoService.encrypt((String) a.getValue()));
                    ct.getOldCryptedPropertyValues().put(a.getName(), (String) a.getValue());
                } catch (Exception e) {
                    throw new CustomValidationException("Unable to encrypt password or null password.");
                }

            }

        });
        ct.setAttributes(attributes);
        return ct;

    }

    @HandleBeforeSave

    public Ct handleCtSave(Ct ct) {
        List<Attribute> attributes = ct.getAttributes();
        List<InstrumentationParameterValue> instrumParameterValues = ct.getInstrumentationParameterValues();

        instrumParameterValues.forEach(a -> {
            if (ct.getType().getDataType(a.getName()).equals(DataType.PASSWORD)) {
                try {
                    if (ct.getOldCryptedPropertyValues() == null) {
                        a.setValue(CryptoService.encrypt((String) a.getValue()));
                        ct.setOldCryptedPropertyValues(oldCryp);
                    } else {
                        if (ct.getOldCryptedPropertyValues().get(a.getName()) == null) {
                            a.setValue(CryptoService.encrypt((String) a.getValue()));

                        }

                    }
                    if (ct.getOldCryptedPropertyValues().get(a.getName()) != null
                            && !ct.getOldCryptedPropertyValues().get(a.getName()).equals((String) a.getValue())) {

                        a.setValue(CryptoService.encrypt((String) a.getValue()));

                    }
                    ct.getOldCryptedPropertyValues().put(a.getName(), (String) a.getValue());

                } catch (Exception e) {
                    throw new CustomValidationException("Unable to encrypt password or null password.");
                }

            }

        });
        ct.setInstrumentationParameterValues(instrumParameterValues);


        attributes.forEach(a -> {
            if (ct.getType().getDataType(a.getName()).equals(DataType.PASSWORD)) {
                try {
                    if (ct.getOldCryptedPropertyValues() == null) {
                        a.setValue(CryptoService.encrypt((String) a.getValue()));
                        ct.setOldCryptedPropertyValues(oldCryp);
                    } else {
                        if (ct.getOldCryptedPropertyValues().get(a.getName()) == null) {
                            a.setValue(CryptoService.encrypt((String) a.getValue()));
                        }
                    }
                    if (ct.getOldCryptedPropertyValues().get(a.getName()) != null
                            && !ct.getOldCryptedPropertyValues().get(a.getName()).equals((String) a.getValue())) {

                        a.setValue(CryptoService.encrypt((String) a.getValue()));

                    }

                    ct.getOldCryptedPropertyValues().put(a.getName(), (String) a.getValue());

                } catch (Exception e) {
                    throw new CustomValidationException("Unable to encrypt password or null password.");
                }

            }

        });
        ct.setAttributes(attributes);
        return ct;


    }

}

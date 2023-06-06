package com.frsi.itoss.model.inconsistency;

import com.frsi.itoss.shared.DataType;
import com.frsi.itoss.shared.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor

public class Column implements Serializable {
    public String name;
    public DataType type;


    public Column(String name, DataType type) {
        this.name = name;
        this.type = type;

    }

    public Column(String name) {
        this.name = name;


    }

    public Column(Object o) {


        this.name = Utils.getColumnName(o);
        this.type = Utils.getColumnType(o);

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type=" + type +

                '}';
    }
}

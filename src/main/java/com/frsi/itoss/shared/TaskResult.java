package com.frsi.itoss.shared;

import java.io.Serializable;
import java.util.Date;


public record TaskResult(Date timestamp, String output, String error, String status) implements Serializable {

}

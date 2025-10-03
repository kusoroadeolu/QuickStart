package org.quickstart.dtos;

import java.util.List;

public record ProfileDeleteResult(List<String> success, List<String> failed) {

    //TODO write to string impl here

}

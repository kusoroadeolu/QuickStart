package org.quickstart.dtos;

import java.util.List;

/**
 * Failed profile deletes
 * @param failed Profiles that failed to delete
 * @param success Profiles that succeeded
 * */
public record ProfileDeleteResult(List<String> success, List<String> failed) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(success != null && !success.isEmpty()) {
            sb.append(String.format("deleted %d profile%s successfully\n",
                    success.size(),
                    success.size() == 1 ? "" : "s"));
        }

        if(failed != null && !failed.isEmpty()) {
            sb.append(String.format("failed to delete %d profile%s: %s\n",
                    failed.size(),
                    failed.size() == 1 ? "" : "s",
                    String.join(", ", failed)));
        }

        if((success == null || success.isEmpty()) && (failed == null || failed.isEmpty())) {
            return "no profiles to delete";
        }

        return sb.toString().trim();
    }

}

package edu.iis.mto.oven;

import java.util.Collections;
import java.util.Map;

public class BakingResult {

    private final BakingProgram sourceProgram;
    private final boolean success;
    private final Map<ProgramStage, Boolean> stageCompletes;
    private final String errorMessage;

    private BakingResult(Builder builder) {
        this.sourceProgram = builder.sourceProgram;
        this.success = builder.success;
        this.stageCompletes = builder.stageCompletes;
        this.errorMessage = builder.errorMessage;
    }

    public BakingProgram getSourceProgram() {
        return sourceProgram;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<ProgramStage, Boolean> getStageCompletes() {
        return stageCompletes;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private BakingProgram sourceProgram;
        private boolean success;
        private Map<ProgramStage, Boolean> stageCompletes = Collections.emptyMap();
        private String errorMessage;

        private Builder() {}

        public Builder withSourceProgram(BakingProgram sourceProgram) {
            this.sourceProgram = sourceProgram;
            return this;
        }

        public Builder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder withStageCompletes(Map<ProgramStage, Boolean> stageCompletes) {
            this.stageCompletes = stageCompletes;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public BakingResult build() {
            return new BakingResult(this);
        }
    }
}

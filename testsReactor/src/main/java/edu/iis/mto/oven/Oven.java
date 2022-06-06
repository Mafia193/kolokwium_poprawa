package edu.iis.mto.oven;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.iis.mto.oven.BakingResult.Builder;

public class Oven {

    private static final int HEAT_UP_AND_FINISH_SETTING_TIME = 0;
    private final HeatingModule heatingModule;
    private final Fan fan;

    public Oven(HeatingModule heatingModule, Fan fan) {
        this.heatingModule = heatingModule;
        this.fan = fan;
    }

    public BakingResult runProgram(BakingProgram program) {
        Map<ProgramStage, Boolean> stageCompletes = new LinkedHashMap<>();
        for (ProgramStage programStage : program) {
            stageCompletes.put(programStage, false);
        }
        Builder builder = BakingResult.builder()
                                      .withSourceProgram(program)
                                      .withStageCompletes(stageCompletes);
        try {
            init(program.getInitialTemp());
            for (ProgramStage programStage : program) {
                runStage(programStage);
                stageCompletes.put(programStage, true);

            }
            cool(program);
            builder.withSuccess(true);
        } catch (HeatingException e) {
            builder.withSuccess(false);
            builder.withErrorMessage(e.getMessage());
        }
        return builder.build();
    }

    private void cool(BakingProgram program) {
        if (program.isCoolAtFinish()) {
            fan.on();
        }
    }

    private void init(int initialTemp) throws HeatingException {
        if (initialTemp > 0) {
            heatingModule.heater(HeatingSettings.builder()
                                                .withTargetTemp(initialTemp)
                                                .withTimeInMinutes(HEAT_UP_AND_FINISH_SETTING_TIME)
                                                .build());
        }
    }

    private void runStage(ProgramStage programStage) throws HeatingException {
        if (programStage.getHeat() == HeatType.THERMAL_CIRCULATION) {
            fan.on();
            heatingModule.thermalCircuit(settings(programStage));
            fan.off();
        } else {
            if (fan.isOn()) {
                fan.off();
            }
            runHeatingProgram(programStage);
        }
    }

    private void runHeatingProgram(ProgramStage stage) throws HeatingException {
        HeatingSettings settings = settings(stage);
        if (stage.getHeat() == HeatType.GRILL) {
            heatingModule.grill(settings);
        } else {
            heatingModule.heater(settings);
        }
    }

    private HeatingSettings settings(ProgramStage stage) {
        return HeatingSettings.builder()
                              .withTargetTemp(stage.getTargetTemp())
                              .withTimeInMinutes(stage.getStageTime())
                              .build();
    }
}

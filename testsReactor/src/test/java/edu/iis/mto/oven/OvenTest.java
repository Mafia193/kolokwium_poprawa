package edu.iis.mto.oven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OvenTest {

    @Mock
    private HeatingModule heatingModule;
    @Mock
    private Fan fan;

    private Oven oven;

    private BakingProgram bakingProgram;
    private ProgramStage programStage1;
    private ProgramStage programStage;

    @BeforeEach
    private void onSetUp() {
        oven = new Oven(heatingModule, fan);

        programStage1 = ProgramStage
                .builder()
                .withTargetTemp(200)
                .withStageTime(60)
                .withHeat(HeatType.THERMAL_CIRCULATION)
                .build();

        programStage = ProgramStage
                .builder()
                .withTargetTemp(180)
                .withStageTime(90)
                .withHeat(HeatType.GRILL)
                .build();

        List<ProgramStage> programStageList = new ArrayList<>();
        programStageList.add(programStage1);
        programStageList.add(programStage);

        bakingProgram = BakingProgram
                .builder()
                .withInitialTemp(21)
                .withStages(programStageList)
                .withCoolAtFinish(true)
                .build();
    }

    @Test
    void itCompiles() {
        assertThat(true, equalTo(true));
    }

    @Test
    void test1() {
        BakingResult result = oven.runProgram(bakingProgram);
        assertTrue(result.isSuccess());
        assertEquals(result.getSourceProgram(), bakingProgram);
    }

   /* @Test
    void test2() throws HeatingException {
        HeatingSettings heatingSettings = HeatingSettings
                .builder()
                .withTargetTemp(200)
                .withTimeInMinutes(60)
                .build();

        doThrow(HeatingException.class).when(HeatingModule.class);
        BakingResult result = oven.runProgram(bakingProgram);
        assertFalse(result.isSuccess());
        assertEquals(result.getSourceProgram(), bakingProgram);
    }*/

    @Test
    void invokeGrillHeatTypeGrill() throws HeatingException {
        ProgramStage programStage = ProgramStage
                .builder()
                .withTargetTemp(180)
                .withStageTime(90)
                .withHeat(HeatType.GRILL)
                .build();

        List<ProgramStage> programStageList = new ArrayList<>();
        programStageList.add(programStage);

        bakingProgram = BakingProgram
                .builder()
                .withInitialTemp(21)
                .withStages(programStageList)
                .withCoolAtFinish(true)
                .build();

        BakingResult result = oven.runProgram(bakingProgram);

        verify(heatingModule, times(1)).grill(any());
        verify(heatingModule, times(1)).heater(any());
    }

    @Test
    void invokeHeaterHeatTypeHeater() throws HeatingException {
        ProgramStage programStage = ProgramStage
                .builder()
                .withTargetTemp(180)
                .withStageTime(90)
                .withHeat(HeatType.HEATER)
                .build();

        List<ProgramStage> programStageList = new ArrayList<>();
        programStageList.add(programStage);

        bakingProgram = BakingProgram
                .builder()
                .withInitialTemp(21)
                .withStages(programStageList)
                .withCoolAtFinish(true)
                .build();

        BakingResult result = oven.runProgram(bakingProgram);

        verify(heatingModule, times(0)).grill(any());
        verify(heatingModule, times(2)).heater(any());
    }

    @Test
    void noSuccessHeatingException() throws HeatingException {
        doThrow(HeatingException.class).when(heatingModule).heater(any());

        BakingResult result = oven.runProgram(bakingProgram);
        assertFalse(result.isSuccess());
    }

    @Test
    void turnOffFanWhenHeatTypeIsNotThermalCirculation() {
        when(fan.isOn()).thenReturn(true);

        programStage = ProgramStage
                .builder()
                .withTargetTemp(180)
                .withStageTime(90)
                .withHeat(HeatType.GRILL)
                .build();

        List<ProgramStage> programStageList = new ArrayList<>();
        programStageList.add(programStage);

        bakingProgram = BakingProgram
                .builder()
                .withInitialTemp(21)
                .withStages(programStageList)
                .withCoolAtFinish(true)
                .build();

        BakingResult result = oven.runProgram(bakingProgram);
        verify(fan, times(1)).off();
    }

   /* void invokeHeaterHeatTypeThermalCirculation() throws HeatingException {
        ProgramStage programStage = ProgramStage
                .builder()
                .withTargetTemp(180)
                .withStageTime(90)
                .withHeat(HeatType.HEATER)
                .build();

        List<ProgramStage> programStageList = new ArrayList<>();
        programStageList.add(programStage);

        bakingProgram = BakingProgram
                .builder()
                .withInitialTemp(21)
                .withStages(programStageList)
                .withCoolAtFinish(true)
                .build();

        verify(heatingModule, times(0)).grill(any());
        verify(heatingModule, times(1)).heater(any());
    }*/

}

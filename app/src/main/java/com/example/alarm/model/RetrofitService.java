package com.example.alarm.model;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.alarm.model.exceptions.AlarmAlreadyExistsException;
import com.example.alarm.model.exceptions.AlarmNotFoundException;
import com.example.alarm.model.exceptions.AppException;
import com.example.alarm.model.exceptions.BackendException;
import com.example.alarm.retrofit.source.AlarmSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RetrofitService implements RetrofitRepository {
    private final AlarmSource alarmSource;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final MutableLiveData<List<Integer>> _initCompleted = new MutableLiveData<>();

    public RetrofitService(AlarmSource alarmSource) {
        this.alarmSource = alarmSource;

        executorService.submit(() -> _initCompleted.postValue(getAlarms()));
    }
    public LiveData<List<Integer>> initCompleted = _initCompleted;

    @Override
    public List<Integer> getAlarms() throws AppException {
        try {
            List<Integer> alarms = executorService.submit(() -> {
                try {
                    return alarmSource.getAlarms();
                } catch (BackendException e) {
                    if(e.getCode() == 404) {
                        throw new AlarmNotFoundException(e);
                    } else {
                        throw e;
                    }
                }
            }).get();
            Log.d("testGetAlarms", alarms.toString());
            return alarms;
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            Log.d("testGetAlarms", e.toString());
            return new ArrayList<>();
        }
    }

    @Override
    public Pair<Boolean, String> setAlarm(Alarm alarm) throws AppException {
        try {
            String message = executorService.submit(() -> {
                try {
                    return alarmSource.setAlarm(alarm);
                } catch (BackendException e) {
                    if(e.getCode() == 404) {
                        throw new AlarmNotFoundException(e);
                    } else if(e.getCode() == 400) {
                        throw new AlarmAlreadyExistsException(e);
                    } else {
                        throw e;
                    }
                }
            }).get();
            Log.d("testSetAlarm", message);
            return new Pair<Boolean, String>(true, message);
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            Log.d("testSetAlarm", e.toString());
            return new Pair<Boolean, String>(false, "Ошибка запуска будильника");
        }
    }

    @Override
    public Pair<Boolean, String> deleteAlarm(int alarm_id) throws AppException {
        try {
            String message = executorService.submit(() -> {
                try {
                    return alarmSource.deleteAlarm(alarm_id);
                } catch (BackendException e) {
                    if(e.getCode() == 404) {
                        throw new AlarmNotFoundException(e);
                    } else {
                        throw e;
                    }
                }
            }).get();
            Log.d("testDeleteAlarm", message);
            return new Pair<Boolean, String>(true, message);
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            Log.d("testDeleteAlarm", e.toString());
            return new Pair<Boolean, String>(false, "Ошибка выключения будильника");
        }
    }

    @Override
    public Pair<Boolean, String> updateAlarm(Alarm alarm) throws AppException {
        try {
            String message = executorService.submit(() -> {
                try {
                    return alarmSource.updateAlarm(alarm);
                } catch (BackendException e) {
                    if(e.getCode() == 404) {
                        throw new AlarmNotFoundException(e);
                    } else if(e.getCode() == 400) {
                        throw new AlarmAlreadyExistsException(e);
                    } else {
                        throw e;
                    }
                }
            }).get();
            Log.d("testUpdateAlarm", message);
            return new Pair<Boolean, String>(true, message);
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            Log.d("testUpdateAlarm", e.toString());
            return new Pair<Boolean, String>(false, "Ошибка обновления будильника");
        }
    }
}

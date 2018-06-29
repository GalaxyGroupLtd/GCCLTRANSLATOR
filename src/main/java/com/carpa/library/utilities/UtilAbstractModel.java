/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carpa.library.utilities;

import com.carpa.library.config.ErrorCodeConfig;
import com.carpa.library.models.ErrorModel;
import com.carpa.library.models.ErrorsListModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <T>
 * @author aubain
 */
public abstract class UtilAbstractModel<T> {
    private final Class<T> modelClass;

    public UtilAbstractModel(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    public T serialize(String input) throws IOException {
        try {
            return (T) DataFactory.stringToObject(modelClass, input);
        } catch (IOException e) {
            List<ErrorModel> errors = new ArrayList<>();
            ErrorModel errorModel = new ErrorModel(ErrorCodeConfig.PARSING_ERROR[0], ErrorCodeConfig.PARSING_ERROR[1], e.getMessage());
            errors.add(errorModel);
            throw new IOException(DataFactory.errorObject(new ErrorsListModel(errors)));
        }
    }

    public List<T> serializeList(String input) throws IOException {
        try {
            List<T> mList = new ArrayList<>();
            List<Object> mObjects = DataFactory.stringToObjectList(modelClass, input);
            for (Object object : mObjects) {
                mList.add((T) object);
            }
            return mList;
        } catch (IOException e) {
            List<ErrorModel> errors = new ArrayList<>();
            ErrorModel errorModel = new ErrorModel(ErrorCodeConfig.PARSING_ERROR[0], ErrorCodeConfig.PARSING_ERROR[1], e.getMessage());
            errors.add(errorModel);
            throw new IOException(DataFactory.errorObject(new ErrorsListModel(errors)));
        }
    }

    public String deSerialize(Object o) throws IOException {
        try {
            return DataFactory.objectToString(o);
        } catch (IOException e) {
            List<ErrorModel> errors = new ArrayList<>();
            ErrorModel errorModel = new ErrorModel(ErrorCodeConfig.PARSING_ERROR[0], ErrorCodeConfig.PARSING_ERROR[1], e.getMessage());
            errors.add(errorModel);
            throw new IOException(DataFactory.errorObject(new ErrorsListModel(errors)));
        }
    }
}

package com.tsolution.base;


import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.tsolution.base.utils.Utils;
import com.tsolution.base.listener.ResponseResult;
import com.tsolution.base.listener.ViewFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Callback;

public class BaseViewModel<T extends BaseModel> extends AndroidViewModel {
    protected final Integer CURR_PAGE = 0;//block query lên api service để lấy dữ liệu
    protected final Integer TOTAL_PAGE = 1;//tổng số block
    protected final Integer IS_LOAD = 3;
    protected final Integer UPDATE_LIST_TYPE = 4;
    protected final Integer LAST_POSITION = 5;
    protected final Integer FETCH_SIZE = 6;//số phần tử lấy ra để hiển thị lên giao diện
    protected final Integer TOTAL_COUNT = 7;//số phần tử lấy ra để hiển thị lên giao diện

    protected ObservableField<T> model;//du lieu o man hinh view
    public ObservableField<List<BaseModel>> baseModels; //danh sach trong recycleview o man hinh view
    public HashMap<Integer, Object> modelInfo = new HashMap<>();
    public ObservableField<ErrMsg> errMsg = new ObservableField<>();
    protected ViewFunction view;//dung de goi tu viewmodel ra tang view ham process


    protected MutableLiveData<AlertModel> alertModel;

    private CompositeDisposable mCompositeDisposable;

    protected void callApi(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    public void initObs() {
        alertModel = new MutableLiveData<>();
    }

    public BaseViewModel(@NonNull Application application) {
        super(application);
        baseModels = new ObservableField<>();
        baseModels.set(new ArrayList<>());
        model = new ObservableField<>();
        errMsg = new ObservableField<>();
    }

    public void clearErro(String errCode) {
        ErrMsg err = errMsg.get();
        if (err == null) {
            err = new ErrMsg();
            errMsg.set(err);
        }
        err.addError(errCode, null);
        errMsg.notifyChange();
    }

    public void addError(String errCode, @StringRes int resId, boolean notice) {
        ErrMsg err = errMsg.get();
        if (err == null) {
            err = new ErrMsg();
            errMsg.set(err);
        }
        err.addError(errCode, getApplication().getResources().getString(resId));
        if (notice) {
            errMsg.notifyChange();
        }
    }

    public void addError(String errCode, String message, boolean notice) {
        ErrMsg err = errMsg.get();
        if (err == null) {
            err = new ErrMsg();
            errMsg.set(err);
        }
        err.addError(errCode, message);
        if (notice) {
            errMsg.notifyChange();
        }
    }

    public void addError(String errCode, @StringRes int resId) {
        addError(errCode, resId, false);
    }


    public int getCount() {
        if (baseModels == null) {
            return 0;
        }
        return baseModels.get().size();
    }


    public void updateModelInfo(List lst) {
        modelInfo.put(TOTAL_COUNT, lst.size());
        modelInfo.put(CURR_PAGE, 0);
        modelInfo.put(FETCH_SIZE, lst.size());
    }


    public void invokeFunc(String methodName, Object... params) {
        try {
            Method method = null;
            Class[] arg = null;
            Method[] methods = getClass().getMethods();
            for (Method m : methods) {
                if (methodName.equals(m.getName())) {
                    method = m;
                    break;
                }
            }
            if (method == null) {
                throw new NoSuchMethodException(methodName);
            }

            method.invoke(this, params);


        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
//            processing.setValue(null);
        }
    }


    public List getCheckedItems() {
        List<BaseModel> lst = new ArrayList<>();
        for (BaseModel bm : baseModels.get()) {
            if (bm.checked != null && bm.checked) {
                lst.add(bm);
            }

        }
        return lst;
    }

    public ObservableField<List<BaseModel>> getModels() {
        return baseModels;
    }

    public void setView(ViewFunction function) {
        this.view = function;
    }

    public void init() {

    }

    public ObservableField<T> getModel() {
        return model;
    }


    public MutableLiveData<AlertModel> getAlertModel() {
        return alertModel;
    }

    public void setAlertModel(MutableLiveData<AlertModel> alertModel) {
        this.alertModel = alertModel;
    }

    public BaseViewModel getViewModel() {
        return this;
    }


    public Object getListener() {
        return this;
    }

    public void setData(List<BaseModel> lst) {
        setData(lst, true);
        updateModelInfo(lst);
    }

    public void setData(List<BaseModel> lst, boolean clear) {
        setData(null, lst, clear, true);
    }


    public void setData(ObservableField datas, List<BaseModel> lst) {
        setData(datas, lst, true, true);
    }

    public void setData(ObservableField datas, List<BaseModel> lst, boolean clear, boolean notify) {
        List<BaseModel> newList = new ArrayList<>();
        if (datas == null) {
            datas = baseModels;

        }
        List list = (List) datas.get();
        if (clear) {
            list.clear();

        }
        list.addAll(lst);

        if (notify) {
            datas.notifyChange();
        }
    }

    public T getModelE() {
        return model.get();
    }

    public void setModelE(T value, boolean notice) {
        this.model.set(value);
        if (notice) {
            model.notifyChange();
        }
    }

    public void setModelE(T value) {
        setModelE(value, true);
    }

    public List<BaseModel> getBaseModelsE() {
        return baseModels.get();
    }

    public void setBaseModelsE(List values, boolean notice) {
        baseModels.set(values);
        if (notice) {
            baseModels.notifyChange();
        }
    }

    public void setBaseModelsE(List values) {
        setBaseModelsE(values, true);
    }

    public void setLoadingInfo(boolean load) {
        modelInfo.put(IS_LOAD, load);
    }

    public Integer getUpdateAdappType() {
        return Utils.nvl((Integer) modelInfo.get(UPDATE_LIST_TYPE), 0);
    }

    public void setUpdateAdappType(Integer type) {
        modelInfo.put(UPDATE_LIST_TYPE, type);
    }

    public void setLastPos(Integer pos) {
        modelInfo.put(LAST_POSITION, pos);
    }

    public Integer getLastPos() {
        Integer pos = (Integer) modelInfo.get(LAST_POSITION);
        return pos == null ? 0 : pos;
    }

    public Integer getNextPage() {
        Integer currPage = (Integer) modelInfo.get(CURR_PAGE);
        Integer totalPage = (Integer) modelInfo.get(TOTAL_PAGE);
        if (currPage == null) {
            return 0;
        }
        if (totalPage == null || currPage >= totalPage) {
            return -2;
        }
        return currPage + 1;
    }

    public void addNewPage(List page, int totalPages) {
        modelInfo.put(TOTAL_PAGE, totalPages);

        Integer currPage = (Integer) modelInfo.get(CURR_PAGE);
        currPage = currPage == null ? 0 : ++currPage;
        modelInfo.put(CURR_PAGE, currPage);
        setData(null, Utils.safe(page), false, false);
        add1Fetch();
    }

    public Integer getFetchSize() {
        return Utils.nvl((Integer) modelInfo.get(FETCH_SIZE), 5);//default 5 lay 5 bg hien thi
    }

    public void setFetchSize(Integer fetchSize) {
        modelInfo.put(FETCH_SIZE, fetchSize);
    }

    public Integer getTotalCount() {
        return Utils.nvl((Integer) modelInfo.get(TOTAL_COUNT), 0);
    }

    public void add1Fetch() {
        Integer fetchSize = getFetchSize();
        List lst = (List) baseModels.get();
        if (lst == null || lst.size() == 0) {
            return;
        }
        if (getTotalCount() + fetchSize < lst.size()) {
            modelInfo.put(TOTAL_COUNT, getTotalCount() + fetchSize);
        } else {
            modelInfo.put(TOTAL_COUNT, lst.size());
        }
    }


    public void search() {

    }

}
